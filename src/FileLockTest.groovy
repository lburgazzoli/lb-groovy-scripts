


def f = new File('data/lock')
f.deleteOnExit()

def r = new RandomAccessFile(f, 'rw')

println "Try lock on ${r} = " + r.channel.tryLock()

for (int i = 0; i < 100; i++) {
    Thread.sleep(1000)
}


