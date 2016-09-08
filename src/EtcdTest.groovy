@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.mousio', module='etcd4j', version='2.12.0')

import mousio.etcd4j.*

//def client = new EtcdClient(URI.create("http://127.0.0.1:2379"))
def client = new EtcdClient()

client.withCloseable {
    def ver = it.version()
    println "server: ${ver.server}, cluster: ${ver.cluster}"
}

