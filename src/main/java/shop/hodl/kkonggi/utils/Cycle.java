package shop.hodl.kkonggi.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cycle {
    public static int intArrayToInt(int[] arr){
        int sum = 0;
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == 1){
                sum += Math.pow(2, i);
            }
        }
        return sum;
    }

    public static ArrayList<String> toTimeSlot(int[] arr){
        ArrayList<String> timeSlot = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == 1){
                if(i == 0) timeSlot.add("D");   // Dawn
                if(i == 1) timeSlot.add("M");   // Morning
                if(i == 2) timeSlot.add("L");   // Launch
                if(i == 3) timeSlot.add("E");   // Evening
                if(i == 4) timeSlot.add("N");   // Night
            }
        }
        return timeSlot;
    }

    public static List<String> getTimeSlotOfMedicineTime(List<String> getTimeSlot){
        if(getTimeSlot.stream().anyMatch(e -> e.equals("M")) && getTimeSlot.stream().anyMatch(e -> e.equals("E"))){
            int morning = getTimeSlot.indexOf(getTimeSlot.stream().filter(e -> e.equals("M")).findFirst().get());
            int evening = getTimeSlot.indexOf(getTimeSlot.stream().filter(e -> e.equals("E")).findFirst().get());

            Collections.swap(getTimeSlot, morning, evening);    // 시간순 정렬1
        }

        List<String> timeSlotRes = new ArrayList<>();
        for(int i = 0; i < getTimeSlot.size(); i++){
            if(getTimeSlot.get(i).equals("D")) timeSlotRes.add("새벽");
            if(getTimeSlot.get(i).equals("M")) timeSlotRes.add("아침");
            if(getTimeSlot.get(i).equals("L")) timeSlotRes.add("점심");
            if(getTimeSlot.get(i).equals("E")) timeSlotRes.add("저녁");
            if(getTimeSlot.get(i).equals("N")) timeSlotRes.add("자기 전");
        }
        return timeSlotRes;
    }

}
