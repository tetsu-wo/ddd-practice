package domain.products_inventory;


class SKU {
    private final String id;
    private final Color color;
    private final Size size;
    private int stockQuantity;

    SKU(String id, Color color, Size size, int stockQuantity) {
        if (stockQuantity < 0) throw new IllegalArgumentException("在庫数は0以上である必要があります。");
        this.id = id;
        this.color = color;
        this.size = size;
        this.stockQuantity = stockQuantity;
    }

    Color getColor() { return color; }
    Size getSize() { return size; }
    int getStockQuantity() { return stockQuantity; }

    void allocateStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("在庫不足です。現在の残り: " + this.stockQuantity);
        }
        this.stockQuantity -= quantity;
    }

    void releaseStock(int quantity) {
        this.stockQuantity += quantity;
    }
}
