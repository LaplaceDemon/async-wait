package io.github.laplacedemon.cb;

public class TestCallback03 {
    @FunctionalInterface
    static interface SelfIntFunc {
        int apply(SelfIntFunc self, int n);
    }

    public static void main(String[] args) {
        // 定义函数
        SelfIntFunc factor = (self, n) -> n <= 0 ? 1 : n * self.apply(self, n - 1);
        
        // 运算
        System.out.println(factor.apply(factor, 10)); // Expect: 3628800
    }

}
