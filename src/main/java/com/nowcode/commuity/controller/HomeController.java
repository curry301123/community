package com.nowcode.commuity.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.domain.Page;
import com.nowcode.commuity.domain.User;
import com.nowcode.commuity.mapper.DiscussPostMapper;
import com.nowcode.commuity.service.DiscussPostService;
import com.nowcode.commuity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    /*@RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model,Page page){
        //方法调用之前，SpringMvc会自动实例化Model和Page,并将Page注入Model
        //所以在thymeleaf中可以直接访问Page对象中的数据
        page.setRows(discussPostService.findAllRows(0));
        page.setPath("/index");
        List<DiscussPost> list = discussPostService.findDiscussPost(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post :list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }*/

    @RequestMapping(path = "/index",method = RequestMethod.GET)
    public String getIndexPage(Model model, @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                               @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<DiscussPost> list = discussPostService.findDiscussPost(0);
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for(DiscussPost post :list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                User user = userService.findById(post.getUserId());
                map.put("user",user);
                discussPosts.add(map);
            }
        }
        PageInfo pageInfo = new PageInfo(list,5);
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("pageTotal",pageInfo.getPages());
        model.addAttribute("pageNum",pageNum);
        model.addAttribute("navigate",pageInfo.getNavigatepageNums());

        return "/index";
    }
}
