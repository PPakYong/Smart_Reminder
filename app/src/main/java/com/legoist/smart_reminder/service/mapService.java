package com.legoist.Smart_Reminder.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.MainActivity;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.notiActivity;

public class mapService extends Service{
	Intent thisIntent;
	double notiLocale,latitude,longitude;
	double distance;
	LocationManager locationManager;
	AudioManager am;
	DB_setting sdb;
	SQLiteDatabase db;
	SoundPool pool;
	int notitype;
	int dingdong;
	int alarm_cnt;
	int startId;
	Location nowLocation,destination;
	String mProvider;							//getBestProvider()로 가장 유리한 위치 제공자를 받아줄 String
	@Override
	public IBinder onBind(Intent arg0) {return null;}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		super.onStartCommand(intent, flags, startId);
		this.startId = startId;
		locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
		am = (AudioManager)getSystemService(AUDIO_SERVICE);
		sdb = new DB_setting(mapService.this);
		notiLocale = intent.getDoubleExtra("value", 300.0);
		
		//목적지
		destination = new Location("destination");
		latitude = intent.getDoubleExtra("latitude", 37.0);
		longitude = intent.getDoubleExtra("longitude", 126.0);
		destination.setLatitude(intent.getDoubleExtra("latitude", 37.0));
		destination.setLongitude(intent.getDoubleExtra("longitude", 126.0));
		
		
		Log.i("error_test", latitude +"          "+ longitude + "");
		
		mProvider = locationManager.getBestProvider(new Criteria(), true);	
		locationManager.requestLocationUpdates(mProvider, 60000, 50, mListener);
		
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy(){
		locationManager.removeUpdates(mListener);
		super.onDestroy();
		final SharedPreferences pref = getSharedPreferences("mapService", 0);
    	final SharedPreferences.Editor editor = pref.edit();
		editor.putInt("alarm_set", 0);
		editor.commit();
	}
	
	/**
	 * LocationListener. 현재위치를 추적한 뒤 DB를 읽고 설정위치와 비교할 쓰레드를 실행한다.
	 */
	LocationListener mListener = new LocationListener(){
		public void onLocationChanged(Location location) {
			nowLocation = location;
			nowLocation = new Location("nowLocation");
			nowLocation.setLatitude(location.getLatitude());
			nowLocation.setLongitude(location.getLongitude());
			distance = (double)nowLocation.distanceTo(destination);
			Toast.makeText(getBaseContext(), distance+"", Toast.LENGTH_SHORT).show();
			if(distance < 100.0){
				alarm();
				stopSelf();
				
				NotificationManager mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				NotificationManager NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
				NM.cancel(notiActivity.noti_id2);
				
				//비활성화 Notification 잠깐 띄움
				Notification notioff = new Notification(R.drawable.map, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
				notioff.flags |= Notification.FLAG_AUTO_CANCEL;
				PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
				notioff.setLatestEventInfo(getBaseContext(), "Smart Reminder", "목적지에 도착했습니다.", content);
				mNotiManager.notify(notiActivity.noti_id2, notioff);
			}
			else if(distance <= notiLocale){
				alarm();
			}
			else{;}
		}
		
		//위치추적 서비스 불가능일때
		public void onProviderDisabled(String provider) {
			Toast.makeText(mapService.this, "서비스불가", Toast.LENGTH_SHORT).show();
		}
		//위치추적 서비스가 가능할 때
		public void onProviderEnabled(String provider) {
		}
		//위치상태가 변경이 될 때
		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch(status){
			//변경된 위치가 서비스 이탈지역일 떄
			case LocationProvider.OUT_OF_SERVICE:
				Toast.makeText(mapService.this, "서비스 범위 이탈", Toast.LENGTH_SHORT).show();
				break;
			//변경된 위치가 일시적 장애지역 일 때
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				Toast.makeText(mapService.this, "서비스 일시적 불능", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	public void alarm(){
		switch (am.getRingerMode()) { 
		    case AudioManager.RINGER_MODE_SILENT: 
		        db = sdb.getWritableDatabase();
		        db.execSQL("UPDATE SettingDB SET notitype=3;"); 
		        db.close();
		        break; 
		    case AudioManager.RINGER_MODE_VIBRATE: 
		    	db = sdb.getWritableDatabase();
		        db.execSQL("UPDATE SettingDB SET notitype=2;"); 
		            db.close();
		            break; 
		        case AudioManager.RINGER_MODE_NORMAL: 
		            break; 
		    }
		    
		   
		    db = sdb.getReadableDatabase();
		    Cursor cursor1 = db.rawQuery("SELECT cnt, notitype FROM SettingDB;", null);
		while(cursor1.moveToNext()){
			alarm_cnt = cursor1.getInt(0);
			notitype = cursor1.getInt(1);
		}cursor1.close();
		
		if(notitype==0 || notitype==1){        
		    pool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
			SoundPool.OnLoadCompleteListener mListener = new SoundPool.OnLoadCompleteListener() {
				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
					if(status==0) dingdong = soundPool.play(sampleId, 1, 1, 0, 0, 1);
				}
			};
			
			pool.setOnLoadCompleteListener(mListener);
			pool.load(getBaseContext(), R.raw.dingdong, 1);
			pool.play(dingdong, 1, 1, 0, 0, 1);
		}
		if(notitype==1 || notitype==2){
			Vibrator vibe = (Vibrator)getSystemService(VIBRATOR_SERVICE); 
			vibe.vibrate(1500);
		}
		
		Intent notiDialogIntent = new Intent(mapService.this, com.legoist.Smart_Reminder.Service.notidialogActivity_map.class);
		notiDialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(notiDialogIntent);
	}
}
