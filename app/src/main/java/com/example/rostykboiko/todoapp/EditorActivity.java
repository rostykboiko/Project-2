package com.example.rostykboiko.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;

public class EditorActivity extends AppCompatActivity {
    private CalendarDB mydb;
    private EditText name;
    private EditText content;
    private TextView eventTimeStart;
    private TextView eventTimeEnd;
    private TextView eventDateStart;
    private TextView eventDateEnd;
    private ImageView backButton;
    private Calendar mcurrentDate;
    private String timeStringStart;
    private String timeStringEnd;

    Bundle extras;
    Snackbar snackbar;
    private String date;
    private long dtstart;//
    private long dtend;
    private int id_To_Update = 0;


    private Toast toast;

    private static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;
    private static final Uri CAL_URI = CalendarContract.Calendars.CONTENT_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.txtname);
        content = (EditText)findViewById(R.id.txtDescription);
        eventTimeStart = (TextView) findViewById(R.id.txtTimeStart);
        eventTimeEnd = (TextView) findViewById(R.id.txtTimeEnd);
        eventDateStart = (TextView) findViewById(R.id.txtDateStart);
        eventDateEnd = (TextView) findViewById(R.id.txtDateEnd);
        backButton = (ImageView) findViewById(R.id.backBtn);

        mcurrentDate = Calendar.getInstance();
        eventDateStart.setOnClickListener(Global_OnClickListener);
        eventTimeStart.setOnClickListener(Global_OnClickListener);
        eventTimeEnd.setOnClickListener(Global_OnClickListener);
        eventDateEnd.setOnClickListener(Global_OnClickListener);
        backButton.setOnClickListener(Global_OnClickListener);

        name.setSelectAllOnFocus(true);

        int dayOfWeek = mcurrentDate.get(Calendar.DAY_OF_WEEK);
        int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
        int month = mcurrentDate.get(Calendar.MONTH);
        int year = mcurrentDate.get(Calendar.YEAR);

        dateInit(day,dayOfWeek,month,year);

        mydb = new CalendarDB(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0) {
                Cursor rs = mydb.getData(Value);
                id_To_Update = Value;
                rs.moveToFirst();
                name.setText(rs.getString(rs.getColumnIndex(CalendarDB.name)));
                content.setText(rs.getString(rs.getColumnIndex(CalendarDB.description)));
                eventTimeStart.setText(rs.getString(rs.getColumnIndex(CalendarDB.dtstart)));
                eventTimeEnd.setText(rs.getString(rs.getColumnIndex(CalendarDB.dtend)));
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
    }

    private String monthOfYear(int month){
        String monthOfYear = "";
        if (month + 1 == 1) monthOfYear = getString(R.string.January);
        if (month + 1 == 2) monthOfYear = getString(R.string.February);
        if (month + 1 == 3) monthOfYear = getString(R.string.March);
        if (month + 1 == 4) monthOfYear = getString(R.string.April);
        if (month + 1 == 5) monthOfYear = getString(R.string.May);
        if (month + 1 == 6) monthOfYear = getString(R.string.June);
        if (month + 1 == 7) monthOfYear = getString(R.string.July);
        if (month + 1 == 8) monthOfYear = getString(R.string.August);
        if (month + 1 == 9) monthOfYear = getString(R.string.September);
        if (month + 1 == 10) monthOfYear = getString(R.string.October);
        if (month + 1 == 11) monthOfYear = getString(R.string.November);
        if (month + 1 == 12) monthOfYear = getString(R.string.December);

        return monthOfYear;
    }

    private void dateInit(int day,int dayOfWeek,int month, int year){
        // DateFormat : Ср,10 серпень 2016
        String dayWeek = null;
        String monthOfYear = null;

        if(dayOfWeek == 1) dayWeek = getString(R.string.sun);
        if(dayOfWeek == 2) dayWeek = getString(R.string.mon);
        if(dayOfWeek == 3) dayWeek = getString(R.string.tue);
        if(dayOfWeek == 4) dayWeek = getString(R.string.wed);
        if(dayOfWeek == 5) dayWeek = getString(R.string.thu);
        if(dayOfWeek == 6) dayWeek = getString(R.string.fri);
        if(dayOfWeek == 7) dayWeek = getString(R.string.sat);



        eventDateStart.setText(dayWeek + "," + day + " " + monthOfYear + " " + year);
        eventDateEnd.setText(dayWeek + "," + day + " " + monthOfYear + " " + year);

        if (mcurrentDate.get(Calendar.HOUR_OF_DAY)<12){
            eventTimeStart.setText("14:00");
            eventTimeEnd.setText("15:00");
        } else if(mcurrentDate.get(Calendar.HOUR_OF_DAY)<17){
            eventTimeStart.setText("18:00");
            eventTimeEnd.setText("19:00");
        } else {
            eventTimeStart.setText("10:00");
            eventTimeEnd.setText("11:00");
        }

    }

    private final View.OnClickListener Global_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            int hour = mcurrentDate.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentDate.get(Calendar.MINUTE);
            int year = mcurrentDate.get(Calendar.YEAR);
            int month = mcurrentDate.get(Calendar.MONTH);
            int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);

            final DatePickerDialog mDatePicker;
            TimePickerDialog mTimePicker;

            switch(v.getId()) {
                case R.id.txtDateStart:
                    mDatePicker = new DatePickerDialog(EditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                            Date date = new Date(year, month, day-1);
                            String dayOfWeek = simpledateformat.format(date);

                            eventDateStart.setText(dayOfWeek + " " + day + ", " +  monthOfYear(month) + " " + year);
                            eventDateEnd.setText(dayOfWeek + " " + day + ", " +  monthOfYear(month) + " " + year);
                        }
                    }, year, month, day);
                    mDatePicker.show();
                    break;
                case R.id.txtTimeStart:
                    mTimePicker = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if(selectedMinute < 10 ){
                                eventTimeStart.setText( selectedHour + ":0" + selectedMinute);
                                eventTimeEnd.setText(( selectedHour + 1 + ":0" + selectedMinute));
                            }
                            else{
                                eventTimeStart.setText( selectedHour + ":" + selectedMinute);
                                eventTimeEnd.setText(( selectedHour + 1 + ":" + selectedMinute));
                            }
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(null);
                    mTimePicker.updateTime(hour,minute);
                    mTimePicker.show();
                    break;
                case R.id.txtDateEnd:
                    mDatePicker = new DatePickerDialog(EditorActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                            Date date = new Date(year, month, day-1);
                            String dayOfWeek = simpledateformat.format(date);

                            eventDateEnd.setText(dayOfWeek + " " + day + ", " +  monthOfYear(month) + " " + year);
                        }
                    }, year, month, day);
                    mDatePicker.show();
                    break;
                case R.id.txtTimeEnd:
                    mTimePicker = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if(selectedMinute < 10 ){
                                eventTimeEnd.setText(( selectedHour + ":0" + selectedMinute));
                            }
                            else{
                                eventTimeEnd.setText(( selectedHour + ":" + selectedMinute));
                            }
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle(null);
                    mTimePicker.updateTime(hour,minute);
                    mTimePicker.show();
                    break;
                case R.id.backBtn:
                    onBackPressed();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            getMenuInflater().inflate(R.menu.display_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.Delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog));
                builder.setMessage(R.string.DeleteNote)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        mydb.deleteNotes(id_To_Update);
                                        Toast.makeText(EditorActivity.this, "Deleted Successfully",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(
                                                getApplicationContext(),
                                                MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {}
                                });
                AlertDialog d = builder.create();
                d.setTitle("Are you sure");
                d.show();
                return true;
            //  Переписати збереження
            //  з пустими полями
            //  Увесь день(дія) - з датою тільки початку
            case R.id.Save:
                Bundle extras = getIntent().getExtras();
                timeStringStart = eventTimeStart.getText().toString();
                timeStringEnd = eventTimeEnd.getText().toString();
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            toast = Toast.makeText(getApplicationContext(),  "Please fill in name of the note", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText().toString(),
                                    timeStringStart, timeStringEnd, content.getText().toString()))
                            {
                                toast = Toast.makeText(getApplicationContext(), "Your note Updated Successfully!", Toast.LENGTH_LONG);
                                toast.show();
                                onBackPressed();
                            } else {
                                toast = Toast.makeText(getApplicationContext(), "There's an error. That's all I can tell. Sorry!", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    } else {
                        if (content.getText().toString().trim().equals("")
                                || name.getText().toString().trim().equals("")) {
                            toast = Toast.makeText(getApplicationContext(), "Please fill in name of the note", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            if (mydb.insertNotes(name.getText().toString(),timeStringStart, timeStringEnd,
                                    content.getText().toString())) {
                                toast = Toast.makeText(getApplicationContext(), "Added Successfully.", Toast.LENGTH_LONG);
                                toast.show();
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                toast = Toast.makeText(getApplicationContext(), "Unfortunately Task Failed.", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(
                getApplicationContext(),
                MainActivity.class);
        startActivity(intent);
        finish();
    }
}

