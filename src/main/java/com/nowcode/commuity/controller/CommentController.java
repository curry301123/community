package com.nowcode.commuity.controller;


import com.nowcode.commuity.domain.Comment;
import com.nowcode.commuity.service.CommentService;
import com.nowcode.commuity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder holder;

    @RequestMapping(value = "/add/{postId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("postId") int postId, Comment comment){
        comment.setUserId(holder.getUser().getId());
        comment.setCreateTime(new Date());
        comment.setStatus(0);
        commentService.addComment(comment);

        return "redirect:/discuss/detail/"+postId;


    }
}
