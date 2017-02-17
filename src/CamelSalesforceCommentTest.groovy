@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.22')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-salesforce', version='2.18.2')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.salesforce.SalesforceComponent
import org.apache.camel.component.salesforce.SalesforceEndpointConfig
import org.apache.camel.component.salesforce.SalesforceLoginConfig
import org.apache.camel.component.salesforce.internal.dto.NotifyForFieldsEnum
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.slf4j.LoggerFactory

// **************************
//
// **************************

def salesforce = new SalesforceComponent()
salesforce.loginConfig = new SalesforceLoginConfig()
salesforce.loginConfig.userName = System.getenv('SALESFORCE_USERNAME')
salesforce.loginConfig.password = System.getenv('SALESFORCE_PASSWORD')
salesforce.loginConfig.clientId = System.getenv('SALESFORCE_CLIENTID')
salesforce.loginConfig.clientSecret = System.getenv('SALESFORCE_CLIENTSECRET')
salesforce.config = new SalesforceEndpointConfig()
salesforce.config.notifyForOperationCreate = true
salesforce.config.notifyForOperationDelete = true
salesforce.config.notifyForOperationUndelete = true
salesforce.config.notifyForOperationUpdate = true
salesforce.config.notifyForFields = NotifyForFieldsEnum.ALL
salesforce.config.apiVersion = '39.0'
salesforce.config.initialReplayIdMap = [ '/topic/comments-1' : -2, '/topic/comments-2' : -1]
salesforce.packages = [ 'salesforce' ]


// **************************
//
// **************************

def log = LoggerFactory.getLogger("camel-salesforce-comment")
def reg = new SimpleRegistry()
reg.put("salesforce", salesforce)

def ctx = new DefaultCamelContext(reg)
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('salesforce:comments-1?updateTopic=true&sObjectQuery=SELECT Id, CommentId__c FROM Comment_Event__c')
            .to('direct:process')
        from('salesforce:comments-2?updateTopic=true&sObjectQuery=SELECT Id FROM Comment_Event__c\'')
            .to('direct:process')

        from('direct:process')
            .to("log:salesforce-comments?level=INFO&showHeaders=false&multiline=false")
            .filter()
                .simple('${body[CommentId__c]} != null')
            .setHeader('sObjectName').constant('CaseComment')
            .setBody()
                .simple('${body[CommentId__c]}')
            .enrich('salesforce:getSObject')
            .to("log:salesforce-comments?level=INFO&showHeaders=false&multiline=false")
    }
})

ctx.start()

/*
def tpl = ctx.createProducerTemplate()
tpl.sendBody('direct:query', 'go!')
*/

for (int i=0; i < 1000; i++) {
    Thread.sleep(1000)
}

ctx.stop()
