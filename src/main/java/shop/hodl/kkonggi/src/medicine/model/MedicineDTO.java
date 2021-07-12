package shop.hodl.kkonggi.src.medicine.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicineDTO {
    private String name;
    private int[] days;    // 순서 : 월,화,수,목,금,토,일
                            // 매일 : {1,1,1,1,1,1,1}, 월,수,금,토{1,0,1,0,1,1,0}
                            // 이틀에 한 번 {2,0,0,0,0,0,0}
    private String start; // YYYY-MM-DD
    private String end;     // YYYY-MM-DD Null가능
    private int[] times; // 5개 순서대로 새벽, 아침, 점심, 저녁, 자기전 {1,1,1,1,1}
}
