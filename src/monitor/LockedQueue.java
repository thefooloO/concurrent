package monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedQueue<T> {

    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    final T[] items;
    int tail, head, count;

    public LockedQueue(int capacity) {
        items = (T[])new Object[capacity];
    }

    public void enq(T x) throws InterruptedException {
        lock.lock();
        try {
            while(count == items.length) {
                notFull.await();            // 释放锁并挂起, 恢复时重新抢占锁
            }
            items[tail] = x;
            if(++tail == items.length)
                tail = 0;
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T deq() throws InterruptedException {
        lock.lock();
        try {
            while(count == 0) {
                notEmpty.await();
            }
            T x = items[head];
            if(++head == items.length)
                head = 0;
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LockedQueue<Integer> lockedQueue = new LockedQueue<>(2);

        new Thread(() -> {
            for(int i = 0; i < 100; i++) {
                try {
                    System.out.println(lockedQueue.deq());
                } catch (InterruptedException e) {}
            }
        }).start();

        new Thread(() -> {
            for(int i = 0; i < 100; i++) {
                try {
                    lockedQueue.enq(i);
                } catch (InterruptedException e) {}
            }
        }).start();

    }
}