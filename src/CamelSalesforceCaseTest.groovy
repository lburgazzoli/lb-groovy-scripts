@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.22')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-salesforce', version='2.18.2')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.salesforce.SalesforceComponent
import org.apache.camel.component.salesforce.SalesforceEndpointConfig
import org.apache.camel.component.salesforce.SalesforceLoginConfig
import org.apache.camel.component.salesforce.api.SalesforceException
import org.apache.camel.component.salesforce.internal.dto.NotifyForFieldsEnum
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
// *****************************************************************************
//
// *****************************************************************************

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
salesforce.config.apiVersion = '38.0'
salesforce.packages = [ 'salesforce' ]

// *****************************************************************************
//
// *****************************************************************************

def reg = new SimpleRegistry()
reg.put("salesforce", salesforce)

def ctx = new DefaultCamelContext(reg)
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('direct:get')
            .setHeader('sObjectName').constant('Case')
            .doTry()
                .enrich('salesforce:getSObject')
                .log('Got ${body}')
            .doCatch(SalesforceException.class)
                .log("Exception caught")
            .end()
    }
})

ctx.start()

def tpl = ctx.createProducerTemplate()
tpl.sendBody('direct:get', '5000Y000001HalPQAS')

ctx.stop()
