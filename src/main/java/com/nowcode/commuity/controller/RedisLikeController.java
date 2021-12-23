package com.nowcode.commuity.controller;

import com.nowcode.commuity.domain.Event;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.event.EventProducer;
import com.nowcode.commuity.service.LikeService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.HostHolder;
import com.nowcode.commuity.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class RedisLikeController implements Constant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping(value = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId){
        User user = holder.getUser();
        likeService.like(user.getId(), entityType,entityId,entityUserId);
        long likeCount = likeService.likeNums(entityType,entityId);
        int likeStatus = likeService.likeStatus(user.getId(), entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",likeStatus);

        if(likeStatus == 1){
            Event event = new Event().setTopic(TOPIC_LIKE)
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setUserId(user.getId())
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }
        if (entityType == ENTITY_TYPE_POST){
            //计算帖子分数
            String redisKey = RedisLikeUtil.getPostScore();
            redisTemplate.opsForSet().add(redisKey,postId);
        }
        return CommunityUtil.getJSONSting(0,"null",map);
    }
}
