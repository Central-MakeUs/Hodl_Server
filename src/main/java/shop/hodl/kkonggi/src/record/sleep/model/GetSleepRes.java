package shop.hodl.kkonggi.src.record.sleep.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetSleepRes {
    private int status;

    private String date;
    private String sleepTime;
    private String wakeUpTime;
    private String memo;
}
