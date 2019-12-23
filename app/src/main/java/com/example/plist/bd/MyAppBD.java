package com.example.plist.bd;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.plist.Fotos;


@Database(entities = {Fotos.class},version = 1)
public abstract class MyAppBD extends RoomDatabase {
    public abstract MyDao myDao();
}
