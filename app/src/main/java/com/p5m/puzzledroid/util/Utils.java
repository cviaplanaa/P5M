package com.p5m.puzzledroid.util;

import java.util.Calendar;
import java.util.Date;

public abstract class Utils {
    //Convert Date to Calendar
    public static Calendar dateToCalendar(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

    //Convert Calendar to Date
    public static Date calendarToDate(Calendar calendar) {
        return calendar.getTime();
    }
}
