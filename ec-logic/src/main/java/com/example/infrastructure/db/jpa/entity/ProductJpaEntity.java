package com.example.infrastructure.db.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

// infrastructure/db/jpa/entity/ProductJpaEntity.java
@Entity
@Table(name = "products")
@Getter
@Setter
public class ProductJpaEntity {
    @Id
    private String id;
    private String name;
    private LocalDateTime salesStartPeriod;
    private LocalDateTime salesEndPeriod;
    private String status;

    @Version // 同時実行時の在庫データ不整合（ロストアップデート）を防ぐ
    private Long version;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private List<SkuJpaEntity> skus;

    public ProductJpaEntity(){

    }
}
