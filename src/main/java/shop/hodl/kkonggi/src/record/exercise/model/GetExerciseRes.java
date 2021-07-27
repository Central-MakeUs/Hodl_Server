package shop.hodl.kkonggi.src.record.exercise.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetExerciseRes {
    private int status;
    private String date;
    private String startTime;   // HH:mm
    private String totalTime;   // HH:mm
    private String memo;
}
