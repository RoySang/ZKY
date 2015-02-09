package sict.zky.main;

import it.sauronsoftware.base64.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.utils.CheckNetworkConnection;
import sict.zky.utils.Config;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private CheckNetworkConnection cnk;
	private int userId;

	private EditText etName;
	private EditText etPwd;

	private String screenName;
	private String password_;
	private String passwordjiami;

	private ProgressDialog mDialog;
	// private CheckBox rem_pw, auto_login;
	private SharedPreferences sp;
	private SharedPreferences rememberUserId;
	private byte[] psw;

	// private int check;

	// private Intent intent00;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginpage);

		// Intent intent1=getIntent();
		// check=intent1.getIntExtra("check", 0);

		etName = (EditText) findViewById(R.id.username_edit);
		etPwd = (EditText) findViewById(R.id.password_edit);
		// etName.setText("lcdoctor");
		// etPwd.setText("9wbDffaZezPPvVWelKdlqmSU5aI=");
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		rememberUserId = this.getSharedPreferences("rememberUserId",
				Context.MODE_WORLD_READABLE);

		// rem_pw = (CheckBox) findViewById(R.id.cb_mima);
		// auto_login = (CheckBox) findViewById(R.id.cb_auto);

		// if (sp.getBoolean("ISCHECK", false)) {
		// // 设置默认是记录密码状态
		// rem_pw.setChecked(true);
		// etName.setText(sp.getString("USER_NAME", ""));
		// etPwd.setText(sp.getString("PASSWORD", ""));
		//
		// }

		// if (sp.getBoolean("AUTO_ISCHECK", false)) {
		// 设置默认是自动登录状态
		// auto_login.setChecked(true);
		// 跳转界面
