package com.legoist.Smart_Reminder.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.MainActivity;
import com.legoist.Smart_Reminder.R;

import java.io.File;
import java.util.ArrayList;

public class settingActivity extends Activity {
	//성정값을 저장할 DB
	DB_setting sdb;
	DB_Item im;
	SQLiteDatabase db;
	
	//설정메뉴 리스트뷰
	ListView setView;
	ArrayList<String> optionMenu;
	ArrayAdapter<String> Adapter;
	
	//알람방식을 받아줄 변수
	int mSelect;
	//알람횟수를 받아줄 변수
	int alarm_cnt;
	
	//Intent
	Intent intent;
	
	//Seekbar Dialog layout
	RelativeLayout SeekbarDialog;
	SeekBar seek;
	TextView text;

	//Dialog
	final static int option1 = 0;
	final static int option2 = 1;
	final static int SeekWarningDialog = 4;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
		AdView adview = (AdView)findViewById(R.id.adView);
	    adview.loadAd(new AdRequest());
        onResume();
        
    }//onCreate()종료
    
    public void onResume(){
    	super.onResume();
    	//설정DB On
        sdb  = new DB_setting(this);        
        
        //설정옵션 메뉴들
        optionMenu = new ArrayList<String>();
        optionMenu.add("1. 알람방식 설정");
        optionMenu.add("2. 알람 반복횟수 설정");
        optionMenu.add("3. Facebook 설정");
        optionMenu.add("4. 어플리케이션 정보");
        optionMenu.add("5. 개발자 정보");
        optionMenu.add("6. 앱 데이터 초기화");
        optionMenu.add("7. 앱 도움말");
        
        
        setView = (ListView)findViewById(R.id.setting_list);
        Adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, optionMenu);   
        setView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setView.setDivider(new ColorDrawable(Color.BLACK));
        setView.setDividerHeight(2);
        setView.setAdapter(Adapter);
        setView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				switch(position){
				case 0:
					db = sdb.getReadableDatabase();
					Cursor HowNoti = db.rawQuery("SELECT notitype FROM SettingDB;", null);
					while(HowNoti.moveToNext()){
						mSelect = HowNoti.getInt(0);
					}HowNoti.close();
					showDialog(option1);				
					break;
				case 1:
					db = sdb.getReadableDatabase();
			        Cursor NotiCnt = db.rawQuery("SELECT cnt FROM SettingDB;", null);
			        while(NotiCnt.moveToNext()){
			        	alarm_cnt = NotiCnt.getInt(0);
			        }NotiCnt.close();
			        showDialog(option2);
			        break;
				case 2:
					new AlertDialog.Builder(settingActivity.this)
					.setCancelable(false)
					.setMessage("준비중입니다")
					.setPositiveButton("확인", null)
					.show();
					break;
				case 3:
					intent = new Intent(settingActivity.this, com.legoist.Smart_Reminder.setting.patchnoteActivity.class);
			   		startActivity(intent);
					break;
				case 4:
					intent = new Intent(settingActivity.this, com.legoist.Smart_Reminder.setting.devinfoActivity.class);
			   		startActivity(intent);
					break;		
				case 5:
					new AlertDialog.Builder(settingActivity.this)
					.setCancelable(false)
					.setMessage("DB를 삭제하고 초기 설치 상태로 돌아갑니다. 지워진 데이터는 어떠한 방법으로도 복구하실 수 없습니다. \n\n" +
							"아울러 서비스 작동 중 이 기능을 수행하면 심각한 장애가 발생할 수 있으니 수행 전 매니저 서비스를 모두 종료해 주십시요.\n\n" +
							"초기화가 되고 나면 자동으로 앱을 재실행합니다."+
							"\nDB 초기화를 동의하십니까?")
					.setPositiveButton("확인", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int which) {
							im = new DB_Item(settingActivity.this);
							db = im.getWritableDatabase();
							im.onUpgrade(db, 0, 1);
							sdb = new DB_setting(settingActivity.this);
							db = sdb.getReadableDatabase();
							sdb.onUpgrade(db, 0, 1);
							File file = new File(Environment.getExternalStorageDirectory()+"/SmartReminder/photo");
						    File[] childFileList = file.listFiles();
						    for(File childFile : childFileList)	   {
						            childFile.delete();    //하위 파일삭제
						    }
						    finish();
						    intent = new Intent(getBaseContext(),MainActivity.class);
						    startActivity(intent);
						}
					})
					.setNegativeButton("취소", null)
					.show();
					break;
				case 6:
					intent = new Intent(settingActivity.this, com.legoist.Smart_Reminder.setting.helpActivity.class);
					startActivity(intent);
					break;
				}
			}
		});
    }
    
    public void setText(){
        switch(seek.getProgress()){
	        case 0:
				text.setText("[경고: 개발자 테스트 모드]");
				text.setTextColor(Color.RED);				
				text.setPaintFlags(text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
				showDialog(SeekWarningDialog);
				break;				       
			case 1:
				text.setText("반복빈도: ☆");
				text.setTextColor(Color.WHITE);
				break;
			case 2:
				text.setText("반복빈도: ☆☆");
				text.setTextColor(Color.WHITE);
				break;
			case 3:
				text.setText("반복빈도: ☆☆☆");
				text.setTextColor(Color.WHITE);
				break;
			case 4:
				text.setText("반복빈도: ☆☆☆☆");
				text.setTextColor(Color.WHITE);
				break;
			 case 5:
				text.setText("반복빈도: ☆☆☆☆☆");
				text.setTextColor(Color.WHITE);
				break;						
		}
	}
    
    public void Seek_Dialog_Setting(){
    	//Seekbar Dialog layout 얻어오기
        SeekbarDialog = (RelativeLayout)View.inflate(this, R.layout.seekbar_dialog, null);
        seek = (SeekBar)SeekbarDialog.findViewById(R.id.option2_notiSeekbar);
        seek.setProgress(alarm_cnt);
        text = (TextView)SeekbarDialog.findViewById(R.id.option2_notiText);
        setText();
        //Seekbar 동작 메소드	        
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {	
			public void onStopTrackingTouch(SeekBar seekBar) {
			}				
			public void onStartTrackingTouch(SeekBar seekBar) {
			}				
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setText();
			}	
		});
    }		
    
    //Dialog
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	   	 case option1:
	   		return new AlertDialog.Builder(this)
	   		.setCancelable(false)
	   		.setTitle("알람방식 설정")
			.setSingleChoiceItems(R.array.alarm_option, mSelect, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					mSelect = which;												
				}
			})
			.setPositiveButton("확인", new DialogInterface.OnClickListener() {											
				public void onClick(DialogInterface dialog, int whichButton) {												
					db = sdb.getWritableDatabase();
					db.execSQL("UPDATE SettingDB SET notitype = '"+mSelect+"';");
					sdb.close();												
					}
			})
			.setNegativeButton("취소", null)
			.show();	
	   	 case option2:
	   		 Seek_Dialog_Setting();
	   		 return new AlertDialog.Builder(this)
	   		.setCancelable(false)
	   		 .setTitle("반복 횟수를 조작하여 설정하세요")
	   		 .setView(SeekbarDialog)
	   		 .setPositiveButton("확인", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					db = sdb.getWritableDatabase();
					db.execSQL("UPDATE SettingDB SET cnt = '"+seek.getProgress()+"';");
					sdb.close();
				}
	   		 })
	   		 .setNegativeButton("취소", null)
	   		 .show();	
	   	 case SeekWarningDialog:
	   		 return new AlertDialog.Builder(this)
	   		.setCancelable(false)
	   		 .setTitle("경고!")
	   		 .setMessage("테스트 모드는 30초 마다 알람이 반복합니다.")
	   		 .setPositiveButton("확인", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
				}
	   		 })
	   		 .show();
	   	 }
	   	 return null;    
    }
}
