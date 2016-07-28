@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='io.fabric8', module='kubernetes-client', version='1.3.100')

import org.slf4j.*
import io.fabric8.kubernetes.client.*

def logger = LoggerFactory.getLogger("k8s")
def config = new ConfigBuilder().withMasterUrl("https://172.28.128.4:8443").build()

new DefaultKubernetesClient(config).withCloseable {
    it.configMaps().withLabels([ 'karaf-id': 'karaf-1' ]).list().items.each {
        logger.info("map ${it}")
    }
}