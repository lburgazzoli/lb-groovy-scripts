@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.19.0-SNAPSHOT')
@Grab(group='org.apache.camel', module='camel-csv', version='2.19.0-SNAPSHOT')

import org.apache.camel.dataformat.csv.CsvDataFormat
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry

def routes = new ByteArrayInputStream('''
<routes xmlns="http://camel.apache.org/schema/spring">
    <route>
      <from uri="direct:unmarshal"/>
      <unmarshal>
        <csv delimiter=";" headerDisabled="true"/>
      </unmarshal>
    </route>
    <route>
      <from uri="direct:marshal"/>
      <marshal>
        <csv headerDisabled="true" quoteDisabled="true"/>
      </marshal>
      <convertBodyTo type="java.lang.String"/>
    </route>
</routes>
'''.bytes)

def reg = new SimpleRegistry()
reg.put('csv-dataformat', new CsvDataFormat())

def ctx = new DefaultCamelContext(reg)
ctx.addRouteDefinitions(ctx.loadRoutesDefinition(routes).routes)
ctx.start()

def template = ctx.createProducerTemplate()
def result = template.requestBody("direct:marshal", [[ "A1", "B1", "C1" ]])

println result

ctx.stop()