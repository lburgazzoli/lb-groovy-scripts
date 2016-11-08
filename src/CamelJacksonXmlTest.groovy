@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.0')
@Grab(group='org.apache.camel', module='camel-jacksonxml', version='2.18.0')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main

def maps = [
    [ key1: 'val1', key3: 'val3'],
    [ key2: 'val2']
]

def main = new Main()
main.addRouteBuilder(new RouteBuilder() {
    void configure() {
        from('timer://foo1?fixedRate=true&period=1000')
            .process()
                .message({ it.body = maps })
            .marshal()
                .jacksonxml()
            .log('before: ${body}')
            .to("xslt:file:data/myxslt.xsl")
            .log('after: ${body}')
    }
})

main.run()
