package io.github.laplacedemon.cb;

public class TestCallback02 {
    @FunctionalInterface
    static interface IntFunc {
        int apply(int n);
    }
    
    public static void main(String[] args) {
        IntFunc[] factor = new IntFunc[1];
        factor[0] = n -> n <= 0 ? 1 : n * factor[0].apply(n - 1);
        System.out.println(factor[0].apply(10)); // Expect: 3628800
    }
}

