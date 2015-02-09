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
	private String titles1[] = { "����ѹ", "����ѹ", "����" };
	private String titles2[] = { "Ѫ��  mmol/L", "", "" };
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
	private static String timeRange[] = { "ȫ��", "һ����", "һ����" };
	private static String ciortime[] = { "�θ���", "ʱ�����" };
	private ArrayAdapter<String> adapter;
	// private ArrayAdapter<String> adapterciortime;
	private int timeorci = 0;
	private long firstTime = 0;
	private SharedPreferences sp;

	private int width;// ��Ļ���

	// �û�������ʽ����
	private TextView datacurve_username_textView;
	private ImageView datacurve_username_imageView;
	private PopupWindow pw_username;// PopupWindow��������
	private LinearLayout datacurve_username_linear;
	private ArrayList<String> usernamelist;// �û�����ʾ�б�
	int clickPsitionofusername = -1;// �û����б���λ��

	// ׷�ٷ�ʽ����
	private TextView datacurve_ciortime_textView;
	private ImageView datacurve_ciortime_imageView;
	// PopupWindow��������
	private PopupWindow pw_ciortime;
	private LinearLayout datacurve_ciortime_linear;
	private ArrayList<String> ciortimelist;// �λ�ʱ����ʾ�б�
	int clickPsitionofciortime = -1;// ��ǰ׷��ѡ�е��б���λ��

	// ʱ�䷶Χ����
	private TextView datacurve_timerange_textView;
	private ImageView datacurve_timerange_imageView;
	// PopupWindow��������
	private PopupWindow pw_timerange;
	private LinearLayout datacurve_timerange_linear;
	private ArrayList<String> timerangelist;// ʱ�䷶Χ��ʾ�б�
	int clickPsitionoftimerange = -1;// ʱ�䷶Χ�б���λ��

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);

		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);

		width = metric.widthPixels; // ��Ļ��ȣ����أ�
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		userId = sp.getInt("USER_ID", -1);
		screenName = sp.getString("USER_NAME", "");
		// Intent intent = getIntent();
		// userId = intent.getIntExtra("userId", -1);
		// screenName = intent.getStringExtra("screenName");
		if (userId < 0) {
			Toast.makeText(getApplicationContext(), "���½", 1).show();
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

			// ��ʼ���ؼ�
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

	private void initusernamepop() {// ʱ�䷶Χpopwindow��ʼ��
		usernamelist = getusernameList();
//		final int size = getuserNamebyuserId.size() + 1;
		// ����Ĭ����ʾ��Text
		datacurve_username_textView.setText(usernamelist.get(0));
		datacurve_username_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_username_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_username, null);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
						pw_username = new PopupWindow(myView, LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT,
								true);
						// pw=new PopupWindow(myView);
//						pw_username.setOutsideTouchable(true);
						pw_username.setBackgroundDrawable(getResources()
								.getDrawable(
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));

						// ���ý���Ϊ�ɵ��
						pw_username.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
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

	private void inittimerangepop() {// ʱ�䷶Χpopwindow��ʼ��
		timerangelist = gettimerangeList();

		// ����Ĭ����ʾ��Text
		datacurve_timerange_textView.setText(timerangelist.get(0));
		datacurve_timerange_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_timerange_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_timerange, null);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
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
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));

						// ���ý���Ϊ�ɵ��
						pw_timerange.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
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
									onepreweek = ct.OnepreweekTimeToString();// �õ�һ��ǰ��ʱ��
									// onepreweek =
									// ct.OnepremonthTimetoString();
									nowweekTime = ct.TimeToString();// �õ���ǰʱ��

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

									onepremonth = ct.OnepremonthTimetoString();// �õ�һ����ǰ��ʱ��
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

	private void initciortimepop() {// ׷�ٷ�ʽpopwindow��ʼ��
		ciortimelist = getciortimeList();
		// ciortimelist=getciortimeList();
		// ����Ĭ����ʾ��Text
		datacurve_ciortime_textView.setText(ciortimelist.get(0));
		datacurve_ciortime_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_ciortime_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_ciortime, null);
//						WindowManager windowManager = getWindowManager();
//						Display display =  windowManager.getDefaultDisplay();  
//						WindowManager.LayoutParams params = getWindow()
//								.getAttributes();
//						params.alpha = 0.7f;
//						getWindow().setAttributes(params);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
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
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));
						
