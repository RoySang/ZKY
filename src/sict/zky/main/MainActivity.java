package sict.zky.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.datacurve.Datacurve;
import sict.zky.datacurve.MainDatacurve;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_user;
import sict.zky.input.inputActivity;
import sict.zky.service.Pc_userService;
import sict.zky.setting.SettingActivity;
import sict.zky.setting.SettingofVisitorActivity;
import sict.zky.setting.UserInfoActivity;
import sict.zky.testBPM.StartTestBGMActivity;
import sict.zky.testBPM.StartTestBPMActivity;
import sict.zky.utils.Config;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

import com.igexin.sdk.PushManager;
import com.testin.agent.TestinAgent;

public class MainActivity extends TabActivity {

	private RadioGroup group;
	private TabHost tabHost;
	public static final String TAB_jiating = "tabjiating";
	public static final String TAB_tongji = "tabtongji";
	public static final String TAB_test = "tabtest";
	public static final String TAB_gongshi = "tabgongshi";
	public static final String TAB_setting = "tabsetting";

	private Pc_userService pc_userService;

	private Button testbloodpressureButton, testbloodglucoseButton,
			datacurveButton, showproductButton, manualinputButton,
			settingButton;

	private boolean isExit;
	private int userId;
	private String screenName;
	// private int check;

