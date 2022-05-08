package com.example.ezlife;

public class PC {
    final static String databaseName = "pc.db";
    final static String tableName = "pcs";
    final static String[] databaseTableNames = {"id", "name", "mac", "ip"};
    final static String[] databaseTableTypes = {"INTEGER PRIMARY KEY", "TEXT", "TEXT", "TEXT"};

    int id;
    String name;
    String mac;
    String ip;

    public PC(int id, String name, String mac, String ip) {
        this.id = id;
        this.name = name;
        this.mac = mac;
        this.ip = ip;
    }
}
