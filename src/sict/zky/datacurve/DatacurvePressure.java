package sict.zky.datacurve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sict.zky.deskclock.R;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.ListViewAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint("NewApi")
public class DatacurvePressure extends FragmentActivity {
	private ActivityMainGaoYa gaoya;
	private ActivityMainDiYa diya;
	private ActivityMainmaiBo maibo;

	private LinearLayout gaoyatab;
	private LinearLayout diyatab;
	private LinearLayout maibotab;
	//
	// private Spinner datacurve_username_spinner;
	
	private int userId;
	private String screenName;
	private String userName;
	private String getuserName;
	private Pc_userService pc_userService;
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;
	private List<String> getuserNamebyuserId;// 通过userId获得userName

	private Integer year = 0, month = 0, day = 0, hour = 0, minute = 0;

	private String onepreweek, nowweekTime, onepremonth;// 分别是一周前的时间节点，当前时间节点，一月前的时间节点

	private CurrentTime ct;
	private String starttime = "0", endtime = "0";// 开始时间和结束时间,"0"代表显示全部
	private int timeorci;// 按次追踪或按时间追踪 0代表按次，1代表按时间
	private long firstTime = 0;
	private SharedPreferences sp;
	private int width;// 屏幕宽度

	// 用户姓名方式下拉
	private TextView datacurve_pressure_username_textView;
	private ImageView datacurve_pressure_username_imageView;
	private PopupWindow pw_pressure_username;// PopupWindow对象声明
	private LinearLayout datacurve_pressure_username_linear;
	private ArrayList<String> pressure_usernamelist;// 用户名显示列表
	int clickPsitionofpressure_username = -1;// 用户名列表项位置

	// 追踪方式下拉
	private TextView datacurve_pressure_ciortime_textView;
	private ImageView datacurve_pressure_ciortime_imageView;
	// PopupWindow对象声明
	private PopupWindow pw_pressure_ciortime;
	private LinearLayout datacurve_pressure_ciortime_linear;
	private ArrayList<String> pressure_ciortimelist;// 次或时间显示列表
	int clickPsitionofpressure_ciortime = -1;// 当前追踪选中的列表项位置

	// 时间范围下拉
	private TextView datacurve_pressure_timerange_textView;
	private ImageView datacurve_pressure_timerange_imageView;
	// PopupWindow对象声明
	private PopupWindow pw_pressure_timerange;
	private LinearLayout datacurve_pressure_timerange_linear;
	private ArrayList<String> pressure_timerangelist;// 时间范围显示列表
	int clickPsitionofpressure_timerange = -1;// 时间范围列表项位置

	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;

	private int type = 0;// 标志位，表示当前选择的是血压页面还是脉搏页面，默认为0血压页
	private Button gaoyabtn,diyabtn,maibobtn;

	// private FragmentTransaction transaction;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		width = metric.widthPixels; // 屏幕宽度（像素）
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		userId = sp.getInt("USER_ID", -1);
		screenName = sp.getString("USER_NAME", "");

		setContentView(R.layout.datacurvepressure);

		ct = new CurrentTime();
		userName = screenName;
		timeorci = 0;
		starttime = "0";
		endtime = "0";

