@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.14')
@Grab(group='org.mousio', module='etcd4j', version='2.12.0')

import mousio.etcd4j.*

println new EtcdClient().version()

