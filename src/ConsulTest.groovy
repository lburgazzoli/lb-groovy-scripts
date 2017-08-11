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

def logger = LoggerFactory.getLogger('consul-test')
def consul = Consul.builder().build()
def kvClient = consul.keyValueClient()
def sessionClient = consul.sessionClient()

while (true) {
    Thread.sleep(1000)
}
