package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.math.BigDecimal;

public class StandardShippingCalculator implements ShippingCalculator {
    private final BigDecimal freeShippingThreshold; // 例: 5000円以上
    private final BigDecimal standardFee;          // 例: 送料500円

    public StandardShippingCalculator(BigDecimal freeShippingThreshold, BigDecimal standardFee) {
        this.freeShippingThreshold = freeShippingThreshold;
        this.standardFee = standardFee;
    }

    @Override
    public BigDecimal calculateShipping(BigDecimal finalProductAmount) {
        // すべての割引が引かれた【後】の金額で送料無料を判定する（仕様定義のベストプラクティス）
        if (finalProductAmount.compareTo(freeShippingThreshold) >= 0) {
            return BigDecimal.ZERO;
        }
        return standardFee;
    }
}

