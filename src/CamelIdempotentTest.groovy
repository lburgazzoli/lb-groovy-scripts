@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.21')
@Grab(group='org.apache.camel', module='camel-core', version='2.17.3')
@Grab(group='org.apache.camel', module='camel-sql', version='2.17.3')
@Grab(group='org.hsqldb', module='hsqldb', version='2.3.4')

import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository
import org.hsqldb.jdbc.JDBCDataSource
import org.slf4j.impl.SimpleLogger

import java.util.concurrent.TimeUnit

System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, 'INFO')
System.setProperty(SimpleLogger.LEVEL_IN_BRACKETS_KEY, 'true')
System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY   , 'true')
System.setProperty(SimpleLogger.DATE_TIME_FORMAT_KEY , 'HH:mm:ss')
System.setProperty(SimpleLogger.SHOW_THREAD_NAME_KEY , 'true')

int msgId1 = 0
int msgId2 = 0

JDBCDataSource ds = new JDBCDataSource()
ds.setDatabase('mem:idempotent')

Main main = new Main();
main.addRouteBuilder(new RouteBuilder() {
    @Override
    public void configure() {
        from('timer:sample1?period=1000')
            .process { it.in.headers['Message-ID'] = msgId1++ }
            .idempotentConsumer(
                header('Message-ID'),
                new JdbcMessageIdRepository(ds, 'idempotent'))
            .toD('log:${routeId}?level=INFO&showHeaders=true')
        from('timer:sample2?period=1000')
            .process { it.in.headers['Message-ID'] = msgId2++ }
            .idempotentConsumer(
                header('Message-ID'),
                new JdbcMessageIdRepository(ds, 'idempotent'))
            .toD('log:${routeId}?level=INFO&showHeaders=true')
    }
})

main.setDuration(10)
main.setTimeUnit(TimeUnit.SECONDS)
main.run(args);