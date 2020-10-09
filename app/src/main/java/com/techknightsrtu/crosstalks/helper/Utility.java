package com.techknightsrtu.crosstalks.helper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Utility {

    //return timestamp of the moment
    public static String getCurrentTimestamp(){

        //Date object
        Date date= new Date();
        //getTime() returns current time in milliseconds
        long time = date.getTime();

        //Passed the milliseconds to constructor of Timestamp class
        Timestamp ts = new Timestamp(time);
        System.out.println("Current Time Stamp: "+ ts);

        return ts.toString();
    }

    //return date from timestamp
    public static String getDateFromTimestamp(String ts){

        String dateFromTimestamp = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        try {

            Date date = sdf.parse(ts);

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
            sdfDate.setTimeZone(java.util.TimeZone.getTimeZone("IST"));

            dateFromTimestamp = sdfDate.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

         return dateFromTimestamp;
    }

    //return time from timestamp
    public static String getTimeFromTimestamp(String ts){

        String timeFromTimestamp = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        try {

            Date date = sdf.parse(ts);

            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a");
            sdfTime.setTimeZone(java.util.TimeZone.getTimeZone("IST"));

            timeFromTimestamp = sdfTime.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeFromTimestamp;

    }


    public static boolean isAppAccessAllowed(){

        int from = 2300;
        int to = 2300;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        return true;
        //return to > from && t >= from && t <= to || to < from && (t >= from || t <= to);
    }

}
