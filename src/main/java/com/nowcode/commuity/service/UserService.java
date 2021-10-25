package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User findById(int userId){
        return userMapper.findById(userId);
    }


}