//		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//		intent.putExtra("userId", sp.getInt("USER_ID", -1));
//		intent.putExtra("screenName", sp.getString("USER_NAME", ""));
//		// intent.putExtra("check", check);
//		LoginActivity.this.startActivity(intent);
//		finish();
		// }

		// 判断自动登陆多选框状态

		// 监听记住密码多选框按钮事件
		// rem_pw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (rem_pw.isChecked()) {
		//
		// System.out.println("记住密码已选中");
		// // sp.edit().putBoolean("ISCHECK", true).commit();
		//
		// } else {
		//
		// System.out.println("记住密码没有选中");
		// // sp.edit().putBoolean("ISCHECK", false).commit();
		//
		// }
		//
		// }
		// });
		// 监听自动登录多选框事件
		// auto_login.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// if (auto_login.isChecked()) {
		// // System.out.println("自动登录已选中");
		// rem_pw.setChecked(true);
		// // sp.edit().putBoolean("ISCHECK", true).commit();
		// // sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
		// // sp.edit().putBoolean("ISCHECK", true).commit();
		//
		// } else {
		// // System.out.println("自动登录没有选中");
		// // sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
		// }
		// }
		// });
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

	public void login(View v) throws UnsupportedEncodingException {

		screenName = etName.getText().toString().trim();
		// Toast.makeText(LoginActivity.this, name, Toast.LENGTH_LONG)
		// .show();
		password_ = etPwd.getText().toString().trim();
		passwordjiami = password_;

		/**
		 * 加密算法
		 */
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-1");
			messageDigest.update(password_.getBytes());
			psw = Base64.encode(messageDigest.digest());
			passwordjiami = new String(psw);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Toast.makeText(LoginActivity.this, pwd, Toast.LENGTH_LONG)
		// .show();

		if (screenName.equals("")) {
			Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_LONG)
					.show();
			return;
		} else if (password_.equals("")) {
			Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG)
					.show();

			return;
		}
		// userId=14614;
		// Intent intent = new Intent(LoginActivity.this,
		// MainActivity.class);
		// intent.putExtra("userId", userId);
		// intent.putExtra("screenName", screenName);
		// startActivity(intent);
		// finish();

		mDialog = new ProgressDialog(LoginActivity.this);
		mDialog.setTitle("登陆");
		mDialog.setMessage("正在登陆，请等待...");
		mDialog.show();
		Thread loginThread = new Thread(new LoginThread());
		loginThread.start();

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				// if (rem_pw.isChecked()) {
				// // 记住用户名、密码、
				// sp.edit().putBoolean("ISCHECK", true).commit();
				// Editor editor = sp.edit();
				// editor.putString("USER_NAME", screenName);
				// editor.putString("PASSWORD", password_);
				// editor.commit();

				// // }
				// if (auto_login.isChecked()) {
				// sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
				// Editor editor = sp.edit();
				// editor.putInt("USER_ID", userId);
				// editor.commit();
				// Editor editor1=rememberUserId.edit();

				// }
				// else {
				Editor editor = sp.edit();
				editor.putString("USER_NAME", screenName);
				editor.putInt("USER_ID", userId);
				editor.commit();
				// }

				// if (sp.getBoolean("AUTO_ISCHECK", false)) {
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				intent.putExtra("userId", userId);
				intent.putExtra("screenName", screenName);
				startActivity(intent);
				finish();
				// }

				// else {
				// Intent intent = new Intent(LoginActivity.this,
				// MainActivity.class);
				// intent.putExtra("userId", -1);
				// startActivity(intent);
				// finish();
				// }

				break;
			case 1:
				mDialog.cancel();
				// sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				// sp.edit().putBoolean("ISCHECK", false).commit();
				Toast.makeText(getApplicationContext(), "登录失败,请输入正确的账号密码！",
						Toast.LENGTH_SHORT).show();
				break;

			case 2:
				mDialog.cancel();
				// sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				// sp.edit().putBoolean("ISCHECK", false).commit();
				Toast.makeText(getApplicationContext(), "登录失败,请输入正确的账号密码！",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				mDialog.cancel();
				// sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
				// sp.edit().putBoolean("ISCHECK", false).commit();
				Toast.makeText(getApplicationContext(), "请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			}

		}
	};

	class LoginThread implements Runnable {

		public void run() {

			int id = loginServer(screenName, passwordjiami);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (id > 0) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else if (id == -2) {// 账号或密码不正确
				msg.what = 1;
				handler.sendMessage(msg);
			} else if (id == -1) {// 角色权限不对
				msg.what = 2;
				handler.sendMessage(msg);
			} else {
				msg.what = 3;
				handler.sendMessage(msg);
			}
		}

	}

	private int loginServer(String username, String password) {
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/Dzkyappweb/LoginServlet");
			// URL url = new
			// URL("http://localhost:8080/Dzkyappweb/LoginServlet");
			URL url = new URL(Config.IPaddress + "/ZKYweb/LoginServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/LoginServlet");
			// URL url = new URL("http://121.42.32.103:80/ZKYweb/LoginServlet");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();

			Log.i("LoginActivity", conn.toString());
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj = new JSONObject();
			obj.put("screenName", username);
			// String passmd5 = MD5(password_);
			//
			// String encryptmd5 = encryptmd5(passmd5);
			obj.put("password_", password);

			pw.write(obj.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));

			userId = Integer.parseInt(br.readLine());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return userId;
	}

	public void visitorsingin(View v) {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		// intent.putExtra("userId", userId);
		// intent.putExtra("screenName", screenName);
		startActivity(intent);
		finish();
	}
	// // MD5加密，32位
	//
	// public static String MD5(String str) {
	//
	// MessageDigest md5 =null;
	//
	// try {
	//
	// md5 = MessageDigest.getInstance("MD5");
	//
	// } catch(Exception e) {
	//
	// e.printStackTrace();
	//
	// return "";
	//
	// }
	//
	//
	//
	// char[] charArray = str.toCharArray();
	//
	// byte[] byteArray =new byte[charArray.length];
	//
	//
	//
	// for (int i = 0; i < charArray.length; i++) {
	//
	// byteArray[i] = (byte) charArray[i];
	//
	// }
	//
	// byte[] md5Bytes = md5.digest(byteArray);
	//
	//
	//
	// StringBuffer hexValue =new StringBuffer();
	//
	// for (int i = 0; i < md5Bytes.length; i++) {
	//
	// int val = ((int) md5Bytes[i]) &0xff;
	//
	// if (val < 16) {
	//
	// hexValue.append("0");
	//
	// }
	//
	// hexValue.append(Integer.toHexString(val));
	//
	// }
	//
	// return hexValue.toString();
	//
	// }
	//
	//
	//
	// // 可逆的加密算法
	//
	// public static String encryptmd5(String str) {
	//
	// char[] a = str.toCharArray();
	//
	// for (int i = 0; i < a.length; i++) {
	//
	// a[i] = (char) (a[i] ^'l');
	//
	// }
	//
	// String s = new String(a);
	//
	// return s;
	//
	// }

}