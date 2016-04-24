package com.legoist.Smart_Reminder;
import java.util.ArrayList;

import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.ViewItem.itemviewActivity;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class listActivity extends Activity {
	//SQLite 사용준비
	DB_Item im;
	SQLiteDatabase db;
	
	//휴대시기와 갯수를 받아줄 리스트 변수
	ArrayList<CarryData> arlist;
	
	//ListView 선언
	ListView carrylist;
	
	//MyListAdapter 선언
	MyListAdapter MyAdapter;
	
	//전체DB갯수를 보여줄 TexView 객체
	TextView allCntText;
	
	//updateiCarryDialog 호출됬을 때 보여줄 CustomDialog layout
	RelativeLayout updateDialog;
	Spinner updateSpinner;
	
	
	//스피너 아답타
	ArrayAdapter<CharSequence> adspin;
	
	//Intent
	Intent intent;
	
	//Cursor
	Cursor carry; // carry값과 갯수을 검색하는 커서
	Cursor alldb; // DB의 전체갯수를 조사할 커서
	Cursor delete_cursor; //소유시기 항목을 삭제할 떄 사용
	Cursor update_cursor; //소유시기 항목을 수정할 때 사용
	
	String putCarryStr; //itemlist로 전달할 소유시기 받아줄 문자열
	String ChangedCarry; //리스트 롱클릭으로 수정될 소유시기를 받아줄 문자열
	String SelectedCarry; //리스트에서 선택된 소유시기를 받아줄 문자열
	
	//해당DB의 갯수, DB의 전체 갯수, 소유시기 미등록 갯수 
	int SelectedCnt,AllCnt,NullCnt;
	
	//갯수가 0개일땐 할 수 없다는 Dialog 출력하기 위한 변수
	final static int ZeroCntDialog = 3;
	
	//리스트를 롱클릭 했을때 나올 Dialog
	final static int ListLongClickDialog = 0;
	final static int updateiCarryDialog = 1;
	final static int DeleteListDialog = 2;
	final static int LongClickFunctionSuccessDialog = 4;
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
		AdView adview = (AdView)findViewById(R.id.adView);
	    adview.loadAd(new AdRequest());
        onResume();        
    }//onCreate()종료   
    @Override
    public void onResume(){
    	super.onResume();
    	
    	//allCnt객체 인스턴스 생성
    	allCntText = (TextView)findViewById(R.id.li_db_total_cnt);
    	    	
    	//Arraylist 인스턴스 받기
        arlist = new ArrayList<CarryData>();
        CarryData cd;
        
        //DB생성
        im = new DB_Item(this);
    	
    	//모든 DB의 개수를 조사하여 출력    
        db = im.getReadableDatabase();
    	alldb = db.rawQuery("SELECT count(*) FROM ItemDB;", null);
    	while(alldb.moveToNext()){
    		AllCnt = alldb.getInt(0);
    	}    	
    	allCntText.setText("전체 소지품 개수: "+AllCnt+"개");
        
        
        //소유시기와 해당 소유시기들의 컬럼갯수를 불러와서 List에 등록
        db = im.getReadableDatabase();
        carry = db.rawQuery("SELECT icarry, count(*) FROM ItemDB GROUP BY icarry;",null);
        while(carry.moveToNext()){
        	if(carry.getString(0).equals("null"))	cd = new CarryData("미분류", carry.getString(1).toString());    
        	else									cd = new CarryData(carry.getString(0).toString(), carry.getString(1).toString()); 
        	arlist.add(cd);
        }
        
        
        MyAdapter = new MyListAdapter(this,R.layout.list_customview, arlist);
        carrylist = (ListView)findViewById(R.id.li_list);
        carrylist.setAdapter(MyAdapter);
        carrylist.setDivider(new ColorDrawable(Color.BLACK));
        carrylist.setDividerHeight(2);
        //List Item LongClick Event
        carrylist.setOnItemLongClickListener(new OnItemLongClickListener(){
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				carry = db.rawQuery("SELECT icarry, count(*) FROM ItemDB GROUP BY icarry;",null);
				carry.moveToPosition(arg2);
				SelectedCarry = carry.getString(0).toString();			//해당하는 항복을 선택했을때 전달할 putCarryStr은 호출받은 icarry값을 초기화한다.
				SelectedCnt = carry.getInt(1);
				showDialog(ListLongClickDialog);
				return false;
			}
        }); 
        
        //List Item Click Event
        carrylist.setOnItemClickListener(new OnItemClickListener (){			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					carry = db.rawQuery("SELECT icarry, count(*) FROM ItemDB GROUP BY icarry;",null);
					carry.moveToPosition(arg2);
					SelectedCarry = carry.getString(0).toString();			//해당하는 항복을 선택했을때 전달할 putCarryStr은 호출받은 icarry값을 초기화한다.
					SelectedCnt = carry.getInt(1);
					
					putCarryStr = SelectedCarry; //선택된 소유시기 값을 다른 액티비티에 던지기 위해 put이라 명명한 변수에 저장
						
				//갯수가 0이 아니라면 listviewActivity로 putCarryStr를 던져주며 호출
				if(SelectedCnt != 0){
					intent = new Intent(getBaseContext(), itemviewActivity.class);				
					intent.putExtra("putCarry", putCarryStr);
					startActivity(intent);
				}
				else{
					showDialog(ZeroCntDialog);
				}
			}        	
        });
    }
    @Override
    public void onPause(){
    	super.onPause();
    }
    @Override
    public void onRestart(){
    	super.onRestart();
    }
    
    //List CarryData 초기화
    class CarryData{
    	CarryData(String acarryStr, String acarryCntStr){
    		carryStr = acarryStr;
    		carryCntStr = acarryCntStr+"개 보유";
    	}
    	String carryStr, carryCntStr;
    }
    
    //MyListAdapter class
    class MyListAdapter extends BaseAdapter{
    	Context maincon;
    	LayoutInflater Inflater;
    	ArrayList<CarryData> arSrc;
    	int layout;
    	public MyListAdapter(Context context,int alayout, ArrayList<CarryData> aarlist){
    		Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		arSrc = aarlist;
    		layout = alayout;
    	}
		public int getCount() {
			return arSrc.size();
		}

		public CarryData getItem(int position) {			
			return arSrc.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final int pos = position;
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			TextView text1 = (TextView)convertView.findViewById(R.id.list_customview_text1);
			text1.setText(arSrc.get(position).carryStr);
			
			TextView text2 = (TextView)convertView.findViewById(R.id.list_customview_text2);
			text2.setText(arSrc.get(position).carryCntStr);
			return convertView;
		}
    }//MyListAdapter()종료
    
    public void updateDialogSetting(){
    	//CustomDialog layout 구성
    	updateDialog = (RelativeLayout)View.inflate(this, R.layout.listupdate_customdialog, null);
    	updateSpinner = (Spinner)updateDialog.findViewById(R.id.dialog_spinner);
    	
    	updateSpinner.setPrompt("소지시기를 다시 설정해주세요.");
        adspin = ArrayAdapter.createFromResource(this, R.array.icarry, android.R.layout.simple_spinner_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateSpinner.setAdapter(adspin);
        
        updateSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){        	
        	public void onItemSelected(AdapterView<?> parent, View view, int position, long id){ 
        		ChangedCarry = adspin.getItem(position).toString();
        	}        	
			public void onNothingSelected(AdapterView<?> arg0) {
			}
        });
    }
    //Dialog
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	   	 case ZeroCntDialog:
	   		  return new AlertDialog.Builder(this)
	   		  	.setCancelable(false)
				.setTitle("알립니다")
				.setMessage("해당 소지품이 없습니다.")
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {						
					}
				})
				.create();
	   	 case ListLongClickDialog:
	   		 return new AlertDialog.Builder(this)
	   		 .setCancelable(false)
	   		 .setItems(new String[] {"수정하기", "삭제하기"}, new DialogInterface.OnClickListener() {				
				public void onClick(DialogInterface dialog, int which) {
					if(which==0) showDialog(updateiCarryDialog);
					else showDialog(DeleteListDialog);
				}
			})
	   		 .show();
	   	 case DeleteListDialog:
	   		 return new AlertDialog.Builder(this)
	   		 	 .setCancelable(false)
	   		 	.setMessage(SelectedCarry+"에 포함된 "+SelectedCnt+"개의 소지품을 삭제합니다. \n 정말로 동의하십니까?")
	   		 	.setPositiveButton("확인", new DialogInterface.OnClickListener() {					
					public void onClick(DialogInterface dialog, int which) {
						db = im.getWritableDatabase();
						db.delete("ItemDB", "icarry='"+SelectedCarry+"'", null);
						showDialog(LongClickFunctionSuccessDialog);
					}
				})
				.setNegativeButton("취소", null)
				.show();
	   	 case updateiCarryDialog:
	   		updateDialogSetting();
	   		 return new AlertDialog.Builder(this)
	   		 	.setCancelable(false)
	   		 	.setView(updateDialog)
	   		 	.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						updateDialogSetting();
						db = im.getWritableDatabase();
						Log.i("1",ChangedCarry);
						Log.i("2",SelectedCarry);
						db.execSQL("UPDATE ItemDB SET icarry = '"+ChangedCarry+"' WHERE icarry = '"+SelectedCarry+"';");
						showDialog(LongClickFunctionSuccessDialog);
					}
	   		 	})
	   		 	.setNegativeButton("취소", null)
				.show();	  
	   	 case LongClickFunctionSuccessDialog:
	   		 return new AlertDialog.Builder(this)
	   		 .setCancelable(false)
	   		 .setMessage("성공적으로 진행했습니다.")
	   		 .setPositiveButton("확인", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					onResume();
				}
			})
			.show();
	   	 }
	   	 return null;
    }
}//최종중괄호
