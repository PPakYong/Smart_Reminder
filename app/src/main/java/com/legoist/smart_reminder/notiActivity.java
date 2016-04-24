package com.legoist.Smart_Reminder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.ads.AdRequest;
import com.google.ads.AdView;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.legoist.Smart_Reminder.R;
import com.legoist.Smart_Reminder.DB.DB_Item;
import com.legoist.Smart_Reminder.DB.DB_setting;
import com.legoist.Smart_Reminder.Service.AlarmReceiver;
import com.legoist.Smart_Reminder.Service.Widget;
import com.legoist.Smart_Reminder.Service.mapService;
import com.legoist.Smart_Reminder.Service.timeService;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class notiActivity extends MapActivity {
	SharedPreferences pref;
	SharedPreferences.Editor editor;
	MapView mMap;
	EditText SearchText;
	ImageButton SearchBtn;
	public static ImageButton MapOnBtn;
	public static ImageButton MapOffBtn;
	ImageButton myLocationBtn, selfChoiceBtn;
	MapController mControl;
	double latitude = 0;
	double longitude = 0;				//목적지의 위도, 경도
	MyLocationOverlay myLocationOverlay;	//현위치를 잡아줄 Overlay
	ArrayList<searchResult> sResult = new ArrayList<searchResult>();
	searchResult resultlist;
	AlertDialog searchDialog;
	String search;
	GeoPoint destination;
	AlertDialog none_destination_select;
	AlertDialog map_menu;
	ListView HowToGo;
	ArrayList<HowToList> choice = new ArrayList<HowToList>();
	View Dialog_HowToGo;
	protected boolean isRouteDisplayed(){
		return false;
	}
	
	int alarm_cnt=0,i=0,j=0,max=0,min=0,dev_mode=0;
	String temp="temp";
	String temp1="";
	
	int noti_carry_num=0;
	
	//Notification
	NotificationManager mNotiManager;
	public static final int noti_id=1;
	public static final int noti_id2=2;
	
	//timenoti Page, mapnoti Page
	View timepage, mappage;
	
	//page btn
	ImageButton timepage_btn, mappage_btn;
	TextView timepage_btn_text, mappage_btn_text;
	
	// 알람on/off상태를 저장할 변수
	int check_noti_switch;
	
	//SQLite 사용준비
	DB_setting sdb;
	DB_Item im;
	SQLiteDatabase db;
	
	// on/off 버튼
	ImageButton switch_btn_on, switch_btn_off;

	
	//휴대시기와 갯수를 받아줄 리스트 변수
	ArrayList<String> arlist;
	
	//모든 DB갯수
	Integer Allcnt;
	
	//ListView 객체 선언
	ListView carrylist;
	
	//선택된 소유시기 값 받을 String변수
	String SelectedCarry;
	
	//MyListAdapter 선언
	ArrayAdapter<String> Adapter;	
	
	//Intent
	Intent intent;
	
	//Cursor
	Cursor carry; // carry값과 갯수을 검색하는 커서
	Cursor alldb; // DB의 전체갯수를 조사할 커서
	
	//Dialog
	final static int noti_on_dialog = 0;
	final static int noti_off_dialog = 1;
	final static int noti_none_select_dialog=2;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_noti);	 
		AdView adview = (AdView)findViewById(R.id.adView);
	    adview.loadAd(new AdRequest());
	    
    	pref = getSharedPreferences("mapService", 0);
    	editor = pref.edit();

    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    
		SelectedCarry = "전체";
		//NotificationManager
		mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		
		//switch버튼을 투명하게
		switch_btn_on = (ImageButton)findViewById(R.id.noti_btn_on);
		switch_btn_off = (ImageButton)findViewById(R.id.noti_btn_off);
		
		//DB open
		sdb = new DB_setting(this);
		im = new DB_Item(this);
		
    	//time page와 그 버튼 객체
    	timepage = findViewById(R.id.timenoti_page);
    	timepage_btn = (ImageButton)findViewById(R.id.timenoti_btn);
    	timepage_btn_text = (TextView)findViewById(R.id.timenoti_btn_name);
    	
    	//map page와 그 버튼 객체
    	mappage = findViewById(R.id.mapnoti_page);	    	
    	mappage_btn = (ImageButton)findViewById(R.id.mapnoti_btn);
    	mappage_btn_text = (TextView)findViewById(R.id.mapnoti_btn_name);
    	
    	//처음엔 time page 만 보이고 map page btn 활성화 처럼 보이게
    	timepage.setVisibility(View.VISIBLE);
    	timepage_btn.setBackgroundColor(0x00000000);
    	timepage_btn_text.setTextColor(Color.BLACK);
    	timepage_btn_text.setPaintFlags(timepage_btn_text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
    	
    	mappage.setVisibility(View.INVISIBLE);
    	mappage_btn.setBackgroundColor(Color.LTGRAY);
    	mappage_btn_text.setTextColor(Color.WHITE);
    	
    	//맵 관련 객체 생성
    	mMap = (MapView)findViewById(R.id.map);
    	SearchText = (EditText)findViewById(R.id.map_search);
    	SearchBtn = (ImageButton)findViewById(R.id.map_btn);
    	MapOnBtn = (ImageButton)findViewById(R.id.map_btn_on);
    	MapOffBtn = (ImageButton)findViewById(R.id.map_btn_off);
    	myLocationBtn = (ImageButton)findViewById(R.id.myLocation);
    	selfChoiceBtn = (ImageButton)findViewById(R.id.selfchoice);
    	myLocationOverlay = new MyLocationOverlay(this, mMap);
    	mMap.getOverlays().add(myLocationOverlay);
    	
    	latitude = pref.getFloat("latitude", 0)*1E6;
    	longitude = pref.getFloat("longitude", 0)*1E6;
    	destination = new GeoPoint((int)(latitude), (int)(longitude));
    	
    	if(pref.getInt("alarm_set", 0) == 1){
			MapOffBtn.setVisibility(View.INVISIBLE);
			MapOnBtn.setVisibility(View.VISIBLE);
			
			selfChoiceBtn.setClickable(false);
			SearchBtn.setClickable(false);
			
			SearchText.setClickable(false);
    		SearchText.setFocusable(false);
    		SearchText.setFocusableInTouchMode(false);
    		SearchText.setHint("매니저 작동 중엔 검색기능은 사용 불가.");
    		
    		Toast.makeText(getBaseContext(), (int)latitude + "  "+(int)longitude, Toast.LENGTH_LONG).show();
    		moveMarker(destination);
    	}
    	else{
			MapOnBtn.setVisibility(View.INVISIBLE);
			MapOffBtn.setVisibility(View.VISIBLE);
    	}
    	
    	Dialog_HowToGo = (RelativeLayout)View.inflate(notiActivity.this, R.layout.dialog_howtogo, null);
		HowToGo = (ListView)Dialog_HowToGo.findViewById(R.id.HowGoingList);
		HowToList howlist = new HowToList("도보&자전거", "알림범위: 300m", "30km/h이하의 속도로 이동합니다.\n위치정보 정확도가 높습니다.");
		choice.add(howlist);
		howlist = new HowToList("자동차", "알림범위: 1km", "80km/h이하의 속도로 이동합니다.\n위치정보 정확도가 정확하지 않습니다.");
		choice.add(howlist);
		howlist = new HowToList("전철", "범위: 2km", "100km/h이하의 속도로 이동합니다.\n위치정보 정확도가 매우 낮습니다.");
		choice.add(howlist);
    	
		map_menu = new AlertDialog.Builder(notiActivity.this)
		.setCancelable(false)
		.setView(Dialog_HowToGo)
		.setNegativeButton("취소", null)
		.create();
		
		none_destination_select = new AlertDialog.Builder(this)
		.setMessage("목적지를 선택하세요.")
		.setCancelable(false)
		.setPositiveButton("확인", null)
		.create();
	}
	
	public void funcClick(View v){
		switch(v.getId()){
		//검색버튼 눌렀을 때의 이벤트
		case R.id.map_btn:
			if(SearchText.getText().length() < 1){
				new AlertDialog.Builder(notiActivity.this)
				.setCancelable(false)
				.setMessage("검색어를 입력하세요!")
				.setPositiveButton("확인", null)
				.show();
			}else{
				search = SearchText.getText().toString();
				searchOnMap();
			}
			
			//검색어를 입력한 후 검색을 누르면 키보드는 화면에서 사라진다.
			InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(SearchText.getWindowToken(), 0);
			break;
		//내 위치로 되돌아가기
		case R.id.myLocation:
			mMap.getController().setZoom(20);
			mMap.getController().animateTo(myLocationOverlay.getMyLocation());
			break;
		case R.id.selfchoice:
			Toast.makeText(notiActivity.this, "목적지를 직접 터치해주세요.", Toast.LENGTH_LONG).show();
			GeoPoint centerGeoPoint = mMap.getMapCenter();
			latitude = (float) centerGeoPoint.getLatitudeE6() / 1000000;
			longitude = (float)	centerGeoPoint.getLongitudeE6() /1000000;
			moveMarker(centerGeoPoint);
			break;
		case R.id.map_btn_on:
			Intent sendChoiceIntent = new Intent(notiActivity.this, mapService.class);
			stopService(sendChoiceIntent);
			
			intent = new Intent(getBaseContext(), MainActivity.class);
			NotificationManager NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
			NM.cancel(notiActivity.noti_id2);
			
			//비활성화 Notification 잠깐 띄움
			Notification notioff = new Notification(R.drawable.map, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
			notioff.flags |= Notification.FLAG_AUTO_CANCEL;
			PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
			notioff.setLatestEventInfo(getBaseContext(), "Smart Reminder", "매니저 작동을 중단했습니다.", content);
			mNotiManager.notify(notiActivity.noti_id2, notioff);
			
			editor.putInt("alarm_set", 0);
			editor.commit();
			MapOnBtn.setVisibility(View.INVISIBLE);
			MapOffBtn.setVisibility(View.VISIBLE);
			
			selfChoiceBtn.setClickable(true);
			SearchBtn.setClickable(true);
			
			SearchText.setClickable(true);
    		SearchText.setFocusable(true);
    		SearchText.setFocusableInTouchMode(true);
    		SearchText.setHint("ex)강남 스타벅스");
    		
    		mMap.getOverlays().clear();
    		mMap.getOverlays().add(myLocationOverlay);
    		
    		editor.putFloat("latitude", 0);
    		editor.putFloat("longitude", 0);
    		editor.commit();
    		break;
		case R.id.map_btn_off:
			if(latitude == 0 || longitude == 0){
				none_destination_select.show();
			}
			else{
				map_menu.show();
				MyListAdapter2 Adapter2 = new MyListAdapter2(notiActivity.this, R.layout.customlist_howto, choice);
				HowToGo.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				HowToGo.setDivider(new ColorDrawable(Color.BLACK));
				HowToGo.setDividerHeight(1);
				HowToGo.setAdapter(Adapter2);
				HowToGo.setOnItemClickListener(new OnItemClickListener(){
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						double[] sendChoice = {300.0, 1000.0, 2000.0};
						Intent sendChoiceIntent = new Intent(notiActivity.this, mapService.class);
						sendChoiceIntent.putExtra("value", sendChoice[arg2]);
						sendChoiceIntent.putExtra("latitude", latitude);
						sendChoiceIntent.putExtra("longitude", longitude);
						startService(sendChoiceIntent);
						
						editor.putInt("alarm_set", 1);
						editor.putFloat("latitude", (float)(latitude));
						editor.putFloat("longitude", (float)(longitude));
						editor.commit();
						MapOffBtn.setVisibility(View.INVISIBLE);
						MapOnBtn.setVisibility(View.VISIBLE);
						
						map_menu.cancel();

						intent = new Intent(getBaseContext(), MainActivity.class);
						Notification noti = new Notification(R.drawable.map, "매니저가 활성화 되었습니다.",System.currentTimeMillis());
						noti.flags |= Notification.FLAG_ONGOING_EVENT;
						PendingIntent content = PendingIntent.getActivity(notiActivity.this, 0, intent, 0);
						noti.setLatestEventInfo(notiActivity.this, "Smart Reminder", "도착지까지" + sendChoice[arg2]+"m 이내부터 알림을 통지합니다.", content);
						mNotiManager.notify(notiActivity.noti_id2, noti);
						
						finish();
					}
				});
			}break;
		}
	}
	class HowToList{
		HowToList(String name, String distance, String note){
			this.name = name;
			this.distance = distance;
			this.note = note;
		}
		String name, distance, note;
	}
	class MyListAdapter2 extends BaseAdapter{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<HowToList> alist;
		int layout;
		public MyListAdapter2(Context context, int alayout, ArrayList<HowToList> list){
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			alist = list;
			layout = alayout;
		}
		public int getCount() {
			return alist.size();
		}

		public String getItem(int position) {
			return alist.get(position).name;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent, false);
			}
			
			TextView name = (TextView)convertView.findViewById(R.id.howName);
			TextView distance = (TextView)convertView.findViewById(R.id.howDistance);
			TextView note = (TextView)convertView.findViewById(R.id.howNote);
			
			name.setText(alist.get(position).name);
			distance.setText(alist.get(position).distance);
			note.setText(alist.get(position).note);
			return convertView;
		}
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//알람 진행 상태 체크
	    db = sdb.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT state, noti_carry_num FROM SettingDB;", null);
		while(cursor.moveToNext()){
			check_noti_switch = cursor.getInt(0);
			noti_carry_num = cursor.getInt(1);
		}cursor.close();
		btnImageChange(check_noti_switch);
		//List
		arlist = new ArrayList<String>();    	//List생성하기
    	db = im.getReadableDatabase();
        carry = db.rawQuery("SELECT count(*) FROM ItemDB;",null);
        while(carry.moveToNext()){
        	arlist.add("전체");		//1번은 무조건 전체
        }
    	
    	//소유시기와 해당 소유시기들의 컬럼갯수를 불러와서 List에 등록
        db = im.getReadableDatabase();
        carry = db.rawQuery("SELECT icarry, count(*) FROM ItemDB GROUP BY icarry;",null);
        while(carry.moveToNext()){
        		SelectedCarry = carry.getString(0).toString();        		
        		arlist.add(SelectedCarry);         		
        }	        
        Adapter = new ArrayAdapter<String>(notiActivity.this,android.R.layout.simple_list_item_single_choice, arlist);
        carrylist = (ListView)findViewById(R.id.timenoti_list);
        carrylist.setAdapter(Adapter);
        carrylist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        carrylist.setDivider(new ColorDrawable(Color.BLACK));
        carrylist.setDividerHeight(2);   
        
        if(noti_carry_num != -1)carrylist.setItemChecked(noti_carry_num, true);
        carrylist.setOnItemClickListener(mItemClickListener);
        
        myLocationOverlay.enableMyLocation();
	}//onResume()종료
	@Override
	public void onPause(){
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	 private void searchOnMap() {
		 GeoPoint centerGeoPoint = mMap.getMapCenter();   
			latitude = ((float)centerGeoPoint.getLatitudeE6() / 1000000);
	    	longitude = ((float)centerGeoPoint.getLongitudeE6() / 1000000);
	    	sResult.clear();
	        StringBuilder responseBuilder = new StringBuilder();
	        // URL을 이용하여 검색신청을 하고, 결과를 받아 responseBuilder에 저장한다.
	        try {
		         // 검색을 위한 URL 생성
		         URL url = new URL("http://ajax.googleapis.com/ajax/services/search/local?v=1.0&q="+
			         URLEncoder.encode(search, "UTF-8") + 
			         "&sll="+latitude+","+longitude+
			         "&hl=ko"+
			         "&start=0"+
			         "&rsz=8"
		         );  // 8.13 수정
		         BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")); // 8.13 수정
		         String inputLine;
		         while ((inputLine = in.readLine()) != null) {
		        	 responseBuilder.append(inputLine);
		         }
		         in.close();
	        } catch (MalformedURLException me) {me.printStackTrace();} 
	          catch (UnsupportedEncodingException ue) {ue.printStackTrace();} 
	          catch (IOException ie) {ie.printStackTrace();}
	        // 위의 결과를 JSONObject를 이용해 원하는 정보를 parsing 한다
	        try {
		         JSONObject json = new JSONObject(responseBuilder.toString());
		         json = json.getJSONObject("responseData");
		         JSONArray jarray = json.getJSONArray("results");
	         
		         for(int i = 0; i < jarray.length(); i++) {
			          // 결과별로 결과 object 얻기
			          JSONObject jtmp = jarray.getJSONObject(i);
			          String title = jtmp.getString("titleNoFormatting"); // html 이 포함되지 않은 주소
			          // full address 파싱
			          String addr_tmp = jtmp.getString("addressLines"); // addressLines(ket) 값을 얻어온다.
			          String[] addr_tmp2 = addr_tmp.split("\""); // ["...."] 이 구조이기 때문에 중간 문자열만 가지고온다
			          String addr = addr_tmp2[1]; // { "[" , ".....", "]" } 이렇게 되어있는 것에서 중간 문자열을 선택
			          // 주소를 이용하여 위도경도 얻어오기
			          // Geocoder를 이용하면 위도경도로 주소를 얻어오거나
			          // 주소를 이용해 위도 경도의 정보를 얻어 올 수 있다
			          Geocoder geocoder = new Geocoder(notiActivity.this);
			          List<Address> addrList = geocoder.getFromLocationName(addr, 1); // 한개의 주소정보만 얻어온다
			          Address adr_tmp = addrList.get(0); // list에서 사용할 Address Object를 가지고 온다
			          double[] location = new double[2];
			          location[0] = adr_tmp.getLatitude(); // 위도 
			          location[1] = adr_tmp.getLongitude(); // 경도 
			          
			          resultlist = new searchResult(title, addr, location[0], location[1]);
			          sResult.add(resultlist);
		         }
	        } catch (JSONException e) {e.printStackTrace();} 
	          catch (IOException e) {e.printStackTrace();}
	        searchDialog();
	 }
	    public void searchDialog(){
	        //여러가지 검색결과를 보여주기 위한 Dialog
	        View dialog_which = (RelativeLayout)View.inflate(notiActivity.this, R.layout.dialog_which, null);
	        ListView whichList = (ListView)dialog_which.findViewById(R.id.multilist);
	        
	        MyListAdapter MyAdapter = new MyListAdapter(notiActivity.this, R.layout.customlist_which,sResult);
	        TextView header = new TextView(this);
	        header.setText("※ 원하는 검색결과가 없다면 검색어를 더 자세히 입력해보십시오. (예: 스타벅스 양재역)");
	        header.setTextColor(Color.WHITE);
	        whichList.addHeaderView(header);
	        whichList.setAdapter(MyAdapter);
	        whichList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        whichList.setDivider(new ColorDrawable(Color.WHITE));
	        whichList.setDividerHeight(1);
	        whichList.setHeaderDividersEnabled(false);
	        whichList.setOnItemClickListener(new OnItemClickListener(){
	 		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
	 			GeoPoint searchPoint = new GeoPoint((int)(sResult.get(arg2-1).latitude*1000000.0), (int)(sResult.get(arg2-1).longitude*1000000.0));
	 			latitude = sResult.get(arg2-1).latitude;
	 			longitude = sResult.get(arg2-1).longitude;
	 			moveMarker(searchPoint);
	 			searchDialog.cancel();
	 		}    	   
	        });
	        
	        searchDialog = new AlertDialog.Builder(notiActivity.this)
	        .setCancelable(false)
	 		.setTitle("검색결과를 선택하세요.")
	 		.setView(dialog_which)
	 		.setNeutralButton("재검색", null)
	 		.show();
	     }
	//List Itemclick Event
    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
			if(arg2 == 0){
				SelectedCarry="전체";
				db = sdb.getWritableDatabase();
				db.execSQL("UPDATE SettingDB SET noti_carry = '"+SelectedCarry+"', noti_carry_num = '"+arg2+"';");				
			}
			else {
				SelectedCarry=arlist.get(arg2).toString();
				db = sdb.getWritableDatabase();
				db.execSQL("UPDATE SettingDB SET noti_carry = '"+SelectedCarry+"', noti_carry_num = '"+arg2+"';");
			}
			
			db = sdb.getReadableDatabase();
			carry = db.rawQuery("SELECT noti_carry_num FROM SettingDB;", null);
			while(carry.moveToNext()){
				noti_carry_num = carry.getInt(0);
			}			
			Log.i("WhatSave", SelectedCarry);
		}
	};	
	
	//알람방법인 서브메뉴 버튼
	public void pageClick(View v){
		switch(v.getId()){
		case R.id.timenoti_btn:
			timepage.setVisibility(View.VISIBLE);
			timepage_btn.setBackgroundColor(Color.WHITE);
			timepage_btn_text.setTextColor(Color.BLACK);
	    	timepage_btn_text.setPaintFlags(timepage_btn_text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);	
			
			mappage.setVisibility(View.INVISIBLE);
			mappage_btn.setBackgroundColor(Color.LTGRAY);
			mappage_btn_text.setTextColor(Color.WHITE);
			break;
		case R.id.mapnoti_btn:
			mappage.setVisibility(View.VISIBLE);
			mappage_btn.setBackgroundColor(Color.WHITE);
			mappage_btn_text.setTextColor(Color.BLACK);
	    	mappage_btn_text.setPaintFlags(timepage_btn_text.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);	
			
	    	timepage.setVisibility(View.INVISIBLE);
			timepage_btn.setBackgroundColor(Color.LTGRAY);
			timepage_btn_text.setTextColor(Color.WHITE);
			
	        mMap.getController().setZoom(20);
	        mMap.setBuiltInZoomControls(true);
	        myLocationOverlay.runOnFirstFix(new Runnable(){
	        	public void run(){
	        		mMap.getController().animateTo(myLocationOverlay.getMyLocation());
	        	}
	        });
	    	myLocationOverlay.enableMyLocation();
			break;			
		}
	}
    public void widget_btn_on(){
		check_noti_switch = 0;
		sdb = new DB_setting(this);
		db = sdb.getWritableDatabase();
		db.execSQL("UPDATE SettingDB SET state = '"+check_noti_switch+"';");
		mNotiManager.cancel(noti_id);
		offAlarm();			    	
    }
    public void widget_btn_off(){    	
	    check_noti_switch = 1;
		sdb = new DB_setting(this);
		db = sdb.getWritableDatabase();
		db.execSQL("UPDATE SettingDB SET state = '"+check_noti_switch+"';");		
		onNotification();
		onAlarm();
    }
    
	//on/off button 기능
	public void switchClick(View v){
		ComponentName Widget = new ComponentName(notiActivity.this, Widget.class);
		AppWidgetManager awm = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
		switch(v.getId()){
		case R.id.noti_btn_on:			
			check_noti_switch = 0;
			btnImageChange(check_noti_switch);
			db = sdb.getWritableDatabase();
			db.execSQL("UPDATE SettingDB SET state = '"+check_noti_switch+"';");
			mNotiManager.cancel(noti_id);
			offAlarm();		
			timeService.set = 0;
			views.setImageViewResource(R.id.widget_btn, R.drawable.off);
			awm.updateAppWidget(Widget, views);
			break;
		case R.id.noti_btn_off:				
			if(noti_carry_num == -1){
				showDialog(noti_none_select_dialog);
				break;
			}
			
			check_noti_switch = 1;
			btnImageChange(check_noti_switch);
			db = sdb.getWritableDatabase();
			db.execSQL("UPDATE SettingDB SET state = '"+check_noti_switch+"';");		
			onNotification();
			onAlarm();
			timeService.set = 1;
			views.setImageViewResource(R.id.widget_btn, R.drawable.on);
			awm.updateAppWidget(Widget, views);
			
			finish();
			 
			break;
		}
	}
	//시간 기반 알람 구현
	public void onAlarm(){
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender;
		
		intent = new Intent(notiActivity.this, AlarmReceiver.class);
		sender = PendingIntent.getBroadcast(notiActivity.this, 0, intent, 0);
		
		getAlarmLevel();
		Log.i("alarm_cnt", ""+alarm_cnt);
		if(alarm_cnt == 0) am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),dev_mode, sender);
		else am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),j, sender);
	}
	
	public void offAlarm(){
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender;		
		intent = new Intent(notiActivity.this, AlarmReceiver.class);
		sender = PendingIntent.getBroadcast(notiActivity.this, 0, intent, 0);
		am.cancel(sender);
		
		//비활성화 Notification 잠깐 띄움
		Notification notioff = new Notification(R.drawable.time, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
		notioff.flags |= Notification.FLAG_AUTO_CANCEL;
		intent = new Intent(getBaseContext(), MainActivity.class);
		PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
		notioff.setLatestEventInfo(notiActivity.this, "Smart Reminder", "매니저 작동을 중단했습니다.", content);
		mNotiManager.notify(notiActivity.noti_id, notioff);
	}
	
	//알람빈도 구하기
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
	
	//on/off버튼 교체
	public void btnImageChange(int value){
		value = check_noti_switch;
		switch(value){
		case 0:
			switch_btn_off.setVisibility(View.VISIBLE);
			switch_btn_on.setVisibility(View.INVISIBLE);						
			break;
		case 1:
			switch_btn_off.setVisibility(View.INVISIBLE);
			switch_btn_on.setVisibility(View.VISIBLE);		
			break;
		}
	}
	
	public void onNotification(){
		//mNotiManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		intent = new Intent(getBaseContext(), MainActivity.class);
		Notification noti = new Notification(R.drawable.time, "매니저가 활성화 되었습니다.",System.currentTimeMillis());
		noti.flags |= Notification.FLAG_ONGOING_EVENT;
		PendingIntent content = PendingIntent.getActivity(notiActivity.this, 0, intent, 0);
		noti.setLatestEventInfo(notiActivity.this, "Smart Reminder", "매니저가 당신의 소지품을 관찰 중 입니다.", content);
		mNotiManager.notify(notiActivity.noti_id, noti);
	}
	
	public void offNotification(){
		intent = new Intent(getBaseContext(), MainActivity.class);
		NotificationManager NM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		NM.cancel(notiActivity.noti_id);
		
		//비활성화 Notification 잠깐 띄움
		Notification notioff = new Notification(R.drawable.time, "매니저가 비활성화 되었습니다.",System.currentTimeMillis());
		notioff.flags |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent content = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
		notioff.setLatestEventInfo(getBaseContext(), "Smart Reminder", "매니저 작동을 중단했습니다.", content);
		mNotiManager.notify(notiActivity.noti_id, notioff);
	}
	
    //Dialog
    protected Dialog onCreateDialog(int id) {
	   	 switch (id) {
	   	 case noti_on_dialog:	   		 
	   	 case noti_off_dialog:
	   	 case noti_none_select_dialog:
	   		 return new AlertDialog.Builder(this)
	   		 	.setCancelable(false)
	   		 	.setMessage("알람 항목을 선택하세요.")
	   		 	.setPositiveButton("확인", null)
	   		 	.show();
	   	 }
	   	 return null;
    }//onCreateDialog()종료
	class searchResult{
		searchResult(String title, String address, double latitude, double longitude){
			this.title = title;
			this.address = address;
			this.latitude = latitude;
			this.longitude = longitude;
		}
		String title, address;
		double latitude, longitude;
	}
	class MyListAdapter extends BaseAdapter{
		Context maincon;
		LayoutInflater Inflater;
		ArrayList<searchResult> ssResult;
		int layout;
		public MyListAdapter(Context context, int alayout, ArrayList<searchResult> sResult){
			maincon = context;
			Inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ssResult = sResult;
			layout = alayout;
		}
		public int getCount() {
			return ssResult.size();
		}

		public String getItem(int position) {
			return ssResult.get(position).title;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = Inflater.inflate(layout, parent,false);
			}
			TextView title = (TextView)convertView.findViewById(R.id.which_title);
			TextView address = (TextView)convertView.findViewById(R.id.which_address);
			TextView textLati = (TextView)convertView.findViewById(R.id.which_latitude);
			TextView textLongi = (TextView)convertView.findViewById(R.id.which_longitude);
			
			title.setText(ssResult.get(position).title);
			address.setText(ssResult.get(position).address);
			textLati.setText(ssResult.get(position).latitude+"");
			textLongi.setText(ssResult.get(position).longitude+"");
			return convertView;
		}
	}	
    /**
     * 마커를 맵 중앙으로 이동시킴
     * @param centerGeoPoint
     */
     private void moveMarker(GeoPoint geoPoint){
    	mMap.getController().animateTo(geoPoint);
    	addMarker(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());
    	
		latitude = (double)geoPoint.getLatitudeE6() / 1E6;
		longitude = (double)geoPoint.getLongitudeE6() / 1E6;
    };
    
    /**
     * MapView에 마커를 추가함.
     * @param markerLatitude
     * @param markerLongitude
     */
    private void addMarker(int markerLatitude, int markerLongitude){
    	Drawable marker = getResources().getDrawable(R.drawable.pin);
    	marker.setBounds(0,0,marker.getIntrinsicHeight(),marker.getIntrinsicWidth());
    	mMap.getOverlays().add(new MyLocations(marker, markerLatitude, markerLongitude));
    }
	/**
	 * MapView가 터치될 때 일어날 이벤트를 정의
	 */
	class MyLocations extends ItemizedOverlay<OverlayItem>{
		private List<OverlayItem> locations = new ArrayList<OverlayItem>();	
		private Drawable marker;
		private OverlayItem myOverlayItem;
		final SharedPreferences pref = getSharedPreferences("mapService", 0);
		boolean isMove;
		
		public MyLocations(Drawable marker, int latitudeE6, int longitudeE6){
			super(marker);
			this.marker = marker;
			
			GeoPoint myPlace = new GeoPoint(latitudeE6, longitudeE6);
			
			myOverlayItem = new OverlayItem(myPlace, "위치", "현재 위치입니다");
			locations.add(myOverlayItem);
			
			populate();
		}
		
		@Override
		protected OverlayItem createItem(int i){
			return locations.get(i);
		}
		@Override
		public int size(){
			return locations.size();
		}
		@Override
		protected boolean onTap(int index){
			return super.onTap(index);
		}
		@Override
		public void draw(Canvas canvas, MapView mpaView, boolean shadow){
			super.draw(canvas, mpaView, shadow);
			boundCenterBottom(marker);
		}
		@Override
		public boolean onTouchEvent(MotionEvent event, MapView map){
			int action = event.getAction();
			if(action == MotionEvent.ACTION_UP && pref.getInt("alarm_set", 0) != 1){
				if(!isMove){
					Projection proj = mMap.getProjection();
					GeoPoint loc = proj.fromPixels((int)event.getX(), (int)event.getY());
					mMap.getOverlays().clear();
					mMap.getOverlays().add(myLocationOverlay);
					moveMarker(loc);
				}
			}else if(action == MotionEvent.ACTION_DOWN){
				isMove = false;
			}else if(action == MotionEvent.ACTION_MOVE){
				isMove = true;
			}
			return super.onTouchEvent(event, map);
		}
	}
}
