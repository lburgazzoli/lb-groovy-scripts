@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.17.3')
@Grab(group='org.apache.camel', module='camel-sql', version='2.17.3')
@Grab(group='org.hsqldb', module='hsqldb', version='2.3.4')
@Grab(group='org.postgresql', module='postgresql', version='9.4.1211')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository
import org.postgresql.ds.PGSimpleDataSource

import javax.sql.DataSource
import java.util.concurrent.TimeUnit

System.setProperty('org.slf4j.simpleLogger.defaultLogLevel' , 'INFO')
System.setProperty('org.slf4j.simpleLogger.levelInBrackets' , 'true')
System.setProperty('org.slf4j.simpleLogger.showDateTime'    , 'true')
System.setProperty('org.slf4j.simpleLogger.dateTimeFormat'  , 'HH:mm:ss')
System.setProperty('org.slf4j.simpleLogger.showThreadName'  , 'true')

DataSource createDataSource() {
    PGSimpleDataSource ds = new PGSimpleDataSource()
    ds.setDatabaseName('idempotent')
    ds.setServerName('localhost')
    ds.setUser('postgres')
    ds.setPassword('pgpass')

    return ds
}

int msgId1 = 0
int msgId2 = 0

Main main = new Main()
main.addRouteBuilder(new RouteBuilder() {
    @Override
    public void configure() {
        from('timer:sample1?period=1000')
            .process { it.in.headers['Message-ID'] = msgId1++ }
            .idempotentConsumer(
                header('Message-ID'),
                new JdbcMessageIdRepository(
                    createDataSource(),
                    'idempotent'))
            .toD('log:${routeId}?level=INFO&showHeaders=true')
        from('timer:sample2?period=1000')
            .process { it.in.headers['Message-ID'] = msgId2++ }
            .idempotentConsumer(
                header('Message-ID'),
                new JdbcMessageIdRepository(
                    createDataSource(),
                    'idempotent'))
            .toD('log:${routeId}?level=INFO&showHeaders=true')
    }
})

main.duration = 10
main.timeUnit = TimeUnit.SECONDS
main.run()