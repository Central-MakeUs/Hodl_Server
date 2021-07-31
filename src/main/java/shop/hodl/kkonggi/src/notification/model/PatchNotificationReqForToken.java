package shop.hodl.kkonggi.src.notification.model;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchNotificationReqForToken {
    private int isServicePush;
    private int isMedicinePush;
    private int isEventPush;
    private int isMarketingPush;
    private String deviceToken;
}
