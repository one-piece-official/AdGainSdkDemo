package com.adgain.demo.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {
    private static SimpleDateFormat dateFormat;

    public static SimpleDateFormat getDateTimeFormat() {
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss SSS", Locale.CHINA);
        }
        return dateFormat;
    }
}
