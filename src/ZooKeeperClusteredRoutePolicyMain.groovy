@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.20.0-SNAPSHOT')
@Grab(group='org.apache.camel', module='camel-zookeeper', version='2.20.0-SNAPSHOT')

import org.apache.camel.CamelContext
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.zookeeper.ha.ZooKeeperClusterService
import org.apache.camel.impl.ExplicitCamelContextNameStrategy
import org.apache.camel.impl.ha.ClusteredRoutePolicyFactory
import org.apache.camel.main.Main
import org.apache.camel.main.MainListenerSupport

def id = UUID.randomUUID().toString()
def main = new Main()
main.addMainListener(new MainListenerSupport() {
    @Override
    void configure(CamelContext context) {
        try {
            def service = new ZooKeeperClusterService()
            service.setId('node-' + id)
            service.setNodes(args[0])
            service.setBasePath('/camel')

            context.setNameStrategy(new ExplicitCamelContextNameStrategy('camel-' + id))
            context.addService(service)
            context.addRoutePolicyFactory(ClusteredRoutePolicyFactory.forNamespace('my-ns'))
        } catch (Exception e) {
            throw new RuntimeException(e)
        }
    }
})

main.addRouteBuilder(new RouteBuilder() {
    @Override
    void configure() throws Exception {
        from('timer:clustered?delay=1s&period=1s')
            .routeId('route-' + id)
            .log('Route ${routeId} is running ...')
    }
})

main.run()
