package io.github.laplacedemon;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

public class TestAsyncPool {
    
    @Test
    public void testSP() throws InterruptedException {
        AsyncPool<Integer> asyncPool = new AsyncPool<>(100, ()->{
            int x = (int)(Math.random()*100);
            return x;
        });
        
        final long NUM = 100000000l;
        
        long t0 = System.nanoTime();
        for(long i = 0;i < NUM; i++) {
            asyncPool.asyncGet((Integer x) -> {
            });
        }
        long t1 = System.nanoTime();
        double dt = (t1-t0)/(1000*1000*1000.0);
        
        double tps = NUM/dt;
        
        System.out.println(tps);
    }
    
    @Test
    public void testMP() throws InterruptedException {
        AsyncPool<Integer> asyncPool = new AsyncPool<>(100, ()->{
            int x = (int)(Math.random()*100);
            return x;
        });
        
        final int THNum = 4;
        CountDownLatch cdl = new CountDownLatch(THNum);
        final long NUM = 30000000l;
        long t0 = System.nanoTime();
        
        for(int thnum = 0; thnum<THNum; thnum++) {
            Thread thread = new Thread(()-> {
                for(long i = 0; i < NUM; i++) {
                    asyncPool.tryGet((Integer x)->{
                        System.out.println(x);
                        asyncPool.returnBack(x);
                    });
                }
                
                cdl.countDown();
            });
            thread.start();
        }
        cdl.await();
        
        long t1 = System.nanoTime();
        double dt = (t1-t0)/(1000*1000*1000.0);
        
        double tps = THNum*NUM/dt;
        
        System.out.println(tps);
    }
}
