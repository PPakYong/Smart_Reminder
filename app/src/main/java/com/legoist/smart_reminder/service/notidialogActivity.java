package com.legoist.Smart_Reminder.Service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.notiActivity;

import java.util.ArrayList;

public class notidialogActivity extends Activity{
	Intent intent;
	String getNotiCarry;
	DB_Item im;
	DB_setting sdb;
	SQLiteDatabase db;
	ArrayList<String> iNameList;
	ArrayAdapter<String> Adapter;
	ListView list;
	String[] Oriniginal_iNameList;
	//Notification
	NotificationManager mNotiManager;
	static final int noti_id=1;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_notidialog);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
        TextView title = (TextView)findViewById(R.id.custom_title);
        title.setText("무작위 알람 알림");      

//		LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
//		Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        
//		String latitude = String.valueOf(location.getLatitude()).substring(0,5);
//		String longitude = String.valueOf(location.getLongitude()).substring(0,6);
		
		AdView adview = (AdView)findViewById(R.id.adView);
	    adview.loadAd(new AdRequest());
      		
        getNotiCarry="";
        
        //NotificationManager
      	mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        
        iNameList = new ArrayList<String>();
        
        sdb = new DB_setting(this);
        db = sdb.getReadableDatabase();

        Cursor getStr = db.rawQuery("SELECT noti_carry FROM SettingDB;", null);       
    		while(getStr.moveToNext()){
    			getNotiCarry = getStr.getString(0).toString();
    		}
    	im = new DB_Item(this);
        db = im.getReadableDatabase();
        
        if(getNotiCarry.equals("전체") || getNotiCarry == "전체"){
        	Log.i("run", "run");
        	 Cursor cursor = db.rawQuery("SELECT iname FROM ItemDB;", null);
             while(cursor.moveToNext()){
             	iNameList.add(cursor.getString(0).toString());
             }
        }
        else{
        	 Cursor cursor = db.rawQuery("SELECT iname FROM ItemDB WHERE icarry = '"+getNotiCarry+"';", null);
             while(cursor.moveToNext()){
             	iNameList.add(cursor.getString(0).toString());
             }        
        } 
        
        Oriniginal_iNameList = iNameList.toArray(new String[iNameList.size()]);
        
        list = (ListView)findViewById(R.id.notidialog_list);
        Adapter = new ArrayAdapter<String>(notidialogActivity.this, android.R.layout.simple_list_item_1, iNameList);
        list.setAdapter(Adapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(mItemClickListener);
        
	}
	
	AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			if(iNameList.get(arg2) == Oriniginal_iNameList[arg2])
				iNameList.set(arg2, iNameList.get(arg2) + " - 확인완료");
			list.clearChoices();
			Adapter.notifyDataSetChanged();
		}
		
	};
	
	
	public void notidialogClick(View v){
		switch(v.getId()){
		case R.id.notidialog_ok_btn:
			finish();
			break;
		case R.id.notidialog_stop_btn:	
			Intent stopServiceIntent = new Intent(notidialogActivity.this, com.legoist.Smart_Reminder.Service.timeService.class);
			stopService(stopServiceIntent);
			//알람 상태 변경
			db = sdb.getWritableDatabase();
			db.execSQL("UPDATE SettingDB SET state = 0;");			
			
			//알람 종료
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
			PendingIntent sender;		
			intent = new Intent(notidialogActivity.this, com.legoist.Smart_Reminder.Service.AlarmReceiver.class);
			sender = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
			am.cancel(sender);
			
			//활성화 Notification 종료
			NotificationManager NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			NM.cancel(notiActivity.noti_id);
			
			//비활성화 Notification 잠깐 띄움
			Notification notioff = new Notification(R.drawable.time, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
			notioff.flags |= Notification.FLAG_AUTO_CANCEL;
			PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
			notioff.setLatestEventInfo(notidialogActivity.this, "Smart Reminder", "매니저 작동을 중단했습니다.", content);
			mNotiManager.notify(notidialogActivity.noti_id, notioff);
			
			
			ComponentName Widget = new ComponentName(notidialogActivity.this,Widget.class);
			AppWidgetManager awm = AppWidgetManager.getInstance(this);
			RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
			com.legoist.Smart_Reminder.Service.timeService.set = 0;
			views.setImageViewResource(R.id.widget_btn, R.drawable.off);
			awm.updateAppWidget(Widget, views);
			
			finish();
			break;
		}
	}
	@Override
	public void onBackPressed(){;}
}
