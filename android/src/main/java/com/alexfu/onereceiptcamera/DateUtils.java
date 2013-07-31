package com.alexfu.onereceiptcamera;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DateUtils {
    private static final Calendar TODAY = Calendar.getInstance();
    private static Calendar TEMP = Calendar.getInstance();

    public static double weeksPassed(Calendar date) {
        final Calendar fromDate = Calendar.getInstance();
        fromDate.setTimeInMillis(date.getTimeInMillis());

        if(TODAY.before(fromDate)) { // Invalid.
            return -1;
        }

        if(fromDate.get(Calendar.MONTH) == TODAY.get(Calendar.MONTH)) { // Within same month
            return (TODAY.get(Calendar.DATE) - fromDate.get(Calendar.DATE))/7.0;
        }

        int days = 0;
        while(fromDate.get(Calendar.MONTH) < TODAY.get(Calendar.MONTH)) {
            days += (fromDate.getActualMaximum(Calendar.DATE)-fromDate.get(Calendar.DATE));
            fromDate.set(Calendar.MONTH,  TODAY.get(Calendar.MONTH)+1);
            fromDate.set(Calendar.DATE, 1);
        }

        return (days + (TODAY.get(Calendar.DATE) - fromDate.get(Calendar.DATE)))/7.0;
    }

    public static String formatDate(long timeInMillis, String format) {
        TEMP.clear();
        TEMP.setTimeInMillis(timeInMillis);
        return new SimpleDateFormat(format).format(TEMP.getTime());
    }

    public static ArrayList<Integer> getDatesList(int month) {
        TEMP.clear();
        TEMP.set(Calendar.MONTH, month);
        ArrayList<Integer> dates = new ArrayList<Integer>();

        int maxDate = TEMP.getActualMaximum(Calendar.DATE);
        for(int i = 1; i <= maxDate; i++) {
            dates.add(i);
        }
        return dates;
    }

    public static ArrayList<Integer> getYearsList() {
        TEMP.clear();
        TEMP.setTimeInMillis(System.currentTimeMillis());
        ArrayList<Integer> years = new ArrayList<Integer>();


        for(int i = 0; i < 10; i++) {
            years.add(TEMP.get(Calendar.YEAR) - i);
        }
        return years;
    }
}
