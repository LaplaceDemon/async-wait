package io.github.laplacedemon.asyncwait;

/**
 * Exclusive lock
 * @author zhuoyun
 *
 */
public class AsyncMutex {
    private AsyncSemaphore as;
    
    public AsyncMutex() {
        as = new AsyncSemaphore(1);
    }
    
    public void asyncLock(Runnable callback) {
        as.asyncAcquire(() -> {
            callback.run();
        });
    }
    
    public void unlock() {
        as.release();
    }

    public boolean isLock() {
        return as.isLock();
    }
}
