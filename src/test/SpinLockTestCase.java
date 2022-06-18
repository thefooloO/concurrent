package test;

import spinlock.*;
import utils.SimpleLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

public class SpinLockTestCase {


    static ExecutorService executorService = Executors.newScheduledThreadPool(10);

    static class Counter {
        private int value;

        public Counter(int value) {
            this.value = value;
        }

        private int getAndIncrement() {
            int temp = value;
            value = temp + 1;
            return temp;
        }
    }


    static TASLock tasLock = new TASLock();
    static TTASLock ttasLock = new TTASLock();
    static BackoffLock backoffLock = new BackoffLock();
    static ALock aLock = new ALock(10);
    static CLHLock clhLock = new CLHLock();
    static ReentrantLock reentrantLock = new ReentrantLock();
    static SimpleLock simpleLock = new SimpleLock();

    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter(0);
        for(int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                for(int j = 0; j < 100; j++) {
                    simpleLock.lock();
                    System.out.println(counter.getAndIncrement());
                    simpleLock.unlock();
                }
            });
        }
    }
}