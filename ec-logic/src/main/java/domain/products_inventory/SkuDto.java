// SkuDto.java
package domain.products_inventory;

/**
 * UI層とアプリケーション層の間でデータを安全に持ち運ぶためのDTO。
 */
public record SkuDto(
        String id,
        String colorName,
        String sizeValue,
        int stockQuantity
) {}
