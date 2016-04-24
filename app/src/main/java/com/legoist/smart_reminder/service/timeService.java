package com.legoist.Smart_Reminder.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.MainActivity;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.notiActivity;

public class timeService extends Service{
	SQLiteDatabase db;
	DB_setting sdb;
	Intent alarmreceiver;
	ComponentName Widget;
	AppWidgetManager awm;
	RemoteViews views;
	String mProvider;
	public static int set;
	int alarm_cnt, max, min, i, j, dev_mode;
	String carry;
	
	@Override
	public IBinder onBind(Intent arg0) {return null;}
	@Override
	public void onCreate(){
		super.onCreate();
		Widget = new ComponentName(this,Widget.class);
		awm = AppWidgetManager.getInstance(this);
		views = new RemoteViews(getPackageName(), R.layout.widget); 
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
			NotificationManager mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			intent = new Intent(getBaseContext(), MainActivity.class);
			sdb = new DB_setting(timeService.this);
			if(set == 0){				
				db = sdb.getReadableDatabase();
				Cursor cursor = db.rawQuery("SELECT state, noti_carry FROM SettingDB;", null);
				cursor.moveToFirst();
				while(cursor.moveToNext()){
					set = cursor.getInt(0);
					carry =cursor.getString(1);
				}
							
				views.setImageViewResource(R.id.widget_btn, R.drawable.on);
				awm.updateAppWidget(Widget, views);
				
				db = sdb.getWritableDatabase();
				db.execSQL("UPDATE SettingDB SET state = 1;");
				set=1;
				Notification noti = new Notification(R.drawable.time, "매니저가 활성화 되었습니다.",System.currentTimeMillis());
				noti.flags |= Notification.FLAG_ONGOING_EVENT;
				PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
				noti.setLatestEventInfo(getBaseContext(), "Smart Reminder", "매니저가 당신의 소지품을 관찰 중 입니다.", content);
				mNotiManager.notify(notiActivity.noti_id, noti);
				
				AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				PendingIntent sender;
				
				intent = new Intent(getBaseContext(), com.legoist.Smart_Reminder.Service.AlarmReceiver.class);
				sender = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
				
				getAlarmLevel();
				Log.i("alarm_cnt", ""+alarm_cnt);
				if(alarm_cnt == 0) am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),dev_mode, sender);
				else am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),j, sender);
			}
			else if(set == 1){
				views.setImageViewResource(R.id.widget_btn, R.drawable.off);
				awm.updateAppWidget(Widget, views);
				
				db = sdb.getWritableDatabase();
				db.execSQL("UPDATE SettingDB SET state = 0;");
				set=0;
				stopSelf(startId);
				
				NotificationManager NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				NM.cancel(notiActivity.noti_id);
				
				//비활성화 Notification 잠깐 띄움
				Notification notioff = new Notification(R.drawable.time, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
				notioff.flags |= Notification.FLAG_AUTO_CANCEL;
				PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
				notioff.setLatestEventInfo(getBaseContext(), "Smart Reminder", "매니저 작동을 중단했습니다.", content);
				mNotiManager.notify(notiActivity.noti_id, notioff);
				
				AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
				PendingIntent sender;		
				intent = new Intent(getBaseContext(), com.legoist.Smart_Reminder.Service.AlarmReceiver.class);
				sender = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
				am.cancel(sender);
			}
		return START_REDELIVER_INTENT;
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
	public void getAlarmLevel(){		
		db = sdb.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT cnt FROM SettingDB;", null);
	    while(cursor.moveToNext()){
	    	alarm_cnt = cursor.getInt(0);
	    }        
	    switch(alarm_cnt){
	    case 0:
	    	dev_mode = 30*1000;
	    	break;
	    case 1:
	    	max = 59;
	    	min = 56;
	    	break;
	    case 2:
	    	max = 30;
	    	min = 25;
	    	break;
	    case 3:
	    	max = 20;
	    	min = 15;
	    	break;
	    case 4:
	    	max = 20;
	    	min = 15;
	    	break;
	    case 5:
	    	max = 20;
	    	min = 15;
	    	break;	    	
	    default:
	    	max = 12;
	    	min = 10;
	    	break;
	    }                
	    while(true){
	        i = (int)(Math.random()*max);
	        if(i >= min) break;  
	    }
	    j = ((i*60)*1000);
	}
}