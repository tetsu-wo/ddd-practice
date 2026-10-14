package domain.products_inventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ColorTest {

    @Nested
    @DisplayName("正常系: 有効なカラーの生成")
    class SuccessCases {

        @ParameterizedTest
        @ValueSource(strings = {"赤", "青"})
        @DisplayName("有効な色名（赤・青）が指定された場合、インスタンスが正しく生成されること")
        void shouldCreateColorWhenValueIsValid(String validColor) {
            // Act
            Color color = new Color(validColor);

            // Assert
            assertEquals(validColor, color.value());
        }
    }

    @Nested
    @DisplayName("異常系: 不正なカラーのバリデーション")
    class FailureCases {

        @ParameterizedTest
        @ValueSource(strings = {"黄", "緑", "", " ", "赤色"})
        @DisplayName("不正な色名が指定された場合、IllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionWhenValueIsInvalid(String invalidColor) {
            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                new Color(invalidColor);
            });

            assertEquals("不正なカラーです。", exception.getMessage());
        }

        @Test
        @DisplayName("nullが指定された場合、NullPointerExceptionまたはIllegalArgumentExceptionがスローされること")
        void shouldThrowExceptionWhenValueIsNull() {
            // 現在の実装では null.equals(...) になり NullPointerException が発生します
            assertThrows(NullPointerException.class, () -> {
                new Color(null);
            });
        }
    }
}
