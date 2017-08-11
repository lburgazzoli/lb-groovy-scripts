@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='com.coreos', module='jetcd', version='0.1.0-SNAPSHOT')

import com.coreos.jetcd.*

def client = Client.builder().endpoints(
    'http://localhost:2379',
    'http://localhost:22379',
    'http://localhost:32379')
    .build()

def response = client.clusterClient.listMember().get()
response.members.each {
    m -> println m.name
}

client.close()
