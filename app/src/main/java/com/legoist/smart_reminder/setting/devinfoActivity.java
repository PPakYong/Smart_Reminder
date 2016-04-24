package com.legoist.Smart_Reminder.setting;

import com.legoist.Smart_Reminder.R;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class devinfoActivity extends Activity {
	Intent intent;
	SoundPool pool;
	int select;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_devinfo);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView title = (TextView)findViewById(R.id.custom_title);
        title.setText("개발자 정보");
		
		TextView member = (TextView)findViewById(R.id.dev_scroll_text1);
		member.setText("since 2011.9: 1기 구성\n"+
				"Project Manager: Park Yong Hyeon\n"+
		        "Documentation: Kim Seung Hyuk\n"+
		        "Quality Assurance: Park Je Sung\n\n\n"+
		        "Developments:\n"+
		        "Android Application - Smart Reminder\n\n");
		Button mInform_exit = (Button)findViewById(R.id.dev_exit);
		mInform_exit.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
	}
}
