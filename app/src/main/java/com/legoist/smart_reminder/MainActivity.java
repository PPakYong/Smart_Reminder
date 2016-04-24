package com.legoist.Smart_Reminder;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;

import com.legoist.Smart_Reminder.EnrollItem.enrollActivity;
import com.legoist.Smart_Reminder.setting.settingActivity;
public class MainActivity extends TabActivity {
	TabHost mTab;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        startActivity(new Intent(this, com.legoist.Smart_Reminder.splashActivity.class));
        mTab = getTabHost(); 
        
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.activity_main, mTab.getTabContentView(), true);
        
        Drawable tabIcon1 = getResources().getDrawable(R.drawable.alarm);
        mTab.addTab(mTab.newTabSpec("alarm")
        		.setIndicator("매니저 메뉴",tabIcon1)
        		.setContent(new Intent(this, com.legoist.Smart_Reminder.notiActivity.class)));
        
        Drawable tabIcon2 = getResources().getDrawable(R.drawable.list);
        mTab.addTab(mTab.newTabSpec("list")
        		.setIndicator("소지품 목록",tabIcon2)
        		.setContent(new Intent(this, com.legoist.Smart_Reminder.listActivity.class)));
        
        Drawable tabIcon3 = getResources().getDrawable(R.drawable.enroll);
        mTab.addTab(mTab.newTabSpec("enroll")
        		.setIndicator("소지품 등록",tabIcon3)
        		.setContent(new Intent(this, enrollActivity.class)));   
        
        Drawable tabIcon4 = getResources().getDrawable(R.drawable.setting);
        mTab.addTab(mTab.newTabSpec("setting")
        		.setIndicator("매니저 설정",tabIcon4)
        		.setContent(new Intent(this, settingActivity.class))); 
    }
}