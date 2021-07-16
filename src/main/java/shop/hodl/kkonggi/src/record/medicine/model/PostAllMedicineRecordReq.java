package shop.hodl.kkonggi.src.record.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostAllMedicineRecordReq {
    private String date; // yyyyMMdd
    private String timeSlot;    // D, M, L, E, N
    private String time;    // hh:ss
    private int[] medicineIdx;
}
