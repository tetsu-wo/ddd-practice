package domain.promotions.repository;

import domain.promotions.model.Cart;
import java.time.LocalDateTime;
import java.util.Map;

public class ExpirySpecification implements CouponSpecification {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public ExpirySpecification(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public boolean isSatisfiedBy(Cart cart, Map<String, Object> context) {
        LocalDateTime currentTime = (LocalDateTime) context.getOrDefault("currentTime", LocalDateTime.now());
        return !currentTime.isBefore(startDate) && !currentTime.isAfter(endDate);
    }
}

