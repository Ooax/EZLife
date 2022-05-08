package com.example.ezlife;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class PCDatabase extends SQLiteOpenHelper {
    public PCDatabase(Context context) {
        super(context, PC.databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createCommand = "create table " + PC.tableName + "(";
        for (int i = 0; i < PC.databaseTableNames.length; i++) {
            String columnName = PC.databaseTableNames[i];
            String columnType = PC.databaseTableTypes[i];
            createCommand += columnName + " " + columnType + ", ";
        }
        createCommand = createCommand.substring(0, createCommand.length() - 2);
        createCommand += ")";
        sqLiteDatabase.execSQL(createCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void addRecord(PC pc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = pcValues(pc);
        db.insertOrThrow(PC.tableName, null, values);
    }

    public boolean removeRecord(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = { Integer.toString(id) };
        return db.delete(PC.tableName, "ID=?", whereArgs) > 0;
    }

    public boolean changeRecord(int id, PC pc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = pcValues(pc);
        String[] whereArgs = { Integer.toString(id) };
        return db.update(PC.tableName, values, "ID=?", whereArgs) > 0;
    }

    private PC pcCursor(Cursor cursor) {
        PC pc = new PC(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
        return pc;
    }

    public PC[] getAllRecords() {
        return getAllRecords(null);
    }

    public PC[] getAllRecords(String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(PC.tableName, PC.databaseTableNames, null, null, null, null, orderBy);
        ArrayList<PC> pcList = new ArrayList<PC>();
        while (cursor.moveToNext()) {
            pcList.add(pcCursor(cursor));
        }
        cursor.close();
        PC[] array = new PC[pcList.size()];
        pcList.toArray(array);
        return array;
    }

    private ContentValues pcValues(PC pc) {
        ContentValues values = new ContentValues();
        values.put(PC.databaseTableNames[0], pc.id);
        values.put(PC.databaseTableNames[1], pc.name);
        values.put(PC.databaseTableNames[2], pc.mac);
        values.put(PC.databaseTableNames[3], pc.ip);
        return values;
    }
}
