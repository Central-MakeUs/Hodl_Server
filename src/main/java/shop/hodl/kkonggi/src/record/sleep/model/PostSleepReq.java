package shop.hodl.kkonggi.src.record.sleep.model;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostSleepReq {
    private String date;    // yyyymmdd
    private String sleepTime;   // HH:mm
    private String wakeUpTime;  // HH:mm
    private String memo;
    private int isSleep;    // 1 : 잤음, 0 : 밤 샜어요
}
