package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutMedicineReq {
    private int userIdx;
    private int medicineIdx;
    private String medicineRealName;
    private String medicineDetail;
    private String startDay;
    private String endDay;
    private int days;

}
