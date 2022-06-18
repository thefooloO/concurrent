package spinlock;

import utils.LockTemplate;
import java.util.concurrent.atomic.AtomicReference;


public class CLHLock extends LockTemplate {

    AtomicReference<QNode> tail;        // 最近加入到队列的节点
    ThreadLocal<QNode> myPred;
    ThreadLocal<QNode> myNode;

    public CLHLock() {
        tail = new AtomicReference<>(new QNode());
        myPred = ThreadLocal.withInitial(() -> new QNode());
        myNode = ThreadLocal.withInitial(() -> new QNode());
    }

    @Override
    public void lock() {
        QNode qNode = myNode.get();
        qNode.locked = true;          // 等待锁或已经获得锁
        QNode pred = tail.getAndSet(qNode);
        myPred.set(pred);
        while(pred.locked) {}         // 轮询前一节点的状态
    }

    @Override
    public void unlock() {
        QNode qNode = myNode.get();
        qNode.locked = false;         // 释放锁
        myNode.set(myPred.get());     // 为了将来的锁访问
    }
}


class QNode {
    volatile boolean locked = false;
}