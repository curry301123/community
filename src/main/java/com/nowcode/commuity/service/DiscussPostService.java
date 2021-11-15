package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.mapper.DiscussPostMapper;
import com.nowcode.commuity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /*public List<DiscussPost> findDiscussPost(int userId,int offset,int limit){
        return discussMapper.selectDiscussPost(userId,offset,limit);
    }*/
    public List<DiscussPost> findDiscussPost(int userId){
        return discussMapper.selectDiscussPost(userId);
    }

    public int findAllRows(int userId){
        return discussMapper.selectAll(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussMapper.insertDiscussPost(post);

    }

    public DiscussPost selectPostDetail(int id){
        return discussMapper.selectPostDetail(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussMapper.updateCommentCount(id,commentCount);
    }



}
