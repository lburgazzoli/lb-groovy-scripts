def f = new File('data/lock')
f.deleteOnExit()

def raf = new RandomAccessFile(f, 'rw')
def lock = raf.channel.tryLock()

println "Try lock on ${f} = ${lock}"

for (int i = 0; i < 15; i++) {
    Thread.sleep(1000)

    if (lock == null) {
        lock = raf.channel.tryLock()
        println "Try lock on ${f} = ${lock}"
    }
}


