package com.itbaizhan.shopping_search_service.repository;

import com.itbaizhan.shopping_common.pojo.GoodsES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GoodsEsRepository extends ElasticsearchRepository<GoodsES,Long> {
}
