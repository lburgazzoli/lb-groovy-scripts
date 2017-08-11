@Grab(group='org.apache.camel', module='camel-core', version='2.19.0-SNAPSHOT')

import java.lang.management.*
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory as JmxFactory
import javax.management.remote.JMXServiceURL as JmxUrl

def serverUrl = 'service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi'
def beanName  = 'org.apache.camel:context=camel-1,type=components,name="timer"'
def server    = JmxFactory.connect(new JmxUrl(serverUrl)).MBeanServerConnection
def gmxb      = new GroovyMBean(server, beanName)

println "Connected to:\n$gmxb\n"
println "Executing verify()"

println gmxb.verify('parameters', [:])