package shop.hodl.kkonggi.utils;

import java.util.Date;

public class SleepTime {
    public static long subTime(Date sleepTime, Date wakeUpTime){
        int hour = 3600000;
        long diff = 0;
        // 잠든 시각이 일어난 시각보다 늦음
        if(sleepTime.getTime() > wakeUpTime.getTime()) diff = ((24 - sleepTime.getTime()) + wakeUpTime.getTime()) / hour;
            // 잠든 시각 == 일어난 시각 (12시간 차이)
        else if (sleepTime.getTime() == wakeUpTime.getTime()) diff = 12;
            // 잠든 시각이 일어난 시각보다 빠름
        else diff = (wakeUpTime.getTime() - sleepTime.getTime()) / hour;

        return diff;
    }
}
