package io.github.laplacedemon;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class AsyncSemaphore {
    private final static int SPIN_LOCK_COUNT = 10;
    private AtomicNonNegativeInteger ai;
    private Object lock;
    private int capacity;
    private ConcurrentLinkedQueue<Runnable> waitingQueue;
    private Consumer<Runnable> taskConsumer;
    
    public AsyncSemaphore(int capacity) {
        this.lock = new Object();
        this.ai = new AtomicNonNegativeInteger(capacity);
        this.waitingQueue = new ConcurrentLinkedQueue<>();
        this.taskConsumer = (Runnable task)-> {
            CompletableFuture.runAsync(task);
        };
    }
    
    public void asyncAcquire(Runnable task) {
        Objects.requireNonNull(task);
        
        while (true) {
            for(int sp = 0; sp < SPIN_LOCK_COUNT; sp++) {
                boolean result = ai.tryDecrement();
                // pool is not empty
                if(result) {
                    this.execute(() -> {
                        task.run();
                    });
                    
                    return ;
                }
            }
            
            // pool maybe empty
            // synchronized is reentrant lock.
            synchronized (lock) {
                boolean poolEmptyAgain = ai.isZero();
                if(poolEmptyAgain) {
                    waitingQueue.add(task);
                    return ;
                } else {
                    // pool is not empty
                    continue;
                }
            }
        }
    }
    
    private void execute(Runnable task) {
        this.taskConsumer.accept(task);
    }
    
    public void release() {
        synchronized (lock) {
            this.ai.increment();
            Runnable r = waitingQueue.poll();
            if(r != null) {
                this.execute(() -> {
                    if(r != null) {
                        this.asyncAcquire(r);
                    }
                });
            }
        }
    }

    public int getCapacity() {
        return capacity;
    }
    
    public boolean isLock() {
        return this.ai.get() == 0;
    }
}
