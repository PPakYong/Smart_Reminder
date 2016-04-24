package com.legoist.Smart_Reminder.setting;

import com.legoist.Smart_Reminder.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class patchnoteActivity extends Activity {
	Intent intent;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_patchnoteview);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_bar);
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        TextView title = (TextView)findViewById(R.id.custom_title);
        title.setText("어플리케이션 정보");
        
        
		TextView note = (TextView)findViewById(R.id.app_inform);
		note.setText(	
		"Version: Alpha 0.9.4v\n"+	
		"Note:\n"+
		"1. (fix)알람 리스트 하단이 버튼과 광고로 가려지는 현상 수정\n"+
		"2. (fix)검색 후 위치를 터치로 수동설정 시, 데이터 인식이 안되던 버그 수정\n"+
		"3. (fix)목적지 도착 시 알람 자동종료의 범위를 100m 로 상향조정\n"+
		"4. (fix)터치를 통한 목적지 수동선택 전환 모드 추가\n"+
		"5. (new)위치산정알람이 작동 중일 떄, 검색과 수동선택 전환 모드가 작동하지 않음\n"+
		"6. (new)위치산정알람이 작동 중일 때, 앱의 맵뷰에서 목적지가 고정으로 기록됨.\n"+
		"7. (fix)기타 버그&잔업 수정 및 앱 경량화(-0.8mb)\n\n\n"+

		"Version: Alpha 0.9.3v\n"+
		"Note:\n"+
		"1. (fix)맵 서비스 미작동에 대한 수정\n"+
		"2. (fix)최소 OS Froyo -> GingerBread로 상승\n\n\n"+
				
		"Version: Alpha 0.9.2v\n"+
		"Note:\n"+
		"1. (fix)위치검색 2회 시도 시 강제종료 현상 수정\n"+
		"2. (fix)지도에서 자신의 위치표시가 중첩되는 버그 수정\n"+
		"3. (fix)소지품 정보 수정에서 달력 호출시 생기는 버그 수정\n\n\n"+
		
		"Version: Alpha 0.9.1v\n"+
		"Note:\n"+
		"1. (fix)허니컴 이상부터 위치검색이 안되는 버그 수정\n"+
		"2. (fix)위치 검색시 소프트 키보드로 인해 화면이 밀리는 현상 수정\n\n\n"+
		
		"Version: Alpha 0.9v\n"+
		"Note:\n"+
		"1. (new)맵 서비스 시험단계 시작\n"+
		"2. (fix)모든 대화상자는 뒤로가기키로 종료 불가능\n"+
		"3. (fix)앱에서 매니저 작동 후, 위젯에서 종료 시 발생하는 버그 수정\n"+
		"4. (fix)ICS 이상에서 앱위젯의 사이즈가 2x2로 설정되는 현상 수정\n"+
		"5. (fix)도움말의 마지막 10페이지까지 확인해야 종료버튼 활성화\n"+
		"6. (fix)로딩화면 변경(Designer: DeviL)\n"+
		"7. (fix)기타 버그 수정\n\n\n"+
		
		"Version: Alpha 0.8v\n"+
		"Note:\n"+
		"1. (new)소지품 정보에서 물품명,구입장소 클릭 시 해당 명칭으로 네이버 검색결과 브라우저를 통해 호출\n"+
		"2. (fix)소지품 정보 수정 후 열람기능 복구\n"+
		"3. (fix)앱 설치 후 바로 앱위젯을 작동시키면 생기는 에러 수정\n"+
		"4. (fix)앱 아이콘 일부 미반영 사항 적용\n"+
		"5. (fix)등록소지품 샘플명 구체화\n"+
		"6. (fix)앱 경량화작업(0.2MB 경감)\n\n\n"+

		
		"Version: Alpha 0.7v\n"+
		"Note:\n"+
		"1. (fix)ICS이상에서 매니저 작동 버그 수정\n"+
		"2. (fix)사진이 없는 소지품 수정 시 강제종료되던 버그 수정\n"+
		"3. (fix)'가치측정' 항목 삭제\n"+
		"4. (fix)HD이외의 해상도 기기에서 소지품 정보열람 시 하단버튼이 안보이는 문제 수정\n"+
		"5. (fix)일부 아이콘 변경\n"+
		"6. (fix)각 탭뷰의 하단 광고공간 고정확보\n"+
		"7. (fix)프로그램 코드 간결화\n"+
		"8. (progress)수정 이 후 다시 열람화면으로 돌아가기\n\n\n"+
		
		"Version: Alpha 0.6v\n"+
		"Note:\n"+
		"**2차 테스트**\n"+
		"1. (new)도움말 추가\n"+
		"2. (new)최초 실행시 앱 사용동의 질의 추가\n"+
		"3. (fix)초기화 기능 수행 후에도 사진데이터가 남던 누락사항 추가\n"+
		"4. (fix)좀비 서비스 버그 수정\n\n\n"+
				
		"Version: Alpha 0.5v\n"+
		"Note:\n"+
		"1. (new)앱 광고 추가\n"+
		"2. (new)시간알람 전용 앱 위젯 추가\n"+
		"3. (new)소지품 저장 및 삭제에 효과음 추가\n"+
		"4. (new)위치산정알람을 위한 맵뷰 준비\n"+
		"5. (new)앱 데이터 초기화 설정옵션 추가\n"+
		"6. (new)앱 도움말 설정옵션 추가\n"+
		"7. (fix)사진을 촬영하지 않고 저장하려면 강제 종료되는 버그 수정\n"+
		"8. (fix)직접 소지품명 입력하기 버그 2차 수정\n\n\n"+
		
		"Version: Alpha 0.4v\n"+
		"Note:\n"+
		"**1차테스트**\n"+
		"1. (new)App 이름 변경: Smart Reminder\n"+
		"2. (new)기능 미구현 된 Widget 추가, 계속 작업중\n"+
		"3. (fix)소지품 등록 시, 직접 입력 후 저장하면 다음 직접 입력이 불가능한 버그 수정\n"+
		"4. (new)테스트 버전을 만료기능 추가\n\n\n"+
				
		"Version: Alpha 0.3v\n"+
		"Note:\n"+
		"1. (new)사진 저장&열람 기능 구현\n"+
		"※이미지 저장 경로: /sdcard/InvenManager/photo/\n"+
		"2. (fix)어플 설치 후 첫 실행에서 목록을 선택하지 않고 알람을 작동하면 종료하는 버그 수정\n\n\n"+
				
		"Version: Alpha 0.25v\n"+
		"Note:\n"+
		"1. (fix)알람팝업이 뜨지 않던 버그 긴급수정 \n\n\n"+
				
		"Version: Alpha 0.2v\n"+
		"Note:\n"+
		"1. (new)사진촬영/갤러리를 이용한 사진 가져오기 구현\n"+
		"   *DB에는 저장이 되었으나 불어오질 못하고 있음\n"+
		"2. (fix)소지품정보 열람의 스크롤뷰에 제스처 적용\n"+
		"3. (fix)UI오류사항 xml을 통한 수정\n\n\n"+
		
		"Version: Alpha 0.1v\n"+
		"Note:\n"+
		"1. (new)UI 전면 리모델링\n"+
		"2. (new)소유시기를 활용한 wishlist 구현\n"+
		"3. (new)wishlist를 통한 알람작동\n"+
		"4. (new)초기 입력 방식에 스피너로 변경\n"+
		"5. (new)어플 첫 기동 시 splash(로딩화면) 추가\n"+
		"6. (new)알람과 소지품 열람/수정화면을 대화상자형 액티비티 적용\n"+
		"7. (fix)기타 버그 수정 및 대규모 소스 최적화\n"+
		"8. (progress)사진을 촬영/가져오기 하여 저장하기\n"+
		"9. (progress)목적지를 활용한 위치 산정 알람\n"+
		"10. (progress)기능별 효과음 추가\n\n\n\n\n\n\n"+
		
		"===========prototype test 종료===========\n\n\n"+
		
		"Version: prototype 7.12\n"+
        "Note:\n"+
        "1. (new)알람빈도: 개발자모드 추가(10초마다 알람작동)\n"+
        "2. (new)알람이 작동 중일 때 시스템 상태바에 noti등록\n"+
        "3. (new)알람작동과 알람종료 버튼을 각각 상대적으로 활성화\n"+
        "4. (new)AlarmCheckView에서 알람 즉시 종료기능 추가\n"+
        "5. (fix)AlarmCheckView.java의 속성값을 singleTask로 변경\n"+
        "6. (fix)AlarmCheckView.java의 속성값을 portrait로 변경\n"+
        "7. (fix)알람이 울릴 때 시스템의 진동/무음 상태를 판별하여 상태에 따라 작동\n"+
        "8. (fix)기타 버그 및 오타 수정\n\n\n"+        
        "Version: prototype 7.11SE\n"+
        "Note:\n"+
        "1. (fix)소지품 수정에서 목록확인을 누르면 EditText가 초기화되는 버그 수정\n"+
        "2. (fix)메인의 알람버튼을 on/off로 분할\n"+
        "3. (fix)알람이 울릴 때 장비의 화면이 켜지도록 수정\n"+
        "4. (fix)알람방식 변경 시 Runtime Error 수정\n"+
        "5. (fix)기타 버그 및 코드수정\n\n\n"+        
        "Version: prototype 7.11\n"+
        "Note\n"+
        "1. (new)랜덤알람기능 구현\n"+
        "2. (new)옵션에서 알림설정과 빈도 설정 구현\n"+
        "3. (fix)코드 수정 및 AlarmDB 생성\n\n\n"+        
        "Version: prototype 7.10SE\n"+
        "Note\n"+
        "1.(fix)옵션에서 '이전'버튼 오작동 수정\n"+
        "2.(fix)기타 잔버그와 코드수정\n\n\n"+                
        "Version: prototype 7.10\n"+
        "Note\n"+
        "1. (new)메뉴버튼을 통한  옵션과 도움말 하단메뉴 추가\n"+
        "2. (new)Dialog-Seekbar 구현\n"+
        "추후 DB에 반복횟수를 저장한 공간 마련\n"+
        "3. (new)어플정보, 개발자정보 마련함\n"+
        "4. (new)도움말 샘플 추가\n"+
        "5. (fix)문품분류 -> 물품분류 오타 수정\n\n\n"+        
        "Version: prototype 7.9SE\n"+
        "Note:\n"+
        "1. (fix)db조회가 소지품명 밖에 안되던 버그 해결\n"+
        "2. (fix)마지막 소지품을 삭제할 시 화면 변동이 없던 버그 해결\n"+
        "3. (new)db가 없으면 소지품 열람 화면은 더이상 보이지 않음\n"+
        "4. (new)어플 버전 기록 시작\n"+
        "5. (new)소지품 삭제할 떄 재확인 물음을 추가함\n"+
        "6. (problem)알람버튼 미해결로 인해 임시잠금\n\n\n"+        
        "Version: prototype 7.9\n"+
        "Note:\n"+
        "1. (problem)AlarmRecevier.java 작성\n"+
        "Runtime Error 해결필요\n\n\n"+        
        "Version: prototype 7.5SE\n"+
        "Note:\n"+
        "1. (fix)최종 소지품 삭제 했을 때 empty data로 재조회\n"+
        "2. (fix)마지막 소지품 삭제하면 이전 소지품으로 재조회, 그 이외에는 다음 소지품을 조회\n"+
        "3. (fix)수정기능 Runtime Error수정\n"+
        "4. (new)안드로이드 기기의 '이전'버튼 명령 수정\n"+
        "(MainFrame: 종료를 되묻기)\n"+
        "(그 이외 Frame: 메인으로 이동함)\n"+
        "5. (new)소지품이 아무것도 없을 땐 수정/삭제/이전/다음 버튼은 화면에서 숨겨짐\n\n\n"+        
        "Version: prototype 6.28\n"+
        "Note:\n1. (new)소지품 조회 - 이전/다음/삭제 구현\n"+
        "2. (new)소지품 조회 - 제스쳐를 이용한 다음/이전 기능 작동\n\n\n"+        
        "Version: prototype 0.3v\n"+
        "Note:\n1. (temp)임시UI 구현");
		
		note.setPaintFlags(note.getPaintFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
		
		Button mInform_exit = (Button)findViewById(R.id.inform_exit);
		mInform_exit.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				finish();
			}
		});
	}
}
