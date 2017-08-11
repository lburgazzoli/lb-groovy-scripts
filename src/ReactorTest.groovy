@Grab(group='org.slf4j', module='slf4j-api', version='1.7.22')
@Grab(group='org.slf4j', module='slf4j-simple', version='1.7.22')
@Grab(group='io.projectreactor', module='reactor-core', version='3.0.5.RELEASE')

import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.concurrent.CountDownLatch
import reactor.core.publisher.TopicProcessor
import java.util.concurrent.TimeUnit

def latch = new CountDownLatch(5)
def proc = TopicProcessor.create()

proc.subscribe(new Subscriber<String>() {
    @Override
    void onSubscribe(Subscription subscription) {
        subscription.request(10)
        println("onSubscribe " + subscription)
    }
    @Override
    void onNext(String data) {
        println("onNext " + data)
        latch.countDown()
    }
    @Override
    void onError(Throwable throwable) {
        println("onError " + throwable)
    }
    @Override
    void onComplete() {
        println("onComplete")
    }
})

proc.onNext("test1")
proc.onNext("test2")

latch.await(10, TimeUnit.SECONDS)
