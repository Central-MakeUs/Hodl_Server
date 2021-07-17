package shop.hodl.kkonggi.src.record.medicine.model;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchMedicineRecordReq {
    private String status;  // Y : 수정, N : 먹지 않음
    private String date;    // 날짜
    private double amount;  // 투약량 (디폴트 1)
    private String time;    // HH:ss
    private String memo;
}
