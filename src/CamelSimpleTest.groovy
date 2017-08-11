@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.1')

import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.builder.RouteBuilder

def ctx = new DefaultCamelContext()
ctx.addRoutes(new RouteBuilder() {
    @Override
    public void configure() {
        from('file:/home/lburgazz/tmp/camel-in')
            .choice()
                .when()
                    .simple('${header.CamelFileName.startsWith("test")}')
                    .log('true')
                .otherwise()
                    .log('false')
    }
});
ctx.start()

for (int i = 0; i< 1000; i++) {
    Thread.sleep(1000)
}

ctx.stop()
