package shop.hodl.kkonggi.src.notification.model;

import lombok.*;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetNotificationRes {
    private int isServicePush;
    private int isMedicinePush;
    private int isEventPush;
    private int isMarketingPush;
}
