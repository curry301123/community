package com.nowcode.commuity.service;

import com.nowcode.commuity.domain.Message;
import com.nowcode.commuity.mapper.MessageMapper;
import com.nowcode.commuity.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageMapper mapper;

    @Autowired
    private SensitiveFilter filter;

    public List<Message> selectConversation(int userId){
        return mapper.selectConversations(userId);
    }

    public int selectConversationCount(int userId){
        return mapper.selectConversationsCount(userId);
    }

    public List<Message> selectLetters(String conversationId){
        return mapper.selectLetters(conversationId);
    }

    public int selectLettersCount(String conversationId){
        return mapper.selectLettersCount(conversationId);
    }

    public int selectUnreadLetters(int userId,String conversationId){
        return mapper.selectUnreadLettersCount(userId,conversationId);
    }

    public int sendLetters(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(filter.filter(message.getContent()));
        return mapper.sendLetter(message);
    }

    public int readMessage(List<Integer> ids){
        return mapper.readLetter(ids, 1);
    }

    public Message findLatestMessage(int userId,String topic){
        return mapper.findMessage(userId,topic);
    }

    public int findCountMessage(int userId,String topic){
        return mapper.findCountMess(userId,topic);
    }

    public int findUnread(int userId,String topic){
        return mapper.findUnreadMess(userId,topic);
    }

    public List<Message> findNotices(int userId,String topic){
        return mapper.selectNotices(userId,topic);
    }


}
