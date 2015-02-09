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

import sict.zky.datacurve.DatacurveGLU;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.main.MainActivity;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class UPLOAD_PC_BGDATAActivity extends Activity {
	private int userId;
	private String userName_;
	private String uploadTime_;

	private String screenName_;
	private String bloodGlucose_;
	private int familyMember_;
	private String familyRole;

	private long firstTime = 0;

	private EditText bloodGlucose, familyMember;

	private CurrentTime ct = new CurrentTime();
	private ProgressDialog mDialog;

	private String getuserName;
	private List<String> getuserNamebyuserId;
	private Pc_bgdata pc_bgdata;
	private Pc_userService pc_userService;
	private Pc_bgdataService pc_bgdataService;
	private int type = 1;

	// 用户姓名方式下拉
	private TextView pc_bgdata_username_textView;
	private ImageView pc_bgdata_username_imageView;
	private PopupWindow pw_username;// PopupWindow对象声明
	private LinearLayout pc_bgdata_username_linear;
	private ArrayList<String> usernamelist;// 用户名显示列表
	int clickPsitionofusername = -1;// 用户名列表项位置

	// 时间方式下拉
	private TextView pc_bgdata_time_textView;
	private ImageView pc_bgdata_time_imageView;
	private PopupWindow pw_time;// PopupWindow对象声明
	private LinearLayout pc_bgdata_time_linear;
	private ArrayList<String> timelist;// 时间显示列表
	int clickPsitionoftime = -1;// 时间名列表项位置

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_pc_bgdataactivity);

		pc_userService = new Pc_userService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		screenName_ = intent.getStringExtra("screenName");
		userName_ = screenName_;
		familyMember_ = 0;
		familyRole = "本人";

		bloodGlucose = (EditText) findViewById(R.id.upload_bloodGlucose);
		mDialog = new ProgressDialog(UPLOAD_PC_BGDATAActivity.this);

		pc_bgdata_username_linear = (LinearLayout) findViewById(R.id.pc_bgdata_username_linear);
		pc_bgdata_username_textView = (TextView) findViewById(R.id.pc_bgdata_username_textView);
		pc_bgdata_username_imageView = (ImageView) findViewById(R.id.pc_bgdata_username_imageView);
		pc_bgdata_time_linear = (LinearLayout) findViewById(R.id.pc_bgdata_time_linear);
		pc_bgdata_time_textView = (TextView) findViewById(R.id.pc_bgdata_time_textView);
		pc_bgdata_time_imageView = (ImageView) findViewById(R.id.pc_bgdata_time_imageView);

		initusernamepop();// 初始化用户名popwindow
		inittimepop();// 初始化时间范围popwindow

	}

	private void initusernamepop() {// 用户名popwindow初始化
		usernamelist = getusernameList();
		// final int size = getuserNamebyuserId.size() + 1;
		// 设置默认显示的Text
		pc_bgdata_username_textView.setText(usernamelist.get(0));
		pc_bgdata_username_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						pc_bgdata_username_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_username, null);
						// 通过view 和宽·高，构造PopopWindow
						pw_username = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);
						// pw_username.setOutsideTouchable(true);
						pw_username.setBackgroundDrawable(getResources()
								.getDrawable(
								// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
										R.drawable.bg_white));

						// 设置焦点为可点击
						pw_username.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_username.showAsDropDown(pc_bgdata_username_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_username);
						lv.setAdapter(new ListViewAdapter(
								UPLOAD_PC_BGDATAActivity.this, usernamelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								pc_bgdata_username_textView
										.setText(usernamelist.get(position));
								if (clickPsitionofusername != position) {
									clickPsitionofusername = position;
								}
								// getuserName =
								// parent.getItemAtPosition(position).toString();
								getuserName = usernamelist.get(position);
								userName_ = getuserName;

								pc_bgdata_username_imageView
										.setImageResource(R.drawable.down);
								pw_username.dismiss();
							}
						});
					}

				});
	}
	
	public ArrayList<String> getusernameList() {//得到用户名
		getuserNamebyuserId = pc_userService.getuserName(userId);
		ArrayList<String> list = (ArrayList<String>) getuserNamebyuserId;
		return list;

	}
	

	private void inittimepop() {// 时间范围popwindow初始化
		timelist = gettimeList();

		// 设置默认显示的Text
		pc_bgdata_time_textView.setText(timelist.get(0));
		pc_bgdata_time_linear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				pc_bgdata_time_imageView.setImageResource(R.drawable.up);
				// 通过布局注入器，注入布局给View对象
				View myView = getLayoutInflater().inflate(
						R.layout.pop_timerange, null);
				// 通过view 和宽·高，构造PopopWindow
				// WindowManager windowManager = getWindowManager();
				// Display display = windowManager.getDefaultDisplay();
				// WindowManager.LayoutParams params = getWindow()
				// .getAttributes();
				// params.alpha = 0.7f;
				// getWindow().setAttributes(params);
				pw_time = new PopupWindow(myView, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				// pw=new PopupWindow(myView);

				pw_time.setBackgroundDrawable(getResources().getDrawable(
				// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
						R.drawable.bg_white));

				// 设置焦点为可点击
				pw_time.setFocusable(true);// 可以试试设为false的结果
				// 将window视图显示在myButton下面
				pw_time.showAsDropDown(pc_bgdata_time_textView);

				ListView lv = (ListView) myView
						.findViewById(R.id.lv_pop_timerange);
				lv.setAdapter(new ListViewAdapter(
						UPLOAD_PC_BGDATAActivity.this, timelist));
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						pc_bgdata_time_textView.setText(timelist.get(position));
						if (clickPsitionoftime != position) {
							clickPsitionoftime = position;
						}

						switch (position) {
						case 0:
							type = 1;
							break;
						case 1:
							type = 2;
							break;
						case 2:
							type = 3;
							break;
						case 3:
							type = 4;
							break;
						default:
							break;
						}

						pc_bgdata_time_imageView
								.setImageResource(R.drawable.down);
						pw_time.dismiss();
					}
				});
			}

		});
	}

	

	public ArrayList<String> gettimeList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("餐前");
		list.add("餐后");
		list.add("睡前");
		list.add("凌晨");
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


	public void uploadpc_bgdata(View v) {
		bloodGlucose_ = bloodGlucose.getText().toString();

		ct.getCurrentTime();
		uploadTime_ = ct.TimeToString();

		if (bloodGlucose_.equals("")) {
			Toast.makeText(UPLOAD_PC_BGDATAActivity.this, "请输入血糖",
					Toast.LENGTH_LONG).show();

		} else if (Double.parseDouble(bloodGlucose_) < 1.1) {
			Toast.makeText(UPLOAD_PC_BGDATAActivity.this, "您输入的血糖低于1.1",
					Toast.LENGTH_LONG).show();

		}

		else {
			if (bloodGlucose_.indexOf(".") == -1) {
				bloodGlucose_ = bloodGlucose_ + ".0";
			}
			mDialog.setTitle("上传");
			mDialog.setMessage("正在上传，请等待...");
			mDialog.show();

			Thread update_pc_bgdataThread = new Thread(
					new Update_pc_bgdataThread());
			update_pc_bgdataThread.start();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();

				pc_bgdata = new Pc_bgdata(userId,
						Double.parseDouble(bloodGlucose_), uploadTime_,
						userName_, screenName_, familyMember_, 1, type);
				pc_bgdataService.insert(pc_bgdata);
				Toast.makeText(UPLOAD_PC_BGDATAActivity.this, "上传成功",
						Toast.LENGTH_SHORT).show();

				finish();
				break;
			case 1:
				mDialog.cancel();
				pc_bgdata = new Pc_bgdata(userId,
						Double.parseDouble(bloodGlucose_), uploadTime_,
						userName_, screenName_, familyMember_, 0, type);
				pc_bgdataService.insert(pc_bgdata);
				Toast.makeText(getApplicationContext(), "上传失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Update_pc_bgdataThread implements Runnable {

		public void run() {
			String str = "";
			str = update_pc_bgdataServer(userId, userName_, screenName_,
					bloodGlucose_, uploadTime_, 0, familyMember_, familyRole,
					type);
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

	private String update_pc_bgdataServer(int userId, String userName,
			String screenName, String bloodGlucose, String uploadTime,
			int uploadType, int familyMember, String familyRole, int type) {
		String str = "";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/ZKYweb/Upload_PC_BGDataServlet");
			// URL url = new
			// URL("http://localhost:8080/ZKYweb/Upload_PC_BGDataServlet");
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Upload_PC_BGDataServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/Upload_PC_BGDataServlet");
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
			obj.put("screenName", screenName);
			obj.put("uploadTime", uploadTime);
			obj.put("familyMember", familyMember);
			obj.put("uploadType", uploadType);
			obj.put("bloodGlucose", bloodGlucose);
			obj.put("familyRole", familyRole);
			obj.put("celiangType", type);
			// System.out.println("eeeeee"+obj.toString());
			array.put(obj);
			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public void forreturn(View v) {

		// Intent intent =new
		// Intent(UPLOAD_PC_BGDATAActivity.this,MainActivity.class);
		// intent.putExtra("userId", userId);
		// intent.putExtra("screenName", screenName_);
		// startActivity(intent);
		finish();
	}

}
