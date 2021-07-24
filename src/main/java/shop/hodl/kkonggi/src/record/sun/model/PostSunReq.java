package shop.hodl.kkonggi.src.record.sun.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSunReq {
    private String date;    // yyyymmdd
    private String startTime;   // HH:mm
    private String totalTime;  // HH:mm
    private String memo;
    private int isSun;    // 1 : 광합성 함, 0 : 안함
}
