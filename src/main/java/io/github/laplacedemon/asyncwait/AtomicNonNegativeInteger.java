package io.github.laplacedemon.asyncwait;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicNonNegativeInteger {
    private AtomicInteger ai;
    
    public AtomicNonNegativeInteger(int x) {
        if(x < 0) {
            throw new IllegalArgumentException();
        }
        this.ai = new AtomicInteger(x);
    }
    
    public boolean tryDecrement() {
        int i = this.ai.get();
        if(i <= 0) {
            return false;
        }
        
        int newi = i - 1;
        return this.ai.compareAndSet(i, newi);
    }
    
    public void increment() {
        this.ai.incrementAndGet();
    }

    public boolean isZero() {
        int i = this.ai.get();
        return i == 0;
    }

    public int get() {
        return ai.get();
    }
}
