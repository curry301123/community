package com.nowcode.commuity.controller;

import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.domain.Page;
import com.nowcode.commuity.service.ElasticSearchService;
import com.nowcode.commuity.service.LikeService;
import com.nowcode.commuity.service.UserService;
import com.nowcode.commuity.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements Constant {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {
        page.setPath("/search?keyword="+keyword);
        page.setLimit(5);

        Map<String,Object> postMap  = elasticSearchService.searchPost(keyword, page.getOffset(), page.getLimit());
        if(postMap != null){
            page.setRows((int)postMap.get("total") == 0?0:(int)postMap.get("total"));
        }
        System.out.println(page.getRows());
        //聚合数据
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        List<DiscussPost> list = (List<DiscussPost>) postMap.get("posts");
        if(postMap.get("posts") != null){
            for(DiscussPost post : list){
                Map<String,Object> map = new HashMap<>();
                map.put("post",post);
                map.put("user",userService.findById(post.getUserId()));
                map.put("likeCount",likeService.likeNums(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussposts",discussPosts);
        model.addAttribute("keyword",keyword);

        return "/site/search";
    }
}
