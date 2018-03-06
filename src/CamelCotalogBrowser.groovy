import groovy.json.JsonSlurper
@Grab(group = 'org.apache.camel', module = 'camel-catalog', version = '2.20.0')

import org.apache.camel.catalog.DefaultCamelCatalog

def slurper = new JsonSlurper()
def catalog = new DefaultCamelCatalog()

println catalog.modelJSonSchema('when')

//catalog.findModelNames().sort().each {
//    println it catalog.modelJSonSchema(it)
//}
