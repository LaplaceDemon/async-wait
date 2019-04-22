package io.github.laplacedemon;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class TailInvoke {
    /**
     * 统一结构的方法,获得当前递归的下一个递归
     *
     * @param nextFrame 下一个递归
     * @param <T>       T
     * @return 下一个递归
     */
    public static <T> TailRecursion<T> call(final TailRecursion<T> nextFrame) {
        return nextFrame;
    }

    /**
     * 结束当前递归，重写对应的默认方法的值,完成状态改为true,设置最终返回结果,设置非法递归调用
     *
     * @param value 最终递归值
     * @param <T>   T
     * @return 一个isFinished状态true的尾递归, 外部通过调用接口的invoke方法及早求值, 启动递归求值。
     */
    public static <T> TailRecursion<T> done(T value) {
        System.out.println("new");
        return new TailRecursion<T>() {
            @Override
            public TailRecursion<T> apply() {
                throw new Error("递归已经结束,非法调用apply方法");
            }

            @Override
            public boolean isFinished() {
                return true;
            }

            @Override
            public T getResult() {
                return value;
            }
        };
    }
    
    public static TailRecursion<Long> factorialTailRecursion(final long factorial, final long number) {
        if (number == 1) {
            // 结束
            return TailInvoke.done(factorial);
        } else {
            // 循环
            System.out.println(number);
            return TailInvoke.call(() -> factorialTailRecursion(0, number));
        }
    }
    
    public static TailRecursion<Object> forloop(Runnable r) {
        r.run();
        return TailInvoke.call(() -> forloop(r));
    }
    
//    public static TailRecursion<Object> forloop(Consumer<Consumer<Runnable>> co) {
//        r.run();
//        return TailInvoke.call(() -> forloop(r));
//    }
    
    public static void main(String[] args) {
//        Long invoke = factorialTailRecursion(1,10).invoke();
//        System.out.println(invoke);
        
        // 同步回调的循环
        {
            Ref<Integer> x = new Ref<Integer>(0);
            TailRecursion<Object> forloop = forloop(() -> {
                x.value++;
                System.out.println("loop:" + x.value);
            });
            
            forloop.invoke();
        }
        
    }
}
