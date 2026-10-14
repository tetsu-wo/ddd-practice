package domain.products_inventory;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SizeTest {

    @Nested
    @DisplayName("正常系のテスト")
    class ValidCases {

        @ParameterizedTest
        @ValueSource(strings = {"S", "M", "L"})
        @DisplayName("許容されたサイズ（S, M, L）で正常にインスタンスが生成されること")
        void shouldCreateInstanceWithValidSizes(String validValue) {
            // Act
            Size size = new Size(validValue);

            // Assert
            assertThat(size.value()).isEqualTo(validValue);
        }
    }

    @Nested
    @DisplayName("異常系のテスト")
    class InvalidCases {

        @ParameterizedTest
        @ValueSource(strings = {"XL", "s", "", " "})
        @DisplayName("不正なサイズ文字列の場合にIllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionForInvalidSizes(String invalidValue) {
            // Act & Assert
            assertThatThrownBy(() -> new Size(invalidValue))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("不正なサイズです。");
        }

        @Test
        @DisplayName("nullが渡された場合にNullPointerExceptionが発生すること")
        void shouldThrowNpeWhenValueIsNull() {
            // 現状の実装（!value.equals(...)）では、valueがnullだとNPEになります
            assertThatThrownBy(() -> new Size(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}


