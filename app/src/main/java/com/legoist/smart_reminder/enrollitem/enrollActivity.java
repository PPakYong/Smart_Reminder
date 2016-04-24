package com.legoist.Smart_Reminder.EnrollItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_Item;

import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class enrollActivity extends Activity {
	int clear;
	
	//Intent
	Intent intent;
	//SQLite 사용준비
	DB_Item im;
	SQLiteDatabase db;
	
	//bitmap을 저장할 파일명을 받아줄 변수
	Integer temp_filename; String filename;
	
	//스피너에서 나온 값을 받아줄 string
	String spinStr1, spinStr2;
	
	//스피너 아답타
	ArrayAdapter<CharSequence> adspin1, adspin2;
	
	//기타물품 입력 대화상자 객체
	RelativeLayout relative;	EditText etc_name;
	
	//저장할 데이터들을 받아줄 객체
	ImageView mPhoto;
	Spinner mName, mCarry;
	TextView etc_view1, etc_view2;
	EditText mPlace;
	Button mDate;
	Button save;
    
    Bitmap photo;
    private Uri mImageCaptureUri;
    byte[] byteArray;
    
    //년,월,일
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    //Dialog
    final static int DATE_DIALOG_ID = 0;
    final static int QuestionDialog = 1;
    final static int SaveWarningDialog = 2;
    final static int SaveSuccessDialog = 3;
    final static int takePictureDialog = 4;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
		AdView adview = (AdView)findViewById(R.id.adView);
	    adview.loadAd(new AdRequest());
        onResume();        
    }//onCreate()종료
    @Override
    public void onResume(){
    	super.onResume();
    	//spinStr 초기화
        spinStr1=""; spinStr2="";
        
        //DB가동
        im = new DB_Item(this);
        
        //객체 인스턴스 받아옴
        mName = (Spinner)findViewById(R.id.en_spinner_name);
        mCarry = (Spinner)findViewById(R.id.en_spinner_carry);
        mDate = (Button)findViewById(R.id.en_purchase_date);
        mPlace = (EditText)findViewById(R.id.en_purchase_place);
        mPhoto = (ImageView)findViewById(R.id.en_image);
        save = (Button)findViewById(R.id.en_save_btn);
        
        //달력버튼과 저장버튼의 반투명화
        Drawable alpha1 = mDate.getBackground();
        alpha1.setAlpha(20);
        Drawable alpha2 = save.getBackground();
        alpha2.setAlpha(20);        
        
        //기타물품 입력 대화상자 레이아웃 인스턴트 받기
        relative = (RelativeLayout)View.inflate(this, R.layout.etc_name, null);
        
        //물품명 스피너 작동 구현
        mName.setPrompt("물품을 선택하세요.");
        adspin1 = ArrayAdapter.createFromResource(this, R.array.iname, android.R.layout.simple_spinner_item);
        adspin1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mName.setAdapter(adspin1);
        
        mName.setOnItemSelectedListener(new OnItemSelectedListener(){
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id){        		
        		if(position==6){
	        		showDialog(QuestionDialog);      
//        			questDialog();
        		}
        		etc_view1 = (TextView)findViewById(R.id.en_view1_1);
        		etc_view1.setText(adspin1.getItem(position)+" 선택");
        		etc_view1.setTextColor(Color.BLUE);
        		spinStr1 = adspin1.getItem(position).toString();         		
        		
        	}
			public void onNothingSelected(AdapterView<?> arg0) {
			}			
        });               
        
        //휴대시기 스피너 작동 구현
        mCarry.setPrompt("주로 언제 소지하십니까?.");
        adspin2 = ArrayAdapter.createFromResource(this, R.array.icarry, android.R.layout.simple_spinner_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCarry.setAdapter(adspin2);
        
        mCarry.setOnItemSelectedListener(new OnItemSelectedListener(){        	
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id){        		
        		etc_view2 = (TextView)findViewById(R.id.en_view2_2);        		
        		etc_view2.setText(adspin2.getItem(position)+" 선택");
        		etc_view2.setTextColor(Color.BLUE);
        		spinStr2 = adspin2.getItem(position).toString();
        	}        	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
    }
    @Override
    public void onPause(){
    	super.onPause();
    	etc_name = (EditText)relative.findViewById(R.id.etc_name_edittext);
    	etc_name.setText("");
    }
    @Override
    public void onRestart(){
    	super.onRestart();
    }
    
    //사진찍기, 날짜가져오기, 저장하기 버튼 구현부
    public void en_click(View v){
    	switch(v.getId()){
    	//사진찍기
    	case R.id.en_image:
    		showDialog(takePictureDialog);
    		break;
    	//날짜가져오기
    	case R.id.en_purchase_date:
    		setToday();
    		showDialog(DATE_DIALOG_ID);
    		updateDisplay();
    		break;
    	//DB에 입력값 저장
    	case R.id.en_save_btn:
    		if(spinStr1.length() < 1 || spinStr1.equals("null")) showDialog(SaveWarningDialog);
			else{				
        		db = im.getWritableDatabase();
        		db.execSQL("INSERT INTO ItemDB VALUES(null,	'"+spinStr1+"', '"+spinStr2+"', '"+mDate.getText().toString()+"', '"+mPlace.getText().toString()+"');");        		
        		db = im.getReadableDatabase();
        	    Cursor max = db.rawQuery("SELECT _id FROM ItemDB WHERE _id = (SELECT MAX(_id) FROM ItemDB);",null);
        		while(max.moveToNext()){
        			temp_filename = max.getInt(0);
        			filename = temp_filename.toString()+"";
        		}
        		try{
	        		String path = Environment.getExternalStorageDirectory()+"/SmartReminder/photo/image"+filename+".png";
	//        		String path ="/data/data/com.legoist.Smart_Reminder/photo/"+filename+".png";
	    	        try {
						File f = new File(path);
						f.createNewFile();
						OutputStream outStream = new FileOutputStream(f);
						photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
						outStream.close();
						} catch (IOException e) {
						e.printStackTrace();
		            }
        		}catch(NullPointerException e){;}
        		
        		showDialog(SaveSuccessDialog);
        		removeDialog(QuestionDialog);
                mPhoto.setImageResource(R.drawable.photo);
                photo = null;
        		spinStr1 =""; spinStr2=""; mDate.setText("");mPlace.setText("");
        	
        		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);                
    	        SoundPool pool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
    			SoundPool.OnLoadCompleteListener mListener = new SoundPool.OnLoadCompleteListener() {
    				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    					if(status==0) clear = soundPool.play(sampleId, 1, 1, 0, 0, 1);
    				}
    			};
    			
    			if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
	    			pool.setOnLoadCompleteListener(mListener);
	    			pool.load(enrollActivity.this, R.raw.clear, 1);
	    			pool.play(clear, 1, 1, 0, 0, 1);
    			}       
        		
    		break;
			}
    	}
    }
    
    //DatePicker 구현
    private void updateDisplay(){
	     mDate.setText(String.format("%d년 %d월 %d일 ", mYear, mMonth+1, mDay));
	}    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() { 
	     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		      mYear = year;
		      mMonth = monthOfYear;
		      mDay = dayOfMonth;
		      updateDisplay();
	     }
    };
	    
	    
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	     case DATE_DIALOG_ID:
	   		  return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
	   	 case QuestionDialog:
	   		  return new AlertDialog.Builder(this)
	   		  	.setCancelable(false)
				.setView(relative)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						etc_name = (EditText)relative.findViewById(R.id.etc_name_edittext);
						spinStr1 = etc_name.getText().toString();
						Log.i("spinStr1", spinStr1);
		        		if(spinStr1.length() < 1 || spinStr1.equals("null")) {
		        			etc_view1.setText("경고: 최소 2자 입력 사항입니다.");	
		        			etc_view1.setTextColor(Color.RED);
		        		}
		        		else {
		        			etc_view1.setText(spinStr1 +" 수동입력");
		        			etc_view1.setTextColor(Color.GREEN);
		        		}
					}
				})
				.setNegativeButton("취소", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						etc_name = (EditText)relative.findViewById(R.id.etc_name_edittext);
						spinStr1 = etc_name.getText().toString();
						Log.i("spinStr1", spinStr1);
						etc_view1.setText("경고: 입력 취소됨");	
	        			etc_view1.setTextColor(Color.RED);
					}
				})
				.show();
	   	 case SaveWarningDialog:
	   		 return new AlertDialog.Builder(this)
	   		 .setCancelable(false)
	   		 .setTitle("저장 실패!")
	   		 .setMessage("소지품명은 필수사항으로 2자 이상입니다.")
	   		 .setPositiveButton("확인", null)
	   		 .show();
	   	 case SaveSuccessDialog:
	   		 return new AlertDialog.Builder(this)
	   		 .setCancelable(false)
	   		 .setMessage("저장되었습니다")
	   		 .setPositiveButton("확인", new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					onResume();
				}
			})
			.show();
	   	 case takePictureDialog:
	   		return new AlertDialog.Builder(this)
	   	     .setCancelable(false)
	   		 .setItems(new String[] {"직접 촬영하기", "갤러리 가져오기"}, new DialogInterface.OnClickListener() {				
					public void onClick(DialogInterface dialog, int which) {
							if(which==0) doTakePhotoAction();					
							else doTakeAlbumAction();
					}
			 })
			 .setNegativeButton("취소", null)
	   		 .show();
	   	 }
	   	 return null;
    }
    //카메라에서 이미지 가져오기
    
    private void doTakePhotoAction()    {
      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      
      // 임시로 사용할 파일의 경로를 생성
      String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
      mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
      
      intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
      // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
      //intent.putExtra("return-data", true);
      startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    
    //앨범에서 이미지 가져오기     
    private void doTakeAlbumAction()    {
      // 앨범 호출
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
      startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    //사진처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)    {
      if(resultCode != RESULT_OK)      {
        return;
      }

      switch(requestCode)      {
        case CROP_FROM_CAMERA:        {
          final Bundle extras = data.getExtras();    
	          if(extras != null)          {
	            photo = extras.getParcelable("data");
	            mPhoto.setImageBitmap(photo);            
	          }    
          // 임시 파일 삭제
          File f = new File(mImageCaptureUri.getPath());
	          if(f.exists())          {
	            f.delete();
	          }    
          break;
        }    
        case PICK_FROM_ALBUM:        {
          // 이후의 처리가 카메라와 같으므로 일단 break없이 진행.
          mImageCaptureUri = data.getData();
        }        
        case PICK_FROM_CAMERA:        {
          // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정.
          // 이후에 이미지 크롭 어플리케이션을 호출.
    
          Intent intent = new Intent("com.android.camera.action.CROP");
          intent.setDataAndType(mImageCaptureUri, "image/*");
    
          intent.putExtra("outputX", 90);
          intent.putExtra("outputY", 90);
          intent.putExtra("aspectX", 1);
          intent.putExtra("aspectY", 1);
          intent.putExtra("scale", true);
          intent.putExtra("return-data", true);
          startActivityForResult(intent, CROP_FROM_CAMERA);    
          break;
        }
      }
    }
    
    //오늘의 날짜 구하기
    public void setToday(){
    	c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }
}//최종중괄호
