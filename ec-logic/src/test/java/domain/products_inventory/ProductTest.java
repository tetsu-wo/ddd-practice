package domain.products_inventory;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Productドメインエンティティのテスト")
class ProductTest {

    private final Color red = new Color("赤");
    private final Size sizeM = new Size("M");
    private final Size sizeL = new Size("L");

    private LocalDateTime baseTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUpPeriods() {
        baseTime = LocalDateTime.of(2026, 10, 14, 10, 0);
        startTime = baseTime.minusDays(1); // 販売開始：昨日
        endTime = baseTime.plusDays(1);     // 販売終了：明日
    }

    @Nested
    @DisplayName("コンストラクタによる初期化とバリデーション")
    class Initialization {

        @Test
        @DisplayName("【異常系】SKUのリストが空の場合、IllegalArgumentExceptionが発生すること")
        void shouldThrowExceptionWhenSkusIsEmpty() {
            assertThatThrownBy(() -> new Product("P001", "Tシャツ", List.of(), "公開", startTime, endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("商品は最低1つのバリエーションが必要です。");
        }
    }

    @Nested
    @DisplayName("動的ステータス（getStatus）の判定テスト")
    class StatusResolution {

        @Test
        @DisplayName("【正常系】ステータスが公開であっても、全SKUの合計在庫が0なら「売り切れ」を返すこと")
        void shouldReturnSoldOutWhenTotalStockIsZero() {
            // Arrange: 在庫0のSKUを定義
            SKU skuM = new SKU("S001", red, sizeM, 0);
            SKU skuL = new SKU("S002", red, sizeL, 0);
            Product product = new Product("P001", "Tシャツ", List.of(skuM, skuL), "公開", startTime, endTime);

            // Act & Assert
            assertThat(product.getStatus()).isEqualTo("売り切れ");
        }

        @Test
        @DisplayName("【正常系】1つでも在庫があるSKUが存在すれば、元のステータス「公開」を返すこと")
        void shouldReturnPublishedWhenAnySkuHasStock() {
            SKU skuM = new SKU("S001", red, sizeM, 0);
            SKU skuL = new SKU("S002", red, sizeL, 1); // 1つだけ在庫あり
            Product product = new Product("P001", "Tシャツ", List.of(skuM, skuL), "公開", startTime, endTime);

            assertThat(product.getStatus()).isEqualTo("公開");
        }
    }

    @Nested
    @DisplayName("販売期間（validateSalesPeriod）の境界値テスト")
    class SalesPeriodValidation {

        private Product product;

        @BeforeEach
        void setUp() {
            SKU sku = new SKU("S001", red, sizeM, 10);
            product = new Product("P001", "Tシャツ", List.of(sku), "公開", startTime, endTime);
        }

        @Test
        @DisplayName("【正常系】販売期間内の注文であれば、例外が発生しないこと")
        void shouldAllowOrderWithinSalesPeriod() {
            // 販売期間のちょうど真ん中の日時で注文
            assertThatNoException().isThrownBy(() ->
                    product.order(red, sizeM, 1, baseTime)
            );
        }

        @Test
        @DisplayName("【異常系】販売開始日時より前の注文は、IllegalStateExceptionが発生すること")
        void shouldThrowExceptionWhenOrderIsBeforeSalesStart() {
            LocalDateTime earlyTime = startTime.minusSeconds(1); // 開始1秒前

            assertThatThrownBy(() -> product.order(red, sizeM, 1, earlyTime))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("販売期間外のため、購入できません。");
        }

        @Test
        @DisplayName("【異常系】販売終了日時より後の注文は、IllegalStateExceptionが発生すること")
        void shouldThrowExceptionWhenOrderIsAfterSalesEnd() {
            LocalDateTime lateTime = endTime.plusSeconds(1); // 終了1秒後

            assertThatThrownBy(() -> product.order(red, sizeM, 1, lateTime))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("販売期間外のため、購入できません。");
        }
    }

    @Nested
    @DisplayName("注文処理（order）とキャンセル（cancelOrder）のテスト")
    class OrderOperations {

        private Product product;
        private SKU skuM;

        @BeforeEach
        void setUp() {
            skuM = new SKU("S001", red, sizeM, 10);
            product = new Product("P001", "Tシャツ", List.of(skuM), "公開", startTime, endTime);
        }

        @Test
        @DisplayName("【正常系】正しい条件で注文すると、対象SKUの在庫が減算されること")
        void shouldDecreaseTargetSkuStockOnOrder() {
            // Act: 3点注文
            product.order(red, sizeM, 3, baseTime);

            // Assert: SKUの在庫が 10 -> 7 に減っていること
            assertThat(skuM.getStockQuantity()).isEqualTo(7);
        }

        @Test
        @DisplayName("【異常系】存在しないバリエーションを指定した場合、IllegalArgumentExceptionが発生すること")
        void shouldThrowExceptionWhenSkuNotFound() {
            Size sizeXL = new Size("L"); // この商品にはMサイズしか登録していない想定

            assertThatThrownBy(() -> product.order(red, sizeXL, 1, baseTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("指定されたバリエーションは存在しません。");
        }

        @Test
        @DisplayName("【正常系】注文をキャンセルすると、対象SKUの在庫が加算（復元）されること")
        void shouldIncreaseTargetSkuStockOnCancel() {
            // Act: 2点キャンセルを反映
            product.cancelOrder(red, sizeM, 2);

            // Assert: SKUの在庫が 10 -> 12 に増えていること
            assertThat(skuM.getStockQuantity()).isEqualTo(12);
        }
    }
}
