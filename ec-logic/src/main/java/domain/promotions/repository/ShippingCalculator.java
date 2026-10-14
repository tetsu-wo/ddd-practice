package domain.promotions.repository;

import java.math.BigDecimal;

public interface ShippingCalculator {
    BigDecimal calculateShipping(BigDecimal finalProductAmount);
}

