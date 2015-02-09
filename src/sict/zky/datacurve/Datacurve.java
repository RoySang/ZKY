package sict.zky.datacurve;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.ListViewAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.testin.agent.TestinAgent;

public class Datacurve extends Activity {
	private String titles1[] = { "收缩压", "舒张压", "脉搏" };
	private String titles2[] = { "血糖  mmol/L", "", "" };
	private int colors1[] = { Color.rgb(0, 173, 239), Color.rgb(151, 208, 91),
			Color.rgb(250, 136, 170) };
	private int colors2[] = { Color.rgb(0, 173, 239), Color.rgb(151, 208, 91),
			Color.rgb(243, 64, 70) };
	private PointStyle pss1[] = { PointStyle.CIRCLE, PointStyle.CIRCLE,
			PointStyle.CIRCLE };
	private PointStyle pss2[] = { PointStyle.CIRCLE, PointStyle.POINT,
			PointStyle.POINT };
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;
	private GraphicalView mChartView1, mChartView2;

	private int userId;
	private String screenName;
	private String userName;
	private Pc_userService pc_userService;
//	private Spinner datacurve_username_spinner;
	// private Spinner datacurve_timerange_spinner;
	// private Spinner datacurve_ciortime_spinner;
	private String getuserName;
	private List<String> getuserNamebyuserId;
	private UserNameAdapter userNameAdapter;
	private String starttime, endtime;

	private Integer year = 0, month = 0, day = 0, hour = 0, minute = 0;
	// private Integer premonth, preday, predayofmonth, predayofyear;
	private String onepreweek, nowweekTime, onepremonth;

	private CurrentTime ct;
	private static String timeRange[] = { "全部", "一周内", "一月内" };
	private static String ciortime[] = { "次跟踪", "时间跟踪" };
	private ArrayAdapter<String> adapter;
	// private ArrayAdapter<String> adapterciortime;
	private int timeorci = 0;
	private long firstTime = 0;
	private SharedPreferences sp;

	private int width;// 屏幕宽度

	// 用户姓名方式下拉
	private TextView datacurve_username_textView;
	private ImageView datacurve_username_imageView;
	private PopupWindow pw_username;// PopupWindow对象声明
	private LinearLayout datacurve_username_linear;
	private ArrayList<String> usernamelist;// 用户名显示列表
	int clickPsitionofusername = -1;// 用户名列表项位置

	// 追踪方式下拉
	private TextView datacurve_ciortime_textView;
	private ImageView datacurve_ciortime_imageView;
	// PopupWindow对象声明
	private PopupWindow pw_ciortime;
	private LinearLayout datacurve_ciortime_linear;
	private ArrayList<String> ciortimelist;// 次或时间显示列表
	int clickPsitionofciortime = -1;// 当前追踪选中的列表项位置

	// 时间范围下拉
	private TextView datacurve_timerange_textView;
	private ImageView datacurve_timerange_imageView;
	// PopupWindow对象声明
	private PopupWindow pw_timerange;
	private LinearLayout datacurve_timerange_linear;
	private ArrayList<String> timerangelist;// 时间范围显示列表
	int clickPsitionoftimerange = -1;// 时间范围列表项位置

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		width = metric.widthPixels; // 屏幕宽度（像素）
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		userId = sp.getInt("USER_ID", -1);
		screenName = sp.getString("USER_NAME", "");
		// Intent intent = getIntent();
		// userId = intent.getIntExtra("userId", -1);
		// screenName = intent.getStringExtra("screenName");
		if (userId < 0) {
			Toast.makeText(getApplicationContext(), "请登陆", 1).show();
			Intent intent0 = new Intent(Datacurve.this, LoginActivity.class);
			startActivity(intent0);
		} else {

			setContentView(R.layout.inputcurve);
			// requestWindowFeature(Window.FEATURE_NO_TITLE);

			userName = screenName;
			ct = new CurrentTime();
			starttime = "";
			endtime = "";

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

			// adapter = new ArrayAdapter<String>(this,
			// android.R.layout.simple_list_item_checked, timeRange);

			// 初始化控件
			datacurve_username_linear = (LinearLayout) findViewById(R.id.datacurve_username_linear);
			datacurve_username_textView = (TextView) findViewById(R.id.datacurve_username_textView);
			datacurve_username_imageView = (ImageView) findViewById(R.id.datacurve_username_imageView);
			datacurve_timerange_linear = (LinearLayout) findViewById(R.id.datacurve_timerange_linear);
			datacurve_timerange_textView = (TextView) findViewById(R.id.datacurve_timerange_textView);
			datacurve_timerange_imageView = (ImageView) findViewById(R.id.datacurve_timerange_imageView);
			datacurve_ciortime_linear = (LinearLayout) findViewById(R.id.datacurve_ciortime_linear);
			datacurve_ciortime_textView = (TextView) findViewById(R.id.datacurve_ciortime_textView);
			datacurve_ciortime_imageView = (ImageView) findViewById(R.id.datacurve_ciortime_imageView);
			initusernamepop();
			initciortimepop();
			inittimerangepop();
		}