	private long firstTime = 0;
	private SharedPreferences rememberclientId;
	private String cid;
	private String appkey = "";
	private String appsecret = "";
	private String appid = "";
	// private String SN = "";
	private String result = "a";
	private boolean ifok = true;
	private SharedPreferences sp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintabs);

		// 崩溃测试入口
		TestinAgent.init(this);
		

		// 初始化推送
		PushManager.getInstance().initialize(this.getApplicationContext());

		// sp = this.getSharedPreferences("userInfo",
		// Context.MODE_WORLD_READABLE);
		// SN=sp.getString("SN", "null");

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		screenName = intent.getStringExtra("screenName");

		if(screenName!=null)
		{
		TestinAgent.setUserInfo(screenName);
		}
			
		// 从AndroidManifest.xml的meta-data中读取SDK配置信息
		String packageName = getApplicationContext().getPackageName();
		ApplicationInfo appInfo;
		try {
			appInfo = getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);
			if (appInfo.metaData != null) {

				appid = appInfo.metaData.getString("PUSH_APPID");
				appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
				appkey = (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData
						.get("PUSH_APPKEY").toString() : null;
			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rememberclientId = this.getSharedPreferences("rememberclientId",
				Context.MODE_WORLD_READABLE);

		// cid=rememberclientId.getString("clientId", "");
		// userId=sp.getInt("USER_ID", -1);

		testbloodpressureButton = (Button) this
				.findViewById(R.id.testbloodpressureButton);
		testbloodglucoseButton = (Button) this
				.findViewById(R.id.testbloodglucoseButton);

		manualinputButton = (Button) this.findViewById(R.id.manualinputButton);
		// settingButton = (Button) this.findViewById(R.id.settingButton);

		pc_userService = new Pc_userService(getApplicationContext());
		if (!pc_userService.ishaveUser(userId) && userId > 0) {
			pc_userService.insert(new Pc_user(userId, screenName, 0, "本人", 0));
		}

		group = (RadioGroup) findViewById(R.id.main_radio);
		tabHost = getTabHost();

		// 家庭???
		Intent intent1 = new Intent(MainActivity.this, UserInfoActivity.class);
		intent1.putExtra("userId", userId);
		intent1.putExtra("screenName", screenName);
		tabHost.addTab(tabHost.newTabSpec(TAB_jiating)
				.setIndicator(TAB_jiating).setContent(intent1));
		// 统计

		Intent intent2 = new Intent(MainActivity.this, MainDatacurve.class);
		intent2.putExtra("userId", userId);
		intent2.putExtra("screenName", screenName);
		tabHost.addTab(tabHost.newTabSpec(TAB_tongji).setIndicator(TAB_tongji)
				.setContent(new Intent(intent2)));

		// 测试主界面？？？
		Intent intent3 = new Intent(MainActivity.this, MainTestActivity.class);
		intent3.putExtra("userId", userId);
		intent3.putExtra("screenName", screenName);
		tabHost.addTab(tabHost.newTabSpec(TAB_test).setIndicator(TAB_test)
				.setContent(intent3));

		// 商城
		Intent intent4 = new Intent(MainActivity.this, ImageActivity.class);
		intent4.putExtra("userId", userId);
		tabHost.addTab(tabHost.newTabSpec(TAB_gongshi)
				.setIndicator(TAB_gongshi).setContent(intent4));

		if (userId > 0) {
			Intent intent5 = new Intent();
			intent5.setClass(MainActivity.this, SettingActivity.class);
			intent5.putExtra("userId", userId);
			intent5.putExtra("screenName", screenName);
			tabHost.addTab(tabHost.newTabSpec(TAB_setting)
					.setIndicator(TAB_setting).setContent(intent5));
		} else {
			Intent intent5 = new Intent();
			intent5.setClass(MainActivity.this, SettingofVisitorActivity.class);
			// intent5.putExtra("userId", userId);
			// intent5.putExtra("screenName", screenName);
			tabHost.addTab(tabHost.newTabSpec(TAB_setting)
					.setIndicator(TAB_setting).setContent(intent5));
		}

		tabHost.setCurrentTab(2);// 设置中间测量界面为主界面
		// tabHost.setCurrentTab(0);// 设置中间家庭成员为主界面
		if (userId > 0 ) {
			new Thread(new MyThread()).start();
		}
		// Toast.makeText(getApplicationContext(), "check:"+check, 1).show();
		// if (check == 1) {
		if(userId>0){
			Thread check_pc_data_getuiThread = new Thread(
					new Check_pc_data_getuiThread());
			check_pc_data_getuiThread.start();
		}
		
		// }

		group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_button0:
					tabHost.setCurrentTabByTag(TAB_jiating);
					break;
				case R.id.radio_button1:
					tabHost.setCurrentTabByTag(TAB_tongji);
					break;
				case R.id.radio_button2:
					tabHost.setCurrentTabByTag(TAB_test);
					break;
				case R.id.radio_button3:
					tabHost.setCurrentTabByTag(TAB_gongshi);
					break;
				case R.id.radio_button4:
					tabHost.setCurrentTabByTag(TAB_setting);
					break;
				default:
					break;
				}
			}
		});

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(MainActivity.this, "有未传送的数据，请等待",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Check_pc_data_getuiThread implements Runnable {
		public void run() {

			String str = check_pc_data_getuiServer(userId);
			// update_pc_bgdataServer(userId,GLU,uploadTime,userName,screenName_,familyMember_);

			Message msg = handler.obtainMessage();
			// // if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else {

				msg.what = 1;
				handler.sendMessage(msg);
			}

		}

	}

	private String check_pc_data_getuiServer(int userId) {
		String str = "";
		try {
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Check_Pc_Data_GetuiServlet");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			// JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);

			// array.put(obj);

			pw.write(obj.toString());
			pw.flush();
			pw.close();
//			System.out.println("eeeeee" + obj.toString());

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
			// System.out.println("eeeeeee"+str);

		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("eeeeee"+e.toString());
		}
		return str;
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

	// 循环执行上传clientId
	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				ifok = false;
				rememberclientId.edit().putInt("count", 1).commit();
				// Toast.makeText(MainActivity.this, "clientId上传成功", 1).show();
				// mythread.stop();
				System.out.println("上传成功");
				break;
			// case 1:
			// ifok = false;
			// System.out.println("已上传");
			// break;
			case 2:

				System.out.println("上传失败" + result + ","
						+ TestSplashActivity.clientId + ", " + userId + " ,"
						+ rememberclientId.getInt("count", -1));
				break;

			}
		}
	};

	public class MyThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			while (ifok) {
				if (!TestSplashActivity.clientId.equals("") && userId > 0
						&& !screenName.equals("")) {
					cid = TestSplashActivity.clientId;
					try {

						URL url = new URL(Config.IPaddress
								+ "/ZKYweb/ClientServlet");

						HttpURLConnection conn = (HttpURLConnection) url
								.openConnection();
						conn.setDoInput(true);
						conn.setDoOutput(true);
						conn.setRequestMethod("POST");
						conn.addRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
						conn.connect();
						PrintWriter pw = new PrintWriter(conn.getOutputStream());

						JSONObject obj = new JSONObject();
						obj.put("cid", cid);
						obj.put("userId", userId);
						obj.put("appid", appid);
						obj.put("screenName", screenName);

						pw.write(obj.toString());
						pw.flush();
						pw.close();

						BufferedReader br = new BufferedReader(
								new InputStreamReader(conn.getInputStream(),
										"UTF-8"));
						result = br.readLine();

					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.toString());
					}

				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}// 线程暂停5秒，单位毫秒
				Message message = null;
				message = new Message();
				if (result.equals("success")) {
					ifok = false;
					message.what = 0;
					handler1.sendMessage(message);// 发送消息
				}
				// else if (result.equals("falseupdate")) {
				// ifok = false;
				// message.what = 1;
				// handler1.sendMessage(message);// 发送消息
				// }
				else {
					message.what = 2;
					handler1.sendMessage(message);// 发送消息
				}

			}
		}
	}

	// 处理同步
	/*
	 * Handler handler = new Handler() { public void handleMessage(Message msg)
	 * { switch (msg.what) { case 0: rememberclientId.edit().putInt("count",
	 * 1).commit(); Toast.makeText(getApplicationContext(),
	 * TestSplashActivity.clientId, 1).show(); break; case 1:
	 * Toast.makeText(getApplicationContext(), TestSplashActivity.clientId,
	 * 1).show(); System.out.println("上传失败"); break; case 2:
	 * System.out.println("访问失败"); Toast.makeText(getApplicationContext(),
	 * TestSplashActivity.clientId, 1).show(); break; }
	 * 
	 * } };
	 * 
	 * class ClientThread implements Runnable {
	 * 
	 * public void run() { String str = ""; str = clientServer(appid,
	 * TestSplashActivity.clientId, userId, SN); Message msg =
	 * handler.obtainMessage();
	 * 
	 * if (str.equals("success")) { msg.what = 0; handler.sendMessage(msg); }
	 * else if (str.equals("failed")) { msg.what = 1; handler.sendMessage(msg);
	 * } else { msg.what = 2; handler.sendMessage(msg); } }
	 * 
	 * }
	 * 
	 * private String clientServer(String appid, String cid, int userId, String
	 * SN) { String str = "failed"; try {
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

	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// exit();
	// return false;
	// } else {
	// return super.onKeyDown(keyCode, event);
	// }
	// }
	//
	// private void exit() {
	// Handler mHandler = new Handler() {
	// public void handlerMessage(Message msg) {
	// super.handleMessage(msg);
	// }
	//
	// };
	//
	// if (!isExit) {
	// isExit = true;
	// Toast.makeText(getApplicationContext(), "再按一次退出程序",
	// Toast.LENGTH_SHORT).show();
	// mHandler.sendEmptyMessageDelayed(0, 200);
	// } else {
	// Intent intent = new Intent(Intent.ACTION_MAIN);
	// intent.addCategory(Intent.CATEGORY_HOME);
	// startActivity(intent);
	// System.exit(0);
	// }
	//
	// }
	//
	// // 双击退出
	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// switch (keyCode) {
	// case KeyEvent.KEYCODE_BACK:
	// long secondTime = System.currentTimeMillis();
	// if (secondTime - firstTime > 2000) {
	// Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
	// firstTime = secondTime;
	// return true;
	// } else {
	// System.exit(0);
	// }
	// break;
	// }
	// return super.onKeyUp(keyCode, event);
	// }

	public class MainListener implements OnClickListener {
		public void onClick(View v) {
			if (v.getId() == R.id.testbloodpressureButton) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, StartTestBPMActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("screenName", screenName);
				MainActivity.this.startActivity(intent);

			}
			if (v.getId() == R.id.testbloodglucoseButton) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, StartTestBGMActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("screenName", screenName);
				MainActivity.this.startActivity(intent);

			}
			if (v.getId() == R.id.manualinputButton) {
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, inputActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("screenName", screenName);
				MainActivity.this.startActivity(intent);
			}
			// if(v.getId() == R.id.datacurveButton){//数据曲线的显式
			// Intent intent =new Intent(MainActivity.this,Datacurve.class);
			// intent.putExtra("userId", userId);
			// intent.putExtra("screenName", screenName);
			// startActivity(intent);
			//
			// }
			// if(v.getId() == R.id.showproductButton){
			// Intent intent =new Intent(MainActivity.this,ImageActivity.class);
			// intent.putExtra("userId", userId);
			// startActivity(intent);
			//
			// }
			//
			//
			// if(v.getId() == R.id.settingButton){
			// Intent intent = new Intent();
			// intent.setClass(MainActivity.this,SettingActivity.class);
			// intent.putExtra("userId", userId);
			// intent.putExtra("screenName", screenName);
			// startActivity(intent);
			//
			// }

		}
	}
}
