package domain.products_inventory;

import static org.assertj.core.api.Assertions.*;

import domain.products_inventory.SKU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SKUドメインエンティティのテスト")
class SKUTest {

    // テスト全体で使い回す不変の属性オブジェクト
    private final Color colorRed = new Color("赤");
    private final Size sizeM = new Size("M");

    @Nested
    @DisplayName("コンストラクタによる初期化のテスト")
    class Initialization {

        @ParameterizedTest
        @ValueSource(ints = {0, 100})
        @DisplayName("【正常系】在庫数が0以上の境界値・通常値であれば、正しくインスタンスが生成されること")
        void shouldCreateSkuWithValidStockQuantity(int validStock) {
            // Act
            SKU sku = new SKU("SKU-001", colorRed, sizeM, validStock);

            // Assert
            assertThat(sku.getColor()).isEqualTo(colorRed);
            assertThat(sku.getSize()).isEqualTo(sizeM);
            assertThat(sku.getStockQuantity()).isEqualTo(validStock);
        }

        @Test
        @DisplayName("【異常系】在庫数に負の数を指定した場合、IllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionWhenStockQuantityIsNegative() {
            // Act & Assert
            assertThatThrownBy(() -> new SKU("SKU-001", colorRed, sizeM, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("在庫数は0以上である必要があります。");
        }
    }

    @Nested
    @DisplayName("在庫引き当て（allocateStock）のテスト")
    class AllocateStock {
        private SKU sku;

        @BeforeEach
        void setUp() {
            // テストごとに毎回「在庫10」のクリーンな状態からスタートする
            sku = new SKU("SKU-001", colorRed, sizeM, 10);
        }

        @Test
        @DisplayName("【正常系】在庫数以下の数量を引き当てた場合、在庫数が正しく減算されること")
        void shouldDecreaseStockWhenAllocatingValidQuantity() {
            // Act
            sku.allocateStock(3);

            // Assert
            assertThat(sku.getStockQuantity()).isEqualTo(7);
        }

        @Test
        @DisplayName("【境界値】在庫数ぴったりの数量を引き当てた場合、在庫数が0になること")
        void shouldAllowAllocatingExactAvailableStock() {
            // Act
            sku.allocateStock(10);

            // Assert
            assertThat(sku.getStockQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("【異常系】在庫数を超える数量を引き当てた場合、IllegalStateExceptionが発生し、在庫数が変更されないこと")
        void shouldThrowExceptionAndKeepOriginalStockWhenAllocatingOverAvailable() {
            // Act & Assert: 例外の発生とエラーメッセージを検証
            assertThatThrownBy(() -> sku.allocateStock(11))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("在庫不足です。現在の残り: 10");

            // 重要: 例外が発生した後も、内部状態（在庫10）が破壊されずに維持されていることを担保する
            assertThat(sku.getStockQuantity()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("在庫返却（releaseStock）のテスト")
    class ReleaseStock {

        @Test
        @DisplayName("【正常系】在庫を返却した場合、在庫数が正しく加算されること")
        void shouldIncreaseStockQuantityWhenReleasing() {
            // Arrange
            SKU sku = new SKU("SKU-001", colorRed, sizeM, 10);

            // Act
            sku.releaseStock(5);

            // Assert
            assertThat(sku.getStockQuantity()).isEqualTo(15);
        }
    }
}
