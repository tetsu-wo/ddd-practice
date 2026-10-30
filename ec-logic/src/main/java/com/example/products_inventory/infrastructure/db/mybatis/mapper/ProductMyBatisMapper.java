// infrastructure/db/mybatis/mapper/ProductQueryMapper.java
package com.example.products_inventory.infrastructure.db.mybatis.mapper;

import com.example.products_inventory.infrastructure.db.jpa.entity.SkuJpaEntity;
import com.example.products_inventory.presentation.dto.ProductDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMyBatisMapper {
    ProductDetailResponse findProductDetailById(@Param("productId") String productId);

    void insertSkuBatch(@Param("skus") List<SkuJpaEntity> skus);

    int selectTest();
}

