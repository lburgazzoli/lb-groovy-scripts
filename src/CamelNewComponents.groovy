@GrabResolver(name='redhat-ea', root='https://origin-repository.jboss.org/nexus/content/groups/ea/')
@Grab(group='org.apache.camel', module='camel-catalog', version='2.21.0.fuse-000055')

import groovy.json.JsonSlurper
import org.apache.camel.catalog.DefaultCamelCatalog
import org.apache.commons.configuration2.INIConfiguration
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser

import java.nio.file.Paths

def slurper  = new JsonSlurper()
def catalog  = new DefaultCamelCatalog()
def template = new TreeMap()

println ""
println "components:"
catalog.findComponentNames().sort().each {
    def json = slurper.parseText(catalog.componentJSonSchema(it))

    if (json.component.firstVersion == args[0]) {
        printf("  %-20.20s %s\n", it, json.component.description)
    }
}

println ""
println "dataformats:"
catalog.findDataFormatNames().sort().each {
    def json = slurper.parseText(catalog.dataFormatJSonSchema(it))

    if (json.dataformat.firstVersion == args[0]) {
        printf("  %-20.20s %s\n", it, json.dataformat.description)
    }
}

println ""
println "languages:"
catalog.findLanguageNames().sort().each {
    def json = slurper.parseText(catalog.languageJSonSchema(it))

    if (json.language.firstVersion == args[0]) {
        printf("  %-20.20s %s\n", it, json.language.description)
    }
}

println ""
println "others:"
catalog.findOtherNames().sort().each {
    def json = slurper.parseText(catalog.otherJSonSchema(it))

    if (json.other.firstVersion == args[0]) {
        printf("  %-20.20s %s\n", it, json.other.description)
    }
}