		// 初始化
		pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());

		getuserNamebyuserId = pc_userService.getuserName(userId);

		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		datacurve_pressure_username_linear = (LinearLayout) findViewById(R.id.datacurve_pressure_username_linear);
		datacurve_pressure_username_textView = (TextView) findViewById(R.id.datacurve_pressure_username_textView);
		datacurve_pressure_username_imageView = (ImageView) findViewById(R.id.datacurve_pressure_username_imageView);
		datacurve_pressure_timerange_linear = (LinearLayout) findViewById(R.id.datacurve_pressure_timerange_linear);
		datacurve_pressure_timerange_textView = (TextView) findViewById(R.id.datacurve_pressure_timerange_textView);
		datacurve_pressure_timerange_imageView = (ImageView) findViewById(R.id.datacurve_pressure_timerange_imageView);
		datacurve_pressure_ciortime_linear = (LinearLayout) findViewById(R.id.datacurve_pressure_ciortime_linear);
		datacurve_pressure_ciortime_textView = (TextView) findViewById(R.id.datacurve_pressure_ciortime_textView);
		datacurve_pressure_ciortime_imageView = (ImageView) findViewById(R.id.datacurve_pressure_ciortime_imageView);
		initusernamepop();// 初始化用户名popwindow
		inittimerangepop();// 初始化时间范围popwindow
		initciortimepop();// 初始化追踪方式popwindow
		initViews();// 初始化血压Fragment

		setTabSelection(0);// 默认显示

	}

	private void initusernamepop() {// 用户名popwindow初始化
		pressure_usernamelist = getusernameList();
		// final int size = getuserNamebyuserId.size() + 1;
		// 设置默认显示的Text
		datacurve_pressure_username_textView.setText(pressure_usernamelist
				.get(0));
		datacurve_pressure_username_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_pressure_username_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_username, null);
						// 通过view 和宽·高，构造PopopWindow
						pw_pressure_username = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);
						// pw_username.setOutsideTouchable(true);
						pw_pressure_username
								.setBackgroundDrawable(getResources()
										.getDrawable(
										// 此处为popwindow
										// 设置背景，同事做到点击外部区域，popwindow消失
												R.drawable.bg_white));

						// 设置焦点为可点击
						pw_pressure_username.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_pressure_username
								.showAsDropDown(datacurve_pressure_username_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_username);
						lv.setAdapter(new ListViewAdapter(
								DatacurvePressure.this, pressure_usernamelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_pressure_username_textView
										.setText(pressure_usernamelist
												.get(position));
								if (clickPsitionofpressure_username != position) {
									clickPsitionofpressure_username = position;
								}
								// getuserName =
								// parent.getItemAtPosition(position).toString();
								getuserName = pressure_usernamelist
										.get(position);
								userName = getuserName;

								setTabSelection(type);

								datacurve_pressure_username_imageView
										.setImageResource(R.drawable.down);
								pw_pressure_username.dismiss();
							}

						});
					}

				});
	}

	private void inittimerangepop() {// 时间范围popwindow初始化
		pressure_timerangelist = gettimerangeList();

		// 设置默认显示的Text
		datacurve_pressure_timerange_textView.setText(pressure_timerangelist
				.get(0));
		datacurve_pressure_timerange_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_pressure_timerange_imageView
								.setImageResource(R.drawable.up);
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
						pw_pressure_timerange = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_pressure_timerange
								.setBackgroundDrawable(getResources()
										.getDrawable(
										// 此处为popwindow
										// 设置背景，同事做到点击外部区域，popwindow消失
												R.drawable.bg_white));

						// 设置焦点为可点击
						pw_pressure_timerange.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_pressure_timerange
								.showAsDropDown(datacurve_pressure_timerange_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_timerange);
						lv.setAdapter(new ListViewAdapter(
								DatacurvePressure.this, pressure_timerangelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_pressure_timerange_textView
										.setText(pressure_timerangelist
												.get(position));
								if (clickPsitionofpressure_timerange != position) {
									clickPsitionofpressure_timerange = position;
								}

								switch (position) {
								case 0:
									starttime = "0";
									endtime = "0";

									break;
								case 1:
									onepreweek = ct.OnepreweekTimeToString();// 得到一周前的时间
									nowweekTime = ct.TimeToString();// 得到当前时间

									starttime = onepreweek;
									endtime = nowweekTime;

									break;
								case 2:

									onepremonth = ct.OnepremonthTimetoString();// 得到一个月前的时间
									nowweekTime = ct.TimeToString();

									starttime = onepremonth;
									endtime = nowweekTime;

									break;

								default:
									// datacurve_timerange_imageView.setImageResource(R.drawable.down);
									break;
								}
								setTabSelection(type);
								datacurve_pressure_timerange_imageView
										.setImageResource(R.drawable.down);
								pw_pressure_timerange.dismiss();
							}
						});
					}

				});
	}

	private void initciortimepop() {// 追踪方式popwindow初始化
		pressure_ciortimelist = getciortimeList();
		// ciortimelist=getciortimeList();
		// 设置默认显示的Text
		datacurve_pressure_ciortime_textView.setText(pressure_ciortimelist
				.get(0));
		datacurve_pressure_ciortime_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_pressure_ciortime_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_ciortime, null);
						// WindowManager windowManager = getWindowManager();
						// Display display = windowManager.getDefaultDisplay();
						// WindowManager.LayoutParams params = getWindow()
						// .getAttributes();
						// params.alpha = 0.7f;
						// getWindow().setAttributes(params);
						// 通过view 和宽·高，构造PopopWindow
						// pw_ciortime = new PopupWindow(myView,
						// LayoutParams.FILL_PARENT,
						// LayoutParams.FILL_PARENT, true);
						// pw=new PopupWindow(myView);

						pw_pressure_ciortime = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_pressure_ciortime
								.setBackgroundDrawable(getResources()
										.getDrawable(
										// 此处为popwindow
										// 设置背景，同事做到点击外部区域，popwindow消失
												R.drawable.bg_white));

						// pw_ciortime.setBackgroundDrawable(new
						// ColorDrawable(0x55000000));
						// pw_ciortime.
						// 设置焦点为可点击
						pw_pressure_ciortime.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_pressure_ciortime
								.showAsDropDown(datacurve_pressure_ciortime_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_ciortime);
						lv.setAdapter(new ListViewAdapter(
								DatacurvePressure.this, pressure_ciortimelist));

						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_pressure_ciortime_textView
										.setText(pressure_ciortimelist
												.get(position));
								if (clickPsitionofpressure_ciortime != position) {
									clickPsitionofpressure_ciortime = position;
								}

								switch (position) {

								case 0:
									timeorci = 0;
									break;
								case 1:
									timeorci = 1;
									break;

								default:
									break;
								}

								//
								setTabSelection(type);

								pw_pressure_ciortime.dismiss();
								datacurve_pressure_ciortime_imageView
										.setImageResource(R.drawable.down);
							}
						});
					}

				});
	}

	public ArrayList<String> getusernameList() {
		ArrayList<String> list = (ArrayList<String>) getuserNamebyuserId;
		return list;

	}

	public ArrayList<String> gettimerangeList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("全部");
		list.add("一周内");
		list.add("一月内");
		return list;

	}

	public ArrayList<String> getciortimeList() {

		ArrayList<String> list = new ArrayList<String>();
		list.add("按次追踪");
		list.add("按时间追踪");
		return list;
	}

	private void initViews() {
		resetBtn();
		gaoyatab = (LinearLayout) findViewById(R.id.id_tab_top_gaoya);
		diyatab = (LinearLayout) findViewById(R.id.id_tab_top_diya);
		maibotab = (LinearLayout) findViewById(R.id.id_tab_top_maibo);
		gaoyabtn = (Button) findViewById(R.id.btn_tab_top_gaoya);
		diyabtn = (Button) findViewById(R.id.btn_tab_top_diya);
		maibobtn = (Button) findViewById(R.id.btn_tab_top_maibo);

		gaoyatab.setOnClickListener(onclicklistener);
		diyatab.setOnClickListener(onclicklistener);
		maibotab.setOnClickListener(onclicklistener);

	}

	public OnClickListener onclicklistener = new OnClickListener() {// 点击事件，切换fragment
		

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.id_tab_top_gaoya:
				gaoyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
				diyabtn.setBackgroundResource(R.drawable.bg_white);
				maibobtn.setBackgroundResource(R.drawable.bg_white);
				
				type = 0;
				setTabSelection(0);

				break;
			case R.id.id_tab_top_diya:
				gaoyabtn.setBackgroundResource(R.drawable.bg_white);
				diyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
				maibobtn.setBackgroundResource(R.drawable.bg_white);
				type = 1;
				setTabSelection(1);

				break;
			case R.id.id_tab_top_maibo:
		        gaoyabtn.setBackgroundResource(R.drawable.bg_white);
			    diyabtn.setBackgroundResource(R.drawable.bg_white);
				maibobtn.setBackgroundResource(R.drawable.cur_btn_bp);
				type = 2;
				setTabSelection(2);
				break;
			default:
				break;
			}

		}
	};

	private void setTabSelection(int index) {// 显示fragment
		
		fragmentManager = getSupportFragmentManager();

		transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {

		case 0:
			
//		gaoyabtn.setOnTouchListener(ButtonTouchListener);
//		gaoyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
//		diyabtn.setBackgroundResource(R.drawable.bg_white);
//		maibobtn.setBackgroundResource(R.drawable.bg_white);
//			gaoyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
			if (gaoya == null) {
				// 如果MessageFragment为空，则创建一个并添加到界面上
				gaoya = new ActivityMainGaoYa();
				
				 setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);

				transaction.add(R.id.id_content1, gaoya);
				transaction.commit();
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来
				gaoya = new ActivityMainGaoYa();
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content1, gaoya); // 将id_content1里的fragment替换为gaoya

				transaction.commit();// 事物提交
			}
			break;
		case 1:
