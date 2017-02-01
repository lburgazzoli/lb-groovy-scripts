@Grab(group='org.jboss.forge.roaster', module='roaster-api', version='2.19.4.Final')
@Grab(group='org.jboss.forge.roaster', module='roaster-jdt', version='2.19.4.Final')
@Grab(group='org.apache.camel', module='camel-core', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-csv', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-spring-boot', version='2.18.2')
@Grab(group='org.apache.camel', module='camel-csv-starter', version='2.18.2')

import org.apache.camel.CamelContext
import org.apache.camel.dataformat.csv.CsvDataFormat
import org.apache.camel.dataformat.csv.springboot.CsvDataFormatConfiguration
import org.jboss.forge.roaster.Roaster
import org.jboss.forge.roaster._shade.org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
import org.jboss.forge.roaster.model.source.JavaClassSource
import org.jboss.forge.roaster.model.util.Formatter

def body = """
    CsvDataFormat dataformat = new CsvDataFormat();
    if (CamelContextAware.class.isAssignableFrom(CsvDataFormat.class)) {
        CamelContextAware contextAware = CamelContextAware.class.cast(dataformat);
        if (contextAware != null) {
            contextAware.setCamelContext(camelContext);
        }
    }
    try {
        Map<String, Object> parameters = new HashMap<>();
        IntrospectionSupport.getProperties(configuration, parameters, null, false);
        IntrospectionSupport.setProperties(camelContext, camelContext.getTypeConverter(), dataformat, parameters);
    } catch (Exception e) {
        throw new RuntimeCamelException(e);
    }
    return dataformat;
"""

def javaClass = Roaster.create(JavaClassSource.class)
javaClass.setPackage("com.github.lburgazzoli.forge")
javaClass.setName("MyGeneratedClass")

def method = javaClass.addMethod()
method.setPublic()
method.addParameter(CamelContext.class, "camelContext")
method.addParameter(CsvDataFormatConfiguration.class, "configuration")
method.setReturnType(CsvDataFormat.class)
method.setName("createDataFormat")
method.setBody(body)

def properties = new Properties()
properties.put(DefaultCodeFormatterConstants.FORMATTER_LINE_SPLIT, "120")
properties.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, "space")
properties.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "4")

println Formatter.format(properties, javaClass)

