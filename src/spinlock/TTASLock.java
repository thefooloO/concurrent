package spinlock;

import utils.LockTemplate;
import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLock extends LockTemplate {

    AtomicBoolean state = new AtomicBoolean(false);

    @Override
    public void lock() {
        while(true) {
            while(state.get()) {}     // 本地旋转(反复读处理器缓存)
            if(!state.getAndSet(true))
                return;
        }
    }

    @Override
    public void unlock() {
        state.set(false);             // 会使得自旋线程的缓存副本失效, 引起一场总线流量风暴
    }
}