		// showpc_data(userName, onepreweek, nowweekTime);
	}

	private void initusernamepop() {// 时间范围popwindow初始化
		usernamelist = getusernameList();
//		final int size = getuserNamebyuserId.size() + 1;
		// 设置默认显示的Text
		datacurve_username_textView.setText(usernamelist.get(0));
		datacurve_username_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_username_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_username, null);
						// 通过view 和宽·高，构造PopopWindow
						pw_username = new PopupWindow(myView, LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT,
								true);
						// pw=new PopupWindow(myView);
//						pw_username.setOutsideTouchable(true);
						pw_username.setBackgroundDrawable(getResources()
								.getDrawable(
								// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
										R.drawable.bg_white));

						// 设置焦点为可点击
						pw_username.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_username.showAsDropDown(datacurve_username_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_username);
						lv.setAdapter(new ListViewAdapter(Datacurve.this,
								usernamelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_username_textView
										.setText(usernamelist.get(position));
								if (clickPsitionofusername != position) {
									clickPsitionofusername = position;
								}
								// getuserName =
								// parent.getItemAtPosition(position).toString();
								getuserName = usernamelist.get(position);
								userName = getuserName;

								if (timeorci == 0) {
									if (starttime.equals("")) {
										showpc_databyname(userName);
										showpc_bgdatabyname(userName);
									} else {
										showpc_data(userName, starttime,
												endtime);
										showpc_bgdata(userName, starttime,
												endtime);
									}
								} else if (timeorci == 1) {
									if (starttime.equals("")) {
										showpc_databynameandtime(userName);
										showpc_bgdatabynameandtime(userName);
									} else {
										showpc_databytime(userName, starttime,
												endtime);
										showpc_bgdatabytime(userName,
												starttime, endtime);
									}
								}
								datacurve_username_imageView
										.setImageResource(R.drawable.down);
								pw_username.dismiss();
							}
						});
					}

				});
	}

	private void inittimerangepop() {// 时间范围popwindow初始化
		timerangelist = gettimerangeList();

		// 设置默认显示的Text
		datacurve_timerange_textView.setText(timerangelist.get(0));
		datacurve_timerange_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_timerange_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_timerange, null);
						// 通过view 和宽·高，构造PopopWindow
