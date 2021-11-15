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
}
