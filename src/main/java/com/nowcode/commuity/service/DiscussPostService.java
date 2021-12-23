package com.nowcode.commuity.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.nowcode.commuity.domain.DiscussPost;
import com.nowcode.commuity.mapper.DiscussPostMapper;
import com.nowcode.commuity.util.SensitiveFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class DiscussPostService {
    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Value("${caffeine.posts.maxsize}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    @Autowired
    private DiscussPostMapper discussMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

  /*  public List<DiscussPost> findDiscussPost(int userId,int offset,int limit){
        return discussMapper.selectDiscussPost(userId,offset,limit);
    }*/

    //Caffeine核心接口：Cache, LoadingCache,AsyncLoadingCache

    //帖子列表缓存
    private LoadingCache<String,List<DiscussPost>> postListCache;

    //帖子总数缓存
    private LoadingCache<Integer,Integer> postRowsCache;

    @PostConstruct
    public void init(){
        //初始化帖子列表缓存
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Override
                    public @Nullable List<DiscussPost> load(@NonNull String key) throws Exception {
                        //当缓存中没有时要告诉缓存如何查找数据
                        if(key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] splits = key.split(":");
                        if(splits == null || splits.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        int offset = Integer.valueOf(splits[0]);
                        int limit = Integer.valueOf(splits[1]);

                        //二级缓存：Redis->mysql
                        logger.debug("load post list from DB.");
                        return discussMapper.selectDiscussPost(0,1);
                    }
                });

        //初始化帖子总数列表

        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds,TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Override
                    public @Nullable Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussMapper.selectAll(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPost(int userId,int offset,int limit,int orderMode){
        if(userId == 0 && orderMode==1){
            return postListCache.get(offset+":"+limit);
        }
        logger.debug("load post list from DB.");
        return discussMapper.selectDiscussPost(userId,orderMode);
    }

    public int findAllRows(int userId){
        if(userId == 0){
            return postRowsCache.get(userId);
        }
        logger.debug("load post rows from DB.");
        return discussMapper.selectAll(userId);
    }

    public int addDiscussPost(DiscussPost post){
        if (post == null){
            throw new IllegalArgumentException("参数不能为空");
        }

        //转义html标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussMapper.insertDiscussPost(post);

    }

    public DiscussPost selectPostDetail(int id){
        return discussMapper.selectPostDetail(id);
    }

    public int updateCommentCount(int id,int commentCount){
        return discussMapper.updateCommentCount(id,commentCount);
    }

    public int updateType(int id,int type){
        return discussMapper.updateType(id,type);
    }

    public int updateStatus(int id,int status){
        return discussMapper.updateStatus(id,status);
    }

    public int updateScore(int id,double score){
        return discussMapper.updateScore(id,score);
    }



}
