package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicineDetailRes {
    private int medicineIdx;
    private String medicineName;
    private String medicineDetail;
    private String medicineStartDay;
    private String medicineEndDay;

    private String medicineCycle;
    private String medicineTimeSlot;
    private double medicineAmount;
}
