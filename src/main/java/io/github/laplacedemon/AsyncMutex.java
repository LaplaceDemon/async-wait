package io.github.laplacedemon;

public class AsyncMutex {
    private AsyncPool<Object> asyncPool;
    
    public AsyncMutex() {
        asyncPool = new AsyncPool<>(1, ()-> {
            return new Object();
        });
    }
    
    public void asyncLock(Runnable callback) {
        asyncPool.asyncGet((Object o) -> {
            callback.run();
        });
    }
    
    public void unLock(Object o) {
        asyncPool.returnBack(o);
    }
}
