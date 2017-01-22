package com.cloudklosett.hackcloset;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Hashtable;
import java.util.TimeZone;

public class CalendarContentResolver {
    public class EventField{
        private String _id;
        private String _desc;
        private String _end;
        private String _start;
        private String _calId;
        private String _title;
        public EventField(String id, String desc, String calId, String end, String start, String title){
            _id = id;
            _desc = desc;
            _calId = calId;
            _end = end;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getDefault());
            if(start == null) { start = end; }
            _start = sdf.format(new Date(Long.parseLong(start)));
            if(end == null) { end = start; }
            _end = sdf.format(new Date(Long.parseLong(end)));
            _title = title;
        }
        public String getGarmentId(){
            String ret = _desc;
            return ret;
        }

        public String getId(){
            String ret = _id;
            return ret;
        }
        public String getDescription(){
            String ret = _desc;
            return ret;
        }
        public String getCalendarId(){
            String ret = _calId;
            return ret;
        }
        public String getEndTime(){
            String ret = _end;
            return ret;
        }
        public String getStartTime(){
            String ret = _start;
            return ret;
        }
        public void setStartTime(String s){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
            sdf.setTimeZone(TimeZone.getDefault());
            _start = sdf.format(new Date(Long.parseLong(s)));
        }

        public String getDay(){
            return _start.substring(0, 2);
        }
        public String getMonth(){
            return _start.substring(3, 5);
        }
        public String getYear(){
            return _start.substring(6, 10);
        }
        public String getDate() {
            return this.getDay() + "-" + this.getMonth() + " " + this.getYear();
        }

        public String getTitle(){
            String ret = _title;
            return ret;
        }
    }
    public static final String[] FIELDS = {
            CalendarContract.Events._ID,//0
            CalendarContract.Events.DESCRIPTION,//1
            CalendarContract.Events.CALENDAR_ID,//2
            CalendarContract.Events.DTEND,//3
            CalendarContract.Events.DTSTART, //4
            CalendarContract.Events.TITLE //5

    };
    Activity context;
    public static String userName;
    public int calendarId;
    public static final Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/events");

    ContentResolver contentResolver;
    Hashtable<String, EventField> events = new Hashtable<String, EventField>();

    public static final int MY_PERMISSIONS_REQUEST_GET_ACCOUNTS = 14;

    public  CalendarContentResolver(Activity ctx) {
        contentResolver = ctx.getContentResolver();
        context = ctx;
    }

    public Hashtable<String, EventField> getCalendar(String month, String year) {
        userName = getUsername();
        Cursor cursor = contentResolver.query(CALENDAR_URI, FIELDS,
                "((" + CalendarContract.Events.ORGANIZER + " = ?) AND (" +
                CalendarContract.Events.TITLE + " = ?))",
                new String[]{userName, "Cloud Klosett"},
                null);

        try {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    EventField e = new EventField(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5)
                    );
                    //dd-MM-yyyy HH:mm:ss
                    if(e.getMonth().compareTo(month) == 0
                            &&
                            e.getYear().compareTo(year)== 0) {
                        events.put(e.getTitle(), e);
                        calendarId = Integer.parseInt(e.getCalendarId());
                    }
                }
            }
        } catch (AssertionError ex) { /*TODO: log exception and bail*/ }
        return events;
    }

    public EventField addEvent(Date date, String garmentId) {
        //ContentResolver cr = getContentResolver();
        String description = "Event added through caldroid!";
        String eTitle = "Added Event";
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, date.getTime());
        values.put(CalendarContract.Events.DTEND, date.getTime() + 100);
        values.put(CalendarContract.Events.TITLE, eTitle);
        values.put(CalendarContract.Events.DESCRIPTION, description);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().toString());
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
            //String id, String desc, String calId, String end, String start, String title
            long eventID = Long.parseLong(uri.getLastPathSegment());
            long utcDate0 = date.getTime();
            long utcDate1 = utcDate0;
            EventField e = new EventField(
                    String.valueOf(eventID),
                    description,
                    "",
                    String.valueOf(utcDate1 + 100),
                    String.valueOf(calendarId),
                    eTitle );
            e.setStartTime(String.valueOf(utcDate0));
            return e;
        }
        return null;
    }

    public String getUsername() {
        AccountManager manager = AccountManager.get(context);
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    MY_PERMISSIONS_REQUEST_GET_ACCOUNTS);

        }
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS)
                == PackageManager.PERMISSION_GRANTED) {
            Account[] accounts = manager.getAccountsByType("com.google");
            List<String> possibleEmails = new LinkedList<String>();

            for (Account account : accounts) {
                possibleEmails.add(account.name);
            }

            if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
                return possibleEmails.get(0);
            }
        }
        return null;

    }
}