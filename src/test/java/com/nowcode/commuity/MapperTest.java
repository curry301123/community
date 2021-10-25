package com.nowcode.commuity;


import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.mapper.DiscussPostMapper;
import com.nowcode.commuity.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    private DiscussPostMapper mapper;

    @Autowired
    private UserMapper userMapper;

//    @Test
//    public void test1(){
//        List<DiscussPost> discussPosts = mapper.selectDiscussPost(0, 0, 10);
//        for(DiscussPost post : discussPosts){
//            System.out.println(post);
//        }
//        int all = mapper.selectAll(0);
//        System.out.println(all);
//    }

    @Test
    public void test2(){
        User user = userMapper.findById(124);
        System.out.println(user.getHeaderUrl());
        System.out.println(user.getEmail());
    }
}
