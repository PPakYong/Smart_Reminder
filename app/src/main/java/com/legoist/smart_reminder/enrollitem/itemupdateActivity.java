package com.legoist.Smart_Reminder.EnrollItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.ViewItem.itemviewActivity;

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
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
public class itemupdateActivity extends Activity {
	int clear;
	//bitmap을 저장할 파일명을 받아줄 변수
	Integer temp_filename; String filename;
	
	//실험변수
	String a;
	
	//SQLite 사용준비
	DB_Item im;
	SQLiteDatabase db;
	
	//저장할 데이터들을 받아줄 객체
	ImageView ePhoto;
	EditText eName, eCarry, ePlace;
	Button eDate, save;
    
    Bitmap photo;
    private Uri mImageCaptureUri;
    byte[] byteArray;
    
    //년,월,일
    Calendar c;
    private int mYear;
    private int mMonth;
    private int mDay;
    
    //itemviewActivity로 부터 소지품 번호를 받을 변수
    int getNum;
    
    //Dialog
    final static int DATE_DIALOG_ID = 0;
    final static int SaveSuccessDialog = 1;
    final static int SaveWarningDialog = 2;
    final static int takePictureDialog = 4;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;
    
    //Intent
    Intent intent;
    
    //Cursor
    Cursor read;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_itemupdate);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
        TextView title = (TextView)findViewById(R.id.custom_title);
        title.setText("소지품 관리");
        //itemviewActivity로부터 putNum받기
        intent = getIntent();
        getNum = intent.getIntExtra("putNum", 0);
        
        //DB가동
        im = new DB_Item(this);
        
        //객체 인스턴스 받아옴
        eName = (EditText)findViewById(R.id.iu_name);
        eCarry = (EditText)findViewById(R.id.iu_carry);
        eDate = (Button)findViewById(R.id.iu_purchase_date);
        ePlace = (EditText)findViewById(R.id.iu_purchase_place);
        ePhoto = (ImageView)findViewById(R.id.iu_image);
        save = (Button)findViewById(R.id.iu_save_btn);
        
        //달력버튼과 저장버튼의 반투명화
        Drawable alpha1 = eDate.getBackground();
        alpha1.setAlpha(20);
        Drawable alpha2 = save.getBackground();
        alpha2.setAlpha(20);
        setToday();
        
        //수정될 소지품 정보의 초기값 셋팅
        db = im.getReadableDatabase();
        read = db.rawQuery("SELECT _id, iname, icarry, idate, iplace FROM ItemDB WHERE _id='"+getNum+"';",null);
        while(read.moveToNext()){
        	temp_filename = read.getInt(0); eName.setText(read.getString(1));	eCarry.setText(read.getString(2));
        	eDate.setText(read.getString(3)); 	ePlace.setText(read.getString(4));
    	}       
        
        filename = String.valueOf(temp_filename);
    	photo = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/SmartReminder/photo/image"+filename+".png");
        ePhoto.setImageBitmap(photo);
    }//onCreate()종료
    
    //DatePicker 구현
    private void updateDisplay(){
	     eDate.setText(String.format("%d년 %d월 %d일 ", mYear, mMonth+1, mDay));
	}    
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() { 
	     public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		      mYear = year;
		      mMonth = monthOfYear;
		      mDay = dayOfMonth;
		      updateDisplay();
	     }
    };
    
    //버튼 클릭 구현
    public void iu_click(View v){
    	switch(v.getId()){
    	//사진찍기
    	case R.id.iu_image:
    		showDialog(takePictureDialog);
    		break;
    	//달력버튼
    	case R.id.iu_purchase_date:
    		showDialog(DATE_DIALOG_ID);
    		updateDisplay();
    		break;
		//저장버튼
    	case R.id.iu_save_btn:
    		if(eName.length() < 1) showDialog(SaveWarningDialog);
			else{				
        		db = im.getWritableDatabase();
        		//실험코딩
        		a = eCarry.getText().toString();
        		if(a.equals("null") || a == null || a.equals("")) a = "미분류";
        		db.execSQL("UPDATE ItemDB SET iname = '"+eName.getText().toString()+"', icarry = '"+a+"', idate = '"+eDate.getText().toString()+"', iplace = '"+ePlace.getText().toString()+"' WHERE _id='"+getNum+"';");        		
        		
        		String path = Environment.getExternalStorageDirectory()+"/SmartReminder/photo/image"+filename+".png";
    	        try{
		    		try {
						File f = new File(path);
						f.createNewFile();
						OutputStream outStream = new FileOutputStream(f);
						photo.compress(Bitmap.CompressFormat.PNG, 100, outStream);
						outStream.close();
						} catch (IOException e) {
						e.printStackTrace();
		            }
    	        }catch(NullPointerException e){e.printStackTrace();}
        		
        		showDialog(SaveSuccessDialog);
        		AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);                
    	        SoundPool pool = new SoundPool(1, AudioManager.STREAM_ALARM, 0);
    			SoundPool.OnLoadCompleteListener mListener = new SoundPool.OnLoadCompleteListener() {
    				public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
    					if(status==0) clear = soundPool.play(sampleId, 1, 1, 0, 0, 1);
    				}
    			};
    			
    			if(am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
	    			pool.setOnLoadCompleteListener(mListener);
	    			pool.load(itemupdateActivity.this, R.raw.clear, 1);
	    			pool.play(clear, 1, 1, 0, 0, 1);
    			}
			}
    		break;
    	}
    }
    
    
    //Dialog    
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	     case DATE_DIALOG_ID:
	   		  return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);	   		
	   	 case SaveWarningDialog:
	   		 return new AlertDialog.Builder(this)
	   		.setCancelable(false)
	   		 .setTitle("수정 실패!")
	   		 .setMessage("소지품명은 필수사항으로 2자 이상입니다.")
	   		 .setPositiveButton("확인", null)
	   		 .show();
	   	 case SaveSuccessDialog:
	   		return new AlertDialog.Builder(this)
	   		.setCancelable(false)
	   		 .setTitle("수정 성공!")
	   		 .setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					intent = new Intent(getBaseContext(), itemviewActivity.class);
					intent.putExtra("returnNum", getNum);
					intent.putExtra("returnCarry", a);
					startActivity(intent);
					finish();
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
    
    //////////////////////////////////////////////////////////////////////////
    
    
    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {
      /*
       * 참고 해볼곳
       * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
       * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
       * http://www.damonkohler.com/2009/02/android-recipes.html
       * http://www.firstclown.us/tag/android/
       */

      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      
      // 임시로 사용할 파일의 경로를 생성
      String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
      mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
      
      intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
      // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리 합니다.
      //intent.putExtra("return-data", true);
      startActivityForResult(intent, PICK_FROM_CAMERA);
    }
    
    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
      // 앨범 호출
      Intent intent = new Intent(Intent.ACTION_PICK);
      intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
      startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
      if(resultCode != RESULT_OK)
      {
        return;
      }

      switch(requestCode)
      {
        case CROP_FROM_CAMERA:
        {
          // 크롭이 된 이후의 이미지를 넘겨 받습니다.
          // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
          // 임시 파일을 삭제합니다.
          final Bundle extras = data.getExtras();
    
          if(extras != null)
          {
            photo = extras.getParcelable("data");
            bitmapToByteArray(photo);
            ePhoto.setImageBitmap(photo);            
          }
    
          // 임시 파일 삭제
          File f = new File(mImageCaptureUri.getPath());
          if(f.exists())
          {
            f.delete();
          }
    
          break;
        }
    
        case PICK_FROM_ALBUM:
        {
          // 이후의 처리가 카메라와 같으므로 일단  break없이 진행합니다.
          // 실제 코드에서는 좀더 합리적인 방법을 선택하시기 바랍니다.
          mImageCaptureUri = data.getData();
        }
        
        case PICK_FROM_CAMERA:
        {
          // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
          // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
    
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
    
    
    public byte[] bitmapToByteArray( Bitmap $bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        $bitmap.compress( CompressFormat.JPEG, 100, stream) ;  
        byteArray = stream.toByteArray() ;  
        return byteArray;  
    }
    //오늘의 날짜 구하기
    public void setToday(){
    	c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }
}//최종중괄호
