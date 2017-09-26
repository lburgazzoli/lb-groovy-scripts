@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.2')
@Grab(group='org.apache.camel', module='camel-undertow', version='2.19.2')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main

def main = new Main()
main.addRouteBuilder(new RouteBuilder() {
    void configure() {
        restConfiguration()
            .host("localhost")
            .port(8080)

        rest("/call")
            .get("/{id}")
            .to("direct:call")

        from("direct:call")
            .to("log:rest?level=INFO&showAll=true&multiline=true")
            .to("undertow:http://localhost:9021")

        from("undertow:http://localhost:9021")
            .transform()
                .simple("Hello !")
    }
})

main.run(args)