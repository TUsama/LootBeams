package com.lootbeams.utils;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
