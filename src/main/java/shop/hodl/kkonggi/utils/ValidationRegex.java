package shop.hodl.kkonggi.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    static final Logger logger = LoggerFactory.getLogger("ValidationRegex");;
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // yyyyMMdd
    public static boolean isRegexDate(String target){
        String regex = "(19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$";
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // yyyy
    public static boolean isRegexYear(String target){
        String regex = "(19|20)\\d{2}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // HH:mm
    public static boolean isRegexTime(String target){
        String regex = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$";
        Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    public static String getMaskedEmail(String email) {
        /*
         * 요구되는 메일 포맷
         * {userId}@domain.com
         * */
        String regex = "\\b(\\S+)+@(\\S+.\\S+)";
        Matcher matcher = Pattern.compile(regex).matcher(email);
        if (matcher.find()) {
            String id = matcher.group(1); // 마스킹 처리할 부분인 userId
            /*
             * userId의 길이를 기준으로 세글자 초과인 경우 뒤 세자리를 마스킹 처리하고,
             * 세글자인 경우 뒤 두글자만 마스킹,
             * 세글자 미만인 경우 모두 마스킹 처리
             */
            int length = id.length();
            if (length < 3) {
                char[] c = new char[length];
                Arrays.fill(c, '*');
                return email.replace(id, String.valueOf(c));
            } else if (length == 3) {
                return email.replaceAll("\\b(\\S+)[^@][^@]+@(\\S+)", "$1**@$2");
            } else {
                return email.replaceAll("\\b(\\S+)[^@][^@][^@]+@(\\S+)", "$1***@$2");
            }
        }
        return email;
    }

}

