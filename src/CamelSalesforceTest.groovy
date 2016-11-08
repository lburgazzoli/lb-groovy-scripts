@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.0')
@Grab(group='org.apache.camel', module='camel-salesforce', version='2.18.0')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.salesforce.SalesforceComponent
import org.apache.camel.component.salesforce.SalesforceEndpointConfig
import org.apache.camel.component.salesforce.SalesforceLoginConfig
import org.apache.camel.component.salesforce.internal.dto.NotifyForFieldsEnum
import org.apache.camel.main.Main
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import org.apache.camel.component.salesforce.api.PicklistEnumConverter;
import org.apache.camel.component.salesforce.api.dto.AbstractSObjectBase;
import org.apache.camel.component.salesforce.api.dto.AbstractQueryRecordsBase;
import com.fasterxml.jackson.annotation.JsonProperty;

// **************************
//
// **************************

@XStreamAlias("Case")
public class Case extends AbstractSObjectBase {
    @JsonProperty("CaseNumber")
    String caseNumber;    
    @JsonProperty("Subject")
    private String subject;
    @JsonProperty("Description")
    String description;
}

public class CaseRecords extends AbstractQueryRecordsBase {
    @XStreamImplicit
    List<Case> records;
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
ecfg.sObjectClass = "Case"

def lcfg = new SalesforceLoginConfig()
lcfg.userName = System.getenv('SALESFORCE_USERNAME')
lcfg.password = System.getenv('SALESFORCE_PASSWORD')
lcfg.clientId = System.getenv('SALESFORCE_CLIENTID')
lcfg.clientSecret = System.getenv('SALESFORCE_CLIENTSECRET')

def salesforce = new SalesforceComponent()
salesforce.loginConfig = lcfg
salesforce.config = ecfg

// **************************
//
// **************************

Main m = new Main()
m.bind("salesforce", salesforce)
m.addRouteBuilder(new RouteBuilder() {
    void configure() {
        from('salesforce:camel-test?updateTopic=true&sObjectQuery=SELECT Id FROM Case')
            .setHeader('sObjectQuery')
                .simple('SELECT Id,CreatedById,CreatedDate,CaseNumber,Subject,Description FROM Case WHERE Id=\'${in.body.id}\'')
            .setHeader('sObjectClass')
                .simple(CaseRecords.class.name)
                .to('log:salesforce-event?level=INFO&showHeaders=true&multiline=true')
            .enrich('salesforce:query')
                .to('log:salesforce-query?level=INFO&showHeaders=true&multiline=true')
    }
})

m.run()
