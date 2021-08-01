package shop.hodl.kkonggi.src.push.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicineNotification {
    private String deviceToken; // 기기 토큰
    private String userNickName;    // 유저 닉네임
    private String timeSlot;    // 시간대
    private int medicineCnt;    // 먹을 약물 수
}
