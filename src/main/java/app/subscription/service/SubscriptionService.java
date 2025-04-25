package app.subscription.service;

import app.subscription.model.Subscription;
import app.subscription.model.SubscriptionPeriod;
import app.subscription.model.SubscriptionType;
import app.subscription.repository.SubscriptionRepository;
import app.user.model.User;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription createDefaultSubscription(User user) {
        Subscription subscription = Subscription.builder()
                .price(BigDecimal.ZERO)
                .period(SubscriptionPeriod.YEARLY)
                .type(SubscriptionType.FREE)
                .renewable(true)
                .user(user)
                .build();
        return subscriptionRepository.save(subscription);
    }
}
