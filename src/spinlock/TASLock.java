package spinlock;

import utils.LockTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

public class TASLock extends LockTemplate {

    AtomicBoolean state = new AtomicBoolean(false);

    @Override
    public void lock() {
        while(state.getAndSet(true)) {}
    }

    @Override
    public void unlock() {
        state.set(false);
    }
}