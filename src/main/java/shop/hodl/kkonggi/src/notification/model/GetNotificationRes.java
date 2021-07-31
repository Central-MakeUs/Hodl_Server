package shop.hodl.kkonggi.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetNotificationRes {
    private int isServicePush;
    private int isMedicinePush;
    private int isEventPush;
    private int isMarketingPush;
}
