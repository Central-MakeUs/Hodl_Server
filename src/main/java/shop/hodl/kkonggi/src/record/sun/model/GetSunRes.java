package shop.hodl.kkonggi.src.record.sun.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSunRes {
    private int status;

    private String date;
    private String startTime;   // HH:mm
    private String totalTime;   // HH:mm
    private String memo;
}
