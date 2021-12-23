package com.nowcode.commuity.mapper.elasticSearch;

import com.nowcode.commuity.domain.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * DiscussPost:接口要处理的实体类
 * Integer:实体中的主键是什么类型
 * ElasticsearchRepository：父接口，其中已经事先定义好对es服务器的增删改查的各种方法
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost,Integer> {

}
