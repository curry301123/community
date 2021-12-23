package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FolloweService implements Constant {
    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    @Autowired
    private UserService userService;

    public void follow(int entityId,int entityType,int userId){
        template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisLikeUtil.getFollowerKey(entityType,entityId);
                String followeeKey = RedisLikeUtil.getFolloweeKey(userId,entityType);
                operations.multi();
                template.opsForZSet().add(followerKey,userId,System.currentTimeMillis());
                template.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                return operations.exec();
            }
        });
    }

    public void unfollow(int entityId,int entityType,int userId){
        template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followerKey = RedisLikeUtil.getFollowerKey(entityId,entityType);
                String followeeKey = RedisLikeUtil.getFolloweeKey(userId,entityType);
                operations.multi();
                template.opsForZSet().remove(followerKey,userId);
                template.opsForZSet().remove(followeeKey,entityId);
                return operations.exec();
            }
        });
    }

    //查询关注的实体的数量
    public long followeeNum(int userId,int entityType){
        String followeeKey = RedisLikeUtil.getFolloweeKey(userId,entityType);
        return template.opsForZSet().zCard(followeeKey);
    }
    //查询某个实体的粉丝数量
    public long followerNum(int entityType,int entityId){
        String followerKey = RedisLikeUtil.getFollowerKey(entityType,entityId);
        return template.opsForZSet().zCard(followerKey);
    }

    //查询某个实体的关注状态
    public boolean followStatus(int userId,int entityType,int entityId){
        String followeeKey = RedisLikeUtil.getFolloweeKey(userId,entityType);
        return  template.opsForZSet().score(followeeKey,entityId) != null;
    }

    //查询某个用户关注的人
    public List<Map<String,Object>> findFollowee(int userId,int offset,int limit){
        String followeeKey = RedisLikeUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);
        Set<Integer> targetIds = template.opsForZSet().reverseRange(followeeKey,offset,offset+limit-1);
        if(targetIds == null){
            return null;
        }
        List<Map<String,Object>> followeeList = new ArrayList<>();
        for(int id : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findById(id);
            map.put("user",user);
            Double score = template.opsForZSet().score(followeeKey,id);
            map.put("followeeTime",new Date(score.longValue()));

            followeeList.add(map);
        }
        return followeeList;
    }

    //查询某个用户的粉丝

    public List<Map<String,Object>> findFollower(int userId,int offset,int limit){
        String followerKey = RedisLikeUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> targetIds = template.opsForZSet().reverseRange(followerKey,offset,offset+limit-1);
        if(targetIds == null){
            return null;
        }
        List<Map<String,Object>> followerList = new ArrayList<>();
        for(int id : targetIds){
            Map<String,Object> map = new HashMap<>();
            User user = userService.findById(id);
            map.put("user",user);
            Double score = template.opsForZSet().score(followerKey,id);
            map.put("followerTime",new Date(score.longValue()));

            followerList.add(map);
        }
        return followerList;
    }
}