//			gaoyabtn = (Button) findViewById(R.id.btn_tab_top_gaoya);
//			diyabtn = (Button) findViewById(R.id.btn_tab_top_diya);
//			maibobtn = (Button) findViewById(R.id.btn_tab_top_maibo);
////			diyabtn.setOnTouchListener(ButtonTouchListener);
//			gaoyabtn.setBackgroundResource(R.drawable.bg_white);
//			diyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
//			maibobtn.setBackgroundResource(R.drawable.bg_white);
			if (diya == null) {
				diya = new ActivityMainDiYa();
				// 传参数，在所有fragment中使用get方法可以得到值
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content1, diya);
				transaction.commit();
			} else {
				// 如果MessageFragment不为空，则直接将它显示出来

				diya = new ActivityMainDiYa();
				// 传参数，在所有fragment中使用get方法可以得到值
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);

				transaction.replace(R.id.id_content1, diya);
				transaction.commit();
			}
			break;
		case 2:
//			gaoyabtn = (Button) findViewById(R.id.btn_tab_top_gaoya);
//			diyabtn = (Button) findViewById(R.id.btn_tab_top_diya);
//			maibobtn = (Button) findViewById(R.id.btn_tab_top_maibo);
////			
////			maibobtn.setOnTouchListener(ButtonTouchListener);
//			gaoyabtn.setBackgroundResource(R.drawable.bg_white);
//			diyabtn.setBackgroundResource(R.drawable.bg_white);
//			maibobtn.setBackgroundResource(R.drawable.cur_btn_bp);
			if (maibo == null) {

				maibo = new ActivityMainmaiBo();
				// 传参数，在所有fragment中使用get方法可以得到值
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content1, maibo);
				transaction.commit();

			} else {
				maibo = new ActivityMainmaiBo();
				setUserName(userName);
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content1, maibo);
				transaction.commit();
			}
			break;

		}

	}
