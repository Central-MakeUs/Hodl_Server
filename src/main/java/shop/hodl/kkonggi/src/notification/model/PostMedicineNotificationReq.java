package shop.hodl.kkonggi.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMedicineNotificationReq {
    private int userIdx;
    private String timeSlot;    // D, M, L, E, N
    private String notificationTime;    // HH:mm
}
