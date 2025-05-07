package app.web.dto;

import app.subscription.model.SubscriptionType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EditUserRequestByAdmin {
    private UUID userId;
    private SubscriptionType subscriptionType;
    private String status;
}
