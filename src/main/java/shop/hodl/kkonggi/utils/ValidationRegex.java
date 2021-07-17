package shop.hodl.kkonggi.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // yyyyMMdd
    public static boolean isRegexDate(String target){
        String regex = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])";
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // hh:ss
    public static boolean isRegexTime(String target){
        String regex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

}

