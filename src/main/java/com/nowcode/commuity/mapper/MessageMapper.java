package com.nowcode.commuity.mapper;

import com.nowcode.commuity.domain.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表
    List<Message> selectConversations(int userId);

    //查询当前用户的会话数量
    int selectConversationsCount(int userId);

    //查询某个会话中的私信列表
    List<Message> selectLetters(String conversationId);

    //查询某个会话中私信的数量
    int selectLettersCount(String conversationId);

    //查询某个用户的未读私信的数量和某个会话中的未读数量
    int selectUnreadLettersCount(int userId,String conversationId);

    //发送私信
    int sendLetter(Message message);
    //已读消息
    int readLetter(List<Integer> ids,int status);

    //根据主题和用户id查询最新的系统消息
    Message findMessage(int userId,String topic);

    //查询系统消息的总数
    int findCountMess(int userId,String topic);

    //查询未读消息的数量
    int findUnreadMess(int userId,String topic);

    List<Message> selectNotices(int userId,String topic);





}
