@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.20.0-SNAPSHOT')
@Grab(group='org.apache.camel', module='camel-undertow', version='2.20.0-SNAPSHOT')


//def repo = new RoutesHealthCheckRepository()
//repo.addEvaluator('inbound', RoutePerformanceCounterEvaluators.exchangesFailed(1))
//main.bind('routes-hc-repo', repo)


import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.health.RoutePerformanceCounterEvaluators
import org.apache.camel.impl.health.RoutesHealthCheckRepository
import org.apache.camel.main.Main

def repo = new RoutesHealthCheckRepository()
repo.addRouteEvaluator('inbound', new RoutePerformanceCounterEvaluators.ExchangesFailed(1))

Main main = new Main()
main.
main.addRouteBuilder(new RouteBuilder() {
    void configure() {
        from('undertow:http://localhost:8080')
            .routeId('inbound')
            .to('undertow:http://wrong.host')
    }
})

main.run(args)