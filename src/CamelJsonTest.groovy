@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.0')
@Grab(group='org.apache.camel', module='camel-jackson', version='2.18.0')

import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.ToString
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.model.dataformat.JsonLibrary


@ToString
class InvocationResult {
    @JsonProperty('code')
    String code;
}

@ToString
class UserDetails {
    @JsonProperty('name')
    String name;
}

class ProcessUserDetails {
    InvocationResult process(UserDetails details) {
        return new InvocationResult(code: "result: ${details.name}")
    }
}

@ToString
class CaseDetails {
    @JsonProperty('number')
    Integer number;
}

class ProcessCaseDetails {
    InvocationResult process(CaseDetails details) {
        return new InvocationResult(code: "result: ${details.number}")
    }
}

def reg = new SimpleRegistry()
reg.put('user-bean', new ProcessUserDetails())
reg.put('case-bean', new ProcessCaseDetails())

def ctx = new DefaultCamelContext(reg)
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('direct:start')
            .choice()
                .when(header("type").isEqualTo("user"))
                    .unmarshal().json(JsonLibrary.Jackson, UserDetails.class)
                    .to('bean:user-bean')
                    .marshal().json(JsonLibrary.Jackson)
                        .to('log:user?showAll=true&multiline=true')
                .when(header("type").isEqualTo("case"))
                    .unmarshal().json(JsonLibrary.Jackson, CaseDetails.class)
                    .to('bean:case-bean')
                    .marshal().json(JsonLibrary.Jackson)
                        .to('log:case?showAll=true&multiline=true')
    }
})


ctx.start()

def template = ctx.createProducerTemplate()
template.requestBodyAndHeader('direct:start', '{ \"name\": \"UserName\" }', "type", "user", String.class)
template.requestBodyAndHeader('direct:start', '{ \"number\": 10 }', "type", "case", String.class)

ctx.stop()