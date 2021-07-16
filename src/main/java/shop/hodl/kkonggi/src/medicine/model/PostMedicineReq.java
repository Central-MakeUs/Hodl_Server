package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostMedicineReq {
    private int userIdx;
    private String medicineRealName;
    // private String cycle;
    private int days;
    // private int takeTimes;
    private String startDay;
    private String endDay;
}
