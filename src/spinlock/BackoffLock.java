package spinlock;

import utils.LockTemplate;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackoffLock extends LockTemplate {

    private AtomicBoolean state = new AtomicBoolean(false);
    private static final int MIN_DEALY = 1;
    private static final int MAX_DELAY = 10;

    @Override
    public void lock() {
        Backoff backoff = new Backoff(MIN_DEALY, MAX_DELAY);
        while(true) {
            while(state.get()) {}
            if(!state.getAndSet(true))
                return;
            backoff.backoff();              // 尝试获取锁并失败时后退
        }
    }

    @Override
    public void unlock() {
        state.set(false);
    }
}


/**
 * 保证争用的并发线程在同一时刻不会反复尝试获得锁, 每次线程尝试得到一个锁并失败后, 就把后退时间加倍, 直到到达一个固定的最大值
 */
class Backoff {

    final int minDelay, maxDelay;
    int limit;
    final Random random;

    public Backoff(int min, int max) {
        minDelay = min;
        maxDelay = max;
        limit = minDelay;
        random = new Random();
    }

    public void backoff() {
        try {
            int delay = random.nextInt(limit);
            limit = Math.min(maxDelay, 2 * limit);
            Thread.sleep(delay);
        } catch (InterruptedException e) {}
    }
}