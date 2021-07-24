package shop.hodl.kkonggi.utils;

import java.util.ArrayList;
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

}
