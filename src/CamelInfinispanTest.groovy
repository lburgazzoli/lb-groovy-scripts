@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.17.2')
@Grab(group='org.apache.camel', module='camel-infinispan', version='2.17.2')

import org.apache.camel.impl.*
import org.apache.camel.builder.RouteBuilder
import org.infinispan.client.hotrod.RemoteCacheManager
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder

def ctx = new DefaultCamelContext()
ctx.addRoutes(new RouteBuilder() {
    void configure() {
        from("direct:run")
            .to("infinispan:localhost")
                .to("log:camel-groovy?level=INFO&showAll=true&multiline=true");
    }
})

ctx.start()

def template = ctx.createProducerTemplate()


template.sendBodyAndHeaders("direct:run", null, [ 
    'CamelInfinispanCacheName' : 'idempotent',
    'CamelInfinispanOperation' : 'CamelInfinispanOperationPut',
    'CamelInfinispanKey'       : 'key', 
    'CamelInfinispanValue'     : UUID.randomUUID().toString() 
])

/*
template.sendBodyAndHeaders("direct:run", null, [ 
    'CamelInfinispanCacheName' : 'idempotent',
    'CamelInfinispanOperation' : 'CamelInfinispanOperationGet',
    'CamelInfinispanKey'       : 'key' 
])
*/

Thread.sleep(5000)

ctx.stop()