//						pw_ciortime.setBackgroundDrawable(new ColorDrawable(0x55000000)); 
						// pw_ciortime.
						// ���ý���Ϊ�ɵ��
						pw_ciortime.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
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
		list.add("ȫ��");
		list.add("һ����");
		list.add("һ����");
		return list;

	}

	public ArrayList<String> getciortimeList() {

		ArrayList<String> list = new ArrayList<String>();
		list.add("����׷��");
		list.add("��ʱ��׷��");
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
			a = pc_datas.size();// �ܹ����ݵĸ���
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

		// ��ʼ����һ��ͼ
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		// mChartView1 = ChartFactory.getTimeChartView(this,
		// buildDateDataset1(titles1, xValues, yValues1),
		// buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		mChartView1 = ChartFactory.getLineChartView(this,
				buildDateDataset01(titles1, xValues, yValues1),
				buildRenderer01(colors1, pss1));
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_databyname(String name) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectData(name);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// �ܹ����ݵĸ���
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

		// ��ʼ����һ��ͼ
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		// mChartView1 = ChartFactory.getTimeChartView(this,
		// buildDateDataset1(titles1, xValues, yValues1),
		// buildRenderer1(colors1, pss1), "");
		mChartView1 = ChartFactory.getLineChartView(this,
				buildDateDataset01(titles1, xValues, yValues1),
				buildRenderer01(colors1, pss1));
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
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

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]��������
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

		// ��ʼ���ڶ���ͼ
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		// mChartView2 = ChartFactory.getTimeChartView(this,
		// buildDateDataset02(titles2, xValues, yValues2),
		// buildRenderer02(colors2, pss2), "MM-dd HH:mm");
		mChartView2 = ChartFactory.getLineChartView(this,
				buildDateDataset02(titles2, xValues, yValues2),
				buildRenderer02(colors2, pss2));
		linearLayout2.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
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

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]��������
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

		// ��ʼ���ڶ���ͼ
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		// mChartView2 = ChartFactory.getTimeChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		mChartView2 = ChartFactory.getLineChartView(this,
				buildDateDataset02(titles2, xValues, yValues2),
				buildRenderer02(colors2, pss2));
		linearLayout2.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }
	}

	// }
	private XYMultipleSeriesRenderer buildRenderer01(int[] colors1,
			PointStyle[] pss1) {//���λ�x��

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));
		renderer1.setAxesColor(Color.BLACK);

		renderer1.setAxisTitleTextSize(20); // ��������������С
		renderer1.setChartTitleTextSize(30);// ͼ����������С
		renderer1.setChartTitle("Ѫѹ��������");

		renderer1.setLabelsTextSize(30);// ���ǩ�����С
		renderer1.setLegendTextSize(40);// ͼ�������С
		renderer1.setPointSize(7f);//
		renderer1.setYLabels(8);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(10);// Sets the approximate number of labels for
									// the
									// X axis.
									// renderer1.setLabelsColor(Color.rgb(0, 0,
									// 0));
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		// renderer1.setShowGrid(true);// �Ƿ���ʾ����
		renderer1.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer1.setGridColor(Color.LTGRAY);// �������ɫ
		renderer1.setPanLimits(new double[] { 0, 10000, 0, 0 });// �����϶�ʱX��Y����������ֵ��Сֵ

		// renderer1.setA
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		// renderer1.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer1.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());

		renderer1.setXAxisMax(10);
		renderer1.setXAxisMin(0);

		renderer1.setYAxisMax(250);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // ����ͼ�����ܵ�����
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

	// ��ͼ
	private XYMultipleSeriesDataset buildDateDataset01(String[] titles1,
			List<double[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles1.length;
		for (int i = 0; i < length; i++) {
			XYSeries series1 = new XYSeries(titles1[i]);
			double[] xV = xValues.get(0); // ��TimeChart��������ҪΪDate��������
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
			PointStyle[] pss2) {//���λ�x��
		XYMultipleSeriesRenderer renderer2 = new XYMultipleSeriesRenderer();

		renderer2.setApplyBackgroundColor(true);
		renderer2.setBackgroundColor(Color.TRANSPARENT);
		renderer2.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer2.setAxisTitleTextSize(20); // �������á�������
		renderer2.setChartTitleTextSize(30);
		renderer2.setChartTitle("Ѫ����������");
		renderer2.setLabelsTextSize(30);
		renderer2.setAxesColor(Color.BLACK);
		renderer2.setLegendTextSize(40);
		renderer2.setPointSize(7f);
		renderer2.setYLabels(10);
		renderer2.setXLabels(10);
		// renderer2.setShowGrid(true);
		renderer2.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer2.setGridColor(Color.LTGRAY);// �������ɫ
		renderer2.setPanEnabled(true, false);
		renderer2.setZoomEnabled(true, false);
		renderer2.setPanLimits(new double[] { 0, 10000, 0, 0 });// �����϶�ʱX��Y����������ֵ��Сֵ
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		// renderer2.setXAxisMin(new Date(year - 1900, month, day-7, hour,
		// minute).getTime());
		// renderer2.setXAxisMax(new Date(year - 1900, month, day+1, hour,
		// minute).getTime());
		renderer2.setXAxisMax(10);
		renderer2.setXAxisMin(0);
		renderer2.setYAxisMax(16);
		renderer2.setYAxisMin(0);
		renderer2.setLabelsColor(Color.rgb(60, 60, 60));
		renderer2.setMargins(new int[] { 40, 50, 40, 0 }); // ����ͼ�����ܵ�����
		int length2 = colors2.length;
		for (int i = 0; i < length2; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors2[i]);
			r.setPointStyle(pss2[i]);
			r.setFillPoints(true);
			r.setLineWidth(3.0f);
			r.setChartValuesSpacing(10);
			r.setChartValuesTextSize(20);

			// r.setDisplayChartValues(true);//��������ʾ����
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
			double[] xV = xValues.get(0); // ��TimeChart��������ҪΪ������
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
			a = pc_datas.size();// �ܹ����ݵĸ���
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

		// ��ʼ����һ��ͼ
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		mChartView1 = ChartFactory.getTimeChartView(this,
				buildDateDataset1(titles1, xValues, yValues1),
				buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		// mChartView1 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles1, xValues, yValues1),
		// buildRenderer2(colors1, pss1));
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout1.addView(mChartView1, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		// }

	}

	public void showpc_databynameandtime(String name) {
		// if (pc_dataService.ishavepc_data(name)) {
		List<Pc_data> pc_datas = pc_dataService.selectData(name);
		int a = 0;
		if (pc_datas != null) {
			a = pc_datas.size();// �ܹ����ݵĸ���
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

		// ��ʼ����һ��ͼ
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.container1);

		mChartView1 = ChartFactory.getTimeChartView(this,
				buildDateDataset1(titles1, xValues, yValues1),
				buildRenderer1(colors1, pss1), "MM-dd HH:mm");
		// mChartView1 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles1, xValues, yValues1),
		// buildRenderer2(colors1, pss1));
		linearLayout1.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
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

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]��������
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

		// ��ʼ���ڶ���ͼ
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		mChartView2 = ChartFactory.getTimeChartView(this,
				buildDateDataset2(titles2, xValues, yValues2),
				buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		// mChartView2 = ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2));
		linearLayout2.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
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

		// Log.v("GUAHU",data1[1]+"HOH");//data1[i]��������
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

		// ��ʼ���ڶ���ͼ
		LinearLayout linearLayout2 = (LinearLayout) findViewById(R.id.container2);
		mChartView2 = ChartFactory.getTimeChartView(this,
				buildDateDataset2(titles2, xValues, yValues2),
				buildRenderer2(colors2, pss2), "MM-dd HH:mm");
		// mChartView2 =
		// ChartFactory.getScatterChartView(this,
		// buildDateDataset2(titles2, xValues, yValues2),
		// buildRenderer2(colors2, pss2));
		linearLayout2.removeAllViews(); // ��remove��add����ʵ��ͳ��ͼ����
		linearLayout2.addView(mChartView2, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	}

	private XYMultipleSeriesRenderer buildRenderer1(int[] colors1,
			PointStyle[] pss1) {//��ʱ�仭x��

		XYMultipleSeriesRenderer renderer1 = new XYMultipleSeriesRenderer();

		renderer1.setApplyBackgroundColor(true);
		renderer1.setBackgroundColor(Color.TRANSPARENT);
		renderer1.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer1.setAxisTitleTextSize(20); // ��������������С
		renderer1.setChartTitleTextSize(30);// ͼ����������С
		renderer1.setChartTitle("Ѫѹ��������");
		renderer1.setLabelsTextSize(30);// ���ǩ�����С
		renderer1.setLegendTextSize(40);// ͼ�������С
		renderer1.setPointSize(7f);//
		renderer1.setAxesColor(Color.BLACK);
		renderer1.setYLabels(6);// Sets the approximate number of labels for
								// the Y axis.
		renderer1.setXLabels(3);// Sets the approximate number of labels for the
		renderer1.setAxesColor(Color.BLACK); // X axis.
		renderer1.setLabelsColor(Color.rgb(60, 60, 60));
		renderer1.setShowGrid(true);
		renderer1.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer1.setGridColor(Color.LTGRAY);// �������ɫ
		renderer1.setZoomEnabled(true, false);
		renderer1.setPanEnabled(true, false);
		// renderer1.setPanLimits(new double[] {0,100,0,0});//�����϶�ʱX��Y����������ֵ��Сֵ
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		renderer1.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer1.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());

		renderer1.setYAxisMax(250);
		renderer1.setYAxisMin(50);
		renderer1.setMargins(new int[] { 40, 50, 40, 0 }); // ����ͼ�����ܵ�����
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

	// ��ͼ
	private XYMultipleSeriesDataset buildDateDataset1(String[] titles1,
			List<Date[]> xValues, List<double[]> yValues1) {
		XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
		int length = titles1.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series1 = new TimeSeries(titles1[i]);
			Date[] xV = xValues.get(0); // ��TimeChart��������ҪΪDate��������
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
			PointStyle[] pss2) {//��ʱ�仭x��
		XYMultipleSeriesRenderer renderer2 = new XYMultipleSeriesRenderer();

		renderer2.setApplyBackgroundColor(true);
		renderer2.setBackgroundColor(Color.TRANSPARENT);
		renderer2.setMarginsColor(Color.argb(0x00, 0x01, 0x01, 0x01));

		renderer2.setAxisTitleTextSize(20); // �������á�������
		renderer2.setChartTitleTextSize(30);
		renderer2.setChartTitle("Ѫ����������");
		renderer2.setLabelsTextSize(30);
		renderer2.setLegendTextSize(40);
		renderer2.setAxesColor(Color.BLACK);
		renderer2.setPointSize(7f);
		renderer2.setYLabels(7);
		renderer2.setXLabels(3);
		renderer2.setShowGrid(true);
		renderer2.setYLabelsAlign(Align.RIGHT);// ���ÿ̶�����Y��֮������λ�ù�ϵ
		renderer2.setGridColor(Color.LTGRAY);// �������ɫ
		renderer2.setPanEnabled(true, false);
		renderer2.setZoomEnabled(true, false);
		// renderer2.setPanLimits(new double[] {0,100,0,0});//�����϶�ʱX��Y����������ֵ��Сֵ
		// �˴������С��ʾ���һ�죬���ź��������װɣ������date�Ĳ���ֵ�йأ��������Լ���ĥ���Գ����ģ��ٷ���Ȩ����û���ҵ���Ŀǰ����
		// ���Զ��޸ļ������ԣ�ͬʱע������˵�Ĳ�����һ����Լ������Ծ�ok��
		renderer2.setXAxisMin(new Date(year - 1900, month, day - 7, hour,
				minute).getTime());
		renderer2.setXAxisMax(new Date(year - 1900, month, day + 1, hour,
				minute).getTime());
		renderer2.setYAxisMax(14);
		renderer2.setYAxisMin(1);
		renderer2.setLabelsColor(Color.rgb(60, 60, 60));
		renderer2.setMargins(new int[] { 40, 40, 40, 0 }); // ����ͼ�����ܵ�����
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
			Date[] xV = xValues.get(0); // ��TimeChart��������ҪΪDate�������� i�޸�Ϊ��0
			double[] yV2 = yValues2.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series2.add(xV[k], yV2[k]);
			}
			dataset2.addSeries(series2);
		}
		return dataset2;

	}

	// ˫���˳�
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "�ٰ�һ�η��ؼ��˳��п��ƽ���", Toast.LENGTH_SHORT)
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
	 * case R.id.ciortime: if(timeorci==0){ item.setTitle("ʱ�����"); if
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