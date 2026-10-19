package com.example.domain.model.products_inventory;

import com.example.domain.model.products_inventory.Size;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("サイズ指定が正しいかチェック")
class SizeTest {

    @Nested
    @DisplayName("正常系テスト")
    class SuccessCases{

        @ParameterizedTest
        @ValueSource(strings = {"S", "M", "L"})
        @DisplayName("許容されたサイズ（S,M,L）で正常にインスタンスが生成されること")
        void success(String validValue){
            // Act
            Size size = new Size(validValue);

            // Assert
            assertThat(size.value()).isEqualTo(validValue);

        }
    }

    @Nested
    @DisplayName("異常系テスト")
    class failureCases {

        @ParameterizedTest
        @ValueSource(strings = {"黄", "緑", "", " ", "赤色"})
        @DisplayName("不正なサイズ文字列の場合に例外エラーがスローされる")
        void failure(String invalidValue) {
            // Act & Assert
            assertThatThrownBy(() -> new Size(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("不正なサイズです。");

        }

        @Test
        @DisplayName("nullが渡された場合にNullPointerExceptionが発生すること")
        void failure() {
            // 現状の実装（!value.equals(...)）では、valueがnullだとNPEになります
            assertThatThrownBy(() -> new Size(null))
                    .isInstanceOf(NullPointerException.class);

        }
    }
}