//						WindowManager windowManager = getWindowManager();
//						Display display =  windowManager.getDefaultDisplay();  
//						WindowManager.LayoutParams params = getWindow()
//								.getAttributes();
//						params.alpha = 0.7f;
//						getWindow().setAttributes(params);
						pw_timerange = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_timerange.setBackgroundDrawable(getResources()
								.getDrawable(
								// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
										R.drawable.bg_white));

						// 设置焦点为可点击
						pw_timerange.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_timerange
								.showAsDropDown(datacurve_timerange_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_timerange);
						lv.setAdapter(new ListViewAdapter(Datacurve.this,
								timerangelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_timerange_textView
										.setText(timerangelist.get(position));
								if (clickPsitionoftimerange != position) {
									clickPsitionoftimerange = position;
								}

								switch (position) {
								case 0:
									starttime = "";
									endtime = "";
									if (timeorci == 1) {
										showpc_databynameandtime(userName);
										showpc_bgdatabynameandtime(userName);
									} else if (timeorci == 0) {
										showpc_databyname(userName);
										showpc_bgdatabyname(userName);
									}

									break;
								case 1:
									onepreweek = ct.OnepreweekTimeToString();// 得到一周前的时间
									// onepreweek =
									// ct.OnepremonthTimetoString();
									nowweekTime = ct.TimeToString();// 得到当前时间

									starttime = onepreweek;
									endtime = nowweekTime;
									// Toast.makeText(getApplicationContext(),
									// onepreweek + " , " + nowweekTime,
									// 1).show();
									// showpc_data(userName,"14-12-8 14:18:55","14-12-8 14:19:11");

									if (timeorci == 1) {
										showpc_databytime(userName, onepreweek,
												nowweekTime);
										showpc_bgdatabytime(userName,
												onepreweek, nowweekTime);
									} else if (timeorci == 0) {
										showpc_data(userName, onepreweek,
												nowweekTime);
										showpc_bgdata(userName, onepreweek,
												nowweekTime);
									}
									// datacurve_timerange_imageView.setImageResource(R.drawable.down);
									break;
								case 2:

									onepremonth = ct.OnepremonthTimetoString();// 得到一个月前的时间
									nowweekTime = ct.TimeToString();

									starttime = onepremonth;
									endtime = nowweekTime;
									// Toast.makeText(getApplicationContext(),
									// onepremonth + " , " + nowweekTime,
									// 1).show();
									if (timeorci == 1) {
										showpc_databytime(userName,
												onepremonth, nowweekTime);
										showpc_bgdatabytime(userName,
												onepremonth, nowweekTime);
									} else if (timeorci == 0) {
										showpc_data(userName, onepremonth,
												nowweekTime);
										showpc_bgdata(userName, onepremonth,
												nowweekTime);
									}
									// datacurve_timerange_imageView.setImageResource(R.drawable.down);
									break;

								default:
									// datacurve_timerange_imageView.setImageResource(R.drawable.down);
									break;
								}
								datacurve_timerange_imageView
										.setImageResource(R.drawable.down);
								pw_timerange.dismiss();
							}
						});
					}

				});
	}

	private void initciortimepop() {// 追踪方式popwindow初始化
		ciortimelist = getciortimeList();
		// ciortimelist=getciortimeList();
		// 设置默认显示的Text
		datacurve_ciortime_textView.setText(ciortimelist.get(0));
		datacurve_ciortime_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_ciortime_imageView
								.setImageResource(R.drawable.up);
						// 通过布局注入器，注入布局给View对象
						View myView = getLayoutInflater().inflate(
								R.layout.pop_ciortime, null);
//						WindowManager windowManager = getWindowManager();
//						Display display =  windowManager.getDefaultDisplay();  
//						WindowManager.LayoutParams params = getWindow()
//								.getAttributes();
//						params.alpha = 0.7f;
//						getWindow().setAttributes(params);
						// 通过view 和宽·高，构造PopopWindow
//						pw_ciortime = new PopupWindow(myView,
//								LayoutParams.FILL_PARENT,
//								LayoutParams.FILL_PARENT, true);
						// pw=new PopupWindow(myView);
						
						
						pw_ciortime = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_ciortime.setBackgroundDrawable(getResources()
								.getDrawable(
								// 此处为popwindow 设置背景，同事做到点击外部区域，popwindow消失
										R.drawable.bg_white));
						
//						pw_ciortime.setBackgroundDrawable(new ColorDrawable(0x55000000)); 
						// pw_ciortime.
						// 设置焦点为可点击
						pw_ciortime.setFocusable(true);// 可以试试设为false的结果
						// 将window视图显示在myButton下面
						pw_ciortime.showAsDropDown(datacurve_ciortime_textView);
						

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_ciortime);
						lv.setAdapter(new ListViewAdapter(Datacurve.this,
								ciortimelist));

						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_ciortime_textView
										.setText(ciortimelist.get(position));
								if (clickPsitionofciortime != position) {
									clickPsitionofciortime = position;
								}

								switch (position) {

								case 0:
									timeorci = 0;

									// Toast.makeText(getApplicationContext(),
									// onepreweek + " , " + nowweekTime,
									// 1).show();
									// showpc_data(userName,"14-12-8 14:18:55","14-12-8 14:19:11");
									if (starttime.equals("")) {
										showpc_databyname(userName);
										showpc_bgdatabyname(userName);
									} else {
										showpc_data(userName, starttime,
												endtime);
										showpc_bgdata(userName, starttime,
												endtime);
									}

									break;
								case 1:

									timeorci = 1;

									if (starttime.equals("")) {
										showpc_databynameandtime(userName);
										showpc_bgdatabynameandtime(userName);
									} else {
										showpc_databytime(userName, starttime,
												endtime);
										showpc_bgdatabytime(userName,
												starttime, endtime);
									}
									break;

								default:
									break;
								}

