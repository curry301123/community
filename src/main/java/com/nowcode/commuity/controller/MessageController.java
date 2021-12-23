package com.nowcode.commuity.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcode.commuity.domain.Message;
import com.nowcode.commuity.domain.Page;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.MessageService;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.*;

@Controller
public class MessageController implements Constant {
    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/letter/list",method = RequestMethod.GET)
    public String getLetterList(Model model,@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                @RequestParam(required = false,defaultValue = "5") Integer pageSize){
        User user = holder.getUser();

        PageHelper.startPage(pageNum,pageSize);
        List<Message> conversationList = messageService.selectConversation(user.getId());
        List<Map<String,Object>> conversations = new ArrayList<>();
        if(conversationList != null){
            for(Message message : conversationList){
                Map<String,Object> map = new HashMap<>();
                map.put("message",message);
                int lettersCount = messageService.selectLettersCount(message.getConversationId());
                map.put("letterCount",lettersCount);
                int unreadLetterCount = messageService.selectUnreadLetters(user.getId(),message.getConversationId());
                map.put("unreadLetter",unreadLetterCount);
                int targetId = message.getFromId() == user.getId() ? message.getToId() : message.getFromId();
                map.put("targetUser",userService.findById(targetId));

                conversations.add(map);
            }
        }
        //查询未读通知的消息
        int messageUnread = messageService.findUnread(user.getId(),null);
        model.addAttribute("messageUnread",messageUnread);
        model.addAttribute("conversations",conversations);
        int letterUnreadCount = messageService.selectUnreadLetters(user.getId(), null);
        model.addAttribute("letterUnreadCount",letterUnreadCount);
        PageInfo pageInfo = new PageInfo(conversationList,3);
        model.addAttribute("pageTotal",pageInfo.getPages());
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navigate",pageInfo.getNavigatepageNums());
        String path = "/letter/list";
        model.addAttribute("path",path);
        return "/site/letter";

    }

    @RequestMapping(value = "/letter/detail/{conversationId}",method = RequestMethod.GET)
    public String getLetterDetail(@PathVariable("conversationId") String conversationId,Model model,@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                  @RequestParam(required = false,defaultValue = "5") Integer pageSize){

        PageHelper.startPage(pageNum,pageSize);
        List<Message> letterList = messageService.selectLetters(conversationId);
        List<Map<String,Object>> letters = new ArrayList<>();
        if(letterList != null){
            for(Message message : letterList){
                Map<String,Object> map = new HashMap<>();
                map.put("letter",message);
                map.put("fromUser",userService.findById(message.getFromId()));
                letters.add(map);
            }

        }
        PageInfo pageInfo = new PageInfo(letterList,3);
        model.addAttribute("pageTotal",pageInfo.getPages());
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navigate",pageInfo.getNavigatepageNums());
        String path = "/letter/detail/"+conversationId;
        model.addAttribute("path",path);
        model.addAttribute("letters",letters);
        model.addAttribute("target",getLetterTarget(conversationId));
        List<Integer> ids = getLetterIds(letterList);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";
    }

    @RequestMapping(value = "/letter/send",method = RequestMethod.POST)
    @ResponseBody
    public String sendLetters(String toName,String content){
        User user = userService.findByName(toName);
        if(user == null){
            return CommunityUtil.getJSONSting(1,"该用户不存在");
        }
        Message message = new Message();
        message.setToId(user.getId());
        message.setContent(content);
        message.setCreateTime(new Date());
        message.setFromId(holder.getUser().getId());
        if(message.getFromId() < message.getToId()){
            message.setConversationId(message.getFromId()+"_"+message.getToId());
        }else {
            message.setConversationId(message.getToId()+"_"+message.getFromId());
        }
        messageService.sendLetters(message);
        return CommunityUtil.getJSONSting(0);
    }

    @RequestMapping(value = "/notice/list",method = RequestMethod.GET)
    public String getNoticeList(Model model){
        User user = holder.getUser();

        //查询评论通知
        Message commMessage = messageService.findLatestMessage(user.getId(), TOPIC_COMMENT);
        if(commMessage != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",commMessage);
            //去掉转义字符
            String content = HtmlUtils.htmlUnescape(commMessage.getContent());
            Map<String,Object> dataMap = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findById((Integer) dataMap.get("userId")));
            messageVo.put("entityType",dataMap.get("entityType"));
            messageVo.put("entityId",dataMap.get("entityId"));
            messageVo.put("postId",dataMap.get("postId"));

            int count = messageService.findCountMessage(user.getId(), TOPIC_COMMENT);
            messageVo.put("count",count);
            int unRead = messageService.findUnread(user.getId(),TOPIC_COMMENT);
            messageVo.put("unRead",unRead);
            model.addAttribute("commentVo",messageVo);
        }

