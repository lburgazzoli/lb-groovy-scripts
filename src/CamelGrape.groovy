import org.apache.camel.ComponentVerifier
import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.grape.GrapeComponent
import org.apache.camel.component.grape.GrapeEndpoint
import org.apache.camel.impl.DefaultCamelContext

@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-grape', version='2.19.0-SNAPSHOT')
import java.util.Map

def ctx = new DefaultCamelContext()
def gtx = GrapeComponent.grapeCamelContext(ctx)
gtx.addRoutes(new RouteBuilder() {
    @Override
    void configure() {
        GrapeEndpoint.loadPatches(gtx)

        from('direct:verify')
            .setHeader('CamelVerifierComponent').simple()
            .to('grape:grab')
            .process(new Processor() {
                @Override
                public void process(Exchange e) throws Exception {
                    def componentName = e.in.getHeader("CamelVerifierComponent", String.class)
                    def componentOpts = e.in.getHeader("CamelVerifierOptions", Map.class)
                    def scope         = e.in.getHeader("CamelVerifierScope", ComponentVerifier.Scope.class)
                    def component     = e.context.getComponent(componentName)

                    e.in.body = component.verifier.verify(scope, componentOpts);
                }
            });
    }
})

gtx.start()

def res = gtx.createProducerTemplate().requestBodyAndHeaders(
    'direct:verify',
    'org.apache.camel/camel-twitter/2.19.0-SNAPSHOT',
    [
        'CamelVerifierComponent': 'twitter',
        'CamelVerifierOptions'  : [:],
        'CamelVerifierScope'    : ComponentVerifier.Scope.CONNECTIVITY
    ],
    ComponentVerifier.Result.class)


println res

gtx.stop()