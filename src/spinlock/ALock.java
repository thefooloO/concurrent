package spinlock;

import utils.LockTemplate;
import java.util.concurrent.atomic.AtomicInteger;

public class ALock extends LockTemplate {

    ThreadLocal<Integer> mySlotIndex = ThreadLocal.withInitial(() -> 0);
    AtomicInteger tail;
    volatile boolean[] flag;
    int size;

    public ALock(int capacity) {
        size = capacity;
        tail = new AtomicInteger(0);
        flag = new boolean[capacity];
        flag[0] = true;
    }


    @Override
    public void lock() {
        int slot = tail.getAndIncrement() % size;    // size为最大并发线程数
        mySlotIndex.set(slot);
        while(!flag[slot]) {}                        // 每个线程在不同的存储单元上旋转, 降低了缓存一致性流量
    }

    @Override
    public void unlock() {
        int slot = mySlotIndex.get();
        flag[slot] = false;
        flag[(slot + 1) % size] = true;              // 通知后面的线程
    }
}