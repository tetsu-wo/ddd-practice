package com.example.domain.model.products_inventory;

/**
 * 商品集約のためのリポジトリインターフェース
 * (ドメイン層に配置し、外部の技術からドメインを守る境界線になります)
 */
public interface ProductRepository {

    // IDを元にProduct集約を引っ張るための契約
    Product findById(String id);

    // 状態の変わったProductを保存するための契約
    void save(Product product);
}
