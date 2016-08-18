@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.mousio', module='etcd4j', version='2.12.0')

import mousio.etcd4j.*

def client = new EtcdClient(
    URI.create("http://192.168.1.100:2379"), 
    URI.create("http://192.168.1.100:2479"), 
    URI.create("http://192.168.1.100:2579")
)

client.withCloseable {
    def ver = it.version()
    println "server: ${ver.server}, cluster: ${ver.cluster}"
}

