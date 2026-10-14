package domain.order_payment.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import domain.cart_price.model.Cart;
import domain.cart_price.model.Price;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Cartエンティティのテスト")
class CartTest {

    private final LocalDateTime baseTime = LocalDateTime.of(2026, 10, 14, 10, 0, 0);

    // Price自体は単体テストで保証済みのため、ここでは単純な固定値として扱う
    private final Price price100 = new Price(100, Price.TaxRate.STANDARD); // 税込110円
    private final Price price200 = new Price(200, Price.TaxRate.STANDARD); // 税込220円

    // ──────────────────────────────────────────────────────────
    // ステップ2: 【お決まりケース】（初期状態の検証）
    // ──────────────────────────────────────────────────────────
    @Test
    @DisplayName("【初期値】カート作成直後は、有効期限切れになっていないこと")
    void shouldNotBeExpiredJustAfterCreation() {
        Cart cart = new Cart("CART-01", baseTime);
        assertFalse(cart.isExpired(baseTime));
    }

    // ──────────────────────────────────────────────────────────
    // ステップ3: 【個別ケース】有効期限のテスト（通常・ピッタリ・やりすぎ）
    // ──────────────────────────────────────────────────────────
    @Nested
    @DisplayName("カート有効期限（30分）の境界値テスト")
    class ExpiryValidation {

        private Cart cart;

        @BeforeEach
        void setUp() {
            cart = new Cart("CART-01", baseTime);
        }

        @Test
        @DisplayName("【通常系】29分59秒後の確認 ➔ まだ有効期限内であること")
        void shouldBeValidBefore30Minutes() {
            LocalDateTime checkTime = baseTime.plusMinutes(30).minusSeconds(1);
            assertFalse(cart.isExpired(checkTime));
        }

        @Test
        @DisplayName("【境界値：ピッタリ】ちょうど30分後の確認 ➔ まだ有効期限内であること")
        void shouldBeValidAtExact30Minutes() {
            LocalDateTime checkTime = baseTime.plusMinutes(30);
            assertFalse(cart.isExpired(checkTime));
        }

        @Test
        @DisplayName("【境界値：やりすぎ】30分1秒後の確認 ➔ 有効期限切れになること")
        void shouldBeExpiredAfter30MinutesAndOneSecond() {
            LocalDateTime checkTime = baseTime.plusMinutes(30).plusSeconds(1);
            assertTrue(cart.isExpired(checkTime));
        }

        @Test
        @DisplayName("【エラー防御】有効期限が切れた後に商品を追加しようとすると、IllegalStateExceptionが発生すること")
        void shouldThrowExceptionWhenAddingProductToExpiredCart() {
            LocalDateTime expiredTime = baseTime.plusMinutes(30).plusSeconds(1);

            assertThrows(IllegalStateException.class, () ->
                    cart.addProduct("SKU-01", price100, 1, expiredTime)
            );
        }

        @Test
        @DisplayName("【状態変化】期限内に商品を追加した場合、その時刻からさらに30分延長されること")
        void shouldExtendExpiryWhenProductIsAddedWithinLimit() {
            LocalDateTime firstActionTime = baseTime.plusMinutes(15); // 10:15:00に追加
            cart.addProduct("SKU-01", price100, 1, firstActionTime);

            LocalDateTime checkTime = baseTime.plusMinutes(45); // 10:45:00
            assertFalse(cart.isExpired(checkTime), "期限が延長されているためセーフ");
        }
    }

    // ──────────────────────────────────────────────────────────
    // ステップ3: 【個別ケース】料金計算のテスト（カート固有の責任：掛け算と足し算の集計）
    // ──────────────────────────────────────────────────────────
    @Nested
    @DisplayName("カート内商品の合計金額計算テスト")
    class TotalAmountCalculation {

        @Test
        @DisplayName("【通常系】複数種類の商品がそれぞれ複数数量あっても、合計金額が正しく集計されること")
        void shouldCalculateTotalAmountWithTaxCorrectly() {
            // Given
            Cart cart = new Cart("CART-01", baseTime);

            // 当該テストの関心事は「(単価×数量)の総和」が正しくループ・集計されるか
            // 商品A: 税込110円 × 2個 = 220円
            cart.addProduct("SKU-A", price100, 2, baseTime);
            // 商品B: 税込220円 × 3個 = 660円
            cart.addProduct("SKU-B", price200, 3, baseTime);

            // When: 220 + 660 = 880円
            int totalAmount = cart.calculateTotalAmountWithTax();

            // Then
            assertEquals(880, totalAmount, "カート内商品の合計（税込）の集計結果が正しいこと");
        }
    }
}
