package com.example.ezlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class AlarmsDatabase extends SQLiteOpenHelper {
    public AlarmsDatabase(Context context) {
        super(context, Alarm.databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createCommand = "create table " + Alarm.tableName + "(";
        for (int i = 0; i < Alarm.databaseTableNames.length; i++) {
            String columnName = Alarm.databaseTableNames[i];
            String columnType = Alarm.databaseTableTypes[i];
            createCommand += columnName + " " + columnType + ", ";
        }
        createCommand = createCommand.substring(0, createCommand.length() - 2);
        createCommand += ")";
        sqLiteDatabase.execSQL(createCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addRecord(Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = alarmValues(alarm);
        db.insertOrThrow(Alarm.tableName, null, values);
    }

    public boolean removeRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { Integer.toString(id) };
        return db.delete(Alarm.tableName, "ID=?", whereArgs) > 0;
    }

    public boolean changeRecord(int id, Alarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = alarmValues(alarm);
        String[] whereArgs = { Integer.toString(id) };
        return db.update(Alarm.tableName, values, "ID=?", whereArgs) > 0;
    }

    private Alarm alarmCursor(Cursor cursor) {
        Alarm alarm = new Alarm(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        return alarm;
    }

    public Alarm[] getAllRecords() {
        return getAllRecords(null);
    }

    public Alarm[] getAllRecords(String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Alarm.tableName, Alarm.databaseTableNames, null, null, null, null, orderBy);
        ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
        while (cursor.moveToNext()) {
            alarmList.add(alarmCursor(cursor));
        }
        cursor.close();
        Alarm[] array = new Alarm[alarmList.size()];
        alarmList.toArray(array);
        return array;
    }

    private ContentValues alarmValues(Alarm alarm) {
        ContentValues values = new ContentValues();
        values.put(Alarm.databaseTableNames[0], alarm.id);
        values.put(Alarm.databaseTableNames[1], alarm.alarmId);
        values.put(Alarm.databaseTableNames[2], alarm.time);
        values.put(Alarm.databaseTableNames[3], alarm.days);
        values.put(Alarm.databaseTableNames[4], alarm.isOn);
        values.put(Alarm.databaseTableNames[5], alarm.isVibrationOn);
        values.put(Alarm.databaseTableNames[6], alarm.alarmSound);
        values.put(Alarm.databaseTableNames[7], alarm.alarmTask);
        return values;
    }
}
