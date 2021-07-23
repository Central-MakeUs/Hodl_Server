package shop.hodl.kkonggi.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class days {
    public static List<String> getDays(int day){
        List<String> days = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            if((day & (int) Math.pow(2, i)) == (int) Math.pow(2, i)){
                System.out.println(i + " day = " + day);
                if(i == 0) days.add("월");
                if(i == 1) days.add("화");
                if(i == 2) days.add("수");
                if(i == 3) days.add("목");
                if(i == 4) days.add("금");
                if(i == 5) days.add("토");
                if(i == 6) days.add("일");
            }
        }
        if(days.size() == 7) {
            days.clear();
            days.add("매일");
        }
        return days;
    }

    public static String getDaysToString(int day){
        String days = "";
        int cnt = 0;
        for(int i = 0; i < 7; i++){
            if((day & (int) Math.pow(2, i)) == (int) Math.pow(2, i)){
                if(cnt > 0) days += ",";
                if(i == 0) days += "월";
                if(i == 1) days += "화";
                if(i == 2) days += "수";
                if(i == 3) days += "목";
                if(i == 4) days += "금";
                if(i == 5) days += "토";
                if(i == 6) days += "일";
                cnt++;
            }
        }
        if(days.length() == 7) {
            days = "매일";
        }
        return days;
    }

    public static String getCurrentTimeStr(){
        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyyMMdd");
        Date current = new Date();
        String currentTimeStr = dtFormat.format(current);
        return currentTimeStr;
    }
}
