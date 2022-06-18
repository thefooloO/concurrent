package monitor;

import utils.LockTemplate;
import utils.SimpleLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 可重入锁：一个锁能被同一个线程多次获得
 */
public class SimpleReentrantLock extends LockTemplate {

    Lock lock;
    Condition condition;
    long owner;
    int holdCount;

    public SimpleReentrantLock() {
        lock = new SimpleLock();
        condition = lock.newCondition();
        owner = 0l;
        holdCount = 0;
    }


    @Override
    public void lock() {
        lock.lock();
        try {
            long me = Thread.currentThread().getId();
            if(owner == me) {
                holdCount++;
                return;
            }

            while(holdCount != 0) {
                condition.await();
            }

            owner = me;
            holdCount = 1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void unlock() {
        lock.lock();
        try {
            if(holdCount == 0 || owner != Thread.currentThread().getId())
                throw new IllegalMonitorStateException();
            holdCount--;
            if(holdCount == 0)
                condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}