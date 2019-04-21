package io.github.laplacedemon;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncPool<T> {
    private final static int SPIN_LOCK_COUNT = 10;
    
    private Object lock;
    private ConcurrentLinkedQueue<T> pool;
    private int capacity;
    private ConcurrentLinkedQueue<Consumer<T>> waitingQueue;
    private Consumer<Runnable> taskConsumer;
    
    public AsyncPool(int capacity, Supplier<T> generator) {
        this.lock = new Object();
        this.pool = new ConcurrentLinkedQueue<>();
        this.waitingQueue = new ConcurrentLinkedQueue<Consumer<T>>();
        this.capacity = capacity;
        
        // init pool
        for(int i = 0; i < capacity; i++) {
            T instance = generator.get();
            this.pool.add(instance);
        }
        
        this.taskConsumer = (Runnable task)-> {
            CompletableFuture.runAsync(task);
        };
    }
    
    public void returnBack(T instance) {
        Objects.requireNonNull(instance);
        
        synchronized (lock) {
            this.pool.add(instance);
            Consumer<T> co = waitingQueue.poll();
            if(co != null) {
                this.execute(() -> {
                    if(co != null) {
                        this.asyncGet(co);
                    }
                });
            }
        }
    }
    
    private void execute(Runnable task) {
        this.taskConsumer.accept(task);
    }
    
    public void asyncGet(Consumer<T> co) {
        Objects.requireNonNull(co);
        
        while (true) {
            for(int sp = 0; sp < SPIN_LOCK_COUNT; sp++) {
                final T instance = this.pool.poll();
                // pool is not empty
                if(instance != null) {
                    this.execute(() -> {
                        if(co != null) {
                            co.accept(instance);
                        }
                    });
                    
                    return ;
                }
            }
            
            // pool maybe empty
            // synchronized is reentrant lock.
            synchronized (lock) {
                boolean poolEmptyAgain = isEmptyPool();
                if(poolEmptyAgain) {
                    waitingQueue.add(co);
                    return ;
                } else {
                    // pool is not empty
                    continue;
                }
            }
        }
    }
    
    public void tryGet(Consumer<T> co) {
        tryGet(co, 1);
    }

    public void tryGet(Consumer<T> co, int tryCount) {
        for(int sp = 0; sp < tryCount; sp++) {
            final T instance = this.pool.poll();
            this.execute(() -> {
                if(co != null) {
                    co.accept(instance);
                }
            });
        }
    }
    
    public int size() {
        return pool.size();
    }
    
    private boolean isEmptyPool() {
        return pool.size() == 0;
    }

    public int getCapacity() {
        return capacity;
    }
    
}
