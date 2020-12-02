package com.techknightsrtu.crosstalks.app.helper;

import android.text.format.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

        if(!isYesterday(ts)){

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

        }else{
            dateFromTimestamp = "Yesterday";
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

            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
            sdfTime.setTimeZone(java.util.TimeZone.getTimeZone("IST"));

            timeFromTimestamp = sdfTime.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeFromTimestamp.toUpperCase();

    }

    public static boolean isYesterday(String timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("IST"));

        Date d;
        boolean isYesterday = false;
        try {

            d = sdf.parse(timestamp);

            isYesterday = DateUtils.isToday(d.getTime() + DateUtils.DAY_IN_MILLIS);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return isYesterday;
    }


}