//								if (pw_ciortime != null && pw_ciortime.isShowing()) {     
//									pw_ciortime.dismiss();     
//									pw_ciortime = null;   
//									      
//								}
								
								pw_ciortime.dismiss();
								datacurve_ciortime_imageView
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

	private final class OnItemSelected implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {

			// showpc_databyname(userName);
			// showpc_bgdatabyname(userName);
			// Toast.makeText(getApplicationContext(),
			// onepremonth+"!,!"+nowweekTime, 1).show();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	public void showpc_data(String name, String startTime, String endTime) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectDatabynameandtimerange(
				name, startTime, endTime);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// 总共数据的个数
		}

		String[] data1 = new String[a];
		double[] y1Values = new double[a];
		double[] y2Values = new double[a];
		double[] y3Values = new double[a];
		for (int i = 0; i < a; i++) {
			data1[i] = pc_datas.get(i).getUploadTime();
			// Log.v("GUAHU",data1[1]+"GS");
			y1Values[i] = pc_datas.get(i).getSystolicPressure();
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();
			y3Values[i] = pc_datas.get(i).getPulse();
		}
		List<double[]> yValues1 = new ArrayList<double[]>();
		yValues1.add(y1Values);
		yValues1.add(y2Values);
		yValues1.add(y3Values);

		// List<Date[]> xValues = new ArrayList<Date[]>();
		// for(int m=0;m<titles1.length;m++){
		List<double[]> xValues = new ArrayList<double[]>();
		// for(int m=0;m<titles1.length;m++){
		double[] datearr1 = new double[a];
		for (int k = 0; k < a; k++) {
			datearr1[k] = k;
			// String str = data1[k];
			// Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			// try {
			// Date d = (Date) f.parseObject(str);
			//
			// datearr1[k] = d;
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// xValues.add(Intek);
		}
		xValues.add(datearr1);

		// 开始画第一张图
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		// mChartView1 = ChartFactory.getTimeChartView(this,
		// buildDateDataset1(titles1, xValues, yValues1),
		// buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		mChartView1 = ChartFactory.getLineChartView(this,
				buildDateDataset01(titles1, xValues, yValues1),
				buildRenderer01(colors1, pss1));
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_databyname(String name) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectData(name);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// 总共数据的个数
		}

		String[] data1 = new String[a];
		double[] y1Values = new double[a];
		double[] y2Values = new double[a];
		double[] y3Values = new double[a];
		for (int i = 0; i < a; i++) {
			data1[i] = pc_datas.get(i).getUploadTime();
			// Log.v("GUAHU",data1[1]+"GS");
			y1Values[i] = pc_datas.get(i).getSystolicPressure();
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();
			y3Values[i] = pc_datas.get(i).getPulse();
		}
		List<double[]> yValues1 = new ArrayList<double[]>();
		yValues1.add(y1Values);
		yValues1.add(y2Values);
		yValues1.add(y3Values);

		List<double[]> xValues = new ArrayList<double[]>();
		// for(int m=0;m<titles1.length;m++){
		double[] datearr1 = new double[a];
		for (int k = 0; k < a; k++) {
			datearr1[k] = k;
			// String str = data1[k];
			// Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			// try {
			// Date d = (Date) f.parseObject(str);
			//
			// datearr1[k] = d;
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// xValues.add(Intek);
		}
		xValues.add(datearr1);

		// 开始画第一张图
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		// mChartView1 = ChartFactory.getTimeChartView(this,
		// buildDateDataset1(titles1, xValues, yValues1),
		// buildRenderer1(colors1, pss1), "");
		mChartView1 = ChartFactory.getLineChartView(this,
				buildDateDataset01(titles1, xValues, yValues1),
				buildRenderer01(colors1, pss1));
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_bgdata(String name, String startTime, String endTime) {

		// Log.v("AHwfew",pc_datas.get(6).getUploadTime());
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_bgdata> pc_bgdatas = pc_bgdataService
				.selectDatabynameandtimerange(name, startTime, endTime);
		int b = pc_bgdatas.size();
		String[] data2 = new String[b];
		double[] y4Values = new double[b];
		double[] y111Values = new double[b];
		double[] y222Values = new double[b];

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]中有数据
		for (int j = 0; j < b; j++) {
			data2[j] = pc_bgdatas.get(j).getUploadTime();
			y4Values[j] = pc_bgdatas.get(j).getBloodGlucose();
			y111Values[j] = 3.3;
			y222Values[j] = 13.3;
		}

		List<double[]> yValues2 = new ArrayList<double[]>();
		yValues2.add(y4Values);
		yValues2.add(y111Values);
		yValues2.add(y222Values);
		List<double[]> xValues = new ArrayList<double[]>();
		// for(int m=0;m<titles1.length;m++){
		double[] datearr1 = new double[b];
		for (int k = 0; k < b; k++) {
			datearr1[k] = k;
			// String str = data1[k];
			// Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			// try {
			// Date d = (Date) f.parseObject(str);
			//
			// datearr1[k] = d;
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// xValues.add(Intek);
		}
		xValues.add(datearr1);

		// 开始画第二张图
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		// mChartView2 = ChartFactory.getTimeChartView(this,
		// buildDateDataset02(titles2, xValues, yValues2),
		// buildRenderer02(colors2, pss2), "MM-dd HH:mm");
		mChartView2 = ChartFactory.getLineChartView(this,
				buildDateDataset02(titles2, xValues, yValues2),
				buildRenderer02(colors2, pss2));
		linearLayout2.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }
		// ChartFactory.getPieChartView(arg0, arg1, arg2)
		// ChartFactory.getDoughnutChartView(arg0, arg1, arg2)

	}

	public void showpc_bgdatabyname(String name) {

		// Log.v("AHwfew",pc_datas.get(6).getUploadTime());
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_bgdata> pc_bgdatas = pc_bgdataService.selectData(name);
		int b = pc_bgdatas.size();
		String[] data2 = new String[b];
		double[] y4Values = new double[b];
		double[] y111Values = new double[b];
		double[] y222Values = new double[b];

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]中有数据
		for (int j = 0; j < b; j++) {
			data2[j] = pc_bgdatas.get(j).getUploadTime();
			y4Values[j] = pc_bgdatas.get(j).getBloodGlucose();
			y111Values[j] = 3.3;
			y222Values[j] = 13.3;
		}

		List<double[]> yValues2 = new ArrayList<double[]>();
		yValues2.add(y4Values);
		yValues2.add(y111Values);
		yValues2.add(y222Values);
		List<double[]> xValues = new ArrayList<double[]>();
		// for(int m=0;m<titles1.length;m++){
		double[] datearr1 = new double[b];
		for (int k = 0; k < b; k++) {
			datearr1[k] = k;
			// String str = data1[k];
			// Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			// try {
			// Date d = (Date) f.parseObject(str);
			//
			// datearr1[k] = d;
			//
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// xValues.add(Intek);
		}
		xValues.add(datearr1);

		// 开始画第二张图
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		// mChartView2 = ChartFactory.getTimeChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		mChartView2 = ChartFactory.getLineChartView(this,
				buildDateDataset02(titles2, xValues, yValues2),
				buildRenderer02(colors2, pss2));
		linearLayout2.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }
	}

	// }
	private XYMultipleSeriesRenderer buildRenderer01(int[] colors1,
			PointStyle[] pss1) {//按次画x轴

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer1.setAxesColor(Color.BLACK);

		renderer1.setAxisTitleTextSize(20); // 坐标轴标题字体大小
		renderer1.setChartTitleTextSize(30);// 图标标题字体大小
		renderer1.setChartTitle("血压数据曲线");

		renderer1.setLabelsTextSize(30);// 轴标签字体大小
		renderer1.setLegendTextSize(40);// 图例字体大小
		renderer1.setPointSize(7f);//
		renderer1.setYLabels(8);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(10);// Sets the approximate number of labels for
									// the
									// X axis.
									// renderer1.setLabelsColor(Color.rgb(0, 0,
									// 0));
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		// renderer1.setShowGrid(true);// 是否显示网格
		renderer1.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer1.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer1.setPanLimits(new double[] { 0, 10000, 0, 0 });// 设置拖动时X轴Y轴允许的最大值最小值

		// renderer1.setA
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		// renderer1.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer1.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());

		renderer1.setXAxisMax(10);
		renderer1.setXAxisMin(0);

		renderer1.setYAxisMax(250);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // 设置图形四周的留白
		int length1 = colors1.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors1[i]);
			r.setPointStyle(pss1[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
			renderer1.addSeriesRenderer(r);
		}
		return renderer1;
	};

	// 画图
	private XYMultipleSeriesDataset buildDateDataset01(String[] titles1,
			List<double[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles1.length;
		for (int i = 0; i < length; i++) {
			XYSeries series1 = new XYSeries(titles1[i]);
			double[] xV = xValues.get(0); // 画TimeChart横坐标需要为Date数据类型
			double[] yV1 = yValues1.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series1.add(xV[k], yV1[k]);
			}
			dataset1.addSeries(series1);
		}
		return dataset1;

	}

	private XYMultipleSeriesRenderer buildRenderer02(int[] colors2,
			PointStyle[] pss2) {//按次画x轴
		XYMultipleSeriesRenderer renderer2 = new XYMultipleSeriesRenderer();

		renderer2.setApplyBackgroundColor(true);
		renderer2.setBackgroundColor(Color.TRANSPARENT);
		renderer2.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer2.setAxisTitleTextSize(20); // 各种设置。。。。
		renderer2.setChartTitleTextSize(30);
		renderer2.setChartTitle("血糖数据曲线");
		renderer2.setLabelsTextSize(30);
		renderer2.setAxesColor(Color.BLACK);
		renderer2.setLegendTextSize(40);
		renderer2.setPointSize(7f);
		renderer2.setYLabels(10);
		renderer2.setXLabels(10);
		// renderer2.setShowGrid(true);
		renderer2.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer2.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer2.setPanEnabled(true, false);
		renderer2.setZoomEnabled(true, false);
		renderer2.setPanLimits(new double[] { 0, 10000, 0, 0 });// 设置拖动时X轴Y轴允许的最大值最小值
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		// renderer2.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer2.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());
		renderer2.setXAxisMax(10);
		renderer2.setXAxisMin(0);
		renderer2.setYAxisMax(16);
		renderer2.setYAxisMin(0);
		renderer2.setLabelsColor(Color.rgb(60, 60, 60));
		renderer2.setMargins(new int[] { 40, 50, 40, 0 }); // 设置图形四周的留白
		int length2 = colors2.length;
		for (int i = 0; i < length2; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors2[i]);
			r.setPointStyle(pss2[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
			r.setChartValuesSpacing(10);
			r.setChartValuesTextSize(20);

			// r.setDisplayChartValues(true);//折线上显示数字
			renderer2.addSeriesRenderer(r);
		}
		return renderer2;
	};

	private XYMultipleSeriesDataset buildDateDataset02(String[] titles2,
			List<double[]> xValues, List<double[]> yValues2) {

		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles2.length;
		for (int i = 0; i < length; i++) {
			XYSeries series1 = new XYSeries(titles2[i]);
			double[] xV = xValues.get(0); // 画TimeChart横坐标需要为次类型
			double[] yV1 = yValues2.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series1.add(xV[k], yV1[k]);

			}
			dataset1.addSeries(series1);
		}
		return dataset1;

	}

	public void showpc_databytime(String name, String startTime, String endTime) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectDatabynameandtimerange(
				name, startTime, endTime);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// 总共数据的个数
		}

		String[] data1 = new String[a];
		double[] y1Values = new double[a];
		double[] y2Values = new double[a];
		double[] y3Values = new double[a];
		for (int i = 0; i < a; i++) {
			data1[i] = pc_datas.get(i).getUploadTime();
			// Log.v("GUAHU",data1[1]+"GS");
			y1Values[i] = pc_datas.get(i).getSystolicPressure();
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();
			y3Values[i] = pc_datas.get(i).getPulse();
		}
		List<double[]> yValues1 = new ArrayList<double[]>();
		yValues1.add(y1Values);
		yValues1.add(y2Values);
		yValues1.add(y3Values);

		List<Date[]> xValues = new ArrayList<Date[]>();
		// for(int m=0;m<titles1.length;m++){
		Date[] datearr1 = new Date[a];
		for (int k = 0; k < a; k++) {

			String str = data1[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			try {
				Date d = (Date) f.parseObject(str);

				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		// 开始画第一张图
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		mChartView1 = ChartFactory.getTimeChartView(this,
				buildDateDataset1(titles1, xValues, yValues1),
				buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		// mChartView1 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles1, xValues, yValues1),
		// buildRenderer2(colors1, pss1));
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_databynameandtime(String name) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectData(name);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// 总共数据的个数
		}

		String[] data1 = new String[a];
		double[] y1Values = new double[a];
		double[] y2Values = new double[a];
		double[] y3Values = new double[a];
		for (int i = 0; i < a; i++) {
			data1[i] = pc_datas.get(i).getUploadTime();
			// Log.v("GUAHU",data1[1]+"GS");
			y1Values[i] = pc_datas.get(i).getSystolicPressure();
			y2Values[i] = pc_datas.get(i).getDiastolicPressure();
			y3Values[i] = pc_datas.get(i).getPulse();
		}
		List<double[]> yValues1 = new ArrayList<double[]>();
		yValues1.add(y1Values);
		yValues1.add(y2Values);
		yValues1.add(y3Values);

		List<Date[]> xValues = new ArrayList<Date[]>();
		// for(int m=0;m<titles1.length;m++){
		Date[] datearr1 = new Date[a];
		for (int k = 0; k < a; k++) {

			String str = data1[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			try {
				Date d = (Date) f.parseObject(str);

				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		// 开始画第一张图
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		mChartView1 = ChartFactory.getTimeChartView(this,
				buildDateDataset1(titles1, xValues, yValues1),
				buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		// mChartView1 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles1, xValues, yValues1),
		// buildRenderer2(colors1, pss1));
		linearLayout1.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_bgdatabytime(String name, String startTime,
			String endTime) {

		// Log.v("AHwfew",pc_datas.get(6).getUploadTime());
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_bgdata> pc_bgdatas = pc_bgdataService
				.selectDatabynameandtimerange(name, startTime, endTime);
		int b = pc_bgdatas.size();
		String[] data2 = new String[b];
		double[] y4Values = new double[b];
		double[] y111Values = new double[b];
		double[] y222Values = new double[b];

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]中有数据
		for (int j = 0; j < b; j++) {
			data2[j] = pc_bgdatas.get(j).getUploadTime();
			y4Values[j] = pc_bgdatas.get(j).getBloodGlucose();
			y111Values[j] = 3.3;
			y222Values[j] = 13.3;
		}

		List<double[]> yValues2 = new ArrayList<double[]>();
		yValues2.add(y4Values);
		yValues2.add(y111Values);
		yValues2.add(y222Values);
		List<Date[]> xValues = new ArrayList<Date[]>();
		// for(int m=0;m<titles1.length;m++){
		Date[] datearr1 = new Date[b];
		for (int k = 0; k < b; k++) {

			String str = data2[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			try {
				Date d = (Date) f.parseObject(str);
				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		// 开始画第二张图
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		mChartView2 = ChartFactory.getTimeChartView(this,
				buildDateDataset2(titles2, xValues, yValues2),
				buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		// mChartView2 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2));
		linearLayout2.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }
		// ChartFactory.getPieChartView(arg0, arg1, arg2)
		// ChartFactory.getDoughnutChartView(arg0, arg1, arg2)

	}

	public void showpc_bgdatabynameandtime(String name) {

		// Log.v("AHwfew",pc_datas.get(6).getUploadTime());
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_bgdata> pc_bgdatas = pc_bgdataService.selectData(name);
		int b = pc_bgdatas.size();
		String[] data2 = new String[b];
		double[] y4Values = new double[b];
		double[] y111Values = new double[b];
		double[] y222Values = new double[b];

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]中有数据
		for (int j = 0; j < b; j++) {
			data2[j] = pc_bgdatas.get(j).getUploadTime();
			y4Values[j] = pc_bgdatas.get(j).getBloodGlucose();
			y111Values[j] = 3.3;
			y222Values[j] = 13.3;
		}

		List<double[]> yValues2 = new ArrayList<double[]>();
		yValues2.add(y4Values);
		yValues2.add(y111Values);
		yValues2.add(y222Values);
		List<Date[]> xValues = new ArrayList<Date[]>();
		// for(int m=0;m<titles1.length;m++){
		Date[] datearr1 = new Date[b];
		for (int k = 0; k < b; k++) {

			String str = data2[k];
			Format f = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			try {
				Date d = (Date) f.parseObject(str);
				datearr1[k] = d;

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		xValues.add(datearr1);

		// 开始画第二张图
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		mChartView2 = ChartFactory.getTimeChartView(this,
				buildDateDataset2(titles2, xValues, yValues2),
				buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		// mChartView2 =
		// ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2));
		linearLayout2.removeAllViews(); // 先remove再add可以实现统计图更新
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}

	private XYMultipleSeriesRenderer buildRenderer1(int[] colors1,
			PointStyle[] pss1) {//按时间画x轴

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer1.setAxisTitleTextSize(20); // 坐标轴标题字体大小
		renderer1.setChartTitleTextSize(30);// 图标标题字体大小
		renderer1.setChartTitle("血压数据曲线");
		renderer1.setLabelsTextSize(30);// 轴标签字体大小
		renderer1.setLegendTextSize(40);// 图例字体大小
		renderer1.setPointSize(7f);//
		renderer1.setAxesColor(Color.BLACK);
		renderer1.setYLabels(6);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(3);// Sets the approximate number of labels for the
		renderer1.setAxesColor(Color.BLACK); // X axis.
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		renderer1.setShowGrid(true);
		renderer1.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer1.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// renderer1.setPanLimits(new double[] {0,100,0,0});//设置拖动时X轴Y轴允许的最大值最小值
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		renderer1.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer1.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());

		renderer1.setYAxisMax(250);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // 设置图形四周的留白
		int length1 = colors1.length;
		for (int i = 0; i < length1; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors1[i]);
			r.setPointStyle(pss1[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
			renderer1.addSeriesRenderer(r);
		}
		return renderer1;
	};

	// 画图
	private XYMultipleSeriesDataset buildDateDataset1(String[] titles1,
			List<Date[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles1.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series1 = new TimeSeries(titles1[i]);
			Date[] xV = xValues.get(0); // 画TimeChart横坐标需要为Date数据类型
			double[] yV1 = yValues1.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series1.add(xV[k], yV1[k]);

			}
			dataset1.addSeries(series1);
		}
		return dataset1;

	}

	private XYMultipleSeriesRenderer buildRenderer2(int[] colors2,
			PointStyle[] pss2) {//按时间画x轴
		XYMultipleSeriesRenderer renderer2 = new XYMultipleSeriesRenderer();

		renderer2.setApplyBackgroundColor(true);
		renderer2.setBackgroundColor(Color.TRANSPARENT);
		renderer2.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer2.setAxisTitleTextSize(20); // 各种设置。。。。
		renderer2.setChartTitleTextSize(30);
		renderer2.setChartTitle("血糖数据曲线");
		renderer2.setLabelsTextSize(30);
		renderer2.setLegendTextSize(40);
		renderer2.setAxesColor(Color.BLACK);
		renderer2.setPointSize(7f);
		renderer2.setYLabels(7);
		renderer2.setXLabels(3);
		renderer2.setShowGrid(true);
		renderer2.setYLabelsAlign(Align.RIGHT);// 设置刻度线与Y轴之间的相对位置关系
		renderer2.setGridColor(Color.LTGRAY);// 网格的颜色
		renderer2.setPanEnabled(true, false);
		renderer2.setZoomEnabled(true, false);
		// renderer2.setPanLimits(new double[] {0,100,0,0});//设置拖动时X轴Y轴允许的最大值最小值
		// 此处最大最小表示相隔一天，相信很容易明白吧，具体跟date的参数值有关，这是我自己琢磨测试出来的，官方的权威的没有找到。目前好用
		// 可以多修改几次试试，同时注意上面说的参数，一块测试几个试试就ok了
		renderer2.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer2.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());
		renderer2.setYAxisMax(14);
		renderer2.setYAxisMin(1);
		renderer2.setLabelsColor(Color.rgb(60, 60, 60));
		renderer2.setMargins(new int[] { 40, 40, 40, 0 }); // 设置图形四周的留白
		int length2 = colors2.length;
		for (int i = 0; i < length2; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors2[i]);
			r.setPointStyle(pss2[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
			// r.setChartValuesSpacing(10);
			// r.setChartValuesTextSize(20);
			// r.setDisplayChartValues(true);
			renderer2.addSeriesRenderer(r);
		}
		return renderer2;
	};

	private XYMultipleSeriesDataset buildDateDataset2(String[] titles2,
			List<Date[]> xValues, List<double[]> yValues2) {

		XYMultipleSeriesDataset dataset2 = new XYMultipleSeriesDataset();
		int length = titles2.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series2 = new TimeSeries(titles2[i]);
			Date[] xV = xValues.get(0); // 画TimeChart横坐标需要为Date数据类型 i修改为了0
			double[] yV2 = yValues2.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series2.add(xV[k], yV2[k]);
			}
			dataset2.addSeries(series2);
		}
		return dataset2;

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

	@Override
	protected void onRestart() {
		// ct = new CurrentTime();
		// userName = screenName;
		// starttime = ct.OnepreweekTimeToString();
		// endtime = ct.TimeToString();
		if (userId < 0) {
			Intent intent0 = new Intent(Datacurve.this, MainActivity.class);
			// intent0.putExtra("userId", userId);
			// intent0.putExtra("screenName",screenName);
			startActivity(intent0);
		}

		super.onRestart();
	}

	@Override
	protected void onResume() {
		if (userId > 0) {
			initusernamepop();
			datacurve_username_imageView.setImageResource(R.drawable.down);
			datacurve_timerange_imageView.setImageResource(R.drawable.down);
			datacurve_ciortime_imageView.setImageResource(R.drawable.down);
			if (!starttime.equals("")) {
				if (timeorci == 0) {
					showpc_data(userName, starttime, endtime);
					showpc_bgdata(userName, starttime, endtime);

				} else if (timeorci == 1) {
					showpc_databytime(userName, starttime, endtime);
					showpc_bgdatabytime(userName, starttime, endtime);
				}
			} else if (starttime.equals("")) {

				if (timeorci == 0) {
					showpc_databyname(userName);
					showpc_bgdatabyname(userName);

				} else if (timeorci == 1) {
					showpc_databynameandtime(userName);
					showpc_bgdatabynameandtime(userName);
				}
			}

		}

		super.onResume();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu items for use in the action bar MenuInflater inflater =
	 * getMenuInflater(); inflater.inflate(R.menu.menu, menu); return
	 * super.onCreateOptionsMenu(menu); }
	 * 
	 * public boolean onOptionsItemSelected(MenuItem item) { // TODO
	 * Auto-generated method stub switch (item.getItemId()) {
	 * 
	 * case R.id.ciortime: if(timeorci==0){ item.setTitle("时间跟踪"); if
	 * (starttime.equals("")) { showpc_databyname(userName);
	 * showpc_bgdatabyname(userName); } else { showpc_data(userName, starttime,
	 * endtime); showpc_bgdata(userName, starttime, endtime); } }else
	 * if(timeorci==1){ if (starttime.equals("")) {
	 * showpc_databynameandtime(userName); showpc_bgdatabynameandtime(userName);
	 * } else { showpc_databytime(userName, starttime, endtime);
	 * showpc_bgdatabytime(userName, starttime, endtime); } } break; default:
	 * break; } return super.onOptionsItemSelected(item); }
	 */

}