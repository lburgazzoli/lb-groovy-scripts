@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.22')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-salesforce', version='2.18.2')

import com.fasterxml.jackson.annotation.JsonProperty
import com.thoughtworks.xstream.annotations.XStreamAlias
import com.thoughtworks.xstream.annotations.XStreamImplicit
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.salesforce.SalesforceComponent
import org.apache.camel.component.salesforce.SalesforceEndpointConfig
import org.apache.camel.component.salesforce.SalesforceLoginConfig
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase
import org.apache.camel.component.salesforce.internal.dto.NotifyForFieldsEnum
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.slf4j.LoggerFactory
// **************************
//
// **************************

@XStreamAlias("CaseComment")
class CaseComment extends AbstractSObjectBase {
    @JsonProperty("ParentId")
    String parentId
    @JsonProperty("IsPublished")
    Boolean isPublished
    @JsonProperty("CommentBody")
    String commentBody
}

class CaseCommentRecords extends AbstractQueryRecordsBase {
    @XStreamImplicit
    List<CaseComment> records;
}

// **************************
//
// **************************

def ecfg = new SalesforceEndpointConfig()
ecfg.notifyForOperationCreate = true
ecfg.notifyForOperationDelete = true
ecfg.notifyForOperationUndelete = true
ecfg.notifyForOperationUpdate = true
ecfg.notifyForFields = NotifyForFieldsEnum.ALL
ecfg.apiVersion = '39.0'

def lcfg = new SalesforceLoginConfig()
lcfg.userName = System.getenv('SALESFORCE_USERNAME')
lcfg.password = System.getenv('SALESFORCE_PASSWORD')
lcfg.clientId = System.getenv('SALESFORCE_CLIENTID')
lcfg.clientSecret = System.getenv('SALESFORCE_CLIENTSECRET')

def salesforce = new SalesforceComponent()
salesforce.loginConfig = lcfg
salesforce.config = ecfg

def log = LoggerFactory.getLogger("camel-salesforce-comment")

// **************************
//
// **************************

def reg = new SimpleRegistry()
reg.put("salesforce", salesforce)

def ctx = new DefaultCamelContext(reg)
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('direct:create')
            .to("salesforce:createSObject")
        from('direct:query')
            .setHeader('sObjectClass').constant('CaseCommentRecords')
            .setHeader('sObjectQuery').constant('SELECT Id, ParentId FROM CaseComment')
            .to('salesforce:query')
            .log('query ${body.records.size()} ${body}')
            .loopDoWhile(simple('${body.done} == false'))
                .setHeader('sObjectClass').constant('CaseCommentRecords')
                .setHeader('sObjectQuery').simple('${body.nextRecordsUrl}')
                .to('salesforce:queryMore')
                .log('queryMore ${body.records.size()} ${body}')
            .end()
    }
})

ctx.start()


def tpl = ctx.createProducerTemplate()

(1..10).each {
    log.info ("{} - {}",
        it,
        tpl.request('direct:create', {
            it.in.body = new CaseComment()
            it.in.body.parentId = '5000Y000001JYh8QAG'
            it.in.body.commentBody = UUID.randomUUID().toString()
        }).in.getBody(String.class))

    Thread.sleep(50)
}

tpl.sendBody('direct:query', 'go!')

/*
for (int i=0; i < 1000; i++) {
    Thread.sleep(1000)
}
*/

ctx.stop()
