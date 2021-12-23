package com.nowcode.commuity.controller;


import com.nowcode.commuity.domain.Comment;
import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.domain.Event;
import com.nowcode.commuity.event.EventProducer;
import com.nowcode.commuity.service.CommentService;
import com.nowcode.commuity.service.DiscussPostService;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.HostHolder;
import com.nowcode.commuity.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements Constant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/add/{postId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        comment.setUserId(holder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        commentService.addComment(comment);

        Event event = new Event().setTopic(TOPIC_COMMENT)
                .setUserId(holder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",postId);
        if(comment.getEntityType() == ENTITY_TYPE_POST){
            DiscussPost post = discussPostService.selectPostDetail(comment.getEntityId());
            event.setEntityUserId(post.getUserId());
        }else if(comment.getEntityType() == ENTITY_TYPE_COMMENT){
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);

        if(comment.getEntityType() == ENTITY_TYPE_POST){
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(postId);
            eventProducer.fireEvent(event);
            //计算帖子分数
            String redisKey = RedisLikeUtil.getPostScore();
            redisTemplate.opsForSet().add(redisKey,postId);
        }



        return "redirect:/discuss/detail/"+postId;


    }
}