//	private OnTouchListener ButtonTouchListener = new OnTouchListener(){
//		public boolean onTouch(View v , MotionEvent event){
//			gaoyabtn = (Button) findViewById(R.id.btn_tab_top_gaoya);
//			diyabtn = (Button) findViewById(R.id.btn_tab_top_diya);
//			maibobtn = (Button) findViewById(R.id.btn_tab_top_maibo);
//			
//			if(v.getId() == R.id.btn_tab_top_gaoya){
//				gaoyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
//				diyabtn.setBackgroundResource(R.drawable.bg_white);
//				maibobtn.setBackgroundResource(R.drawable.bg_white);
//				
//			}else{
//				if(v.getId() == R.id.btn_tab_top_diya){
//					gaoyabtn.setBackgroundResource(R.drawable.bg_white);
//					diyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
//					maibobtn.setBackgroundResource(R.drawable.bg_white);
//				}else{
//					if(v.getId() == R.id.btn_tab_top_maibo){
//						gaoyabtn.setBackgroundResource(R.drawable.bg_white);
//						diyabtn.setBackgroundResource(R.drawable.bg_white);
//						maibobtn.setBackgroundResource(R.drawable.cur_btn_bp);
//					}
//				}
//			}
//			return false;
//			
//		}
//	};

	

	private void resetBtn() {
		gaoyabtn = (Button) findViewById(R.id.btn_tab_top_gaoya);
		diyabtn = (Button) findViewById(R.id.btn_tab_top_diya);
		maibobtn = (Button) findViewById(R.id.btn_tab_top_maibo);
		gaoyabtn.setBackgroundResource(R.drawable.cur_btn_bp);
		diyabtn.setBackgroundResource(R.drawable.bg_white);
		maibobtn.setBackgroundResource(R.drawable.bg_white);

	}

	private void hideFragments(FragmentTransaction transaction) {// 隐藏所有fragment，仅仅是设为不可见，并不会销毁
		if (gaoya != null) {
			transaction.hide(gaoya);// 隐藏当前的Fragment，仅仅是设为不可见，并不会销毁
		}
		if (diya != null) {
			transaction.hide(diya);
		}
		if (maibo != null) {
			transaction.hide(maibo);
		}

	}

	// get set方法 fragment里可以使用Activity里的get方法得到值
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public int getTimeorci() {
		return timeorci;
	}

	public void setTimeorci(int timeorci) {
		this.timeorci = timeorci;
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
