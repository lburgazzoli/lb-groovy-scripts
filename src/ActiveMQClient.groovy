@Grab('org.slf4j:slf4j-api:1.7.25')
@Grab('org.slf4j:slf4j-simple:1.7.25')
@Grab('org.apache.activemq:activemq-client:5.15.2')
@Grab('org.springframework:spring-jms:4.3.14.RELEASE')

import java.util.UUID
import org.apache.activemq.ActiveMQConnectionFactory

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate
import org.springframework.jms.core.MessageCreator

def cnx = new ActiveMQConnectionFactory()
cnx.brokerURL = 'tcp://localhost:61616'
cnx.userName = 'syndesis'
cnx.password = 'syndesis'

def tpl = new JmsTemplate(cnx)

Thread.start {
    (1..1000).each {        
        def answer = tpl.receive("syndesis-out")
        println "${answer.text}"
    }
}

(1..100).each {
    def msg = UUID.randomUUID().toString()

    tpl.send("syndesis-in", new MessageCreator() {
        Message createMessage(Session session) throws JMSException {
            return session.createTextMessage(msg)
        }
    })

    Thread.sleep(1000)
}
