// infrastructure/db/mybatis/mapper/ProductQueryMapper.java
package com.example.infrastructure.db.mybatis.mapper;

import com.example.infrastructure.db.jpa.entity.SkuJpaEntity;
import com.example.presentation.dto.ProductDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMyBatisMapper {
    ProductDetailResponse findProductDetailById(@Param("productId") String productId);

    void insertSkuBatch(@Param("skus") List<SkuJpaEntity> skus);

    int selectTest();
}

