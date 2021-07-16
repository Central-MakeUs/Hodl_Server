package shop.hodl.kkonggi.record.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicineListRes {
    private String timeSlot;
    private List<Medicine> medicineList;


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Medicine {
        private int medicineIdx;
        private String medicineName;
        private String time;
        private String status;
    }

}
