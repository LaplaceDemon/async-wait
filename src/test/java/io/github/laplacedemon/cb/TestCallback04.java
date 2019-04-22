package io.github.laplacedemon.cb;

public class TestCallback04 {
    @FunctionalInterface
    static interface SelfIntFunc {
        int apply(SelfIntFunc self, int n);
    }
    
    public static void main(String[] args) {
        int value = (
                // 匿名类，定义方法
                (SelfIntFunc) 
                (self, n) -> n <= 0 ? 1 : n * self.apply(self, n - 1)
        // 传参
        ).apply(
                (self, n) -> n <= 0 ? 1 : n * self.apply(self, n - 1)
                        , 10
        );
        System.out.println(value); // Expect: 3628800}
    }
}
