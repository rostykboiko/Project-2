package com.example.rostykboiko.todoapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rostykboiko.todoapp.adapter.DateAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditorActivity extends AppCompatActivity {
    private CalendarDB mydb;
    private EditText name;
    private EditText content;
    private TextView eventTimeStartTxt;
    private TextView eventTimeEndTxt;
    private TextView eventDateStartTxt;
    private TextView eventDateEndTxt;
    private Calendar mcurrentDate;

    private int id_To_Update = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.txtname);
        content = (EditText)findViewById(R.id.txtDescription);
        eventTimeStartTxt = (TextView) findViewById(R.id.txtTimeStart);
        eventTimeEndTxt = (TextView) findViewById(R.id.txtTimeEnd);
        eventDateStartTxt = (TextView) findViewById(R.id.txtDateStart);
        eventDateEndTxt = (TextView) findViewById(R.id.txtDateEnd);
        ImageView backButton = (ImageView) findViewById(R.id.backBtn);

        mcurrentDate = Calendar.getInstance();
        eventDateStartTxt.setOnClickListener(Global_OnClickListener);
        eventTimeStartTxt.setOnClickListener(Global_OnClickListener);
        eventTimeEndTxt.setOnClickListener(Global_OnClickListener);
        eventDateEndTxt.setOnClickListener(Global_OnClickListener);
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
                eventTimeStartTxt.setText(rs.getString(rs.getColumnIndex(CalendarDB.dataStart)));
                eventTimeEndTxt.setText(rs.getString(rs.getColumnIndex(CalendarDB.dataEnd)));
                if (!rs.isClosed()) {
                    rs.close();
                }
            }
        }
    }

    private void dateInit(int day,int dayOfWeek, int month, int year){
        // DateFormat : Ср,10 серпень 2016

        eventDateStartTxt.setText(DateAdapter.dayOfWeekString(this, dayOfWeek) + "," + day + " " + DateAdapter.monthOfYear(this, month) + " " + year);
        eventDateEndTxt.setText(DateAdapter.dayOfWeekString(this, dayOfWeek) + "," + day + " " + DateAdapter.monthOfYear(this, month) + " " + year);

        if (mcurrentDate.get(Calendar.HOUR_OF_DAY)<12){
            eventTimeStartTxt.setText("14:00");
            eventTimeEndTxt.setText("15:00");
        } else if(mcurrentDate.get(Calendar.HOUR_OF_DAY)<17){
            eventTimeStartTxt.setText("18:00");
            eventTimeEndTxt.setText("19:00");
        } else {
            eventTimeStartTxt.setText("10:00");
            eventTimeEndTxt.setText("11:00");
        }

    }

    private final View.OnClickListener Global_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            final SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");

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
                            Date date = new Date(year, month, day-1);
                            String dayOfWeek = simpledateformat.format(date);

                            eventDateStartTxt.setText(dayOfWeek + " " + day + ", " + DateAdapter.monthOfYear(EditorActivity.this, month) + " " + year);
                            eventDateEndTxt.setText(dayOfWeek + " " + day + ", " +  DateAdapter.monthOfYear(EditorActivity.this, month) + " " + year);
                        }
                    }, year, month, day);
                    mDatePicker.show();
                    break;
                case R.id.txtTimeStart:
                    mTimePicker = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if(selectedMinute < 10 ){
                                eventTimeStartTxt.setText( selectedHour + ":0" + selectedMinute);
                                eventTimeEndTxt.setText(( selectedHour + 1 + ":0" + selectedMinute));
                            }
                            else{
                                eventTimeStartTxt.setText( selectedHour + ":" + selectedMinute);
                                eventTimeEndTxt.setText(( selectedHour + 1 + ":" + selectedMinute));
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

                            eventDateEndTxt.setText(dayOfWeek + " " + day + ", " +  DateAdapter.monthOfYear(EditorActivity.this, month) + " " + year);
                        }
                    }, year, month, day);
                    mDatePicker.show();
                    break;
                case R.id.txtTimeEnd:
                    mTimePicker = new TimePickerDialog(EditorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            if(selectedMinute < 10 ){
                                eventTimeEndTxt.setText(( selectedHour + ":0" + selectedMinute));
                            }
                            else{
                                eventTimeEndTxt.setText(( selectedHour + ":" + selectedMinute));
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
            getMenuInflater().inflate(R.menu.display_menu, menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        String timeStringStart;
        String timeStringEnd;
        Toast toast;

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
            // TODO Увесь день(дія) - з датою тільки початку
            case R.id.Save:
                Bundle extras = getIntent().getExtras();
                timeStringStart = eventTimeStartTxt.getText().toString();
                timeStringEnd = eventTimeEndTxt.getText().toString();
                if (extras != null) {
                    int Value = extras.getInt("id");
                    if (Value > 0) {
                        if ( name.getText().toString().trim().equals("")) {
                            toast = Toast.makeText(getApplicationContext(),  "Please set title of event", Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            if (mydb.updateNotes(id_To_Update, name.getText().toString(),
                                    timeStringStart, timeStringEnd, content.getText().toString(), ""))
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
                            if (mydb.insertNotes(name.getText().toString(),timeStringStart, timeStringEnd,
                                    content.getText().toString(), "")) {
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

