package domain.promotions.model;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;

import domain.promotions.model.Cart;
import domain.promotions.repository.DiscountStrategy;
import domain.promotions.repository.ShippingCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PromotionEngine: ビジネスロジックの結合テスト（モックなし）")
class PromotionEngineTest {

    // テスト用のシンプルな送料計算スタブ（5000円以上は送料無料、未満は500円）
    private final ShippingCalculator stubShippingCalculator = finalProductAmount ->
            finalProductAmount.compareTo(new BigDecimal("5000")) >= 0
                    ? BigDecimal.ZERO
                    : new BigDecimal("500");

    // テスト用のダミーカートを生成するヘルパーメソッド
    private Cart createDummyCart(String rawTotal) {
        return new Cart() {
            @Override
            public BigDecimal getRawTotalAmount() {
                return new BigDecimal(rawTotal);
            }
        };
    }

    // テスト用の割引戦略を生成するヘルパーメソッド
    private DiscountStrategy createStubStrategy(String discountAmount) {
        return cart -> new BigDecimal(discountAmount);
    }

    @Test
    @DisplayName("ケース2: 複数のまとめ買い割引が累積して正しく引かれること")
    void shouldAccumulateMultipleBundleDiscounts() {
        // Given
        PromotionEngine engine = new PromotionEngine(stubShippingCalculator);
        Cart cart = createDummyCart("10000"); // 元値: 10,000円
        List<DiscountStrategy> strategies = List.of(
                createStubStrategy("1000"), // 割引1: 1,000円
                createStubStrategy("2000")  // 割引2: 2,000円
        );
        BigDecimal couponDiscount = BigDecimal.ZERO;

        // When
        OrderSummary summary = engine.calculate(cart, strategies, couponDiscount);

        // Then
        // 10,000 - 3,000 = 7,000円（5,000円以上なので送料無料ライン、しかし今回のスタブ仕様は5000円以上が無料なので、7000円は0円）
        assertEquals(new BigDecimal("10000"), summary.rawTotal());
        assertEquals(new BigDecimal("3000"), summary.bundleDiscount());
        assertEquals(new BigDecimal("7000"), summary.productFinalAmount());
        assertEquals(BigDecimal.ZERO, summary.shippingFee());
        assertEquals(new BigDecimal("7000"), summary.finalOrderAmount());
    }

    @Test
    @DisplayName("ケース4: 割引の合計が元の金額を超えた場合、商品金額は0円に丸められ、送料のみが請求されること")
    void shouldRoundToZeroWhenDiscountExceedsRawTotal() {
        // Given
        PromotionEngine engine = new PromotionEngine(stubShippingCalculator);
        Cart cart = createDummyCart("5000"); // 元値: 5,000円
        List<DiscountStrategy> strategies = List.of(createStubStrategy("2000")); // 割引: 2,000円
        BigDecimal couponDiscount = new BigDecimal("4000"); // クーポン: 4,000円 (合計6,000円引き)

        // When
        OrderSummary summary = engine.calculate(cart, strategies, couponDiscount);

        // Then
        // 5,000 - 2,000 - 4,000 = -1,000円 ➔ 0円に丸められる
        // 商品最終が0円（5,000円未満）になるため、送料500円が発生する
        assertEquals(new BigDecimal("0"), summary.getFinalProductAmount(), "マイナス値は0に丸められるべきです");
        assertEquals(new BigDecimal("500"), summary.getShippingFee(), "0円ベースで送料が計算されるべきです");
        assertEquals(new BigDecimal("500"), summary.getFinalOrderAmount(), "最終請求は送料の500円のみになるべきです");
    }

    @Test
    @DisplayName("ケース5: 割引後の最終請求金額ベースで送料無料ラインが判定されること")
    void shouldCalculateShippingBasedOnFinalProductAmount() {
        // Given
        PromotionEngine engine = new PromotionEngine(stubShippingCalculator);
        Cart cart = createDummyCart("10000");
        List<DiscountStrategy> strategies = List.of(createStubStrategy("6000")); // 10,000 - 6,000 = 4,000円
        BigDecimal couponDiscount = BigDecimal.ZERO;

        // When
        OrderSummary summary = engine.calculate(cart, strategies, couponDiscount);

        // Then
        // 最終金額が4,000円（5,000円未満）になるため、送料無料ラインを下回り、送料500円が加算される
        assertEquals(new BigDecimal("4000"), summary.getFinalProductAmount());
        assertEquals(new BigDecimal("500"), summary.getShippingFee(), "元値ではなく割引後金額で送料判定されるべきです");
        assertEquals(new BigDecimal("4500"), summary.getFinalOrderAmount());
    }
}
