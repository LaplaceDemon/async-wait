package io.github.laplacedemon;

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
    
    public void release(Object o) {
        as.release();
    }
}
