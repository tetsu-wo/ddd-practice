package com.example.products_inventory.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat; // 👈 AssertJのassertThat
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("色指定が正しいかチェック")
class ColorTest {

    @Nested
    @DisplayName("正常系：有効なカラーの生成")
    class SuccessCases {

        @ParameterizedTest
        @ValueSource(strings = {"赤", "青"})
        @DisplayName("有効な色名（赤・青）が指定された場合、インスタンスが生成されていること")
        void shouldCreateColorWhenValueIsValid(String validcolor) {

            //act
            Color color = new Color(validcolor);

            //assert
            assertThat(color.value()).isEqualTo(validcolor);

        }
    }

    @Nested
    @DisplayName("異常系：不正なカラーのバリデーション")
    class FailureCases{}

        @ParameterizedTest
        @ValueSource(strings = {"黄", "緑", "", " ", "赤色"})
        @DisplayName("無効な色名（赤・青以外）が指定された場合")
        void shouldThrowExceptionWhenValueIsInvalid(String invalidColor){

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Color(invalidColor);});

            assertThat(exception.getMessage().contains("不正なカラーです")).isTrue();

        }

        @Test
        @DisplayName("nullが指定された場合、NullPointerExceptionまたはIllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionWhenValueIsNull() {
            assertThrows(NullPointerException.class, () -> {
                new Color(null);
            });

        }
}
