package test;

import monitor.SimpleReentrantLock;
import utils.SimpleLock;

public class ReentrantLockTestCase {

    static SimpleReentrantLock simpleReentrantLock = new SimpleReentrantLock();
    static SimpleLock simpleLock = new SimpleLock();

    static void A() {
        simpleReentrantLock.lock();
        System.out.println("A()");
        B();
        simpleReentrantLock.unlock();
    }

    static void B() {
        simpleReentrantLock.lock();
        System.out.println("B()");
        simpleReentrantLock.unlock();
    }


    public static void main(String[] args) {
        A();
    }
}