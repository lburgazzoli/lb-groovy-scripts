@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.0')
@Grab(group='org.apache.camel', module='camel-core-xml', version='2.18.0')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.properties.PropertiesComponent
import org.apache.camel.main.Main

def p = new Properties()

def main = new Main()
main.bind('root-props', [ 'root.key': 'root.var'] as Properties)

main.addRouteBuilder(new RouteBuilder() {
    void configure() {
        def pc = new PropertiesComponent()
        pc.ignoreMissingLocation = true
        pc.locations = [
           'ref:root-props',
           'file:data/properties/common.properties',
           'file:data/properties/override_.properties',
        ]

        context.addComponent("properties", pc);

        from('timer://foo1?fixedRate=true&period=1000')
            .log('application.domain: {{application.domain}}')
            .log('db.user: {{db.user}}')
            .log('root.key: {{root.key}}')
    }
})

main.run()
