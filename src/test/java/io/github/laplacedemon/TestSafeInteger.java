package io.github.laplacedemon;

import java.util.function.Consumer;
import java.util.function.Function;

import org.junit.Test;

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
    
}


class For {
    private int i = 0;
    
    public Runnable runnable;
    
    public For() {
        loopBody();
    }
    
    void loopBody() {
        runnable.run();
        loopBody();
    }
}
