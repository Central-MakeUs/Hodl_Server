package shop.hodl.kkonggi.src.notification.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicineNotificationRes {
    private List<MedicineNotification> medicineNotifications;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MedicineNotification{
        private String timeSlot;    // D, M, L, E, N
        private String notificationTime;    // 알림 시간
        private int status; // 1 : on, 0 : off
    }
}
