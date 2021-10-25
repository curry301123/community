package com.nowcode.commuity.mapper;

import com.nowcode.commuity.domain.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     *
     * @param userId 在后续开发中需要用到userId来查找具体用户发的帖子，查询总数时默认为0
     * @param
     * @param
     * @return
     */
//    public List<DiscussPost> selectDiscussPost(int userId,int offset,int limit);
      public List<DiscussPost> selectDiscussPost(@Param("userId") int userId);

    /**
     *返回总贴数
     * @param userId 这个注解用于给参数起别名，如果只有一个参数，并且这个参数在<if></if>中使用，则必须起别名
     * @return
     */
    public int selectAll(@Param("userId") int userId);

}
