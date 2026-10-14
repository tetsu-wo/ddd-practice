package domain.cart_price.model;

public record Price(int value, TaxRate taxRate) {
    public enum TaxRate {
        STANDARD(10), REDUCED(8);
        private final int percent;
        TaxRate(int percent) { this.percent = percent; }
    }

    public Price {
        if (value < 0) throw new IllegalArgumentException("金額は0以上である必要があります。");
    }

    // 消費税（外税・端数切り捨て）を計算するビジネスロジック
    public int calculateTax() {
        return (int) Math.floor((double) value * taxRate.percent / 100);
    }

    public int totalWithTax() {
        return value + calculateTax();
    }
}

