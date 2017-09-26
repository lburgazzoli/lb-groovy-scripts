import org.jgroups.JChannel
import org.jgroups.ReceiverAdapter
import org.jgroups.View
import org.jgroups.protocols.TCP
import org.jgroups.protocols.TCPPING
import org.jgroups.stack.IpAddress

@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.jgroups', module='jgroups', version='4.0.6.Final')


/*
TCP tcp = new TCP()
tcp.setBindPort(Integer.parseInt(args[0]))

args[1].split(',').each {
    new IpAddress(it)
}.collect {
    it
}

TCPPING tcpping = new TCPPING()
tcpping.initialHosts = args[1].split(',').each {
    new IpAddress(it)
}.collect {
    it
}
*/

JChannel channel = new JChannel("data/tcp.xml")
TCP tcp = channel.getProtocolStack().findProtocol(TCP.class)
tcp.bindPort = Integer.parseInt(args[0])
tcp.bindAddress = InetAddress.getByName('127.0.0.1')


TCPPING tcpping = channel.getProtocolStack().findProtocol(TCPPING.class)
tcpping.initialHosts = args[1].split(',').collect {
    new IpAddress(it)
}

channel.receiver = new ReceiverAdapter() {
    void viewAccepted(View view) {
        println "<< ${view.viewId} accepted"
    }
}
channel.connect("test")

while(true) {
    println "<< id=${channel.view.viewId}, members=${channel.view.members}"
    Thread.sleep(1000)
}