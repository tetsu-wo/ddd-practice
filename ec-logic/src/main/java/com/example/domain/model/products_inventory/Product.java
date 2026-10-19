package com.example.domain.model.products_inventory;

import lombok.Getter;
import com.example.presentation.dto.ProductDetailResponse;
import java.time.LocalDateTime;
import java.util.List;

public class Product {
    @Getter private final ProductIdentity productIdentity;
    final List<SKU> skus;
    private String status;

    public Product(ProductIdentity productIdentity, List<SKU> skus, String status) {
        if (skus == null || skus.isEmpty()) throw new IllegalArgumentException("商品は最低1つのバリエーションが必要です。");
        this.productIdentity = productIdentity;
        this.skus = List.copyOf(skus);
        this.status = status;
    }

    public String getStatus() {
        if (this.status.equals("公開") && getTotalStockQuantity() == 0) {
            return "売り切れ";
        }
        return this.status;
    }

    private int getTotalStockQuantity() {
        return skus.stream().mapToInt(SKU::getStockQuantity).sum();
    }


    public List<ProductDetailResponse.SkuResponse> toSkuResponses() {
        return this.skus.stream()
                .map(sku -> new ProductDetailResponse.SkuResponse(
                        sku.getColor().toString(),
                        sku.getSize().toString(),
                        sku.getStockQuantity()
                ))
                .toList();
    }

    public void order(Color color, Size size, int quantity, LocalDateTime orderDate) {
        validateSalesPeriod(orderDate);

        if (!this.getStatus().equals("公開")) {
            throw new IllegalStateException("この商品は現在購入できません（非公開または売り切れ）。");
        }

        SKU targetSku = findSku(color, size);
        targetSku.allocateStock(quantity);
    }

    public void cancelOrder(Color color, Size size, int quantity) {
        SKU targetSku = findSku(color, size);
        targetSku.releaseStock(quantity);
    }

    private SKU findSku(Color color, Size size) {
        return skus.stream()
                .filter(sku -> sku.getColor().equals(color) && sku.getSize().equals(size))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("指定されたバリエーションは存在しません。"));
    }

    private void validateSalesPeriod(LocalDateTime orderDate) {
        if (productIdentity.salesStartPeriod() == null || productIdentity.salesEndPeriod() == null) return;
        if (orderDate.isBefore(productIdentity.salesStartPeriod()) || orderDate.isAfter(productIdentity.salesEndPeriod())) {
            throw new IllegalStateException("販売期間外のため、購入できません。");
        }
    }
}
