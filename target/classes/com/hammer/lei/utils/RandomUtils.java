package com.hammer.lei.utils;

public class RandomUtils {
    public static int generateDateWithBitwiseNot() {
        long time = System.currentTimeMillis();
        return generateDateWithBitwiseNot(time);
    }

    public static int generateDateWithBitwiseNot(long time) {
        return -((int) time + 1);
    }
}
