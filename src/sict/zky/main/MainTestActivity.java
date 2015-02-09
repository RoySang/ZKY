package sict.zky.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.deskclock.GetuiSdkDemoActivity;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.domain.Pc_user;
import sict.zky.input.inputActivity;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.setting.AdduserActivity;
import sict.zky.testBPM.StartTestBGMActivity;
import sict.zky.testBPM.StartTestBPMActivity;
import sict.zky.testBPM.TestBGMBLEActivity;
import sict.zky.utils.Config;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.GetPc_data;
import sict.zky.utils.Uploadpc_bgdata;
import sict.zky.utils.Uploadpc_data;
import sict.zky.utils.Uploadpc_user;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class MainTestActivity extends Activity {

	private TextView showcid;

	private RadioGroup group;
	private TabHost tabHost;
	private ProgressBar progressBar;
	public static final String TAB_jiating = "tabjiating";
	public static final String TAB_tongji = "tabtongji";
	public static final String TAB_test = "tabtest";
	public static final String TAB_gongshi = "tabgongshi";
	public static final String TAB_setting = "tabsetting";

	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;
	private GetPc_data getpc_data;
	// private GetPc_bgdata getpc_bgdata;
	private Uploadpc_data uploadpc_data;
	private Uploadpc_bgdata uploadpc_bgdata;
	private Uploadpc_user uploadpc_user;
	private CurrentTime ct = new CurrentTime();
	private String currentTime;

	// private Pc_user pc_user;
	private Pc_userService pc_userService;

	private Button testbloodpressureButton, testbloodglucoseButton,
			datacurveButton, showproductButton, manualinputButton,
			settingButton;
	private long firstTime = 0;

	private boolean isExit;
	private int userId;
	private String screenName;
	private int type;

	private SharedPreferences rememberclientId;
	private String cid;
	private String appkey = "";
	private String appsecret = "";
	private String appid = "";
	private SharedPreferences sp;

	private boolean getUsersresult = false;// 判断家庭成员是否更新成功

	// private Pc_userService pc_userService;

	// private String SN = "L15280814";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SysApplication.getInstance().addActivity(this);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		screenName = sp.getString("USER_NAME", "");
		// Toast.makeText(getApplicationContext(), "screenName"+screenName,
		// 1).show();

		// if(high_>0&&low_>0){
		// Intent intent1= new Intent(MainTestActivity.this,
		// GetuiSdkDemoActivity.class);
		// intent1.putExtra("userId", userId);
		// intent1.putExtra("screenName", screenName);
		// intent1.putExtra("high", high_);
		// intent1.putExtra("low", low_);
		// intent1.putExtra("pulse", pulse_);
		// intent1.putExtra("uploadTime", uploadTime_);
		// MainTestActivity.this.startActivity(intent1);
		// finish();
		// }

		showcid = (TextView) this.findViewById(R.id.showcid);
		if (TestSplashActivity.clientId != "" && userId > 0) {
			showcid.setText("当前状态：可以收到推送消息");
		}

		if (TestSplashActivity.clientId == "" && userId > 0) {
			showcid.setText("当前状态：不能收到推送消息,请稍候");
		}

		progressBar = (ProgressBar) this.findViewById(R.id.round_progressbar);
		progressBar.setVisibility(View.INVISIBLE);

		testbloodpressureButton = (Button) this
				.findViewById(R.id.testbloodpressureButton);
		testbloodglucoseButton = (Button) this
				.findViewById(R.id.testbloodglucoseButton);
		// datacurveButton = (Button) this.findViewById(R.id.datacurveButton);
		// showproductButton = (Button)
		// this.findViewById(R.id.showproductButton);
		manualinputButton = (Button) this.findViewById(R.id.manualinputButton);
		// settingButton = (Button) this.findViewById(R.id.settingButton);

		pc_userService = new Pc_userService(getApplicationContext());
		// pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());
		getpc_data = new GetPc_data(getApplicationContext(), false);
		// getpc_bgdata = new GetPc_bgdata(getApplicationContext());
		uploadpc_data = new Uploadpc_data(getApplicationContext());
		uploadpc_bgdata = new Uploadpc_bgdata(getApplicationContext());
		uploadpc_user = new Uploadpc_user(getApplicationContext());

		if (!pc_userService.ishaveUser(userId)) {
			pc_userService.insert(new Pc_user(userId, screenName, 0, "本人", 0));
		}

		if (userId > 0) {
			ct.getCurrentTime();
			currentTime = ct.TimeToString();

			progressBar.setVisibility(View.VISIBLE);

			Thread getusers = new Thread(new GetUsersThread());
			getusers.start();

			// if(getUsersresult){
			// Thread get_dataThread = new Thread(new Get_dataThread());
			// get_dataThread.start();
			// }

			uploadpc_user.setUserId(userId);
			uploadpc_user.uploadpc_user(userId);

			uploadpc_data.setUserId(userId);
			uploadpc_data.uploadpc_data(userId);

			uploadpc_bgdata.setUserId(userId);
			uploadpc_bgdata.uploadpc_bgdata(userId);

		}

		testbloodpressureButton.setOnClickListener(new MainListener());
		testbloodglucoseButton.setOnClickListener(new MainListener());

		manualinputButton.setOnClickListener(new MainListener());

	}

	@Override
	protected void onStart() {
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		TestinAgent.onStop(this);
	}

	// Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case 0:
	// Toast.makeText(getApplicationContext(), "上传成功", 1).show();
	// break;
	//
	// case 1:
	// Toast.makeText(getApplicationContext(), "上传失败", 1).show();
	// break;
	// case 2:
	// Toast.makeText(getApplicationContext(), "访问失败", 1).show();
	// break;
	// }
	//
	// }
	// };

	//

	/*
	 * class ClientThread implements Runnable {
	 * 
	 * public void run() { String str = clientServer(appid,
	 * TestSplashActivity.clientId, userId, SN); Message msg =
	 * handler.obtainMessage(); // if (str.trim().equals("success")) { if
	 * (str.equals("success")) { msg.what = 0; handler.sendMessage(msg); } else
	 * if (str.equals("failed")) { msg.what = 1; handler.sendMessage(msg); }
	 * else { msg.what = 2; handler.sendMessage(msg); }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * private String clientServer(String appid, String cid, int userId, String
	 * SN) { String str = ""; try {
	 * 
	 * // URL url = new //
	 * URL("http://192.168.136.9:8080/Dzkyappweb/LoginServlet"); // URL url =
	 * new // URL("http://localhost:8080/Dzkyappweb/LoginServlet"); URL url =
	 * new URL(Config.IPaddress + "/ZKYweb/ClientServlet"); // URL url = new //
	 * URL("http://192.168.0.100:8080/ZKYweb/LoginServlet"); // URL url = new
	 * URL("http://121.42.32.103:80/ZKYweb/LoginServlet");
	 * 
	 * HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	 * conn.setDoInput(true); conn.setDoOutput(true);
	 * conn.setRequestMethod("POST"); conn.addRequestProperty("Content-Type",
	 * "application/x-www-form-urlencoded"); conn.connect(); PrintWriter pw =
	 * new PrintWriter(conn.getOutputStream());
	 * 
	 * JSONObject obj = new JSONObject(); obj.put("cid", cid); obj.put("userId",
	 * userId); obj.put("appid", appid); obj.put("SN", SN); // Log.v("aaa",
	 * obj.toString()); // String passmd5 = MD5(password_); // String encryptmd5
	 * = encryptmd5(passmd5); // obj.put("password_", password);
	 * 
	 * pw.write(obj.toString()); pw.flush(); pw.close();
	 * 
	 * BufferedReader br = new BufferedReader(new InputStreamReader(
	 * conn.getInputStream(), "UTF-8")); str = br.readLine();
	 * 
	 * } catch (Exception e) { e.printStackTrace(); //
	 * System.out.println("aaaaaaaaaaaaa++++" + e.toString()); }
	 * 
	 * return str; }
	 */

	public class MainListener implements OnClickListener {
		public void onClick(View v) {
			if (v.getId() == R.id.testbloodpressureButton) {
				// // 判断设备是否支持蓝牙4.0
				// if (getPackageManager().hasSystemFeature(
				// PackageManager.FEATURE_BLUETOOTH_LE)) {
				// Intent intent = new Intent();
				// intent.setClass(MainTestActivity.this,
				// StartTestBPMActivity.class);
				// intent.putExtra("userId", userId);
				// intent.putExtra("screenName", screenName);
				// MainTestActivity.this.startActivity(intent);
				//
				// } else {
				//
				// Toast.makeText(getApplicationContext(), "设备不支持蓝牙4.0",
				// Toast.LENGTH_SHORT).show();
				//
				// }

				if (userId > 0) {
					if (TestSplashActivity.clientId != "") {
						Toast.makeText(getApplicationContext(),
								"请打开血压计测量，等待测量结果。", 1).show();
					}
					if (TestSplashActivity.clientId == "") {
						Toast.makeText(getApplicationContext(),
								"当前状态:不能收到透传测量结果，请稍候", 1).show();

					}

				} else {
					Toast.makeText(getApplicationContext(), "请先登录账号", 1).show();
				}

			}
			if (v.getId() == R.id.testbloodglucoseButton) {

				if (userId > 0) {
					// if (getPackageManager().hasSystemFeature(
					// PackageManager.FEATURE_BLUETOOTH_LE)) {
					// Intent intent = new Intent();
					// intent.setClass(MainTestActivity.this,
					// StartTestBGMActivity.class);
					// intent.putExtra("userId", userId);
					// intent.putExtra("screenName", screenName);
					// MainTestActivity.this.startActivity(intent);
					// } else {
					Intent intent = new Intent();
					intent.setClass(MainTestActivity.this,
							TestBGMBLEActivity.class);
					intent.putExtra("userId", userId);
					intent.putExtra("screenName", screenName);
					MainTestActivity.this.startActivity(intent);
					// Toast.makeText(getApplicationContext(), "设备不支持蓝牙4.0",
					// Toast.LENGTH_SHORT).show();
					// }
				} else {
					Toast.makeText(getApplicationContext(), "请先登录账号",
							Toast.LENGTH_SHORT).show();
				}

			}
			if (v.getId() == R.id.manualinputButton) {
				Intent intent = new Intent();
				intent.setClass(MainTestActivity.this, inputActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("screenName", screenName);
				MainTestActivity.this.startActivity(intent);
				// finish();
			}

		}
	}

	public void tongbu(View v) throws InterruptedException {

		ct.getCurrentTime();
		currentTime = ct.TimeToString();
		if (userId > 0) {

			progressBar.setVisibility(View.VISIBLE);

			Thread getusers = new Thread(new GetUsersThread());
			getusers.start();

			// if(getUsersresult){
			// Thread get_dataThread = new Thread(new Get_dataThread());
			// get_dataThread.start();
			// }

			uploadpc_user.setUserId(userId);
			uploadpc_user.uploadpc_user(userId);

			uploadpc_data.setUserId(userId);
			uploadpc_data.uploadpc_data(userId);

			uploadpc_bgdata.setUserId(userId);
			uploadpc_bgdata.uploadpc_bgdata(userId);

		} else {
			Toast.makeText(getApplicationContext(), "请登录", 1).show();
			Intent intent0 = new Intent(MainTestActivity.this,
					LoginActivity.class);
			startActivity(intent0);
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				getUsersresult = true;
				// Toast.makeText(MainTestActivity.this, "家庭成员同步成功",
				// Toast.LENGTH_SHORT).show();

				break;
			case 2:
				getUsersresult = false;
				progressBar.setVisibility(View.INVISIBLE);
				break;

			}

		}
	};

	class GetUsersThread implements Runnable {

		public void run() {

			String str = GetUsersServer(userId);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				Thread get_dataThread = new Thread(new Get_dataThread());
				get_dataThread.start();
				msg.what = 0;
				handler.sendMessage(msg);
			} else if (str.equals("0")) {
				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				msg.what = 2;
				handler.sendMessage(msg);

			}

		}
	}

	private String GetUsersServer(int userId) {
		String str = "false";

		try {

			URL url = new URL(Config.IPaddress + "/ZKYweb/GetUsersServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj1 = new JSONObject();
			obj1.put("userId", userId);
			pw.write(obj1.toString());
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

			JSONArray array = new JSONArray(result);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				Pc_user pc_user = new Pc_user(userId,
						obj.getString("userName"), obj.getInt("familyMember"),
						obj.getString("familyRole"), 1);
				if (!pc_userService.ishaveUserbyuserName(obj
						.getString("userName"))) {
					pc_userService.insert(pc_user);
				}
			}
			str = "success";
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("eeeeeeeeeeee"+e.toString());
		}

		return str;

	}

	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// mDialog.cancel();
				// progressBar.findViewById(R.id.round_progressbar);
				progressBar.setVisibility(View.INVISIBLE);
				// downloaded=true;
				// setDownloaded(true);
				Toast.makeText(MainTestActivity.this, "同步成功",
						Toast.LENGTH_SHORT).show();

				break;
			case 1:
				// mDialog.cancel();
				// downloaded=true;
				// setDownloaded(true);
				progressBar.setVisibility(View.INVISIBLE);

				break;
			case 2:
				// mDialog.cancel();
				// downloaded=true;
				// setDownloaded(true);
				progressBar.setVisibility(View.INVISIBLE);

				break;
			case 3:
				// mDialog.cancel();
				// downloaded=true;
				// setDownloaded(true);
				progressBar.setVisibility(View.INVISIBLE);
				Toast.makeText(MainTestActivity.this, "同步失败",
						Toast.LENGTH_SHORT).show();

				break;
			}

		}
	};

	class Get_dataThread implements Runnable {

		public void run() {

			String str = Get_pcdataServer(userId);
			String str1 = Get_pcbgdataServer(userId);
			Message msg = handler1.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success") && str1.equals("success")) {
				msg.what = 0;
				handler1.sendMessage(msg);
			} else if (!str.equals("success")) {

				msg.what = 1;
				handler1.sendMessage(msg);
			} else if (!str1.equals("success")) {

				msg.what = 2;
				handler1.sendMessage(msg);
			} else {
				msg.what = 3;
				handler1.sendMessage(msg);
			}

		}

	}

	private String Get_pcdataServer(int userId) {
		String str = "";
		try {
			Pc_userService pc_userService = new Pc_userService(
					MainTestActivity.this);
			Pc_dataService pc_dataService = new Pc_dataService(
					MainTestActivity.this);
			Pc_data pc_data;
			URL url = new URL(Config.IPaddress + "/ZKYweb/GetPc_dataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
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

			// Toast.makeText(getApplicationContext(), result, 1).show();
			JSONArray array = new JSONArray(result);

			// System.out.println(array.toString());
			// List<HashMap<String, Object>> data = new
			// ArrayList<HashMap<String, Object>>();
			System.out.println(result);
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj1 = array.getJSONObject(i);

				int systolicPressure = obj1.getInt("systolicPressure");
				int diastolicPressure = obj1.getInt("diastolicPressure");
				int pulse = obj1.getInt("pulse");
				// String screenName = obj1.getString("screenName");
				String uploadTime = obj1.getString("uploadTime");
				int familyMember_ = obj1.getInt("familyMember");
				String userName = pc_userService.getuserNamebyfamilyMember(
						familyMember_, userId);
				// System.out.println("eeeee"+familyMember_+" , "+userId+" , "+userName);
				pc_data = new Pc_data(userId, systolicPressure,
						diastolicPressure, pulse, uploadTime, userName,
						screenName, familyMember_);
				// Pc_dataService pc_dataService0=new
				// Pc_dataService(GetPc_dataActivity.this);
				if (!pc_dataService.ishavepc_data(userId, userName,
						systolicPressure, diastolicPressure, pulse, uploadTime)) {
					pc_dataService.insert(pc_data);
					// System.out.println("eeeee"+pc_dataService.ishavepc_data(userId,
					// userName,
					// systolicPressure, diastolicPressure, pulse, uploadTime));

				}
			}
			str = "success";

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("eeeeeeeeeee" + e.toString());
		}

		return str;

	}

	private String Get_pcbgdataServer(int userId) {
		String str = "";
		try {
			Pc_userService pc_userService = new Pc_userService(
					MainTestActivity.this);
			Pc_bgdataService pc_bgdataService = new Pc_bgdataService(
					MainTestActivity.this);
			Pc_bgdata pc_bgdata;
			URL url = new URL(Config.IPaddress + "/ZKYweb/GetPc_bgdataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
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

			// Toast.makeText(getApplicationContext(), result, 1).show();
			JSONArray array = new JSONArray(result);

			System.out.println(array.toString());
			// List<HashMap<String, Object>> data = new
			// ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj1 = array.getJSONObject(i);

				String bloodGlucoseS = obj1.getString("bloodGlucose");
				Double bloodGlucose = Double.valueOf(bloodGlucoseS);
				String screenName = obj1.getString("screenName");
				String uploadTime = obj1.getString("uploadTime");
				int familyMember_ = obj1.getInt("familyMember");
				String typeS = obj1.getString("celiangType");
				int type = Integer.parseInt(typeS);
				String userName = pc_userService.getuserNamebyfamilyMember(
						familyMember_, userId);

				pc_bgdata = new Pc_bgdata(userId, bloodGlucose, uploadTime,
						userName, screenName, familyMember_, 1, type);
				// Pc_dataService pc_dataService0=new
				// Pc_dataService(GetPc_dataActivity.this);
				if (!pc_bgdataService.ishavepc_bgdata(userId, bloodGlucose,
						uploadTime)) {
					pc_bgdataService.insert(pc_bgdata);

				}

				str = "success";

			}
		} catch (Exception e) {
			System.out.println("eeeeeeeeeee" + e.toString());
			e.printStackTrace();

		}

		return str;

	}

	// 友盟
	public void onResume() {
		super.onResume();
		getUsersresult = false;

		MobclickAgent.onResume(this);

		if (TestSplashActivity.clientId != "" && userId > 0) {
			showcid.setText("当前状态：可以收到推送消息");
		}
		// Toast.makeText(getApplicationContext(), sp.getInt("USER_ID", 0)+"",
		// 1).show();
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// 双击退出
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次返回键退出中科云健康", Toast.LENGTH_SHORT)
						.show();
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