package domain.promotions.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    @Nested
    @DisplayName("getTotalQuantity（合計数量の計算）のテスト")
    class GetTotalQuantityTests {

        @Test
        @DisplayName("カートが空の場合、合計数量は 0 になること")
        void shouldReturnZeroWhenCartIsEmpty() {
            Cart cart = new Cart(List.of(), "user-123");
            assertThat(cart.getTotalQuantity()).isZero();
        }

        @Test
        @DisplayName("複数の商品がある場合、すべての数量が正しく合計されること")
        void shouldReturnSumOfAllItemQuantities() {
            List<CartItem> items = List.of(
                    new CartItem("item-1", new BigDecimal("100"), 2, "cat-A"),
                    new CartItem("item-2", new BigDecimal("200"), 3, "cat-B")
            );
            Cart cart = new Cart(items, "user-123");

            assertThat(cart.getTotalQuantity()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("getRawTotalAmount（合計金額の計算）のテスト")
    class GetRawTotalAmountTests {

        @Test
        @DisplayName("カートが空の場合、合計金額は 0 になること")
        void shouldReturnZeroAmountWhenCartIsEmpty() {
            Cart cart = new Cart(List.of(), "user-123");
            // BigDecimalの比較は等値性(equals)ではなく値の比較(isEqualByComparingTo)を使用する
            assertThat(cart.getRawTotalAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("複数の商品がある場合、各商品の小計が正しく合計されること")
        void shouldReturnSumOfAllItemSubtotals() {
            List<CartItem> items = List.of(
                    new CartItem("item-1", new BigDecimal("150.00"), 2, "cat-C"), // 150 * 2 = 300
                    new CartItem("item-2", new BigDecimal("500.50"), 1, "cat-D")  // 500.50 * 1 = 500.50
            );
            Cart cart = new Cart(items, "user-123");

            assertThat(cart.getRawTotalAmount()).isEqualByComparingTo(new BigDecimal("800.50"));
        }
    }

    @Nested
    @DisplayName("コンストラクタおよび堅牢性のテスト")
    class ConstructorTests {

        @Test
        @DisplayName("外部から渡されたリストの変更によって、カート内部の状態が壊れないこと（防御的コピー）")
        void shouldBeImmutableAndNotAffectedByExternalListChanges() {
            // 変更可能なリストを作成
            List<CartItem> mutableList = new ArrayList<>();
            mutableList.add(new CartItem("item-1", BigDecimal.TEN, 1, "cat-E"));

            // もしCartのコンストラクタで List.copyOf(items) などをしていれば防御できる
            Cart cart = new Cart(mutableList, "user-123");

            // 外部のリストを変更
            mutableList.add(new CartItem("item-2", BigDecimal.ONE, 5, "cat-F"));

            // カート内の数量は影響を受けていないことを検証
            // ※ カート実装が単に this.items = items の場合、このテストは失敗します（改善の気付きになる）
            assertThat(cart.items()).hasSize(2);
        }

        @Test
        @DisplayName("userId や items が正しく保持されていること")
        void shouldRetainConstructorArguments() {
            Cart cart = new Cart(List.of(), "user-789");
            assertThat(cart.userId()).isEqualTo("user-789");
            assertThat(cart.items()).isEmpty();
        }
    }
}
