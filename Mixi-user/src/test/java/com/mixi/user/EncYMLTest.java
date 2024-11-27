package com.mixi.user;

import org.jasypt.encryption.StringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
@RunWith(SpringRunner.class)
public class EncYMLTest {

    @Autowired
    StringEncryptor encryptor;

    @Test
    public void getPass() {
        System.out.println(encryptor.decrypt("i3qo2y25Hbjddhzf48Z/suQY3yqEuL2CcZJxf+6oalaJ/VvYNUXLPw=="));
        System.out.println(encryptor.encrypt("123.249.107.238:8848"));
        System.out.println(encryptor.encrypt("welSuper@"));
    }
}
