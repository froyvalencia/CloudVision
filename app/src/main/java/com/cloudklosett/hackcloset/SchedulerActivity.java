package com.cloudklosett.hackcloset;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Set;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class SchedulerActivity extends AppCompatActivity {

    public CaldroidFragment caldroidFragment;
    CalendarContentResolver contentResolver;
    Hashtable<String, CalendarContentResolver.EventField> CCEvents;
    public static final int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 12;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 13;

    public static String PACKAGE_NAME;
    ColorDrawable blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduler);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    MY_PERMISSIONS_REQUEST_WRITE_CALENDAR);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR},
                    MY_PERMISSIONS_REQUEST_READ_CALENDAR);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                        == PackageManager.PERMISSION_GRANTED) {

            contentResolver = new CalendarContentResolver(this);
            //calendar = (CalendarView) findViewById(R.id.calendar);

            caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();

            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            caldroidFragment.setArguments(args);
            caldroidFragment.setCaldroidListener(listener);

            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            t.replace(R.id.calendar1, caldroidFragment);
            t.commit();

            String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
            if(month.length() == 1){ month = '0' + month; }
            String year = String.valueOf(cal.get(Calendar.YEAR));
            CCEvents = contentResolver.getCalendar(month, year);
            blue = new ColorDrawable(ContextCompat.getColor(this, R.color.caldroid_sky_blue));
            for (String key : CCEvents.keySet()) {
                CalendarContentResolver.EventField e = CCEvents.get(key);
                Date d = new GregorianCalendar (
                        Integer.parseInt(e.getYear()),
                        Integer.parseInt(e.getMonth()) - 1,
                        Integer.parseInt(e.getDay())).getTime();

                caldroidFragment.setBackgroundDrawableForDate(blue, d);
            }
            caldroidFragment.refreshView();
            Log.d("Calendar Info: ", CCEvents.toString());
        }
    }

    public final CaldroidListener listener = new CaldroidListener() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");

        @Override
        public void onSelectDate(Date date, View view) {

            //Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_LONG).show();
            for (String key : CCEvents.keySet()) {
                if (CCEvents.get(key).getDay().compareTo(formatter.format(date).substring(0, 2)) == 0) {
                    Toast.makeText(getApplicationContext(), CCEvents.get(key).getDescription(), Toast.LENGTH_LONG).show();
                    //TODO: add logic to launch the closet to view outfit
                    return;
                }
            }
            //TODO: add logic to launch empty closet
            CalendarContentResolver.EventField e = contentResolver.addEvent(date);
            if(e != null){ CCEvents.put(e.getTitle(), e); }
            caldroidFragment.setBackgroundDrawableForDate(blue, date);
            caldroidFragment.refreshView();
        }

        @Override
        public void onChangeMonth(int month, int year) {
            String text = "month: " + month + " year: " + year;
            Toast.makeText(getApplicationContext(), text,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLongClickDate(Date date, View view) {
            Toast.makeText(getApplicationContext(),
                    "Long click " + formatter.format(date),
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCaldroidViewCreated() {
            Toast.makeText(getApplicationContext(),
                    "Caldroid view is created",
                    Toast.LENGTH_SHORT).show();
        }
    };

}