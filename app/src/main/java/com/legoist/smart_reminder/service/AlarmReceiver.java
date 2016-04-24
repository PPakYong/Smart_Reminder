package com.legoist.Smart_Reminder.Service;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;
import android.util.Log;

import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.R;

public class AlarmReceiver extends BroadcastReceiver{
	int notitype;
	int dingdong;
	int alarm_cnt;
	DB_Item im;
	DB_setting sdb;
	SQLiteDatabase db;
	SoundPool pool;
	private Context mContext;
	
	
	int temp=0;
	@TargetApi(8)
	@Override
	public void onReceive(Context context, Intent intent)	{
		Log.i("tmepint", ""+(temp++));
		mContext = context;
		Intent i = new Intent( context, com.legoist.Smart_Reminder.Service.notidialogActivity.class );
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_ONE_SHOT);
		try {
			pi.send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
		
		sdb = new DB_setting(context);        
       
		
		AudioManager am = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
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
			pool.load(context, R.raw.dingdong, 1);
			pool.play(dingdong, 1, 1, 0, 0, 1);
        }
        if(notitype==1 || notitype==2){
        	Vibrator vibe = (Vibrator) mContext.getSystemService(mContext.VIBRATOR_SERVICE); 
        	vibe.vibrate(1500);
        }
	}
}




