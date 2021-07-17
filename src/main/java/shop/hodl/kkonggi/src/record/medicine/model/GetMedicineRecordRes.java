package shop.hodl.kkonggi.src.record.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicineRecordRes {
    private String status; // record : 기록 화면, modify : 수정 화면
    private int medicineIdx;
    private String medicineName;
    private List<String> slot;  // 새벽, 오전, 점심, 저녁, 자기전
    private int slotCnt; // 총 몇 번?
    private String date;    // 날짜
    private String time;    // 투약 시간
    private double amount;  // 투약량
    private String memo;    // 투약 관련 메모
    private List<String> days;  // 매일 혹은 월, 화, 금, 토, 일
}
