package io.github.laplacedemon;

import org.junit.Test;

import io.github.laplacedemon.asyncwait.AsyncMutex;

public class TestAsyncMutex {

    @Test
    public void testExclusiveLock() throws InterruptedException {
        final AsyncMutex asyncMutex = new AsyncMutex();
        
        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);
            asyncMutex.unlock();
        });

        while (true) {
            boolean islock = asyncMutex.isLock();
            System.out.println("mutex lock state:" + islock);
            if (!islock) {
                return;
            }
        }

    }

    @Test
    public void testMutilExclusiveLock() throws InterruptedException {
        final AsyncMutex asyncMutex = new AsyncMutex();

        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);
            asyncMutex.unlock();
        });

        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);
            asyncMutex.unlock();
        });

        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);
            asyncMutex.unlock();
        });

        while (true) {
            boolean islock = asyncMutex.isLock();
            System.out.println("mutex lock state:" + islock);
            if (!islock) {
                return;
            }
        }

    }

    @Test
    public void testDeadExclusiveLock0() throws InterruptedException {
        // 这种情况不会造成死锁
        final AsyncMutex asyncMutex = new AsyncMutex();

        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);

            asyncMutex.asyncLock(() -> {
                boolean islock2 = asyncMutex.isLock();
                System.out.println("获得锁, mutex lock state:" + islock2);
                asyncMutex.unlock();
            });

            // 获取锁，并立即释放锁
            asyncMutex.unlock();
        });
        
        while (true) {
            boolean islock = asyncMutex.isLock();
            System.out.println("mutex lock state:" + islock);
            if (!islock) {
                return;
            }
        }
    }
    
    @Test
    public void testDeadExclusiveLock() throws InterruptedException {
        // 这种情况就会造成死锁了！
        final AsyncMutex asyncMutex = new AsyncMutex();

        asyncMutex.asyncLock(() -> {
            boolean islock = asyncMutex.isLock();
            System.out.println("获得锁, mutex lock state:" + islock);

            // 重复获取锁，但不释放锁
            asyncMutex.asyncLock(() -> {
                boolean islock2 = asyncMutex.isLock();
                System.out.println("获得锁, mutex lock state:" + islock2);
                asyncMutex.unlock();
                asyncMutex.unlock();
            });

        });
        
        while (true) {
            boolean islock = asyncMutex.isLock();
            System.out.println("mutex lock state:" + islock);
            if (!islock) {
                return;
            }
        }
    }
}
