package shop.hodl.kkonggi.utils;

import java.util.ArrayList;
import java.util.List;

public class days {
    public static List<String> getDays(int day){
        String bDay = Integer.toBinaryString(day);

        List<String> days = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            if((day & (int) Math.pow(2, i)) == 1){
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
}
