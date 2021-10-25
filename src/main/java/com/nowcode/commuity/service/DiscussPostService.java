package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.mapper.DiscussPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussMapper;

    /*public List<DiscussPost> findDiscussPost(int userId,int offset,int limit){
        return discussMapper.selectDiscussPost(userId,offset,limit);
    }*/
    public List<DiscussPost> findDiscussPost(int userId){
        return discussMapper.selectDiscussPost(userId);
    }

    public int findAllRows(int userId){
        return discussMapper.selectAll(userId);
    }



}
