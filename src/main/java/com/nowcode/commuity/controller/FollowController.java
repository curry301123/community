package com.nowcode.commuity.controller;

import com.nowcode.commuity.domain.Event;
import com.nowcode.commuity.domain.Page;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.event.EventProducer;
import com.nowcode.commuity.service.FolloweService;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements Constant {

    @Autowired
    private FolloweService followeService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    @RequestMapping(value = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityId,int entityType){
        User user = holder.getUser();

        followeService.follow(entityId,entityType, user.getId());

        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setUserId(user.getId())
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONSting(0,"已关注");
    }

    @RequestMapping(value = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityId,int entityType){
        User user = holder.getUser();

        followeService.unfollow(entityId,entityType, user.getId());
        return CommunityUtil.getJSONSting(0,"已取消关注");
    }

    @RequestMapping(value = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followees/"+userId);
        page.setRows((int) followeService.followeeNum(userId,ENTITY_TYPE_USER));
        List<Map<String, Object>> userList = followeService.findFollowee(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map map : userList){
                User followeeUser = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(followeeUser.getId()));
            }
        }
        model.addAttribute("users",userList);
        return "/site/followee";
    }

    @RequestMapping(value = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model){
        User user = userService.findById(userId);
        if(user == null){
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user",user);
        page.setLimit(5);
        page.setPath("/followers/"+userId);
        page.setRows((int) followeService.followerNum(ENTITY_TYPE_USER,userId));
        List<Map<String, Object>> userList = followeService.findFollower(userId, page.getOffset(), page.getLimit());
        if(userList != null){
            for(Map map : userList){
                User followerUser = (User) map.get("user");
                map.put("hasFollowed",hasFollowed(followerUser.getId()));
            }
        }
        model.addAttribute("users",userList);
        //follower.html出现未至错误，目前无法解决，所以复制followee.html为follower2进行代替
        return "/site/follower2";
    }

    private boolean hasFollowed(int userId){
        if(holder.getUser() == null){
            return false;
        }
        return followeService.followStatus(holder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
