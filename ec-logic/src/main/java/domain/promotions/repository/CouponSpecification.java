package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.util.Map;

public interface CouponSpecification {
    boolean isSatisfiedBy(Cart cart, Map<String, Object> context);
}

