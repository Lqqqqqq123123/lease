package com.atguigu.lease;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordTest {
    @Test
    public void testMd5(){
        String password = "123456";
        String psdToMd5 = DigestUtils.md5Hex(password);
        System.out.println(psdToMd5);
    }
}
