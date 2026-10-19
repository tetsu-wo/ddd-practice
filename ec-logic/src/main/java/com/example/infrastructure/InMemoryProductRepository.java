package com.example.infrastructure;

import com.example.domain.model.products_inventory.Product;
import com.example.domain.model.products_inventory.ProductIdentity;
import com.example.domain.model.products_inventory.ProductRepository; // ドメイン層のインターフェースをインポート
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.domain.model.products_inventory.ProductTestFactory.createDummySkus;

/**
 * ⭕ 画面連携テスト用のインメモリリポジトリ (フェイク実装クラス)
 * ドメイン層の ProductRepository インターフェースを具現化します。
 */
@Repository // Spring Bootに「本物の代わりにこれを使って」と登録するアノテーション
public class InMemoryProductRepository implements ProductRepository {

    // データベースの代わりに、JavaのMap（メモリ空間）にデータを一時保存します
    private final Map<String, Product> database = new ConcurrentHashMap<>();

    /**
     * コンストラクタ: サーバーが起動した瞬間に、画面連携で使うためのダミー商品を1つ仕込みます。
     */
    public InMemoryProductRepository() {
        // ① 商品の識別子（Identity）を作る
        ProductIdentity identity = new ProductIdentity("PROD-001", "テスト用の素晴らしいシャツ", LocalDateTime.now(), LocalDateTime.now().plusYears(1));

        // ② 本来はここにSKUのリストを入れますが、まずは空のリスト、
        // もしくはテスト用の最小限のバリエーションでProductをインスタンス化します
        Product dummyProduct = new Product(identity, createDummySkus(), "公開");

        // ③ メモリ上のダミーデータベースに保存しておく
        database.put("PROD-001", dummyProduct);
    }

    @Override
    public Product findById(String id) {
        // メモリ上にデータがあればそれを返し、なければnullを返します
        return database.get(id);
    }

    @Override
    public void save(Product product) {
        // データベースにSQLを発行する代わりに、Mapに上書き保存して状態をキープします
        String id = product.getProductIdentity().id();
        database.put(id, product);
        System.out.println("【インメモリDBログ】Productが保存されました。ID: " + id + ", ステータス: " + product.getStatus());
    }
}
