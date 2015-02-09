package sict.zky.input;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.main.MainActivity;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.testBPM.AnalyseBPMResult;
import sict.zky.utils.Config;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.ListViewAdapter;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UPLOAD_PC_DATAActivity extends Activity {
	private int userId;
	private int systolicPressure_ = 0;
	private int diastolicPressure_ = 0;
	private int pulse_ = 0;
	private int oxygen_ = 0;
	private String userName_;
	private String screenName_;
	private String uploadTime_;
	private int familyMember_;
	private String familyRole;
	private EditText systolicPressure, diastolicPressure, pulse, oxygen,
			familyMember;
	private String getuserName;
	private List<String> getuserNamebyuserId;
	private Pc_data pc_data;
	private Pc_userService pc_userService;
	private Pc_dataService pc_dataService;

	private CurrentTime ct = new CurrentTime();
	private ProgressDialog mDialog;

	private ImageView upload_level_image;
	private Button tianjia_upload_data;

	// 用户姓名方式下拉
	private TextView pc_data_username_textView;
	private ImageView pc_data_username_imageView;
	private PopupWindow pw_username;// PopupWindow对象声明
	private LinearLayout pc_data_username_linear;
	private ArrayList<String> usernamelist;// 用户名显示列表
	int clickPsitionofusername = -1;// 用户名列表项位置

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_pc_dataactivity);

		pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		screenName_ = intent.getStringExtra("screenName");
		userName_ = screenName_;
		familyMember_ = 0;
		familyRole = "本人";

		systolicPressure = (EditText) findViewById(R.id.upload_systolicPressure);
		diastolicPressure = (EditText) findViewById(R.id.upload_diastolicPressure);
		pulse = (EditText) findViewById(R.id.upload_pulse);

		upload_level_image = (ImageView) findViewById(R.id.upload_level_image);
		tianjia_upload_data = (Button) findViewById(R.id.tianjia_upload_data);

		mDialog = new ProgressDialog(UPLOAD_PC_DATAActivity.this);

		pc_data_username_linear = (LinearLayout) findViewById(R.id.pc_data_username_linear);
		pc_data_username_textView = (TextView) findViewById(R.id.pc_data_username_textView);
		pc_data_username_imageView = (ImageView) findViewById(R.id.pc_data_username_imageView);
		initusernamepop();
	}

	private void initusernamepop() {// 用户名popwindow初始化
		usernamelist = getusernameList();
		// final int size = getuserNamebyuserId.size() + 1;
		// 设置默认显示的Text
		pc_data_username_textView.setText(usernamelist.get(0));
		pc_data_username_linear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				pc_data_username_imageView.setImageResource(R.drawable.up);
				// 通过布局注入器，注入布局给View对象
				View myView = getLayoutInflater().inflate(
						R.layout.pop_username, null);
				// 通过view 和宽·高，构造PopopWindow
				pw_username = new PopupWindow(myView, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				// pw=new PopupWindow(myView);
				// pw_username.setOutsideTouchable(true);
				pw_username.setBackgroundDrawable(getResources().getDrawable(
				// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
						R.drawable.bg_white));

				// 设置焦点为可点击
				pw_username.setFocusable(true);// 可以试试设为false的结果
				// 将window视图显示在myButton下面
				pw_username.showAsDropDown(pc_data_username_textView);

				ListView lv = (ListView) myView
						.findViewById(R.id.lv_pop_username);
				lv.setAdapter(new ListViewAdapter(UPLOAD_PC_DATAActivity.this,
						usernamelist));
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						pc_data_username_textView.setText(usernamelist
								.get(position));
						if (clickPsitionofusername != position) {
							clickPsitionofusername = position;
						}
						// getuserName =
						// parent.getItemAtPosition(position).toString();
						getuserName = usernamelist.get(position);
						userName_ = getuserName;

						pc_data_username_imageView
								.setImageResource(R.drawable.down);
						pw_username.dismiss();
					}
				});
			}

		});
	}

	public ArrayList<String> getusernameList() {// 得到用户名
		getuserNamebyuserId = pc_userService.getuserName(userId);
		ArrayList<String> list = (ArrayList<String>) getuserNamebyuserId;
		return list;

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

	public void uploadpc_data(View v) {
		if (systolicPressure.getText().toString().trim().equals("")) {
			systolicPressure_ = 0;
		} else {
			systolicPressure_ = Integer.parseInt(systolicPressure.getText()
					.toString().trim());
		}

		if (diastolicPressure.getText().toString().trim().equals("")) {
			diastolicPressure_ = 0;
		} else {
			diastolicPressure_ = Integer.parseInt(diastolicPressure.getText()
					.toString().trim());
		}

		if (pulse.getText().toString().trim().equals("")) {
			pulse_ = 0;
		} else {
			pulse_ = Integer.parseInt(pulse.getText().toString().trim());
		}

		ct.getCurrentTime();
		uploadTime_ = ct.TimeToString();

		if (systolicPressure_ <= diastolicPressure_) {
			systolicPressure.setText("");
			diastolicPressure.setText("");

			Toast.makeText(UPLOAD_PC_DATAActivity.this, "高压值应大于低压值", 0).show();

		} else if (systolicPressure_ > 250 || diastolicPressure_ > 200) {
			systolicPressure.setText("");
			diastolicPressure.setText("");
			Toast.makeText(UPLOAD_PC_DATAActivity.this, "您输入的血压值过高，请重输", 0)
					.show();
		} else if (systolicPressure_ < 50 || diastolicPressure_ < 50) {
			systolicPressure.setText("");
			diastolicPressure.setText("");
			Toast.makeText(UPLOAD_PC_DATAActivity.this, "您输入的血压值过低，请重输", 0)
					.show();
		} else if (pulse_ > 200) {
			pulse.setText("");
			Toast.makeText(UPLOAD_PC_DATAActivity.this, "您输入的脉搏值过高，请重输", 0)
					.show();
		} else if (pulse_ < 40) {
			pulse.setText("");
			Toast.makeText(UPLOAD_PC_DATAActivity.this, "您输入的脉搏值过低，请重输", 0)
					.show();

		} else {

			mDialog.setTitle("上传");
			mDialog.setMessage("上传中，请稍等...");
			mDialog.show();
			Thread update_pc_bgdataThread = new Thread(
					new Update_pc_dataThread());
			update_pc_bgdataThread.start();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				pc_data = new Pc_data(userId, systolicPressure_,
						diastolicPressure_, pulse_, uploadTime_, userName_,
						screenName_, familyMember_, 1);
				pc_dataService.insert(pc_data);
				// 改变图片背景，字体颜色
				AnalyseBPMResult analyresulut = new AnalyseBPMResult();
				int level = analyresulut.analyse_bpm_result(systolicPressure_,
						diastolicPressure_);
				result(level);
				// tianjia_upload_data.setEnabled(false);

				Toast.makeText(UPLOAD_PC_DATAActivity.this, "上传成功",
						Toast.LENGTH_SHORT).show();

				// finish();
				// userName.setText(userName_);
				// bloodGlucose.setText(bloodGlucose_);

				// familyMember.setText(String.valueOf(familyMember_));

				break;
			case 1:
				mDialog.cancel();
				pc_data = new Pc_data(userId, systolicPressure_,
						diastolicPressure_, pulse_, uploadTime_, userName_,
						screenName_, familyMember_, 0);
				pc_dataService.insert(pc_data);
				Toast.makeText(getApplicationContext(), "上传失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Update_pc_dataThread implements Runnable {

		public void run() {

			String str = update_pc_dataServer(userId, systolicPressure_,
					diastolicPressure_, pulse_, 0, uploadTime_, 0, userName_,
					screenName_, familyMember_, familyRole);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str != null) {
				if (str.equals("success")) {
					msg.what = 0;
					handler.sendMessage(msg);
				} else {

					msg.what = 1;
					handler.sendMessage(msg);
				}
			}

		}

	}

	private String update_pc_dataServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse, int oxygen, String uploadTime,
			int uploadType, String userName, String screenName,
			int familyMember, String familyRole) {
		String str = "";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/Dzkyappweb/Upload_PG_DataServlet");
			// URL url = new
			// URL("http://localhost:8080/Dzkyappweb/Upload_PG_DataServlet");
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Upload_PC_DataServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/Upload_PC_DataServlet");
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
			System.out.println("eeeeeeee" + array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
			System.out.println("eeeeeeee" + str);
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println("eeeeeeeeeeeeeeeeeeee"+e.toString());
		}
		return str;
	}

	private void result(int level) {
		switch (level) {
		case 1:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_1));

			break;

		case 2:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_2));

			break;

		case 3:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_2));

			break;

		case 4:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_3));

			break;

		case 5:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_4));

			break;

		case 6:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_5));

			break;

		case 7:
			upload_level_image.setImageDrawable(getResources().getDrawable(
					R.drawable.level_6));

			break;

		}

	}

	public void forreturn(View v) {

		// if (systolicPressure.getText().toString().trim().equals("")) {
		// systolicPressure_ = 0;
		// } else {
		// systolicPressure_ = Integer.parseInt(systolicPressure.getText()
		// .toString().trim());
		// }
		//
		// if (diastolicPressure.getText().toString().trim().equals("")) {
		// diastolicPressure_ = 0;
		// } else {
		// diastolicPressure_ = Integer.parseInt(diastolicPressure.getText()
		// .toString().trim());
		// }
		//
		// if (pulse.getText().toString().trim().equals("")) {
		// pulse_ = 0;
		// } else {
		// pulse_ = Integer.parseInt(pulse.getText().toString().trim());
		// }
		//
		// if(systolicPressure_!=0||diastolicPressure_!=0||pulse_!=0){
		// pc_data = new Pc_data(userId, systolicPressure_,
		// diastolicPressure_, pulse_, uploadTime_, userName_,
		// screenName_, familyMember_);
		// pc_dataService.insert(pc_data);
		// }
		// Intent intent = new Intent(UPLOAD_PC_DATAActivity.this,
		// MainActivity.class);
		// intent.putExtra("userId", userId);
		// intent.putExtra("screenName", screenName_);
		// startActivity(intent);
		finish();
	}
}
