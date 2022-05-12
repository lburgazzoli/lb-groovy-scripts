// camel-k: language=groovy
// camel-k: dependency=camel-endpoint-dsl

def k = kafka("default-topic")
    .brokers('{{KAFKA_URL}}')
    .securityProtocol('SASL_SSL')
    .saslMechanism('PLAIN')
    .saslJaasConfig('org.apache.kafka.common.security.plain.PlainLoginModule required username="{{KAFKA_USR}}" password="{{KAFKA_PWD}}";')
    .keySerializer('org.apache.kafka.common.serialization.StringSerializer')
    .valueSerializer('org.apache.kafka.common.serialization.StringSerializer')

from('file://data?idempotent=true&delete=false&noop=true')
    .unmarshal().json()
    .split().body()
    //.to('log:info?multiLIne=true')
    .setHeader("kafka.KEY").simple('${body[event][key]}')
    .setHeader("kafka.OVERRIDE_TOPIC").simple('${body[topic]}')
    .setBody().simple('${body[event][value]}')
    .marshal().json()
    .to('log:info?multiLIne=true&showHeaders=true')
    .to(k)
