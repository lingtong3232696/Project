package com.controller;

import com.entity.User;
import com.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

        @Autowired
        UserService userService;

        @RequestMapping(value = "/test",method = RequestMethod.POST)
        public Map Test(@RequestParam("id") int id){
           User user =(User) userService.selectById(id);
           Map map = new HashMap();
           map.put("user",user);
           return map;
        }

}
