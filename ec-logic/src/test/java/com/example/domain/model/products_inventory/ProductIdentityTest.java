package com.example.domain.model.products_inventory;

import com.example.domain.model.products_inventory.ProductIdentity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat; // 👈 AssertJのassertThat
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("商品属性の時間指定が正しいかをチェックする")
class ProductIdentityTest {

        // テストで使い回す基準の日時（2026年10月15日 10:00）
        private final LocalDateTime baseTime = LocalDateTime.of(2026, 10, 15, 10, 0);

        @Nested
        @DisplayName("正常系のテスト")
        class SuccessTest {

            @Test
            @DisplayName("正常な販売期間（開始が終了より前）であれば正常にインスタンスが生成されること")
            void success_normal_period() {
                LocalDateTime start = baseTime;
                LocalDateTime end = baseTime.plusHours(1); // 1時間後

                ProductIdentity identity = new ProductIdentity("P001", "テスト商品", start, end);

                // 値が正しく設定されているかの確認（以前お話しした器のチェック！）
                assertThat(identity.salesStartPeriod()).isEqualTo(start);
                assertThat(identity.salesEndPeriod()).isEqualTo(end);
            }

            @Test
            @DisplayName("開始日時と終了日時が全く同じであっても正常にインスタンスが生成されること")
            void success_same_datetime() {
                LocalDateTime start = baseTime;
                LocalDateTime end = baseTime; // 同じ時間

                ProductIdentity identity = new ProductIdentity("P001", "テスト商品", start, end);

                assertThat(identity.salesStartPeriod()).isEqualTo(start);
                assertThat(identity.salesEndPeriod()).isEqualTo(end);
            }
        }

        @Nested
        @DisplayName("異常系のテスト")
        class FailureTest {

            @Test
            @DisplayName("開始日時が終了日時よりも後の時間を指定された場合は例外が発生すること")
            void failure_start_after_end() {
                LocalDateTime start = baseTime.plusHours(1); // 11:00 (後)
                LocalDateTime end = baseTime;               // 10:00 (前)

                // AssertJの綺麗なチェーンでエラーとメッセージを検証
                assertThatThrownBy(() -> new ProductIdentity("P001", "テスト商品", start, end))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("販売開始日時は、販売終了日時より前の時間を指定してください。");
            }
        }

        @Nested
        @DisplayName("nullチェックのテスト")
        class NullTest {

            @Test
            @DisplayName("販売開始日時がnullの場合は例外が発生すること")
            void failure_start_is_null() {
                LocalDateTime end = baseTime;

                assertThatThrownBy(() -> new ProductIdentity("P001", "テスト商品", null, end))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("販売期間の開始日時と終了日時は必須です。");
            }

            @Test
            @DisplayName("販売終了日時がnullの場合は例外が発生すること")
            void failure_end_is_null() {
                LocalDateTime start = baseTime;

                assertThatThrownBy(() -> new ProductIdentity("P001", "テスト商品", start, null))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessage("販売期間の開始日時と終了日時は必須です。");
            }
        }
    }


