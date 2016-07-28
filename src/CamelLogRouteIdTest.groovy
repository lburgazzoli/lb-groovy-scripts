@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.17.0')

import org.apache.camel.impl.*
import org.apache.camel.builder.RouteBuilder

def ctx = new DefaultCamelContext()
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('timer://foo1?fixedRate=true&period=1000')
            .toD('log:${routeId}?level=INFO&groupSize=10');
        from('timer://foo2?fixedRate=true&period=500')
            .toD('log:${routeId}?level=INFO&groupSize=5');
    }
})

ctx.start()

while(true) {
    Thread.sleep(1000)
}

ctx.stop()
