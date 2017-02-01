@Grab(group='org.jboss.forge.roaster', module='roaster-api', version='2.19.4.Final')
@Grab(group='org.jboss.forge.roaster', module='roaster-jdt', version='2.19.4.Final')

import org.jboss.forge.roaster.Roaster
import org.jboss.forge.roaster.model.source.JavaClassSource
import org.jboss.forge.roaster.model.util.Formatter

JavaClassSource javaClass = Roaster.create(JavaClassSource.class)
javaClass.setPackage("com.github.lburgazzoli.forge")
javaClass.setName("MyGeneratedClass")
javaClass.addMethod()
    .setPublic()
    .setReturnTypeVoid()
    .setName("createDataFormatFactory")
    .setBody("""
        return new DataFormatFactory() {
            public void newInstance() throws IllegalArgumentException, IllegalStateException {
            }
        };
    """)

Properties properties = new Properties()
properties.put("org.eclipse.jdt.core.formatter.lineSplit", "120")
properties.put("org.eclipse.jdt.core.formatter.tabulation.char", "space")
properties.put("org.eclipse.jdt.core.formatter.tabulation.size", "4")
properties = Formatter.applyShadedPackageName(properties)

println Formatter.format(properties, javaClass)

