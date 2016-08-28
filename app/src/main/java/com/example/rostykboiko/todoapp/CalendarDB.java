package com.example.rostykboiko.todoapp;

import android.content.ContentValues;
        import android.content.Context;
        import android.database.Cursor;
        import android.database.DatabaseUtils;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

class CalendarDB extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public HashMap hp;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'");

    private static final String mycalendar = "mycalendar";
    private static final String dbname = "cal1.db";
    static final String _id = "_id";
    static final String name = "name";
    static final String dtstart = "dtstart";
    static final String dtend = "dtend";
    static final String dataStart = sdf.format(new Date());
    static final String dataEnd = sdf.format(new Date());
    static final String description = "description";
    static final String attachment = "attachment";


    CalendarDB(Context context){
        super(context,dbname,null,1);

    }

    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table " + mycalendar
                + "(_id integer primary key, dtstart text, dtend text, name text, description text, attachment text)");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + mycalendar);
        onCreate(db);
    }


    Cursor fetchAll() {
        db = this.getReadableDatabase();
        Cursor mCursor = db.query(mycalendar, new String[]
                { "_id", "dtstart", "dtend", "name", "description", "attachment" }
                , null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    boolean insertNotes(String name, String dtstart, String dtend, String description, String attachment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("dtstart", dtstart);
        contentValues.put("dtend", dtend);
        contentValues.put("description", description);
        contentValues.put("attachment", attachment);
        db.insert(mycalendar, null, contentValues);
        return true;
    }

    Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor z = db.rawQuery("select * from " + mycalendar + " where _id=" + id
                + "", null);
        return z;
    }
    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, mycalendar);
        return numRows;
    }
    boolean updateNotes(Integer id, String name, String dtstart, String dtend, String description, String attachment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("dtstart", dtstart);
        contentValues.put("dtend", dtend);
        contentValues.put("description", description);
        contentValues.put("attachment", attachment);
        db.update(mycalendar, contentValues, "_id = ? ",
                new String[] { Integer.toString(id) });
        return true;
    }
    Integer deleteNotes(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(mycalendar, "_id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList getAll() {
        ArrayList array_list = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + mycalendar, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex("_id")));
            array_list.add(res.getString(res.getColumnIndex(name)));
            array_list.add(res.getString(res.getColumnIndex(dtstart)));
            array_list.add(res.getString(res.getColumnIndex(dtend)));
            array_list.add(res.getString(res.getColumnIndex(description)));
            array_list.add(res.getString(res.getColumnIndex(attachment)));

            res.moveToNext();
        }
        return array_list;
    }

}