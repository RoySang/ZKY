package sict.zky.setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.DeskClockMainActivity;
import sict.zky.deskclock.GetuiSdkDemoActivity;
import sict.zky.deskclock.R;
import sict.zky.main.Binding_EquipmentActivity;
import sict.zky.main.GreenChannelActivity;
import sict.zky.main.HealthKnowledgeActivity;
import sict.zky.main.ImageActivity;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.main.TestSplashActivity;
import sict.zky.service.Getui_storeService;
import sict.zky.utils.Config;
import sict.zky.utils.UpdateManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingActivity extends Activity {
	// private LinearLayout btlinear;
	private LinearLayout userlinear;

	private LinearLayout reminderlinear;
	private LinearLayout helplinear;
	private LinearLayout deviceinfolinear;
	private LinearLayout greenchannellinear;
	private LinearLayout healthknowledgelinear;
	private LinearLayout messagelinear;
	private LinearLayout aboutuslinear;

	private LinearLayout checkupdatelinear;

	private LinearLayout exitlinear;

	private int userId;
	private String screenName;
	private SharedPreferences sp;
	private SharedPreferences rememberclientId;
	private String phone1;
	private String phone2;
	private String phone3;
	private String agencyID;
	private String agencyName;
	private ProgressDialog mDialog;
	private ImageView mymessageimage;
	private Getui_storeService getui_storeService;

	private UpdateManager mUpdateManager;

	private long firstTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		SysApplication.getInstance().addActivity(this);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		
		rememberclientId = this.getSharedPreferences("rememberclientId",
				Context.MODE_WORLD_READABLE);

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		// intent.getClass();
		screenName = intent.getStringExtra("screenName");
		agencyID = "1";

		// accountlinear = (LinearLayout)
		// findViewById(R.id.settings_account_layout);
		reminderlinear = (LinearLayout) findViewById(R.id.settings_reminder_layout);
		helplinear = (LinearLayout) findViewById(R.id.settings_helpdocument_layout);
		deviceinfolinear = (LinearLayout) findViewById(R.id.settings_deviceinformation_layout);
		greenchannellinear = (LinearLayout) findViewById(R.id.settings_greenchannel_layout);
		healthknowledgelinear = (LinearLayout) findViewById(R.id.settings_healthknowledge_layout);
		aboutuslinear = (LinearLayout) findViewById(R.id.settings_aboutus_layout);
		messagelinear = (LinearLayout) findViewById(R.id.settings_mymessage_layout);
		checkupdatelinear = (LinearLayout) findViewById(R.id.settings_checkupdate_layout);
		exitlinear = (LinearLayout) findViewById(R.id.settings_exit_layout);

		messagelinear.setOnClickListener(onclicklistener);
		reminderlinear.setOnClickListener(onclicklistener);
		helplinear.setOnClickListener(onclicklistener);
		deviceinfolinear.setOnClickListener(onclicklistener);
		greenchannellinear.setOnClickListener(onclicklistener);
		healthknowledgelinear.setOnClickListener(onclicklistener);
		aboutuslinear.setOnClickListener(onclicklistener);
		checkupdatelinear.setOnClickListener(onclicklistener);
		exitlinear.setOnClickListener(onclicklistener);
		

		mymessageimage=(ImageView)findViewById(R.id.mymessageimage);
		
		getui_storeService=new Getui_storeService(getApplicationContext());
		if(getui_storeService.ishavenoreadmsg(userId)){
			mymessageimage.setImageResource(R.drawable.ic_list_message1);
		}
		
		
//		mUpdateManager = new UpdateManager(SettingActivity.this);
//		mUpdateManager.checkUpdateInfo();
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		TestinAgent.onStop(this);
	}

	public OnClickListener onclicklistener = new OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.settings_reminder_layout:// 提醒
				Intent intent1 = new Intent();
				intent1.setClass(SettingActivity.this,
						DeskClockMainActivity.class);
				SettingActivity.this.startActivity(intent1);

				break;
			case R.id.settings_helpdocument_layout:// 帮助文档
				Intent intent2 = new Intent();
				intent2.setClass(SettingActivity.this,
						ConductHelpActivity.class);
				SettingActivity.this.startActivity(intent2);
				break;

			case R.id.settings_mymessage_layout:// 我的消息

				Intent intent3 = new Intent(SettingActivity.this,
						Getui_MSG_listActivity.class);
				intent3.putExtra("userId", userId);
				SettingActivity.this.startActivity(intent3);

				break;
			case R.id.settings_deviceinformation_layout:// 设备帮助信息

				Intent intent4 = new Intent(SettingActivity.this,
						DeviceInformationActivity.class);
				intent4.putExtra("userId", userId);

				startActivity(intent4);

				break;

			case R.id.settings_healthknowledge_layout:// 健康知识
				Intent intent5 = new Intent(SettingActivity.this,
						HealthKnowledgeActivity.class);
				intent5.putExtra("userId", userId);

				startActivity(intent5);

				break;
			case R.id.settings_greenchannel_layout:// 绿色通道

				Intent intent6 = new Intent(SettingActivity.this,
						GreenChannelActivity.class);
				intent6.putExtra("userId", userId);
				startActivity(intent6);

				break;

			case R.id.settings_aboutus_layout:// 关于我们

//				Intent intent8 = new Intent(SettingActivity.this,
//						AboutusActivity.class);
				Intent intent8 = new Intent(SettingActivity.this,
						AboutusActivity.class);
				intent8.putExtra("userId", userId);
				startActivity(intent8);

				break;

			case R.id.settings_checkupdate_layout:// 检查更新

				// Intent intent9 =new
				// Intent(SettingActivity.this,GreenChannelActivity.class);
				// intent9.putExtra("userId", userId);
				// startActivity(intent9);
				// Toast.makeText(getApplicationContext(), "即将推出，敬请期待",
				// 1).show();

				// 这里来检测版本是否需要更新
				mUpdateManager = new UpdateManager(SettingActivity.this);
				mUpdateManager.checkUpdateInfo();

				break;
			case R.id.settings_exit_layout:// 注销
				Editor editor = sp.edit();
				editor.remove("USER_NAME");
				editor.remove("PASSWORD");
//				editor.remove("ISCHECK");
				editor.remove("USER_ID");
//				editor.remove("AUTO_ISCHECK");
				editor.clear();
//				editor.notifyAll();
				editor.commit();
				Editor editor1 = rememberclientId.edit();
				editor1.remove("count");
				editor1.commit();
				Intent intent = new Intent(SettingActivity.this,
						MainActivity.class);
				intent.putExtra("userId", -1);
				startActivity(intent);
				Toast.makeText(getApplicationContext(), "已成功注销", 0).show();
				finish();

				break;

			default:
				break;

			}
		}
	};

	// 双击退出
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次返回键退出中科云健康", Toast.LENGTH_SHORT).show();
				firstTime = secondTime;
				return true;
			} else {
				SysApplication.getInstance().exit();
			}
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void opengetui(View v){
		
	}

	@Override
	protected void onResume() {
		getui_storeService=new Getui_storeService(getApplicationContext());
		if(getui_storeService.ishavenoreadmsg(userId)){
			mymessageimage.setImageResource(R.drawable.ic_list_message1);
		}else{
			mymessageimage.setImageResource(R.drawable.ic_list_message);
		}
		super.onResume();
	}
	
	
}
