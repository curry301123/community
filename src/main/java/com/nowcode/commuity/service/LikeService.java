package com.nowcode.commuity.service;

import com.nowcode.commuity.util.RedisLikeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {


    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate template;

    //点赞功能
    public void like(int userId,int entityType,int entityId,int entityUserId){
        template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisLikeUtil.keyGenerate(entityType,entityId);
                String userLikeKey = RedisLikeUtil.userKeyGenerate(entityUserId);
                boolean isMember = template.opsForSet().isMember(entityLikeKey,userId);
                operations.multi();
                if(isMember){
                    operations.opsForSet().remove(entityLikeKey,userId);
                    operations.opsForValue().decrement(userLikeKey);
                }else {
                    operations.opsForSet().add(entityLikeKey,userId);
                    operations.opsForValue().increment(userLikeKey);
                }
                return operations.exec();
            }
        });
    }
    //点赞数量
    public long likeNums(int entityType,int entityId){
        String entityLikeKey = RedisLikeUtil.keyGenerate(entityType,entityId);
        return template.opsForSet().size(entityLikeKey);
    }

    //点赞状态
    //这里返回整型的原因是方便以后业务拓展，有可能加入”踩“的业务；
    public int likeStatus(int userId,int entityType,int entityId){
        String entityLikeKey = RedisLikeUtil.keyGenerate(entityType,entityId);
        boolean isMember = template.opsForSet().isMember(entityLikeKey,userId);
        return  isMember ? 1: 0;
    }

    public int userLikeNums(int userId){
        String userLikeKey = RedisLikeUtil.userKeyGenerate(userId);
       Integer count = (Integer) template.opsForValue().get(userLikeKey);
       return count == null ? 0: count;
    }


}
