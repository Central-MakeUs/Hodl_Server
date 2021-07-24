package shop.hodl.kkonggi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SleepTime {
    private static final Logger logger = LoggerFactory.getLogger("SleepTime");
    public static long subTime(Date sleepTime, Date wakeUpTime, Date mid){
        int hour = 3600000;
        long diff = 0;
        // 잠든 시각이 일어난 시각보다 늦음
        if(sleepTime.before(wakeUpTime)) {
            logger.info("before");
            diff = (wakeUpTime.getTime() - sleepTime.getTime()) / hour;
        }
            // 잠든 시각이 일어난 시각보다 빠름
        else if (sleepTime.after(wakeUpTime)) {
            logger.info("after");
            logger.info((mid.getTime() - sleepTime.getTime()) + "sub by mid ");
            logger.info((mid.getTime() - sleepTime.getTime() + wakeUpTime.getTime()) + " + wakeUpTime");
            diff = 24 + (wakeUpTime.getTime() - sleepTime.getTime()) / hour;
        }
            // 잠든 시각 == 일어난 시각 (12시간 차이)
        else diff = 12;
        return diff;
    }
}
