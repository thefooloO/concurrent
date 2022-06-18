package monitor;

import utils.SimpleLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 可以让至多c个线程进入临界区
 */
public class SimpleSemaphore {

    final int capacity;
    int state;
    Lock lock;
    Condition condition;

    public SimpleSemaphore(int c) {
        capacity = c;
        state = 0;
        lock = new SimpleLock();
        condition = lock.newCondition();
    }

    public void acquire() {
        lock.lock();
        try {
            while(state == capacity) {
                condition.await();
            }
            state++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            state--;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}