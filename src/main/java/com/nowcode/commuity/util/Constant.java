package com.nowcode.commuity.util;

public interface Constant {
    /*
    * 激活成功
    * */

    int ACTIVATION_SUCCESS = 0;

    /*
    *重复激活
    * */

    int ACTIVATION_REPEAT = 1;

    /*
    * 激活失败
    * */

    int ACTIVATION_FAILED = 2;

    /*
    * 默认过期时间
    * */
    long DEFAULT_EXPIRED = 3600*12;

    /*
    * 记住后的过期时间
    * */
    long REMEMBER_EXPIRED = 3600*24*100;

    /**
     * 给帖子的评论
     */
    int ENTITY_TYPE_POST = 1;

    /*
    * 给评论的回复
    * */
    int ENTITY_TYPE_COMMENT = 2;

    /*
     * 给评论的回复
     * */
    int ENTITY_TYPE_USER = 3;

    /**
     * 主题:评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * 主题：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * 主题：关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * 主题：发帖
     */
    String TOPIC_PUBLISH= "publish";

    /**
     * 主题：删帖
     */
    String TOPIC_DELETE= "delete";
    /**
     * 主题：分享
     */
    String TOPIC_SHARE= "share";

    /**
     * 系统用户id
     */
    int SYSTEM_USER = 1;

    /**
     * 普通用户
     */
    String AUTHORITY_USER = "user";
    /**
     *
     * 管理员
     */

    String AUTHORITY_ADMIN = "admin";
    /**
     * 版主
     */
    String AUTHORITY_MODERATOR = "moderator";

}
