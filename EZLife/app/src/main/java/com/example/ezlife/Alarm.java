package com.example.ezlife;

public class Alarm {
    final static String databaseName = "alarm.db";
    final static String tableName = "alarms";
    final static String[] databaseTableNames = {"id", "alarmId", "time", "days", "isOn", "isVibrationOn", "alarmSound", "alarmTask"};
    final static String[] databaseTableTypes = {"INTEGER PRIMARY KEY", "TEXT", "INTEGER", "STRING", "TEXT", "TEXT", "TEXT", "TEXT"};

    int id;
    String alarmId;
    int time;
    String days;
    String isOn;
    String isVibrationOn;
    String alarmSound;
    String alarmTask;

    public Alarm(int id, String alarmId, int time, String days, String isOn, String isVibrationOn, String alarmSound, String alarmTask) {
        this.id = id;
        this.alarmId = alarmId;
        this.time = time;
        this.days = days;
        this.isOn = isOn;
        this.isVibrationOn = isVibrationOn;
        this.alarmSound = alarmSound;
        this.alarmTask = alarmTask;
    }

}
