@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='io.fabric8', module='kubernetes-client', version='1.3.102')

import org.slf4j.*
import org.slf4j.impl.*
import io.fabric8.kubernetes.client.*

System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

def log = LoggerFactory.getLogger('k8s')
def url = System.getenv('KUBERNETES_MASTER')    ?: 'https://192.168.99.100:8443'
def ns  = System.getenv('KUBERNETES_NAMESPACE') ?: 'default'
def cfg = new ConfigBuilder().withNamespace(ns).withMasterUrl(url).build()

new DefaultKubernetesClient(cfg).withCloseable {
    def map = it.configMaps().withName('map2').get()
    log.info("got > ${map?.data}")
}
