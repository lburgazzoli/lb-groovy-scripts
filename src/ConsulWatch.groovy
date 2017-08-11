@Grab(group='org.slf4j', module='slf4j-api', version='1.7.25')
@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.25')
@Grab(group='com.orbitz.consul', module='consul-client', version='0.15.0')

import com.orbitz.consul.Consul
import com.orbitz.consul.async.ConsulResponseCallback
import com.orbitz.consul.model.ConsulResponse
import com.orbitz.consul.model.kv.Value
import com.orbitz.consul.model.session.ImmutableSession
import com.orbitz.consul.option.QueryOptions
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicReference

def logger = LoggerFactory.getLogger('consul-watch')
def consul = Consul.builder().build()
def kvClient = consul.keyValueClient()
def sessionClient = consul.sessionClient()
def sessionId = sessionClient.createSession(ImmutableSession.builder().name("test").ttl("60s").build()).getId()

def callback = new ConsulResponseCallback<Optional<Value>>() {
    AtomicReference<BigInteger> index = new AtomicReference<BigInteger>(null)

    @Override
    void onComplete(ConsulResponse<Optional<Value>> consulResponse) {
        logger.info("onComplete")
        if (consulResponse.getResponse().isPresent()) {
            Value v = consulResponse.getResponse().get()
            logger.info('Value is: {}', v.getValue())

        }

        index.set(consulResponse.getIndex())
        onWatch()
    }

    void onWatch() {
        logger.info('onWatch')
        sessionClient.renewSession(sessionId)
        kvClient.getValue('/foo', QueryOptions.blockSeconds(5, index.get()).build(), this)
    }

    @Override
    void onFailure(Throwable throwable) {
        logger.warn("", throwable)
        onWatch()
    }
}

kvClient.acquireLock('/foo', sessionId)
kvClient.getValue('/foo', QueryOptions.blockSeconds(5, new BigInteger('0')).build(), callback)

while (true) {
    Thread.sleep(1000)
}