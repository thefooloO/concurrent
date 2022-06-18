package monitor;

import utils.LockTemplate;
import utils.SimpleLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

public class SimpleReadWriteLock implements ReadWriteLock {

    int readers;
    boolean writer;
    Lock lock;
    Condition condition;
    Lock readLock, writeLock;


    public SimpleReadWriteLock() {
        writer = false;
        readers = 0;
        lock = new SimpleLock();
        condition = lock.newCondition();
        readLock = new ReadLock();
        writeLock = new WriteLock();
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }


    class ReadLock extends LockTemplate {

        @Override
        public void lock() {
            lock.lock();
            try {
                while(writer)
                    condition.await();
                readers++;
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
                readers--;
                if(readers == 0)
                    condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }


    class WriteLock extends LockTemplate {

        @Override
        public void lock() {
            lock.lock();
            try {
                while(readers > 0 || writer)
                    condition.await();
                writer = true;
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
                writer = false;
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }
}