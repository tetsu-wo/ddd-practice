package domain.products_inventory;

import domain.products_inventory.*;
import java.time.LocalDateTime;
import java.util.List;

public class Product {
    private final String id;
    private final String name;
    private final List<SKU> skus;
    private String status;
    private LocalDateTime salesStartPeriod;
    private LocalDateTime salesEndPeriod;

    public Product(String id, String name, List<SKU> skus, String status, LocalDateTime start, LocalDateTime end) {
        if (skus == null || skus.isEmpty()) throw new IllegalArgumentException("商品は最低1つのバリエーションが必要です。");
        this.id = id;
        this.name = name;
        this.skus = List.copyOf(skus);
        this.status = status;
        this.salesStartPeriod = start;
        this.salesEndPeriod = end;
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
        if (salesStartPeriod == null || salesEndPeriod == null) return;
        if (orderDate.isBefore(salesStartPeriod) || orderDate.isAfter(salesEndPeriod)) {
            throw new IllegalStateException("販売期間外のため、購入できません。");
        }
    }
}
