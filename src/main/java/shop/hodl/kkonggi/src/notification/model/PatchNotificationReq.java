package shop.hodl.kkonggi.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchNotificationReq {
    private String isServicePush;
    private String isMedicinePush;
    private String isEventPush;
    private String isMarketingPush;
}
