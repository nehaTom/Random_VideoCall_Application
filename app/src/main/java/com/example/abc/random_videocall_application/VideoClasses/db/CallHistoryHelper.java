package com.example.abc.random_videocall_application.VideoClasses.db;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class CallHistoryHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "history";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "callerName";
    public static final String CONTACTS_COLUMN_CallerId = "callerId";
    public static final String CONTACTS_COLUMN_Date = "dateAndTime";
    public static final String CONTACTS_COLUMN_Duration = "duration";
    public static final String CONTACTS_COLUMN_Type = "type";
    private HashMap hp;

    public CallHistoryHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table " + CONTACTS_TABLE_NAME + " ("
                + CONTACTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + CONTACTS_COLUMN_NAME + " text,"
                + CONTACTS_COLUMN_CallerId + " text,"
                + CONTACTS_COLUMN_Date + " text,"
                + CONTACTS_COLUMN_Duration + " text,"
                + CONTACTS_COLUMN_Type + " text"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertEntry (String callerName, String callerId, String dateValue,String duration,String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, callerName);
        contentValues.put(CONTACTS_COLUMN_CallerId, callerId);
        contentValues.put(CONTACTS_COLUMN_Date, dateValue);
        contentValues.put(CONTACTS_COLUMN_Duration, duration);
        contentValues.put(CONTACTS_COLUMN_Type,type);
        db.insert(CONTACTS_TABLE_NAME , null, contentValues);
        int numberOfRows = this.numberOfRows();
        if(numberOfRows>100){
            ArrayList<String> list = getAllCotacts();
            db.delete(CONTACTS_TABLE_NAME, "id = ?",new String[] {list.get(0)});

        }
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (String id, String callerName, String callerId, String dateValue,String duration,String type  ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONTACTS_COLUMN_NAME, callerName);
        contentValues.put(CONTACTS_COLUMN_CallerId, callerId);
        contentValues.put(CONTACTS_COLUMN_Date, dateValue);
        contentValues.put(CONTACTS_COLUMN_Duration, duration);
        contentValues.put(CONTACTS_COLUMN_Type,type);
        db.update(CONTACTS_TABLE_NAME, contentValues, "id = ? ",new String[] {id} );
        return true;
    }

    public Integer deleteContact (String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(CONTACTS_TABLE_NAME,
                "id = ? ",
                new String[] { (id) });
    }

    public ArrayList<String> getAllCotacts() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" order by  id  ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_ID)));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> getAllHistory() {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONTACTS_TABLE_NAME+" order by  id  ASC", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String value = "";
            value = res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)) +"  ";
            value = value + res.getString(res.getColumnIndex(CONTACTS_COLUMN_Type))+"  ";
            value = value + res.getString(res.getColumnIndex(CONTACTS_COLUMN_Date));

            array_list.add(value);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}
