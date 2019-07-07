package com.yootk.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StringToDateConverter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;
    private StringToDateConverter() {}
    public static Date converter(String data) {
        if (data == null || "".equals(data)) {
            return null ;
        }
        LocalDate localDate = LocalDate.parse(data) ;
        ZoneId zoneId = ZoneId.systemDefault() ; // 获取默认的时区
        Instant instant = localDate.atStartOfDay().atZone(zoneId).toInstant() ;
        return Date.from(instant) ;
    }
}
