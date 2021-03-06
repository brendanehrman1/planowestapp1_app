package com.example.planowestapp1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.TimeZone;

import static com.example.planowestapp1.MainActivity.PREF_NAME;

public class AddTimeActivity extends AppCompatActivity {

    private TextView actionDisplay;
    private TextView statusDisplay;
    private Button todayBtn;
    private Button tomorrowBtn;
    private Button inTwoDaysBtn;
    private Button previousBtn;
    private Button continueBtn;
    private TimePicker startTimePicker;
    private TimePicker durationPicker;
    private EditText descriptionInput;
    private View wrapper;

    private String displayName;
    private String day;
    private int hour;
    private int minute;
    private int duration;
    private String description;
    private String newDay;
    private int newHour;
    private int newMinute;
    private int newDuration;
    private String newDescription;
    private int page;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_time);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        loadTimeData();
        page = 1;
        durationPicker.setIs24HourView(true);
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field fieldHr = classForid.getField("hour");
            NumberPicker mHourPicker = (NumberPicker) durationPicker.findViewById(fieldHr.getInt(null));
            //mHourPicker.setWrapSelectorWheel(false);
            Field fieldMin = classForid.getField("minute");
            NumberPicker mMinutePicker = (NumberPicker) durationPicker.findViewById(fieldMin.getInt(null));
            mMinutePicker.setMinValue(0);
            mMinutePicker.setMaxValue(3);
            ArrayList<String> mDisplayedValuesMin = new ArrayList<String>();
            for (int i = 0; i < 60; i += 15) {
                mDisplayedValuesMin.add(String.format("%02d", i));
            }
            mMinutePicker.setDisplayedValues(mDisplayedValuesMin.toArray(new String[0]));
            //mMinutePicker.setWrapSelectorWheel(false);
            mMinutePicker = (NumberPicker) startTimePicker.findViewById(fieldMin.getInt(null));
            mMinutePicker.setMaxValue(3);
            mDisplayedValuesMin = new ArrayList<String>();
            for (int i = 0; i < 60; i += 15) {
                mDisplayedValuesMin.add(String.format("%02d", i));
            }
            mMinutePicker.setDisplayedValues(mDisplayedValuesMin.toArray(new String[0]));
            //mMinutePicker.setWrapSelectorWheel(false);

        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {

        }
        startTimePicker.setVisibility(View.GONE);
        durationPicker.setVisibility(View.GONE);
        descriptionInput.setVisibility(View.GONE);
        previousBtn.setVisibility(View.GONE);
        actionDisplay.setText("DAY");
        todayBtn.setBackgroundColor(Color.parseColor("#3A3742"));
        wrapper.getLayoutParams().height = 650;
        wrapper.requestLayout();
    }

    public void goToAccount(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AccountActivity.class));
    }

    public void goToTimes(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
    }

    public void goToCalendar(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, CalendarActivity.class));
    }

    public void goToFriends(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, FriendActivity.class));
    }

    public void goToAddTime(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, AddTimeActivity.class));
    }

    public void changePassword(View v) throws IOException {
        startActivity(new Intent(AddTimeActivity.this, ChangePasswordActivity.class));
    }

    public void loadTimeData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        displayName = sharedPreferences.getString("NAME", null);
        actionDisplay = (TextView) findViewById(R.id.actionDisplay);
        statusDisplay = (TextView) findViewById(R.id.statusDisplay);
        todayBtn = (Button) findViewById(R.id.todayBtn);
        tomorrowBtn = (Button) findViewById(R.id.tomorrowBtn);
        inTwoDaysBtn = (Button) findViewById(R.id.inTwoDaysBtn);
        previousBtn = (Button) findViewById(R.id.previousBtn);
        continueBtn = (Button) findViewById(R.id.continueBtn);
        startTimePicker = (TimePicker) findViewById(R.id.startTimePicker);
        durationPicker = (TimePicker) findViewById(R.id.durationPicker);
        descriptionInput = (EditText) findViewById(R.id.description);
        wrapper = (View) findViewById(R.id.wrapper);
        newDay = "Today";
        newHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + (Calendar.getInstance().get(Calendar.MINUTE) + 15) / 60;
        newMinute = Calendar.getInstance().get(Calendar.MINUTE) / 15 * 15 + 15;
        newDuration = 60;
        newDescription = "";
    }

    public void setDay(View v) {
        if (v.getId() == R.id.todayBtn) {
            newDay = "Today";
            todayBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            tomorrowBtn.setBackgroundColor(Color.parseColor("#F7567C"));
            inTwoDaysBtn.setBackgroundColor(Color.parseColor("#F7567C"));
        } else if (v.getId() == R.id.tomorrowBtn) {
            newDay = "Tomorrow";
            tomorrowBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            todayBtn.setBackgroundColor(Color.parseColor("#F7567C"));
            inTwoDaysBtn.setBackgroundColor(Color.parseColor("#F7567C"));
        } else {
            newDay = "In Two Days";
            inTwoDaysBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            todayBtn.setBackgroundColor(Color.parseColor("#F7567C"));
            tomorrowBtn.setBackgroundColor(Color.parseColor("#F7567C"));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void next(View v) throws IOException {
        if (page == 1) {
            todayBtn.setVisibility(View.GONE);
            tomorrowBtn.setVisibility(View.GONE);
            inTwoDaysBtn.setVisibility(View.GONE);
            startTimePicker.setVisibility(View.VISIBLE);
            previousBtn.setVisibility(View.VISIBLE);
            startTimePicker.setHour(newHour);
            startTimePicker.setMinute(newMinute / 15);
            actionDisplay.setText("START TIME");
        } else if (page == 2) {
            newHour = startTimePicker.getHour();
            newMinute = startTimePicker.getMinute() * 15;
            startTimePicker.setVisibility(View.GONE);
            durationPicker.setVisibility(View.VISIBLE);
            durationPicker.setHour(newDuration / 60);
            durationPicker.setMinute((newDuration % 60) / 15);
            actionDisplay.setText("DURATION");
        } else if (page == 3) {
            newDuration = durationPicker.getHour() * 60 + durationPicker.getMinute() * 15;
            durationPicker.setVisibility(View.GONE);
            descriptionInput.setVisibility(View.VISIBLE);
            descriptionInput.setText(description);
            actionDisplay.setText("DESCRIPTION");
            continueBtn.setText("FINISH");
        } else {
            newDescription = descriptionInput.getText().toString();
            int offsetHours = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000;
            int changedDay = 0;
            if (newDay.equals("Tomorrow"))
                changedDay = 1;
            else if (newDay.equals("In Two Days"))
                changedDay = 2;
            int hour = newHour;
            hour -= offsetHours;
            if (hour > 24) {
                changedDay++;
                hour -= 24;
            } else if (hour < 0) {
                changedDay--;
                hour += 24;
            }

            int minute = newMinute;
            int duration = newDuration;
            String description = newDescription;
            if (description.length() == 0) {
                wrapper.getLayoutParams().height = 700;
                wrapper.requestLayout();
                statusDisplay.setText("Sorry, you must provide a description to set a time. Please try again.");
            } else if (changedDay == 0 && (hour < Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY) || hour == Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.HOUR_OF_DAY) && minute < Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE))) {
                wrapper.getLayoutParams().height = 700;
                wrapper.requestLayout();
                statusDisplay.setText("Sorry, your event must be held after the current time. Please try again.");
            } else {
                addTimeData(changedDay + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1, hour, minute, duration, description);
                //JSONObject json = new JSONObject(info);
                //status.setText(json.get("status") + " " + hour + " " + minute + " " + (Integer.toString(Integer.parseInt(dayStr) + Calendar.getInstance().get(Calendar.DAY_OF_YEAR) - 1)));
                startActivity(new Intent(AddTimeActivity.this, TimesActivity.class));
            }
        }
        if (page < 4)
            page++;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void previous(View v) {
        wrapper.getLayoutParams().height = 650;
        wrapper.requestLayout();
        statusDisplay.setText("");
        if (page == 2) {
            newHour = startTimePicker.getHour();
            newMinute = startTimePicker.getMinute() * 15;
            startTimePicker.setVisibility(View.GONE);
            previousBtn.setVisibility(View.GONE);
            todayBtn.setVisibility(View.VISIBLE);
            tomorrowBtn.setVisibility(View.VISIBLE);
            inTwoDaysBtn.setVisibility(View.VISIBLE);
            if (newDay.equals("Today"))
                todayBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            else if (newDay.equals("Tomorrow"))
                tomorrowBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            else
                inTwoDaysBtn.setBackgroundColor(Color.parseColor("#3A3742"));
            actionDisplay.setText("DAY");
        } else if (page == 3) {
            newDuration = durationPicker.getHour() * 60 + durationPicker.getMinute() * 15;
            durationPicker.setVisibility(View.GONE);
            startTimePicker.setVisibility(View.VISIBLE);
            startTimePicker.setHour(newHour);
            startTimePicker.setMinute(newMinute / 15);
            actionDisplay.setText("START TIME");
        } else {
            newDescription = descriptionInput.getText().toString();
            descriptionInput.setVisibility(View.GONE);
            durationPicker.setVisibility(View.VISIBLE);
            durationPicker.setHour(newDuration / 60);
            durationPicker.setMinute((newDuration % 60) / 15);
            actionDisplay.setText("DURATION");
            continueBtn.setText("CONTINUE");
        }
        page--;
    }

    public String addTimeData(int date, int hour, int minute, int duration, String description) throws IOException {

        description = description.replaceAll(" ", "%20");
        description = description.replaceAll("&", "%26");
        description = description.replaceAll("#", "%23");

        String url = "http://ec2-3-23-128-64.us-east-2.compute.amazonaws.com:8080/times/add?userName=" + displayName + "&date=" + date + "&hour=" + hour + "&minute=" + minute + "&duration=" + duration + "&description=" + description;

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        if (responseCode == 200) {
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            while (scanner.hasNextLine()) {
                response += scanner.nextLine();
                response += "\n";
            }
            scanner.close();

            return response;
        }

        // an error happened
        return null;
    }

}