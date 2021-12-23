package com.nowcode.commuity.util;

public class RedisLikeUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    public static  final String PREFIX_USER_LIKE = "like:user";
    public static  final String PREFIX_FOLLOWEE = "followee";
    public static  final String PREFIX_FOLLOWER = "follower";
    public static final String PREFIX_KAPTCHA = "kaptcha";
    public static final String PREFIX_TICKET = "ticket";
    public static final String PREFIX_USER = "user";
    public static final String PREFIX_UV = "uv";
    public static final String PREFIX_DAU = "dau";
    public static final String PREFIX_POST = "post";


    //某个实体的赞
    //like:entity:entityType:entityId -> set(userId)
    //采用set集合的原因是可以存储userId 用来记录那个用户点赞
    public static String keyGenerate(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }

    //某个用户的赞
    public static String userKeyGenerate(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }

    //某个用户关注的实体
    //followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //某个实体拥有的粉丝
    //follower:entityType:entityId-> zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //验证码关键词
    public static String getKaptchaKey(String owner){
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    //获取ticket关键词
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }
    //单日UV
    public static String getUVkey(String date){
        return PREFIX_UV + SPLIT + date;
    }

    //合并UV
    public static String getUnionUVkey(String start,String end){
        return PREFIX_UV+SPLIT+start+SPLIT+end;
    }

    //单日DAU
    public static String getDAUKey(String date){
        return PREFIX_DAU + SPLIT +date;
    }

    //合并DAU
    public static String getUnionDAUkey(String start,String end){
        return PREFIX_DAU+SPLIT+start+SPLIT+end;
    }

    //返回统计帖子分数的key
    public static String getPostScore(){
        return PREFIX_POST+SPLIT+"score";
    }
}
