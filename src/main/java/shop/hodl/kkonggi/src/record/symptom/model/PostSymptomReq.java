package shop.hodl.kkonggi.src.record.symptom.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSymptomReq {
    public String date; // 기록하려는 날짜
    public int[] symptomIdx;    // 체크된 부작용 식별자
}
