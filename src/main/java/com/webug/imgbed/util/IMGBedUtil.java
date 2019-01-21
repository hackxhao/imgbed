package com.webug.imgbed.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class IMGBedUtil {
    /**
     * 获取当前日期或者时间
     * @param format
     * @return
     */
    public static String getNowDate(String format){
        String dateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        dateString = formatter.format(new Date());
        return dateString;
    }
}
