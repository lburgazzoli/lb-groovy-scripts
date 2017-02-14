import org.apache.camel.Exchange
@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.17.0')
import org.apache.camel.LoggingLevel
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext

import java.util.concurrent.TimeUnit

System.setProperty('org.slf4j.simpleLogger.showDateTime', 'true')
System.setProperty('org.slf4j.simpleLogger.dateTimeFormat', 'HH:mm:ss:SSS')

def rnd = new Random()
def ctx = new DefaultCamelContext()
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from('scheduler:timer?delay=2s&initialDelay=5s')
            .log(LoggingLevel.INFO, "Trigger")
            .to('direct:sleep')
        from('direct:sleep')
            .process(new Processor() {
                @Override
                void process(Exchange exchange) throws Exception {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(rnd.nextInt(5)))
                }
            })
    }
})

ctx.start()

while(true) {
    Thread.sleep(1000)
}

ctx.stop()
