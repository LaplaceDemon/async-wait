package io.github.laplacedemon.cb;

public class TestCallback06 {
    @FunctionalInterface
    static interface IntFunc {
        int apply(int n);
    }
    
    @FunctionalInterface
    static interface SelfIntFunc {
        int apply(SelfIntFunc self, int n);
    }
    
    public static void main(String[] args) {
System.out.println(((Function<SelfIntFunc, IntFunc>) self -> n -> self.apply(self, n)).apply((self, n) -> n <= 0 ? 1 : n * self.apply(self, n - 1)).apply(10)); // Expect: 3628800
}

}
