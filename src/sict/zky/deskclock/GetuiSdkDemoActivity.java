package sict.zky.deskclock;

/*
 *  
 * SDK Ver: 2.0.0.0
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.domain.Pc_data;
import sict.zky.domain.Pc_user;
import sict.zky.main.MainTestActivity;
import sict.zky.main.TestSplashActivity;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.utils.Config;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.testin.agent.TestinAgent;

public class GetuiSdkDemoActivity extends Activity {

	private static final String MASTERSECRET = "5zU6sLjfNB7ZIXy45jicC2";

	private static final int ADDTAG = 100;
	private static final int VERSION = 101;
	private static final int SILENTTIME = 102;
	private static final int EXIT = 103;
	private static final int GETCLIENTID = 106;

	// public static TextView tView = null;
	// public static TextView tLogView = null;
	// private SharedPreferences sp;

	private Pc_data pc_data;
	private Pc_user pc_user;
	private Pc_userService pc_userService;
	private Pc_dataService pc_dataService;

	private String appkey = "";
	private String appsecret = "";
	private String appid = "";

	private int userId;
	private int high_;
	private int low_;
	private int pulse_;
	private String uploadTime_;
	private String userName;
	private int familyMember;
	private String familyRole;
	private ProgressDialog mDialog;
	public String str;
	private TextView get_high, get_low, get_pulse, get_uploadTime;
	// private ArrayAdapter<String> adapter;
	// private static String familyMember_[] = { "0:本人", "1:父亲", "2:母亲", "3:配偶",
	// "4:子女", "5:祖父", "6:祖母", "7:兄弟", "8:姐妹" };
	private List<String> getuserNamebyuserId;
	private UserNameAdapter userNameAdapter;
	// private String familyRole;
	private Spinner get_username_spinner;
	private Button btn_upload = null;
	// private String getuserName;
	// private Pc_userService pc_userService;
	// private List<String> getuserNamebyuserId;
	// private UserNameAdapter userNameAdapter;
	private String screenName;
	private TestSplashActivity ts;

	private SharedPreferences sp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showreceiverdata);

		// ts = new TestSplashActivity();
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		// sp.edit().clear().commit();
		// sp.edit().notify();

		Intent intent = getIntent();

		// userId = intent.getIntExtra("userId", 0);
		// screenName = intent.getStringExtra("screenName");
		// userId = sp.getInt("USER_ID", 0);

		// Toast.makeText(getApplicationContext(), sp.getInt("USER_ID", 0)+"",
		// 1).show();
		// screenName = sp.getString("USER_NAME", "");
//		userId=intent.getIntExtra("userId", -1);
		userId=sp.getInt("USER_ID", 0);
		screenName = sp.getString("USER_NAME", "");
		high_ = intent.getIntExtra("high", 0);
		low_ = intent.getIntExtra("low", 0);
		pulse_ = intent.getIntExtra("pulse", 0);
		uploadTime_ = intent.getStringExtra("uploadTime");

		get_high = (TextView) findViewById(R.id.get_high);
		get_low = (TextView) findViewById(R.id.get_low);
		get_pulse = (TextView) findViewById(R.id.get_pulse);
		get_uploadTime = (TextView) findViewById(R.id.get_uploadTime);
		get_username_spinner = (Spinner) findViewById(R.id.get_username_spinner);
		btn_upload = (Button) findViewById(R.id.btn_upload);

		userName = screenName;

		get_high.setText(high_ + "");
		get_low.setText(low_ + "");
		get_pulse.setText(pulse_ + "");
		get_uploadTime.setText(uploadTime_);

		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_userService = new Pc_userService(getApplicationContext());

//		 if(userId<0){
//		 
//		 }

		getuserNamebyuserId = pc_userService.getuserName(userId);
		userNameAdapter = new UserNameAdapter(this, getuserNamebyuserId);
		get_username_spinner.setAdapter(userNameAdapter);

		Thread update_sendThread = new Thread(new Update_sendThread());
		update_sendThread.start();

		// get_username_spinner.setPrompt("请选择家庭成员类型:");
		get_username_spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						userName = getuserNamebyuserId.get(position);
						familyRole = pc_userService
								.getfamilyRolebyuserIdanduserName(userId,userName);
						familyMember = pc_userService
								.getfamilyMemberbyuserIdanduserName(userId,userName);
					}

					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		// 从AndroidManifest.xml的meta-data中读取SDK配置信息
		/*
		 * String packageName = getApplicationContext().getPackageName();
		 * ApplicationInfo appInfo; try { appInfo =
		 * getPackageManager().getApplicationInfo(packageName,
		 * PackageManager.GET_META_DATA); if (appInfo.metaData != null) {
		 * 
		 * appid = appInfo.metaData.getString("PUSH_APPID"); appsecret =
		 * appInfo.metaData.getString("PUSH_APPSECRET"); appkey =
		 * (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData
		 * .get("PUSH_APPKEY").toString() : null; }
		 * 
		 * } catch (NameNotFoundException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 */

		// SDK初始化，第三方程序启动时，都要进行SDK初始化工�? Log.d("GetuiSdkDemo",
		// "initializing sdk...");
		// PushManager.getInstance().initialize(this.getApplicationContext());

	}

	Handler sendhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:

				break;
		
			}

		}
	};

	class Update_sendThread implements Runnable {

		public void run() {

			String str = update_sendServer(userId, high_, low_, pulse_,
					uploadTime_);
			Message msg = sendhandler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str != null) {
				if (str.equals("success")) {
					msg.what = 0;
					sendhandler.sendMessage(msg);
				} else if (str.equals("false")) {
					msg.what = 1;
					sendhandler.sendMessage(msg);
				} else if (str.equals("null")) {
					msg.what = 2;
					sendhandler.sendMessage(msg);
				}
			}
		}
	}

	private String update_sendServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse, String uploadTime) {
		String str = "false";
		try {
			URL url = new URL(Config.IPaddress + "/ZKYweb/Update_SendServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("systolicPressure", systolicPressure);
			obj.put("diastolicPressure", diastolicPressure);
			obj.put("pulse", pulse);
			obj.put("uploadTime", uploadTime);

			array.put(obj);
			pw.write(array.toString());
//			System.out.println("eeeee" + array.toString());
			pw.flush();
			pw.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
//			System.out.println("eeeee" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("eeeee" + e.toString());
		}
		return str;
	}

	@Override
	protected void onStart() {
		super.onStart();
		TestinAgent.onStart(this);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();

				pc_data = new Pc_data(userId, high_, low_, pulse_, uploadTime_,
						userName, screenName, familyMember, 1);
				if (pc_dataService.ishavepc_data(userId, high_, low_, pulse_,
						uploadTime_)) {
					pc_dataService.updatefamilyMember(userId, userName, high_,
							low_, pulse_, uploadTime_, familyMember);
					System.out.println("eeeee" + uploadTime_ + " , ");
					Toast.makeText(GetuiSdkDemoActivity.this, "修改成功",
							Toast.LENGTH_SHORT).show();
				} else {
					pc_dataService.insert(pc_data);
					Toast.makeText(GetuiSdkDemoActivity.this, "上传成功",
							Toast.LENGTH_SHORT).show();
				}

				break;
			case 1:
				mDialog.cancel();
				pc_data = new Pc_data(userId, high_, low_, pulse_, uploadTime_,
						userName, screenName, familyMember, 1);
				if (pc_dataService.ishavepc_data(userId, high_, low_, pulse_,
						uploadTime_)) {
					pc_dataService.updatefamilyMember(userId, userName, high_,
							low_, pulse_, uploadTime_, familyMember);
				} else {
					pc_dataService.insert(pc_data);
				}
				Toast.makeText(GetuiSdkDemoActivity.this, "已上传",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				mDialog.cancel();
				pc_data = new Pc_data(userId, high_, low_, pulse_, uploadTime_,
						userName, screenName, familyMember, 1);
				if (pc_dataService.ishavepc_data(userId, high_, low_, pulse_,
						uploadTime_)) {
					pc_dataService.updatefamilyMember(userId, userName, high_,
							low_, pulse_, uploadTime_, familyMember);
				} else {
					pc_dataService.insert(pc_data);

				}
				Toast.makeText(GetuiSdkDemoActivity.this, "已保存",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				pc_data = new Pc_data(userId, high_, low_, pulse_, uploadTime_,
						userName, screenName, familyMember, 1);
				if (pc_dataService.ishavepc_data(userId, high_, low_, pulse_,
						uploadTime_)) {
					pc_dataService.updatefamilyMember(userId, userName, high_,
							low_, pulse_, uploadTime_, familyMember);
				} else {
					pc_dataService.insert(pc_data);

				}
				Toast.makeText(GetuiSdkDemoActivity.this, "已上传",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	class Update_pc_dataThread implements Runnable {

		public void run() {

			String str = update_pc_dataServer(userId, high_, low_, pulse_, 0,
					uploadTime_, 0, userName, screenName, familyMember,familyRole);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str != null) {
				if (str.equals("success")) {
					msg.what = 0;
					handler.sendMessage(msg);
				} else if (str.equals("alreadyexist")) {
					msg.what = 1;
					handler.sendMessage(msg);
				} else if (str.equals("false")) {

					msg.what = 2;
					handler.sendMessage(msg);
				} else {
					msg.what = 3;
					handler.sendMessage(msg);
				}
			}
		}
	}

	private String update_pc_dataServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse, int oxygen, String uploadTime,
			int uploadType, String userName, String screenName, int familyMember,String familyRole) {
		String str = "false";
		try {
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Update_PC_DataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("systolicPressure", systolicPressure);
			obj.put("diastolicPressure", diastolicPressure);
			obj.put("pulse", pulse);
			obj.put("uploadTime", uploadTime);
			obj.put("userName", userName);
			obj.put("screenName", screenName);
			obj.put("familyMember", familyMember);
			obj.put("oxygen", oxygen);
			obj.put("uploadType", uploadType);
			obj.put("familyRole", familyRole);

			array.put(obj);
			pw.write(array.toString());
			pw.flush();
			pw.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
			// System.out.println("eeeeeeeee"+str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("eeeee" + e.toString());
		}
		return str;
	}

	@Override
	public void onDestroy() {
		Log.d("GetuiSdkDemo", "onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onStop() {
		Log.d("GetuiSdkDemo", "onStop()");
		super.onStop();

		TestinAgent.onStop(this);
	}

	public void upload_GSM(View v) {
		mDialog = new ProgressDialog(GetuiSdkDemoActivity.this);
		mDialog.setTitle("上传");
		mDialog.setMessage("上传中，请稍等...");
		mDialog.show();
		Thread update_pc_dataThread = new Thread(new Update_pc_dataThread());
		update_pc_dataThread.start();

	}

	public void forreturn(View v) {

		finish();
	}

	public boolean isNetworkConnected() {

		ConnectivityManager mConnectivityManager = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (mNetworkInfo != null) {
			return mNetworkInfo.isAvailable();
		}
		return false;
	}



}
