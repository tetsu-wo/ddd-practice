package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.math.BigDecimal;
import java.util.Map;

public class MinAmountSpecification implements CouponSpecification {
    private final BigDecimal minAmount;

    public MinAmountSpecification(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    @Override
    public boolean isSatisfiedBy(Cart cart, Map<String, Object> context) {
        return cart.getRawTotalAmount().compareTo(minAmount) >= 0;
    }
}

