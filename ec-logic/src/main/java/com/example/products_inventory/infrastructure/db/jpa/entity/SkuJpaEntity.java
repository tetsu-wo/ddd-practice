package com.example.products_inventory.infrastructure.db.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "skus") // 実際のテーブル名に合わせてください
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SkuJpaEntity {

    @Id
    private String id; // SkuIdentityのidをマッピング

    // 💡 参照型の Color と Size も、DBに保存できるプリミティブ型（String等）に分解して定義
    private String color;

    private String size;

    private int stockQuantity;

}
