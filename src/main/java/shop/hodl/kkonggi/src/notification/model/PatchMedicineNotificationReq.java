package shop.hodl.kkonggi.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PatchMedicineNotificationReq {
    private String timeSlot;    // D, M, L, E, N
    private String notificationTime;    // 알림 시간 HH:mm
    private String status; // A : on, I : off
}
