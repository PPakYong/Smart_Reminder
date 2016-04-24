package com.legoist.Smart_Reminder;

import java.io.File;
import java.util.Calendar;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.setting.helpActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class splashActivity extends Activity {
	SQLiteDatabase db;	
	DB_setting sdb;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_splashview);
	    
	    String str = Environment.getExternalStorageState();
    	if ( str.equals(Environment.MEDIA_MOUNTED)) {
    	
        String dirPath = Environment.getExternalStorageDirectory()+"/SmartReminder/photo/"; 
        File file = new File(dirPath); 
        if( !file.exists() )  // 원하는 경로에 폴더가 있는지 확인
          file.mkdirs();
    	}
    	else Toast.makeText(this, "SD Card 인식 실패", Toast.LENGTH_SHORT).show();
	       
	    sdb = new DB_setting(this);
	    db = sdb.getReadableDatabase();
	    Cursor cursor = db.rawQuery("SELECT first FROM SettingDB;", null);
	    cursor.moveToFirst();
	    int first = cursor.getInt(0);
	    
	    
	    if(first != 1){
		    new AlertDialog.Builder(splashActivity.this)
		    .setCancelable(false)
		    .setMessage("Alpha 0.9v Release\n테스트 중 나는 오류는 반드시 신고해주십시오.\n\n앱에서 위치정보 사용함을 동의하며 시작합니다.")
//		    .setMessage("이 앱은 테스트 단계의 버전으로써 의도치 않은 버그로 비정상적인 작동이 일어날 수 있으며, " +
//		    		"설치 중 위치정보 제공 권한에 대해서는 아직 기능 미구현으로 사용자의 위치정보를 일절 사용하지 않음을 알립니다." +
//		    		"\n\n앱의 테스트기간은 8월15일 까지이며, 테스트 설문 이후 받아지는 정보를 토대로 버그 수정 및 추가기능을 탑재하여 오는 9월 내에 구글Play에서 정식 다운로드 받으실 수 있습니다." +
//		    		"\n\n앱에 대한 저작권은 [LEGOIST]에게 있으며, 저작권 접촉 시 그 책임을 물을 수 있습니다. 자세한 사항은 www.facebook.com/team.legoist 페이지를 참고하십시오.")
    		.setPositiveButton("동의", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				    db = sdb.getWritableDatabase();
				    db.execSQL("UPDATE SettingDB SET first=1;");
					finish();
					Intent intent = new Intent(splashActivity.this, helpActivity.class);
					startActivity(intent);
				}
			})
			.setNeutralButton("거절", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					moveTaskToBack(true);
    	 	        android.os.Process.killProcess(android.os.Process.myPid());
				}
			})
			.show();
	    }
	    
//	    else{  
//	    	Calendar c = Calendar.getInstance();
//	        int mYear = c.get(Calendar.YEAR);
//	        int mMonth = c.get(Calendar.MONTH);
//	        int mDay = c.get(Calendar.DAY_OF_MONTH);
//	        
//	        if(mYear==2012 && (mMonth+1)==8 && mDay > 15){
//	        	new AlertDialog.Builder(splashActivity.this)
//	        	.setMessage("테스트가 종료되었습니다. 설문지를 작성 및 제출해 주시고 최우수 테스터 이벤트에 꼭 참가 바랍니다!\n\n감사합니다^^   -by LEGOIST\n\n문의: team.legoist@gmail.com\nwww.facebook.com/team.legoist")
//	        	.setPositiveButton("확인", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//	    	    		moveTaskToBack(true);
//	    	 	        android.os.Process.killProcess(android.os.Process.myPid());
//					}
//				})
//				.show();
//	        }
	        else{
		  	    Handler handler = new Handler() {
		  	    	@Override
		  	    	public void handleMessage(Message msg){
		  	    		finish();	    		
		  	    	}
		  	    };	
		  	    handler.sendEmptyMessageDelayed(0, 2500);
	        }
//        }
    }
}