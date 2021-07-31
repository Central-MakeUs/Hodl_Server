package shop.hodl.kkonggi.config;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class Constant {
    public static final List<List<String>> TIMES = Arrays.asList(Arrays.asList("D", "M", "L", "E", "N"), Arrays.asList("06:00", "09:00", "12:00", "18:00", "21:00"));
    public static final SimpleDateFormat LogDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
}