        //查询点赞通知

        commMessage = messageService.findLatestMessage(user.getId(), TOPIC_LIKE);
        if(commMessage != null){
            Map <String,Object> messageVo = new HashMap<>();
            messageVo.put("message",commMessage);
            //去掉转义字符
            String content = HtmlUtils.htmlUnescape(commMessage.getContent());
            Map<String,Object> dataMap = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findById((Integer) dataMap.get("userId")));
            messageVo.put("entityType",dataMap.get("entityType"));
            messageVo.put("entityId",dataMap.get("entityId"));
            messageVo.put("postId",dataMap.get("postId"));
            int count = messageService.findCountMessage(user.getId(), TOPIC_LIKE);
            messageVo.put("count",count);
            int unRead = messageService.findUnread(user.getId(),TOPIC_LIKE);
            messageVo.put("unRead",unRead);
            model.addAttribute("likeVo",messageVo);
        }

        //查询关注通知
        commMessage = messageService.findLatestMessage(user.getId(), TOPIC_FOLLOW);
        if(commMessage != null){
            Map<String,Object> messageVo = new HashMap<>();
            messageVo.put("message",commMessage);
            //去掉转义字符
            String content = HtmlUtils.htmlUnescape(commMessage.getContent());
            Map<String,Object> dataMap = JSONObject.parseObject(content,HashMap.class);
            messageVo.put("user",userService.findById((Integer) dataMap.get("userId")));
            messageVo.put("entityType",dataMap.get("entityType"));
            messageVo.put("entityId",dataMap.get("entityId"));

            int count = messageService.findCountMessage(user.getId(), TOPIC_FOLLOW);
            messageVo.put("count",count);
            int unRead = messageService.findUnread(user.getId(),TOPIC_FOLLOW);
            messageVo.put("unRead",unRead);
            model.addAttribute("followVo",messageVo);
        }

        //查询未读消息数量
        int letterUnread = messageService.selectUnreadLetters(user.getId(), null);
        model.addAttribute("letterUnread",letterUnread);
        //查询未读通知的消息
        int messageUnread = messageService.findUnread(user.getId(),null);
        model.addAttribute("messageUnread",messageUnread);

        return "/site/notice";

    }

    @RequestMapping(value = "/notice/detail/{topic}",method = RequestMethod.GET)
    public String getNoticeDetail(@PathVariable("topic") String topic,Model model,@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                  @RequestParam(required = false,defaultValue = "5") Integer pageSize){
        User user = holder.getUser();
        PageHelper.startPage(pageNum,pageSize);
        List<Message> notices = messageService.findNotices(user.getId(), topic);
        List<Map<String,Object>> noticeVo = new ArrayList<>();
        if(notices != null){
            for(Message notice : notices){
                Map<String ,Object> map = new HashMap<>();
                map.put("notice",notice);
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String,Object> data = JSONObject.parseObject(content);
                map.put("entityType",data.get("entityType"));
                map.put("entityId",data.get("entityId"));
                map.put("postId",data.get("postId"));
                map.put("fromUser",userService.findById(notice.getFromId()));
                map.put("user",userService.findById((Integer) data.get("userId")));
                noticeVo.add(map);
            }
        }
        model.addAttribute("noticeVo",noticeVo);
        PageInfo pageInfo = new PageInfo(notices,3);
        model.addAttribute("pageTotal",pageInfo.getPages());
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navigate",pageInfo.getNavigatepageNums());
        String path = "/notice/detail/"+topic;
        model.addAttribute("path",path);
        List<Integer> ids = getLetterIds(notices);
        if(!ids.isEmpty()){
            messageService.readMessage(ids);
        }
        return "/site/notice-detail";
    }

    private User getLetterTarget(String conversationId){
        String[] ids = conversationId.split("_");
        int d0 = Integer.parseInt(ids[0]);
        int d1 = Integer.parseInt(ids[1]);

        if(holder.getUser().getId() == d0){
            return userService.findById(d1);
        }else {
            return userService.findById(d0);
        }
    }

    private List<Integer> getLetterIds(List<Message> letterList){
        List<Integer> ids = new ArrayList<>();
        if(letterList != null){
            for (Message message : letterList){
                if(holder.getUser().getId() == message.getToId() && message.getStatus() == 0){
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }


}
