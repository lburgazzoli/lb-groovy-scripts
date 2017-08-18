@Grab(group='org.apache.camel', module='camel-catalog', version='2.20.0-SNAPSHOT')
@Grab(group='org.apache.commons', module='commons-csv', version='1.2')

import groovy.json.JsonSlurper
import org.apache.camel.catalog.DefaultCamelCatalog
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser

import java.nio.file.Paths

def slurper  = new JsonSlurper()
def catalog  = new DefaultCamelCatalog()
def template = new TreeMap()

Paths.get('data/camel-support-matrix-orig.csv').withReader { reader ->
    CSVParser csv = new CSVParser(reader, CSVFormat.DEFAULT)

    for (record in csv.iterator()) {
        def name = record.get(0)

        if (!template.containsKey(name)) {
            template[name] = [:]
        }

        template[name]['Karaf'] = record.get(1)
        template[name]['SpringBoot'] = record.get(2)
    }
}

def components = new TreeMap()
catalog.findComponentNames().sort().each {
    def json = slurper.parseText(catalog.componentJSonSchema(it))
    def name = "camel-${it}"

    components[name] = [:]
    components[name]['Karaf'] = ''
    components[name]['SpringBoot'] = ''
    components[name]['EAP'] = 'no'
    components[name]['FirstVersion'] = json.component.firstVersion
    components[name]['Camel'] = json.component.deprecated ? 'deprecated' : 'yes'

    def tpl = template[name]
    if (tpl) {
        components[name]['Karaf'] = tpl.Karaf
        components[name]['SpringBoot'] = tpl.SpringBoot
    }

    Paths.get('data/component.roadmap').eachLine {
        line -> if (it == line.trim()) {
            components[name]['EAP'] = 'yes'
        }
    }
}

def dataformats = new TreeMap()
catalog.findDataFormatNames().sort().each {
    def json = slurper.parseText(catalog.dataFormatJSonSchema(it))
    def name = "camel-${it}"

    dataformats[name] = [:]
    dataformats[name]['Karaf'] = ''
    dataformats[name]['SpringBoot'] = ''
    dataformats[name]['EAP'] = 'no'
    dataformats[name]['FirstVersion'] = json.dataformat.firstVersion
    dataformats[name]['Camel'] = json.dataformat.deprecated ? 'deprecated' : 'yes'

    def tpl = template[name]
    if (tpl) {
        dataformats[name]['Karaf'] = tpl.Karaf
        dataformats[name]['SpringBoot'] = tpl.SpringBoot
    }

    Paths.get('data/dataformat.roadmap').eachLine {
        line -> if (it == line.trim()) {
            dataformats[name]['EAP'] = 'yes'
        }
    }
}

println 'Component,First Version,Camel,Karaf,Spring Boot,EAP'
components.each {
    k,v -> println "${k},${v.FirstVersion},${v.Camel},${v.Karaf},${v.SpringBoot},${v.EAP}"
}

println 'DataFormat,First Version,Camel,Karaf,Spring Boot,EAP'
dataformats.each {
    k,v -> println "${k},${v.FirstVersion},${v.Camel},${v.Karaf},${v.SpringBoot},${v.EAP}"
}

