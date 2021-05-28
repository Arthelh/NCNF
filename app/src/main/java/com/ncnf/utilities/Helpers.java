package com.ncnf.utilities;

import java.util.concurrent.CompletableFuture;

public class Helpers {

    /**
     * Method to combine the result of multiple CompletableFuture
     */
    public static CompletableFuture<Boolean> combine(CompletableFuture<Boolean> u1, CompletableFuture<Boolean> u2) {
        return u1.thenCombine(u2, (v1, v2) -> v1 && v2)
                .exceptionally(exception -> false);
    }
}
