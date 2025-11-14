package com.atguigu.lease;

import org.junit.jupiter.api.Test;

import java.util.Random;

public class RandomTest {
    @Test
    public void test(){
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int j = random.nextInt(100000, 999999);
            System.out.println("验证码：" + j);
        }
    }
}
