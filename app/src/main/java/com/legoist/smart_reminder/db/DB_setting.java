package com.legoist.Smart_Reminder.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_setting extends SQLiteOpenHelper{
            public DB_setting(Context context){
                super(context, "InvenDB_setting.db", null, 1);
            }
            public void onCreate(SQLiteDatabase db){
                db.execSQL("CREATE TABLE SettingDB(cnt integer, notitype integer, state integer, noti_carry, noti_carry_num text, first);");
                db.execSQL("INSERT INTO SettingDB(cnt, notitype, state, noti_carry, noti_carry_num, first) VALUES (4,1,0,'전체',-1,0);");
            }         
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            	db.execSQL("DROP TABLE SettingDB;");
                onCreate(db);
            }
        }