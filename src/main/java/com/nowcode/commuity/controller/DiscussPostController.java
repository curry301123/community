package com.nowcode.commuity.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcode.commuity.domain.Comment;
import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.service.CommentService;
import com.nowcode.commuity.service.DiscussPostService;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.CommunityUtil;
import com.nowcode.commuity.util.Constant;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Component
@RequestMapping("/discuss")
public class DiscussPostController implements Constant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content){
        User user = holder.getUser();
        if(user == null) {
            return CommunityUtil.getJSONSting(403, "你还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);
        //报错的情况统一处理
        return CommunityUtil.getJSONSting(0,"发布成功");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getPostDetail(@PathVariable("discussPostId") int id, Model model,@RequestParam(required = false,defaultValue = "1") Integer pageNum,
                                @RequestParam(required = false,defaultValue = "5") Integer pageSize){

        DiscussPost post = discussPostService.selectPostDetail(id);
        model.addAttribute("post",post);

        User user = userService.findById(post.getUserId());
        model.addAttribute("user",user);

        //评论列表
        PageHelper.startPage(pageNum,pageSize);
        List<Comment> commentList = commentService.selectComment(ENTITY_TYPE_POST, post.getId());
        //评论VoList
        List<Map<String,Object>> commenVolist = new ArrayList<>();
        if(commentList != null){
            for (Comment comment : commentList){
                //评论Vo
                Map<String,Object> commentMap = new HashMap<>();
                commentMap.put("comment",comment);
                //评论作者
                User CommentUser = userService.findById(comment.getUserId());
                commentMap.put("CommentUser",CommentUser);

                //回复列表
                List<Comment> replyList = commentService.selectComment(ENTITY_TYPE_COMMENT,comment.getId());
                //回复VoList
                List<Map<String,Object>> replyVolist = new ArrayList<>();
                if(replyList != null){
                    for(Comment reply : replyList){
                        //回复Vo
                        Map<String,Object> replyMap = new HashMap<>();
                        replyMap.put("reply",reply);
                        //回复作者
                        User replyUser = userService.findById(reply.getUserId());
                        replyMap.put("replyUser",replyUser);
                        //targetId
                        User target = reply.getTargetId() == 0 ? null : userService.findById(reply.getTargetId());
                        replyMap.put("targetUser",target);
                        replyVolist.add(replyMap);
                    }
                }
                commentMap.put("replyVolist",replyVolist);
                int replyCount = commentService.selectCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentMap.put("replyCount",replyCount);
                commenVolist.add(commentMap);
            }
        }

        PageInfo pageInfo = new PageInfo(commentList,3);
        //model.addAttribute("commentList",commenVolist);
        model.addAttribute("commentVoList",commenVolist);
        model.addAttribute("pageTotal",pageInfo.getPages());
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navigate",pageInfo.getNavigatepageNums());
        model.addAttribute("startRow",pageInfo.getStartRow());
        String path = "/discuss/detail/"+id;
        model.addAttribute("path",path);
        return "/site/discuss-detail";

    }
}
