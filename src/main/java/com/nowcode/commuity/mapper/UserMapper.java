package com.nowcode.commuity.mapper;

import com.nowcode.commuity.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    public User findById(int userId);

    User findByName(String username);
    User findByEmail(String email);

    int insertUser(User user);
    int updateStatus(int id,int status);
    int updateHeader(int id,String url);
    int updatePassword(int id,String password);
}
