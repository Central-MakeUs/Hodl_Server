package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMedicine {
    private int totalCnt;
    private List<Medicine> medicineList;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Medicine{
        private int medicineIdx;
        private String medicineName;
        private String cycle;
        private String amount;
        private String endDay;
    }
}
