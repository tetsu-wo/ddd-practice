package domain.promotions.model;

import domain.promotions.model.Cart;
import domain.promotions.repository.CouponSpecification;

import java.util.List;
import java.util.Map;

public class CouponValidator {
    private final List<CouponSpecification> specifications;

    public CouponValidator(List<CouponSpecification> specifications) {
        this.specifications = specifications;
    }

    public boolean validate(Cart cart, Map<String, Object> context) {
        // 全ての条件（Specification）を満たしているかチェック
        return specifications.stream().allMatch(spec -> spec.isSatisfiedBy(cart, context));
    }
}

