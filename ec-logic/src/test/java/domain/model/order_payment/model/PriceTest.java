package domain.model.order_payment.model;

import static org.junit.jupiter.api.Assertions.*;

import com.example.domain.model.cart_price.Price;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Price値オブジェクトのテスト")
class PriceTest {

    // ──────────────────────────────────────────────────────────
    // ステップ2: 【お決まりケース】（コンストラクタによる防御）
    // ──────────────────────────────────────────────────────────
    @Nested
    @DisplayName("コンストラクタの初期化バリデーションのテスト")
    class Initialization {

        @ParameterizedTest
        @ValueSource(ints = {-1, -100, -99999})
        @DisplayName("【エラーケース】負の金額を指定した場合、一律でIllegalArgumentExceptionを投げること")
        void shouldThrowExceptionWhenPriceIsNegative(int negativeValue) {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    new Price(negativeValue, Price.TaxRate.STANDARD)
            );
            assertEquals("金額は0以上である必要があります。", exception.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────
    // ステップ3: 【個別ケース】（ビジネスルール：税金と税込合計の計算）
    // ──────────────────────────────────────────────────────────
    @Nested
    @DisplayName("消費税および税込合計金額の計算テスト")
    class TaxAndTotalCalculation {

        @ParameterizedTest
        @CsvSource({
                // 金額,  税率,      期待される税額, 期待される税込総額
                "100,  STANDARD, 10,            110", // 10%の基本
                "10,   STANDARD, 1,             11",  // 最小税額の境界
                "0,    STANDARD, 0,             0",   // 下限の境界値
                "100,  REDUCED,  8,             108", // 8%の基本
                "105,  REDUCED,  8,             113", // 端数 8.4円 -> 切り捨てで8円（境界値）
                "110,  REDUCED,  8,             118"  // 端数 8.8円 -> 切り捨てで8円（四捨五入の防止確認）
        })
        @DisplayName("【通常・境界値】指定した税率に応じて、端数切り捨てで正確に税金と総額が計算されること")
        void shouldCalculateTaxAndTotalCorrectly(
                int value,
                Price.TaxRate taxRate,
                int expectedTax,
                int expectedTotal
        ) {
            // Given: 金額オブジェクトを生成
            Price price = new Price(value, taxRate);

            // When & Then: 消費税額の検証
            assertEquals(expectedTax, price.calculateTax(),
                    String.format("金額[%d], 税率[%s] の時の税額が違います", value, taxRate));

            // When & Then: 税込合計金額の検証
            assertEquals(expectedTotal, price.totalWithTax(),
                    String.format("金額[%d], 税率[%s] の時の税込合計が違います", value, taxRate));
        }
    }
}
