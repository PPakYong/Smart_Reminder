package com.legoist.Smart_Reminder.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB_Item extends SQLiteOpenHelper{
            public DB_Item(Context context){
                super(context, "InvenDB_Item.db", null, 1);
            }
            public void onCreate(SQLiteDatabase db){
                db.execSQL("CREATE TABLE ItemDB(_id integer primary key autoincrement, iname text primark key, icarry text, idate integer, iplace text);");                
            }         
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            	db.execSQL("DROP TABLE ItemDB;");
                onCreate(db);
            }
        }