package com.legoist.Smart_Reminder.setting;

import com.legoist.Smart_Reminder.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewAnimator;

public class helpActivity extends Activity {
	int cnt=1;
	ImageView view;
	Button before, next, exit;
	TextView title;
	
	//Gesture 기능을 위한 준비
	protected static final float DISTANCE = 0;
	protected static final float VELOCITY = 0;
	GestureDetector mDetector;
	ViewAnimator viewAnimator;
	ScrollView scrollView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_help);
	    
	    view = (ImageView)findViewById(R.id.helpView);
	    before = (Button)findViewById(R.id.helpBefore);
	    next = (Button)findViewById(R.id.helpNext);
	    exit = (Button)findViewById(R.id.helpExit);
	    exit.setVisibility(View.INVISIBLE);
	    title = (TextView)findViewById(R.id.helpTitle);
	    changeHelpImage();
	    
	  //Gesture를 내장
    	 mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
         	@Override
         	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
         		if(Math.abs(velocityX) > 1000 && Math.abs(velocityY) < 500){
         		if(velocityX < 0){
        			if(cnt<10)cnt++;
        			changeHelpImage();
         		}
         		else{
        			if(cnt>0)cnt--;
        			changeHelpImage();
         		}
         	}
				return false;
         	}
         });			
		//mDetector.setIsLongpressEnabled(false);
    }
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev){
    	if(mDetector.onTouchEvent(ev)){
    		return true;
    	}
    	return super.dispatchTouchEvent(ev);
    }
	public void helpClick(View v){
		switch(v.getId()){
		case R.id.helpBefore:
			if(cnt>0)cnt--;
			changeHelpImage();
			break;
		case R.id.helpNext:
			if(cnt<10)cnt++;
			changeHelpImage();
			break;
		case R.id.helpExit:
			finish();
		}
	}
	
	public void changeHelpImage(){
		switch(cnt){
		case 10:
			view.setImageResource(R.drawable.help10);
			next.setVisibility(View.INVISIBLE);
			exit.setVisibility(View.VISIBLE);
			break;
		case 9:
			view.setImageResource(R.drawable.help9);
			if(next.getVisibility() == View.INVISIBLE)next.setVisibility(View.VISIBLE);
			if(exit.getVisibility() == View.VISIBLE)exit.setVisibility(View.INVISIBLE);
			title.setText("앱위젯 도움말");
			break;
		case 8:
			view.setImageResource(R.drawable.help8);
			title.setText("매니저 알림창 도움말");
			break;
		case 7:
			view.setImageResource(R.drawable.help7);
			title.setText("매니저 설정 도움말");
			break;
		case 6:
			view.setImageResource(R.drawable.help6);
			title.setText("소지품 등록 도움말");
			break;
		case 5:
			view.setImageResource(R.drawable.help5);
			break;
		case 4:
			view.setImageResource(R.drawable.help4);
			break;
		case 3:
			view.setImageResource(R.drawable.help3);
			title.setText("매니저 메뉴 도움말");
			break;
		case 2:
			view.setImageResource(R.drawable.help2);
			if(before.getVisibility() == View.INVISIBLE)before.setVisibility(View.VISIBLE);
			break;
		case 1:
			view.setImageResource(R.drawable.help1);
			before.setVisibility(View.INVISIBLE);
			break;
		}
	}
}
