@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.0-SNAPSHOT')
@Grab(group='org.apache.camel', module='camel-csv', version='2.19.0-SNAPSHOT')
import org.apache.camel.impl.DefaultCamelContext

def routes = new ByteArrayInputStream('''
<routes xmlns="http://camel.apache.org/schema/spring">
    <route>
        <from uri="file:/home/lburgazz/tmp/camel-in" />         
        <choice>
            <when>
            <simple>${header.CamelFileName.startsWith('test')} == 'true'</simple>
                <to uri="file:/home/lburgazz/tmp/camel-out?noop=true" />                  
            </when>
        </choice>
    </route>
</routes>
'''.bytes)

def ctx = new DefaultCamelContext()
ctx.addRouteDefinitions(ctx.loadRoutesDefinition(routes).routes)
ctx.start()

for (int i = 0; i< 1000; i++) {
    Thread.sleep(1000)
}

ctx.stop()