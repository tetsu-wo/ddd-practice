package domain.model.products_inventory;

import com.example.domain.model.products_inventory.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("商品エンティティの仕様テスト")
class ProductTest {

    // テストで一貫して使い回す基準の日時（Arrangeの安定化）
    private final LocalDateTime startPeriod = LocalDateTime.of(2026, 10, 15, 10, 0);
    private final LocalDateTime endPeriod = LocalDateTime.of(2026, 10, 15, 11, 0);
    private final LocalDateTime validOrderDate = LocalDateTime.of(2026, 10, 15, 10, 30); // 期間内

    private final Color red = new Color("赤");
    private final Size sizeM = new Size("M");

    @Nested
    @DisplayName("① 初期化（コンストラクタ）のテスト")
    class InitializationTest {

        @Test
        @DisplayName("必要なデータがすべて揃っていれば、正常にインスタンスが生成され値が保持されること")
        void success() {
            // 【Arrange】身代わりメソッドを使って1行で綺麗なテストデータを準備（サボる！）
            ProductIdentity identity = createDefaultIdentity();
            List<SKU> skus = List.of(createDefaultSku());
            String status = "販売中";

            // 【Act】
            Product product = new Product(identity, skus, status);

            // 【Assert】器のチェック（値オブジェクトの時と同じ、ちゃんと入っているか確認）
            assertThat(product.getProductIdentity()).isEqualTo(identity);
            assertThat(product.getStatus()).isEqualTo(status);
            assertThat(product.getSkus()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("② 業務ルールおよび ③ nullチェックのテスト")
    class ValidationTest {

        @Test
        @DisplayName("SKUリストが空（Empty）の場合は例外が発生すること")
        void failure_skus_empty() {
            ProductIdentity identity = createDefaultIdentity();
            List<SKU> emptySkus = Collections.emptyList(); // 👈 空っぽのリスト

            // AssertJの綺麗なチェーンで例外とメッセージを検証
            assertThatThrownBy(() -> new Product(identity, emptySkus, "販売中"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("商品は最低1つのバリエーションが必要です。");
        }

        @Test
        @DisplayName("SKUリストがnullの場合は例外が発生すること")
        void failure_skus_null() {
            ProductIdentity identity = createDefaultIdentity();

            assertThatThrownBy(() -> new Product(identity, null, "販売中"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("商品は最低1つのバリエーションが必要です。");
        }


    @Nested
    @DisplayName("1. getStatus()：ステータスおよび在庫連動の検証（戻り値の確認）")
    class GetStatusTest {

        @Test
        @DisplayName("ステータスが「公開」で在庫が1個以上あれば、そのまま「公開」が返ること")
        void success_public_with_stock() {
            // 【Arrange】在庫が5個あるSKUで商品を準備
            SKU sku = createSku(red, sizeM, 5);
            Product product = createProduct("公開", sku);

            // 【Act & Assert】
            assertThat(product.getStatus()).isEqualTo("公開");
        }

        @Test
        @DisplayName("ステータスが「公開」であっても、全SKUの在庫が0の場合は「売り切れ」に変わること")
        void success_sold_out_when_stock_zero() {
            // 【Arrange】在庫が0個のSKUで商品を準備
            SKU sku = createSku(red, sizeM, 0);
            Product product = createProduct("公開", sku);

            // 【Act & Assert】
            assertThat(product.getStatus()).isEqualTo("売り切れ");
        }

        @Test
        @DisplayName("ステータスが「非公開」であれば、在庫が0であっても「非公開」のまま返ること")
        void success_private_even_if_stock_zero() {
            SKU sku = createSku(red, sizeM, 0);
            Product product = createProduct("非公開", sku);

            assertThat(product.getStatus()).isEqualTo("非公開");
        }
    }

    @Nested
    @DisplayName("2. order()：購入操作とガードの検証（ステータス遷移・業務ルール）")
    class OrderTest {

        @Test
        @DisplayName("【正常系】販売期間内かつ「公開」であれば注文でき、指定したSKUの在庫が減ること")
        void success_order() {
            SKU sku = createSku(red, sizeM, 10);
            Product product = createProduct("公開", sku);

            // 【Act】期間内の日時で2個注文
            product.order(red, sizeM, 2, validOrderDate);

            // 【Assert】器のチェック（対象SKUの在庫が 10 -> 8 に減っていること）
            assertThat(sku.getStockQuantity()).isEqualTo(8);
        }

        @Test
        @DisplayName("【期間外ガード】注文日時が販売開始日時より前の場合はエラーになること")
        void failure_before_sales_period() {
            Product product = createProduct("公開", createSku(red, sizeM, 10));
            LocalDateTime beforeTime = startPeriod.minusMinutes(1); // 09:59 (開始前)

            assertThatThrownBy(() -> product.order(red, sizeM, 1, beforeTime))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("販売期間外のため、購入できません。");
        }

        @Test
        @DisplayName("【期間外ガード】注文日時が販売終了日時より後の場合はエラーになること")
        void failure_after_sales_period() {
            Product product = createProduct("公開", createSku(red, sizeM, 10));
            LocalDateTime afterTime = endPeriod.plusMinutes(1); // 11:01 (終了後)

            assertThatThrownBy(() -> product.order(red, sizeM, 1, afterTime))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("販売期間外のため、購入できません。");
        }

        @Test
        @DisplayName("【非公開ガード】ステータスが「非公開」の場合は購入できないこと")
        void failure_when_private() {
            Product product = createProduct("非公開", createSku(red, sizeM, 10));

            assertThatThrownBy(() -> product.order(red, sizeM, 1, validOrderDate))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("この商品は現在購入できません（非公開または売り切れ）。");
        }

        @Test
        @DisplayName("【売り切れガード】在庫が0（getStatusが売り切れになる状態）の場合は購入できないこと")
        void failure_when_sold_out() {
            Product product = createProduct("公開", createSku(red, sizeM, 0)); // 在庫0

            assertThatThrownBy(() -> product.order(red, sizeM, 1, validOrderDate))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("この商品は現在購入できません（非公開または売り切れ）。");
        }

        @Test
        @DisplayName("【存在チェック】商品に存在しないバリエーションを指定して注文した場合はエラーになること")
        void failure_sku_not_found() {
            Product product = createProduct("公開", createSku(red, sizeM, 10));
            Color blue = new Color("青"); // 商品に登録していない色

            assertThatThrownBy(() -> product.order(blue, sizeM, 1, validOrderDate))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("指定されたバリエーションは存在しません。");
        }
    }

    @Nested
    @DisplayName("3. cancelOrder()：キャンセルの検証（可変データの戻し）")
    class CancelOrderTest {

        @Test
        @DisplayName("【正常系】注文をキャンセルした場合、指定したSKUの在庫が正しく増えること")
        void success_cancel() {
            SKU sku = createSku(red, sizeM, 5);
            Product product = createProduct("公開", sku);

            // 【Act】3個分キャンセル
            product.cancelOrder(red, sizeM, 3);

            // 【Assert】在庫が 5 -> 8 に増えていること
            assertThat(sku.getStockQuantity()).isEqualTo(8);
        }

        @Test
        @DisplayName("【存在チェック】存在しないバリエーションをキャンセルしようとした場合はエラーになること")
        void failure_cancel_sku_not_found() {
            Product product = createProduct("公開", createSku(red, sizeM, 5));
            Size sizeL = new Size("L"); // 存在しないサイズ

            assertThatThrownBy(() -> product.cancelOrder(red, sizeL, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("指定されたバリエーションは存在しません。");
        }
    }



}

// =========================================================================
// ✨ 【Arrange（準備）をサボるための身代わりメソッド達】
// =========================================================================

// 状態を外から指定してProductをサクッと new するメソッド
private Product createProduct(String status, SKU sku) {
    ProductIdentity identity = new ProductIdentity("P001", "テスト商品", startPeriod, endPeriod);
    return new Product(identity, List.of(sku), status);
}

// 在庫数を指定してSKUをサクッと作るメソッド
private SKU createSku(Color color, Size size, int stock) {
    // SKUのコンストラクタ構造（SkuIdentityを中に持つなど）に合わせて内部を微調整してください
    SkuIdentity skuIdentity = new SkuIdentity("SKU001", color, size);
    //    // もしSKUクラスに初期在庫を設定するメソッドやコンストラクタがあれば、ここでstockの値を反映させます
    //    sku.setStockQuantity(stock); // 例としてセッターまたは初期化処理
    return new SKU(skuIdentity, stock);
}




// =========================================================================
// ✨ 【Arrange用の身代わりメソッド】ここに面倒な new 達を完全に閉じ込める！
// =========================================================================
private ProductIdentity createDefaultIdentity() {
    return new ProductIdentity(
            "P001",
            "デフォルト商品名",
            LocalDateTime.of(2026, 10, 15, 10, 0),
            LocalDateTime.of(2026, 10, 15, 11, 0)
    );
}

private SKU createDefaultSku() {
    // ※SKUのコンストラクタに合わせて適宜ダミー値を調整してください
    return new SKU(new SkuIdentity("SKU001", new Color("赤"), new Size("M")), 1);
}
}
