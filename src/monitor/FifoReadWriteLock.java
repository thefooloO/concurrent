package monitor;

import utils.LockTemplate;
import utils.SimpleLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * 公平的读写锁：一旦一个写者调用了写锁的lock()方法, 则不允许有更多的读者获得读锁
 */
public class FifoReadWriteLock implements ReadWriteLock {

    int readAcquires, readReleases;   // 读锁获取、释放的总次数     当readAcquires == readReleases时, 没有线程持有读锁
    boolean writer;                   // writer为true时表示写者尝试锁定或已锁定
    Lock lock;
    Condition condition;
    Lock readLock, writeLock;

    public FifoReadWriteLock() {
        readAcquires = readReleases = 0;
        writer = false;
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
                readAcquires++;
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
                readReleases++;
                if(readAcquires == readReleases)
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
                while(writer)
                    condition.await();

                writer = true;

                while(readAcquires != readReleases)
                    condition.await();
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