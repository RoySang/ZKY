package sict.zky.setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.DeskClockMainActivity;
import sict.zky.deskclock.R;
import sict.zky.main.GreenChannelActivity;
import sict.zky.main.HealthKnowledgeActivity;
import sict.zky.main.ImageActivity;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
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
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingofVisitorActivity extends Activity {
	private LinearLayout btlinear;
	private LinearLayout userlinear;
	private LinearLayout accountlinear;
	private LinearLayout reminderlinear;
	private LinearLayout helplinear;
	private LinearLayout loginlinear;
	private LinearLayout greenchannellinear;
	private LinearLayout healthknowledgelinear;
	private LinearLayout aboutuslinear;
	private LinearLayout checkupdatelinear;
	private int userId;
	private String screenName;
	private SharedPreferences sp;
	private String phone1;
	private String phone2;
	private String phone3;
	private String agencyID;
	private String agencyName;
	private ProgressDialog mDialog;
	private UpdateManager mUpdateManager;

	private long firstTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingofvisitor);
		SysApplication.getInstance().addActivity(this);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

		// Intent intent =getIntent();
		// userId=intent.getIntExtra("userId",0);
		// intent.getClass();
		// screenName=intent.getStringExtra("screenName");
		// agencyID="1";

		// btlinear = (LinearLayout)
		// findViewById(R.id.settings_bindingdevice_layout);
		userlinear = (LinearLayout) findViewById(R.id.visitor_settings_deviceinformation_layout);
		// accountlinear = (LinearLayout)
		// findViewById(R.id.settings_account_layout);
		reminderlinear = (LinearLayout) findViewById(R.id.visitor_settings_reminder_layout);
		helplinear = (LinearLayout) findViewById(R.id.visitor_settings_helpdocument_layout);
		loginlinear = (LinearLayout) findViewById(R.id.settings_login_layout);
		// greenchannellinear=(LinearLayout)findViewById(R.id.settings_greenchannel_layout);
		healthknowledgelinear = (LinearLayout) findViewById(R.id.visitor_settings_healthknowledge_layout);
		aboutuslinear = (LinearLayout) findViewById(R.id.visitor_settings_aboutus_layout);

		checkupdatelinear = (LinearLayout) findViewById(R.id.visitor_settings_checkupdate_layout);
		// btlinear.setOnClickListener(onclicklistener);
		userlinear.setOnClickListener(onclicklistener);

		reminderlinear.setOnClickListener(onclicklistener);
		helplinear.setOnClickListener(onclicklistener);
		loginlinear.setOnClickListener(onclicklistener);
		// greenchannellinear.setOnClickListener(onclicklistener);
		healthknowledgelinear.setOnClickListener(onclicklistener);
		aboutuslinear.setOnClickListener(onclicklistener);
		checkupdatelinear.setOnClickListener(onclicklistener);
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
			case R.id.visitor_settings_reminder_layout:// 提醒
				Intent intent1 = new Intent();
				intent1.setClass(SettingofVisitorActivity.this,
						DeskClockMainActivity.class);
				SettingofVisitorActivity.this.startActivity(intent1);

				break;
			case R.id.visitor_settings_helpdocument_layout:// 帮助文档
				Intent intent2 = new Intent();
				intent2.setClass(SettingofVisitorActivity.this,
						ConductHelpActivity.class);
				SettingofVisitorActivity.this.startActivity(intent2);
				break;

			case R.id.visitor_settings_deviceinformation_layout:// 设备帮助信息

				Intent intent3 = new Intent();
				intent3.setClass(SettingofVisitorActivity.this,
						DeviceInformationActivity.class);
				SettingofVisitorActivity.this.startActivity(intent3);

				break;

			case R.id.visitor_settings_healthknowledge_layout:// 健康知识
				Intent intent4 = new Intent(SettingofVisitorActivity.this,
						HealthKnowledgeActivity.class);
				intent4.putExtra("userId", userId);
				startActivity(intent4);

				break;
			case R.id.visitor_settings_aboutus_layout:// 关于我们

				Intent intent8 = new Intent(SettingofVisitorActivity.this,
						AboutusActivity.class);
				intent8.putExtra("userId", userId);
				startActivity(intent8);

				break;

			case R.id.visitor_settings_checkupdate_layout:// 检查更新
				mUpdateManager = new UpdateManager(
						SettingofVisitorActivity.this);
				mUpdateManager.checkUpdateInfo();

				break;

			case R.id.settings_login_layout:// 登录
				// Editor editor = sp.edit();
				// editor.remove("USER_NAME");
				// editor.remove("PASSWORD");
				// editor.remove("ISCHECK");
				// editor.remove("USER_ID");
				// editor.remove("AUTO_ISCHECK");
				// editor.commit();
				sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				sp.edit().putBoolean("ISCHECK", false).commit();
				Intent intent = new Intent(SettingofVisitorActivity.this,
						LoginActivity.class);
				// intent.putExtra("userId", -1);
				startActivity(intent);
				// Toast.makeText(getApplicationContext(), "已成功注销", 0).show();
				// finish();

				break;

			default:
				break;

			}
		}
	};
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				Toast.makeText(SettingofVisitorActivity.this,
						"代理商: " + agencyName + ", 电话: " + phone1, 0).show();
				Intent intent6 = new Intent();
				intent6.setAction("android.intent.action.CALL");
				// intent.addCategory("android.intent.category.DEFAULT");
				intent6.setData(Uri.parse("tel:" + phone1));
				startActivity(intent6);
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "查询失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class GreenChannelThread implements Runnable {

		public void run() {

			String str = greenChannelServer(agencyID);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else {

				msg.what = 1;
				handler.sendMessage(msg);
			}

		}

	}

	private String greenChannelServer(String agencyID) {
		String str = "";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/ZKYweb/GreenChannelServlet");
			// URL url = new
			// URL("http://localhost:8080/ZKYweb/GreenChannelServlet");
			URL url = new URL(Config.IPaddress + "/ZKYweb/GreenChannelServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/GreenChannelServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj = new JSONObject();
			obj.put("agencyID", agencyID);

			pw.write(obj.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer("");
			String result = "";
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			result = sb.toString();

			JSONObject jobj = new JSONObject(result);
			if (jobj != null) {
				str = "success";
				agencyName = jobj.getString("agencyName");
				phone1 = jobj.getString("phone1");
				// phonenumber2=jobj.getString("phone2");
				// phonenumber3=jobj.getString("phone3");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}

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
}
