package com.nowcode.commuity.mapper;

import com.nowcode.commuity.domain.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType,int entityId);

    int selectCountByEntity(int entityType,int entityId);

    int addComment(Comment comment);
}
