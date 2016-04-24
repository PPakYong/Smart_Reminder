package com.legoist.Smart_Reminder.ViewItem;

import java.util.ArrayList;

import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.EnrollItem.itemupdateActivity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class itemviewActivity extends Activity{	
	int delete;
	//bitmap을 저장할 파일명을 받아줄 변수
	Integer temp_filename; String filename;
	
	Bitmap photo;
	byte[] byteArray;
	
	//Gesture 기능을 위한 준비
	protected static final float DISTANCE = 0;
	protected static final float VELOCITY = 0;
	GestureDetector mDetector;
	ScrollView scrollView;
	
	//SQLite 사용준비
	DB_Item im;
	SQLiteDatabase db;
	
	//저장할 데이터들을 받아줄 객체
	ImageView vPhoto;
	TextView vName, vCarry, vDate, vPlace, vNum;
	
	//listActivity에서 건너오는 putCarry값을 받을 string
	String getCarry;
	
	//Cursor
	Cursor read;
	
	//db index를 관리할 리스트와 변수들
	ArrayList index = new ArrayList(); 	
	int i,j, temp1,temp2,temp3,cnt;
	
	//Intent
	Intent intent;
	
	//Dialog
	final static int FirstItemDialog = 0;
	final static int LastItemDialog = 1;
	final static int DeleteItemDialog = 2;		
	
	String temp;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_itemview);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
        TextView title = (TextView)findViewById(R.id.custom_title);
        title.setText("소지품 관리");

        //listActivity로부터 putCarry받기
        intent = getIntent();
        getCarry = intent.getStringExtra("putCarry");
        if(intent.getStringExtra("returnCarry") != null)getCarry = intent.getStringExtra("returnCarry");
        //i,j,temp초기화
        i=0;j=0;temp1=0; temp2=0; temp3=0; cnt=0;
        
        //객체 인스턴스 얻기
        vPhoto = (ImageView)findViewById(R.id.iv_image);
        vName = (TextView)findViewById(R.id.iv_view_name);
        vCarry = (TextView)findViewById(R.id.iv_view_carry);
        vDate = (TextView)findViewById(R.id.iv_purchase_date);
        vPlace = (TextView)findViewById(R.id.iv_purchase_place);
        vNum = (TextView)findViewById(R.id.iv_item_num);
        
        vPlace.setPaintFlags(vPlace.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        vName.setPaintFlags(vName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        //물품명을 클릭했을 때 그 물품명으로 네이버에서 검색
        vName.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				Uri uri = Uri.parse("http://search.naver.com/search.naver?sm=tab_hty&where=nexearch&query="+vName.getText().toString());
				Intent search = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(search);
			}
        });
        //구입장소를 클릭했을 때 그 지점명으로 네이버에서 검색
        vPlace.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Uri uri = Uri.parse("http://search.naver.com/search.naver?sm=tab_hty&where=nexearch&query="+vPlace.getText().toString());
				Intent search = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(search);
			}
		});
        //DB생성
        im = new DB_Item(this);
        //DB열람 및 정보값을 각 객체에 셋팅
        makeIdList();       
        dbcnt();
        func_db();
        
    	//Gesture를 내장
    	 mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
         	@Override
         	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
         		if(Math.abs(velocityX) > 1000 && Math.abs(velocityY) < 500){
         		if(velocityX < 0){
         			nextItem();
         		}
         		else
         			beforeItem();
         		}
         		return false;
         	}
         });			
		//mDetector.setIsLongpressEnabled(false);
    }//onCreate()종료
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
    	if(mDetector.onTouchEvent(ev)){
    		return true;
    	}
    	return super.dispatchTouchEvent(ev);
    }
        
	//이전버튼함수
	public void beforeItem(){
		int temp1 = (Integer) index.get(0);			
		if(i > temp1){
			do{
				temp3 = (Integer) index.get(j-1);
				i--;
			}while(i == temp1);
		if(j>0) j--;			
		}
		if(i == temp1){
			showDialog(FirstItemDialog);				
		}
		func_db();
		Log.i("i", ""+i);
		Log.i("j", ""+j);
	}
	//다음버튼함수
	public void nextItem(){		
		int temp2 = (Integer) index.get(index.size()-1);
		if(i < temp2){						
			do{
				temp3 = (Integer) index.get(j+1);
				i++;
			}while(i == temp3);
			j++;
			func_db();				
		}
		else {
			showDialog(LastItemDialog);				
		}
	}
	
	//삭제함수
	public void deleteItem(){
			dbcnt();			
			i = (Integer) index.get(j);			
			if(cnt==1){
				new AlertDialog.Builder(this)
				.setCancelable(false)
				.setTitle("경고!")
				.setMessage(getCarry+"로 등록된 유일한 소지품입니다. \n정말로 삭제하시겠습니까?")
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						db.delete("ItemDB", "_id="+i, null);
						showDialog(DeleteItemDialog);
					}
				})
				.setNegativeButton("아니요", null)
				.show();				
			}
			else {
				new AlertDialog.Builder(this)
				.setCancelable(false)
				.setMessage("보이는 소지품을 삭제하시겠습니까?")
				.setPositiveButton("예", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						db.delete("ItemDB", "_id="+i, null);
						showDialog(DeleteItemDialog);						

						if(j == index.size()-1) j--;
						else j++; 
						func_db();						
						im.close();					
					}
				})
				.setNegativeButton("아니요", null)
				.show();				
			}
		}
	//db열람하기 함수
	public void func_db(){
		i = (Integer) index.get(j);
		vNum.setText("※소지품 번호: "+i);
		Log.i("index", ""+i);
        db = im.getReadableDatabase();
        read = db.rawQuery("SELECT _id, iname, icarry, idate, iplace FROM ItemDB WHERE icarry='"+getCarry+"' AND _id='"+i+"';",null);
        while(read.moveToNext()){
        	temp_filename = read.getInt(0);		vName.setText(read.getString(1));	vCarry.setText(read.getString(2));
        	vDate.setText(read.getString(3)); 	vPlace.setText(read.getString(4)); 
        		
        	filename = String.valueOf(temp_filename);
        	Log.i("FileName", filename);
        	photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/SmartReminder/photo/image"+filename+".png");
//        	photo = BitmapFactory.decodeFile("/data/data/com.legoist.Smart_Reminder/photo/image"+filename+".png");
            Log.i("photo::", ""+photo);
            vPhoto.setImageBitmap(photo);
        }              
	}

	//id list 만들기 함수
	public void makeIdList(){		
		db = im.getReadableDatabase();
		Cursor id = db.rawQuery("SELECT _id FROM ItemDB WHERE icarry='"+getCarry+"';", null);
	    while(id.moveToNext()){			    			    	
			index.add(id.getInt(0));
	    }	id.close();
	}
		
	//현재 이 소유물품들의 db갯수
	public void dbcnt(){		
		db = im.getReadableDatabase();
		Cursor dbcnt = db.rawQuery("SELECT count(*) FROM ItemDB WHERE icarry='"+getCarry+"';", null);			
		while(dbcnt.moveToNext()){
			cnt = dbcnt.getInt(0);			
		}	dbcnt.close();
	}
	//하단 4버튼 작동 구현
	public void iv_click(View v){
		switch(v.getId()){
		//다음버튼
		case R.id.iv_next_btn:
			nextItem();
			break;
		//이전버튼
		case R.id.iv_before_btn:
			beforeItem();
			break;
		//수정버튼
		case R.id.iv_update_btn:
			intent = new Intent(getBaseContext(), itemupdateActivity.class);				
			intent.putExtra("putNum",(Integer)index.get(j));
			startActivity(intent); finish();
			break;
		//삭제버튼
		case R.id.iv_delete_btn:			
			deleteItem();
			break;
		}
	}

	//Dialog
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	   	 case FirstItemDialog:
	   		  return new AlertDialog.Builder(this)
	   		  	.setCancelable(false)
				.setMessage("첫번째 소지품 입니다.")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {						
					}
				})
				.create();
	   	 case LastItemDialog:
	   		  return new AlertDialog.Builder(this)
	   		  	.setCancelable(false)
				.setMessage("마지막 소지품 입니다.")
				.setPositiveButton("확인", null)
				.create();
	   	 case DeleteItemDialog:
				AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);                
		        SoundPool pool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
				SoundPool.OnLoadCompleteListener mListener = new SoundPool.OnLoadCompleteListener() {
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						if(status==0) delete = soundPool.play(sampleId, 1, 1, 0, 0, 1);
					}
				};
				
				if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
	    			pool.setOnLoadCompleteListener(mListener);
	    			pool.load(itemviewActivity.this, R.raw.delete, 1);
	    			pool.play(delete, 1, 1, 0, 0, 1);
				}
	   		 return new AlertDialog.Builder(this)
	   		 	.setCancelable(false)
	   		 	.setMessage("삭제되었습니다.")
	   		 	.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
	   		 	})
	   		 	.show();
	   	 }
	   	 return null;
   }


}//최종중괄호
