@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='io.fabric8', module='kubernetes-client', version='1.4.4')

import org.slf4j.*
import org.slf4j.impl.*
import io.fabric8.kubernetes.client.*

System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");

def log = LoggerFactory.getLogger('k8s')
def url = System.getenv('KUBERNETES_MASTER')    ?: 'https://192.168.99.100:8443'
def ns  = System.getenv('KUBERNETES_NAMESPACE') ?: 'default'
def cfg = new ConfigBuilder().withNamespace(ns).withMasterUrl(url).build()

new DefaultKubernetesClient(cfg).withCloseable {
    def map = it.configMaps().withName('map-props2').get()
    map?.data.each {
        k,v -> log.info("got key=${k}, val=${v}")

        //def p = new Properties()
        //p.load(new StringReader(v))

        //log.info("got ${p}")
    }
}
