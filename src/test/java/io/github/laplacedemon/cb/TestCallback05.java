package io.github.laplacedemon.cb;

public class TestCallback05 {
    @FunctionalInterface
    static interface IntFunc {
        int apply(int n);
    }
    
    @FunctionalInterface
    static interface SelfIntFunc {
        int apply(SelfIntFunc self, int n);
    }
    
    static IntFunc build(SelfIntFunc self) {
        return n -> self.apply(self, n);
    }

    public static void main(String[] args) {
        int value = build (  // 构建函数
                (self, n) -> n <= 0 ? 1 : n * self.apply(self, n - 1)
             )
             // 调用
            .apply(10);
        
        System.out.println(value); // Expect: 3628800
    }

}
