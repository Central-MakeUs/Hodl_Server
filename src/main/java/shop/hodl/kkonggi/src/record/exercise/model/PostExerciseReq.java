package shop.hodl.kkonggi.src.record.exercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostExerciseReq {
    private String date;    // yyyymmdd
    private String startTime;   // HH:mm
    private String totalTime;  // HH:mm
    private String memo;
    private int isExercise;    // 1 : 광합성 함, 0 : 안함
}
