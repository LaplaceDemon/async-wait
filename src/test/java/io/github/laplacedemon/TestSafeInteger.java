package io.github.laplacedemon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.junit.Test;

import io.github.laplacedemon.AsyncFor.Self;

public class TestSafeInteger {
    
    @Test
    public void testMutilExclusiveLock() throws InterruptedException {
        
        Integer x = 0;
        
        final Ref<Integer> ref = new Ref<>(x);
        
        final AsyncMutex asyncMutex = new AsyncMutex();
//        new Thread(() -> {
////            final Runnable r = () -> {
////                ref.value++;
////                asyncMutex.unlock();
////                break label;
////            };
//            
//            label:do {
//                asyncMutex.asyncLock(() -> {
//                    ref.value++;
//                    asyncMutex.unlock();
//                    goto label;
//                });
//            } while(false);
//        }).start();
//        
//        new Thread(()->{
//            asyncMutex.asyncLock(() -> {
//                ref.value++;
//                asyncMutex.unlock();
//            });
//        }).start();
//        
//        new Thread(()->{
//            asyncMutex.asyncLock(() -> {
//                ref.value++;
//                asyncMutex.unlock();
//            });
//        }).start();
        
//        asyncFor(asyncMutex::asyncLock,() -> {});
        
        
      asyncMutex.asyncLock(() -> {
          ref.value++;
          asyncMutex.unlock();
      });
      
      Runnable r = () -> {
          ref.value++;
          asyncMutex.unlock();
      };
      
      Consumer<Runnable> t = (final Runnable rr)->{
          r.run();
          asyncMutex.asyncLock(rr);
      };
      t.accept(r);
        Thread.sleep(1000000);
    }
//    
//    @Test
//    public void testCBFor() throws InterruptedException {
//        Ref<Integer> x = new Ref<Integer>(0);
//        final AsyncFor cbFor;
//        cbFor = () -> {
//            x.value++;
//            CompletableFuture.runAsync(() -> {
//                System.out.println("loop:" + x.value);
////                r.run();
////                cbFor.runnable.run();
////                AsyncFor.this.asyncLoop();
//                cbFor.asyncLoop();
//            });
//        };
//        
////        cbFor.forloop();
//        
//        Thread.sleep(100000000);
//    }
    
    @Test
    public void testCBFor() throws InterruptedException  {
        Ref<Integer> x = new Ref<Integer>(0);
        AsyncFor forloop = AsyncFor.forloop((Self self)->{
            x.value++;
            CompletableFuture.runAsync(() -> {
                System.out.println("loop:" + x.value);
                
                // loop
                AsyncFor.nextLoop(self);
            });
        });
        
        forloop.run();
        Thread.sleep(100000000);
    }
    
    @Test
    public void testSleep() throws InterruptedException  {
        
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        
        Ref<Integer> x = new Ref<Integer>(0);
        
        AsyncFor.forloop((Self self)->{
            
            x.value++;
            ses.schedule(()->{
                System.out.println("loop:" + x.value);
                // loop
                AsyncFor.nextLoop(self);
            }, 1000, TimeUnit.MILLISECONDS);
            
        }).run();
        
        Thread.sleep(100000000);
    }
}

interface AsyncFor extends Runnable {
    
    @FunctionalInterface
    static interface Self {
        void apply(Self self);
    }
    
    public static void nextLoop(Self self) {
        self.apply(self);
    }
    
    public static AsyncFor forloop(Self self) {
        return ()->{
            self.apply(self);
        };
    }
}
