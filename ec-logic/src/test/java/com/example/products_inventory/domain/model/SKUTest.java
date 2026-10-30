package com.example.products_inventory.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SKUエンティティの単体テスト")
class SKUTest {

    // テスト用のダミーIdentity（引数用の共通オブジェクト）
    private final Color red = new Color("赤");
    private final Size sizeM = new Size("M");
    private final SkuIdentity dummyIdentity = new SkuIdentity("T001", red, sizeM);

    // =========================================================================
    // 1. コンストラクタ（初期化）のテスト
    // =========================================================================
    @Nested
    @DisplayName("初期化（コンストラクタ）のテスト")
    class ConstructorTest {

        @Test
        @DisplayName("正常系：在庫数0で初期化できること（下限の境界値）")
        void testConstructorWithZeroStock() {
            // Given & When
            SKU sku = new SKU(dummyIdentity, 0);

            // Then
            assertEquals(0, sku.getStockQuantity());
        }

        @Test
        @DisplayName("正常系：正の在庫数で初期化できること")
        void testConstructorWithPositiveStock() {
            // Given & When
            SKU sku = new SKU(dummyIdentity, 10);

            // Then
            assertEquals(10, sku.getStockQuantity());
        }

        @Test
        @DisplayName("異常系：負の在庫数を指定した場合、例外が発生すること（境界値の外側）")
        void testConstructorWithNegativeStockThrowsException() {
            // Given
            int invalidStock = -1;

            // When & Then
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new SKU(dummyIdentity, invalidStock);
            });
            // エラーメッセージの文言まで厳密にチェック（リファクタリング時の漏れを防ぐ）
            assertEquals("在庫数は0以上である必要があります。", exception.getMessage());
        }
    }

    // =========================================================================
    // 2. 在庫引き当て（allocateStock）のテスト
    // =========================================================================
    @Nested
    @DisplayName("allocateStock（在庫引き当て）のテスト")
    class AllocateStockTest {

        private SKU sku;

        @BeforeEach
        void setUp() {
            // テストごとに在庫数10のSKUを準備
            sku = new SKU(dummyIdentity, 10);
        }

        @Test
        @DisplayName("正常系：在庫数より少ない数を引き当てられること")
        void testAllocateSuccess() {
            // Given (setUpで在庫10)

            // When
            sku.allocateStock(3);

            // Then
            assertEquals(7, sku.getStockQuantity(), "在庫が7に減っていること");
        }

        @Test
        @DisplayName("境界値：在庫数とぴったり同じ数を引き当てられること（残数が0になる境界）")
        void testAllocateExactlySameAsStock() {
            // Given (setUpで在庫10)

            // When
            sku.allocateStock(10);

            // Then
            assertEquals(0, sku.getStockQuantity(), "在庫がぴったり0になること");
        }

        @Test
        @DisplayName("異常系：在庫数を超える数を引き当てようとした場合、例外が発生すること（境界値の外側）")
        void testAllocateOverStockThrowsException() {
            // Given (setUpで在庫10)
            int overQuantity = 11;

            // When & Then
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                sku.allocateStock(overQuantity);
            });
            assertEquals("在庫不足です。現在の残り: 10", exception.getMessage());
        }
    }

    // =========================================================================
    // 3. 在庫返却（releaseStock）のテスト
    // =========================================================================
    @Nested
    @DisplayName("releaseStock（在庫返却）のテスト")
    class ReleaseStockTest {

        @Test
        @DisplayName("正常系：指定した数量だけ在庫が加算されること")
        void testReleaseStockSuccess() {
            // Given
            SKU sku = new SKU(dummyIdentity, 5);

            // When
            sku.releaseStock(5);

            // Then
            assertEquals(10, sku.getStockQuantity(), "在庫が10に増えていること");
        }
    }
}
