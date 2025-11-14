package com.atguigu.lease;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String,  Object> redisTemplate;

    @Test
    public void testAdd() {
        redisTemplate.opsForValue().set("user", new User("zs", 18));
        System.out.println(redisTemplate.opsForValue().get("user"));
    }
}


@Data

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private Integer age;

    public User(String name, Integer age){
        this.name = name;
        this.age = age;
    }

    public User(){};

}