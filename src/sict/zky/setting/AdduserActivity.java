package sict.zky.setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_user;
import sict.zky.input.UPLOAD_PC_DATAActivity;
import sict.zky.input.inputActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Pc_userService;
import sict.zky.testBPM.StartTestBGMActivity;
import sict.zky.utils.Config;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AdduserActivity extends Activity {
	private int userId;
	private String userName;
	private int familyMember;
	private EditText  user_name;
	private Spinner add_familymember_spinner;
	private static String familyMember_[] = { "1:父亲", "2:母亲", "3:配偶", "4:子女",
			"5:祖父", "6:祖母", "7:兄弟", "8:姐妹", "9:其他" };
	private ArrayAdapter<String> adapter;
	private Pc_userService pc_userService;
	private Pc_user pc_user;
	private String familyRole;
	private int countofother = 0;
	private String screenName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adduseractivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		screenName = intent.getStringExtra("screenName");
		user_name = (EditText) findViewById(R.id.add_user_name);
		add_familymember_spinner = (Spinner) findViewById(R.id.add_familymember_spinner);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, familyMember_);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		pc_userService = new Pc_userService(getApplicationContext());

		add_familymember_spinner.setAdapter(adapter);
		add_familymember_spinner.setPrompt("请选择家庭成员类型:");
		add_familymember_spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// familyMember = position + 1;
						switch (position) {
						case 0:
							familyRole = "父亲";
							// userName = "父亲";
//							user_name.setText(userName);
							break;
						case 1:
							familyRole = "母亲";
							// userName = "母亲";
//							user_name.setText(userName);
							break;
						case 2:
							familyRole = "配偶";
							// userName = "配偶";
//							user_name.setText(userName);
							break;
						case 3:
							familyRole = "子女";
							// userName = "子女";
//							user_name.setText(userName);
							break;
						case 4:
							familyRole = "祖父";
							// userName = "祖父";
//							user_name.setText(userName);
							break;
						case 5:
							familyRole = "祖母";
							// userName = "祖母";
//							user_name.setText(userName);
							break;
						case 6:
							// countofother=pc_userService.getCountofother(7)+1;
							familyRole = "兄弟";
							// userName = "兄弟";
//							user_name.setText(userName);
							// +countofother;
							// countofother=0;
							break;
						case 7:
							// countofother=pc_userService.getCountofother(8)+1;
							familyRole = "姐妹";
							// userName = "姐妹";
//							user_name.setText(userName);
							// +countofother;
							// countofother=0;
							break;
						case 8:
							user_name.setText("");
							user_name.setEnabled(true);
							Toast.makeText(AdduserActivity.this, "请输入家庭成员用户名",
									1).show();

							// countofother=pc_userService.getCountofother(9)+1;
							familyRole = "其他";
							// userName="其他"+countofother;
							// countofother=0;
							break;
						default:
							break;
						}
						// Toast.makeText(getApplicationContext(),
						// String.valueOf(familyMember), 0);
					}

					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});
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

	public void adduser(View v) {
		// userName=user_name.getText().toString().trim();
		userName = user_name.getText().toString().trim();
		familyMember = pc_userService.getlastfamilyMember(userId) + 1;
		if (userName.equals("")) {
			Toast.makeText(getApplicationContext(), "请输入姓名", 0).show();
		} else {
			if (pc_userService.ishaveUserbyuserName(userName)) {
				Toast.makeText(getApplicationContext(), "已存在该用户，无法创建", 0)
						.show();
			} else {
				Thread add_usersThread = new Thread(new Add_UsersThread());
				add_usersThread.start();

				// finish();
			}

		}
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				pc_user = new Pc_user(userId, userName, familyMember,
						familyRole, 1);
				pc_userService.insert(pc_user);
				finish();

				break;
			case 1:
				// mDialog.cancel();
				Toast.makeText(getApplicationContext(), "保存至本地",
						Toast.LENGTH_SHORT).show();
				pc_user = new Pc_user(userId, userName, familyMember,
						familyRole, 0);
				pc_userService.insert(pc_user);
				finish();
				break;

			}

		}
	};

	class Add_UsersThread implements Runnable {

		public void run() {

			// String str =
			// update_pc_bgdataServer(userId,GLU,uploadTime,userName,screenName_,familyMember_);
			String str = add_usersServer(userId, userName, familyMember,
					familyRole);

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

	private String add_usersServer(int userId, String userName,
			int familyMember, String familyRole) {

		String str = "no";
		try {

			URL url = new URL(Config.IPaddress + "/ZKYweb/Upload_UsersServlet");

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
			obj.put("userName", userName);
			// obj.put("screenName", screenName);
			obj.put("familyMember", familyMember);
			obj.put("familyRole", familyRole);

			array.put(obj);
			// System.out.println("eeeeeeeeeeee"+obj.toString());
			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
			// System.out.println("eeeeeeeeeeeestr" + str);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("eeeeeeeeeeee" + e.toString());
		}
		return str;
	}

	public void forreturn(View v) {
		// Intent intent = new Intent(AdduserActivity.this, MainActivity.class);
		// intent.putExtra("userId", userId);
		// startActivity(intent);
		finish();
	}

}
