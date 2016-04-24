package com.legoist.Smart_Reminder.Service;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.R;

public class Widget extends AppWidgetProvider {
	DB_setting sdb;
	SQLiteDatabase db;
	RemoteViews views;
	int set;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		views = new RemoteViews(context.getPackageName(), R.layout.widget);
	
		sdb = new DB_setting(context);
		db = sdb.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT state FROM SettingDB;", null);
		cursor.moveToFirst();
		while(cursor.moveToNext()){
			set = cursor.getInt(0);
		}
		
		Intent ServiceIntent = new Intent(context, com.legoist.Smart_Reminder.Service.timeService.class);
		PendingIntent PendingService = PendingIntent.getService(context, 0, ServiceIntent, 0);
		
		views.setOnClickPendingIntent(R.id.widget_btn, PendingService);
		if(set == 1){
			views.setImageViewResource(R.id.widget_btn, R.drawable.on);
		}
		appWidgetManager.updateAppWidget(appWidgetIds, views);
			
	}
}