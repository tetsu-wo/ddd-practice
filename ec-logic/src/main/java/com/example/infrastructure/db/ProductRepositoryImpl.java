package com.example.infrastructure.db;

import com.example.domain.model.products_inventory.Product;
import com.example.domain.model.products_inventory.ProductRepository;
import com.example.infrastructure.db.jpa.entity.ProductJpaEntity;
import com.example.infrastructure.db.jpa.repository.ProductJpaRepository;
import com.example.infrastructure.db.mybatis.mapper.ProductMyBatisMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.infrastructure.db.jpa.mapper.ProductJpaMapper;

@Repository // インフラ層のDI用マップ（Springコンテナ）に登録する
public class ProductRepositoryImpl implements ProductRepository {

    // DIマップから自動生成されたプロキシ（JPA/MyBatis）と、詰め替え職人（マッパー）を受け取る
    private final ProductJpaRepository jpaRepository;
    private final ProductMyBatisMapper myBatisMapper;
    private final ProductJpaMapper jpaMapper;

    public ProductRepositoryImpl(
            ProductJpaRepository jpaRepository,
            ProductMyBatisMapper myBatisMapper,
            ProductJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.myBatisMapper = myBatisMapper;
        this.jpaMapper = jpaMapper;
    }

    // ──────────────────────────────────────────────────────────
    // 【保存：Command（変更）側】
    // ──────────────────────────────────────────────────────────
    @Override
    @Transactional // 2段構えのSQL発行がすべて成功するか、まとめてロールバックするかを保証する
    public void save(Product product) {
        // 1. 【マッパーの仕事】 ドメインモデルをJPAエンティティに安全に変換
        ProductJpaEntity jpaEntity = jpaMapper.toEntity(product);

        // 2. 【JPAプロキシの仕事】 データベースに標準データを保存（INSERT / UPDATE）
        jpaRepository.save(jpaEntity);

        // 3. 【MyBatisプロキシの仕事】 2段構えの連動。JPAが苦手な大量一括更新（バルク処理）などを実行
        myBatisMapper.insertSkuBatch(jpaEntity.getSkus());
    }

    // ──────────────────────────────────────────────────────────
    // 【取得：Command側の参照（復元）】
    // ──────────────────────────────────────────────────────────
    @Override
    public Product findById(String id) {
        // 1. 【JPAプロキシの仕事】 DBから生の入れ物（エンティティ）を取得
        ProductJpaEntity jpaEntity = jpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));

        // 2. 【マッパーの仕事】
        // 取得した生のエンティティをマッパーに渡す。
        // ★この「jpaMapper.toDomain」の内部でだけ、Product.reconstruct が安全に呼び出される！
        return jpaMapper.toDomain(jpaEntity);
    }
}
