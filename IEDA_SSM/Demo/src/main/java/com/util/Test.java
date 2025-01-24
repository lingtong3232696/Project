package com.util;

import com.dao.UserDao;
import com.entity.User;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-mybatis.xml"})
public class Test {

    @Autowired
    UserDao userDao;

    @org.junit.Test
    public void Test() throws Exception{
        User user = userDao.selectById(2);
        System.out.println("userNmae==="+user.getUsername());
    }

}
