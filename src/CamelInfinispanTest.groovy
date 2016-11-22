@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.0')
@Grab(group='org.apache.camel', module='camel-infinispan', version='2.18.0')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.infinispan.InfinispanConstants
import org.apache.camel.main.Main
import org.infinispan.client.hotrod.RemoteCacheManager
import org.infinispan.manager.DefaultCacheManager

Main m = new Main()
m.bind("cache-manager",  Boolean.getBoolean('infinispan.remote') ? new RemoteCacheManager() : new DefaultCacheManager())
m.addRouteBuilder(new RouteBuilder() {
    void configure() {
        from('timer:test?period=1s')
            .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.PUT))
            .setHeader(InfinispanConstants.KEY, constant('CamelTimerCounter'))
            .setHeader(InfinispanConstants.VALUE, simple('val: ${exchangeProperty[CamelTimerCounter]}'))
            .to('infinispan:infinispan?cacheContainer=#cache-manager&cacheName=misc_cache')
              .setHeader(InfinispanConstants.OPERATION, constant(InfinispanConstants.GET))
              .to('infinispan:infinispan?cacheContainer=#cache-manager&cacheName=misc_cache')
                .log('get result: ${header[CamelInfinispanOperationResult]}')
    }
})

m.run(args)