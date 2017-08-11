@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.0-SNAPSHOT')
@Grab(group='org.apache.camel', module='camel-csv', version='2.19.0-SNAPSHOT')

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main

def main = new Main()
main.addRouteBuilder(new RouteBuilder() {
    void configure() {
        from('timer://test?fixedRate=true&period=1000')
            .log('I am alive')
    }
})

main.run()