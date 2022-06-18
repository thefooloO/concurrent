package test;

import monitor.FifoReadWriteLock;
import monitor.SimpleReadWriteLock;

import java.util.Random;

public class ReadWriteLockTestCase {

    static SimpleReadWriteLock simpleReadWriteLock = new SimpleReadWriteLock();
    static FifoReadWriteLock fifoReadWriteLock = new FifoReadWriteLock();


    public static void main(String[] args) {
        Datatest datatest = new Datatest();

        for(int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    datatest.set(new Random().nextInt(30));
                } catch (InterruptedException e) {}
            }).start();
        }

        for(int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    datatest.get();
                } catch (InterruptedException e) {}
            }).start();
        }
    }



    static class Datatest {
        private int data;

        public void set(int data) throws InterruptedException {
            simpleReadWriteLock.writeLock().lock();
            try {
                System.out.println();
                System.out.println(Thread.currentThread().getName() + "准备写入数据：" + data);
                Thread.sleep(20);
                this.data = data;
                System.out.println(Thread.currentThread().getName() + "成功写入数据：" + data);
                System.out.println();
            } finally {
                simpleReadWriteLock.writeLock().unlock();
            }
        }


        public void get() throws InterruptedException {
            simpleReadWriteLock.readLock().lock();
            try {
                System.out.println(Thread.currentThread().getName() + "准备读取数据：" + data);
                Thread.sleep(20);
                System.out.println(Thread.currentThread().getName() + "成功读取数据：" + data);
            } finally {
                simpleReadWriteLock.readLock().unlock();
            }
        }
    }
}