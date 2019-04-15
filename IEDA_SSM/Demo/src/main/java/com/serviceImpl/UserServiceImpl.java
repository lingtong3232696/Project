package com.serviceImpl;

import com.dao.UserDao;
import com.entity.User;
import org.springframework.stereotype.Service;
import com.service.UserService;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService{

    @Resource
    UserDao userDao;

    public User selectById(int id) {
        return userDao.selectById(id);
    }
}
