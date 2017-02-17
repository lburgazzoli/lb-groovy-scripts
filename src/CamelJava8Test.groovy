@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.22')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.0-SNAPSHOT')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext

import java.util.function.BiFunction
import java.util.function.Function
import java.util.function.Predicate

def ctx = new DefaultCamelContext()
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from("direct:aggregate")
            .aggregate()
                .body(Long.class, { Long b -> (b & 1) == 0 } as Function )
                .strategy()
                    .body(Long.class, { Long o, Long n -> o != null ? o + n : n } as BiFunction )
                .completion()
                    .body(Long.class, { Long b -> b >= 10 } as Predicate)
            .log('${body}')
    }
})

ctx.start()

def tpl = ctx.createProducerTemplate()
tpl.sendBody('direct:aggregate', 0)
tpl.sendBody('direct:aggregate', 1)
tpl.sendBody('direct:aggregate', 2)
tpl.sendBody('direct:aggregate', 3)
tpl.sendBody('direct:aggregate', 4)
tpl.sendBody('direct:aggregate', 5)
tpl.sendBody('direct:aggregate', 6)
tpl.sendBody('direct:aggregate', 7)

for (int i=0; i < 1000; i++) {
    Thread.sleep(1000)
}

ctx.stop()
