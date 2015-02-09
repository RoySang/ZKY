package sict.zky.datacurve;

import java.util.ArrayList;
import java.util.List;

import sict.zky.deskclock.R;
import sict.zky.main.LoginActivity;
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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DatacurveGLU extends FragmentActivity {
	private ActivityMainShuiQian shuiqian;
	private ActivityMaincanqian canqian;
	private ActivityMaincanhou canhou;
	private ActivityMainlingchen lingchen;

	private LinearLayout shuiqiantab;
	private LinearLayout canqiantab;
	private LinearLayout canhoutab;
	private LinearLayout lingchentab;

	private Pc_userService pc_userService;
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;

	private List<String> getuserNamebyuserId;// ͨ��userId���userName
	private String getuserName;
	private SharedPreferences sp;

	private int userId;
	private String screenName;
	private String userName;
	private CurrentTime ct;
	private String onepreweek, nowweekTime, onepremonth;
	private String starttime = "0", endtime = "0";// ��ʼʱ��ͽ���ʱ��,"0"������ʾȫ��
	private int timeorci;// ����׷�ٻ�ʱ��׷�� 0�����Σ�1����ʱ��
	private long firstTime = 0;

	// �û�������ʽ����
	private TextView datacurve_glu_username_textView;
	private ImageView datacurve_glu_username_imageView;
	private PopupWindow pw_glu_username;// PopupWindow��������
	private LinearLayout datacurve_glu_username_linear;
	private ArrayList<String> glu_usernamelist;// �û�����ʾ�б�
	int clickPsitionofglu_username = -1;// �û����б���λ��

	// ׷�ٷ�ʽ����
	private TextView datacurve_glu_ciortime_textView;
	private ImageView datacurve_glu_ciortime_imageView;
	// PopupWindow��������
	private PopupWindow pw_glu_ciortime;
	private LinearLayout datacurve_glu_ciortime_linear;
	private ArrayList<String> glu_ciortimelist;// �λ�ʱ����ʾ�б�
	int clickPsitionofglu_ciortime = -1;// ��ǰ׷��ѡ�е��б���λ��

	// ʱ�䷶Χ����
	private TextView datacurve_glu_timerange_textView;
	private ImageView datacurve_glu_timerange_imageView;
	// PopupWindow��������
	private PopupWindow pw_glu_timerange;
	private LinearLayout datacurve_glu_timerange_linear;
	private ArrayList<String> glu_timerangelist;// ʱ�䷶Χ��ʾ�б�
	int clickPsitionofglu_timerange = -1;// ʱ�䷶Χ�б���λ��

	private FragmentManager fragmentManager;
	private FragmentTransaction transaction;
	private int type = 0;// ��־λ����ʾ��ǰѡ�����0��ǰ��1�ͺ�2˯ǰ��3�賿
	private Button canqianbtn,canhoubtn,shuiqianbtn,lingchenbtn;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		// Intent intent = getIntent();
		// userId = intent.getIntExtra("userId", -1);
		// screenName = intent.getStringExtra("screenName");

		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		userId = sp.getInt("USER_ID", -1);
		screenName = sp.getString("USER_NAME", "");

		setContentView(R.layout.datacurveglu);

		pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());

		getuserNamebyuserId = pc_userService.getuserName(userId);

		ct = new CurrentTime();
		userName = screenName;
		timeorci = 0;
		starttime = "0";
		endtime = "0";

		datacurve_glu_username_linear = (LinearLayout) findViewById(R.id.datacurve_glu_username_linear);
		datacurve_glu_username_textView = (TextView) findViewById(R.id.datacurve_glu_username_textView);
		datacurve_glu_username_imageView = (ImageView) findViewById(R.id.datacurve_glu_username_imageView);
		datacurve_glu_timerange_linear = (LinearLayout) findViewById(R.id.datacurve_glu_timerange_linear);
		datacurve_glu_timerange_textView = (TextView) findViewById(R.id.datacurve_glu_timerange_textView);
		datacurve_glu_timerange_imageView = (ImageView) findViewById(R.id.datacurve_glu_timerange_imageView);
		datacurve_glu_ciortime_linear = (LinearLayout) findViewById(R.id.datacurve_glu_ciortime_linear);
		datacurve_glu_ciortime_textView = (TextView) findViewById(R.id.datacurve_glu_ciortime_textView);
		datacurve_glu_ciortime_imageView = (ImageView) findViewById(R.id.datacurve_glu_ciortime_imageView);
		initusernamepop();// ��ʼ���û���popwindow
		inittimerangepop();// ��ʼ��ʱ�䷶Χpopwindow
		initciortimepop();// ��ʼ��׷�ٷ�ʽpopwindow

		initViews();
		setTabSelection(0);
		// setTabSelection(4);

	}

	private void initusernamepop() {// �û���popwindow��ʼ��
		glu_usernamelist = getusernameList();
		// final int size = getuserNamebyuserId.size() + 1;
		// ����Ĭ����ʾ��Text
		datacurve_glu_username_textView.setText(glu_usernamelist.get(0));
		datacurve_glu_username_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_glu_username_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_username, null);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
						pw_glu_username = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);
						// pw_username.setOutsideTouchable(true);
						pw_glu_username.setBackgroundDrawable(getResources()
								.getDrawable(
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));

						// ���ý���Ϊ�ɵ��
						pw_glu_username.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
						pw_glu_username
								.showAsDropDown(datacurve_glu_username_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_username);
						lv.setAdapter(new ListViewAdapter(DatacurveGLU.this,
								glu_usernamelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_glu_username_textView
										.setText(glu_usernamelist.get(position));
								if (clickPsitionofglu_username != position) {
									clickPsitionofglu_username = position;
								}
								// getuserName =
								// parent.getItemAtPosition(position).toString();
								getuserName = glu_usernamelist.get(position);
								userName = getuserName;

								setTabSelection(type);

								datacurve_glu_username_imageView
										.setImageResource(R.drawable.down);
								pw_glu_username.dismiss();
							}
						});
					}

				});
	}

	private void inittimerangepop() {// ʱ�䷶Χpopwindow��ʼ��
		glu_timerangelist = gettimerangeList();

		// ����Ĭ����ʾ��Text
		datacurve_glu_timerange_textView.setText(glu_timerangelist.get(0));
		datacurve_glu_timerange_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_glu_timerange_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_timerange, null);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
						// WindowManager windowManager = getWindowManager();
						// Display display = windowManager.getDefaultDisplay();
						// WindowManager.LayoutParams params = getWindow()
						// .getAttributes();
						// params.alpha = 0.7f;
						// getWindow().setAttributes(params);
						pw_glu_timerange = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_glu_timerange.setBackgroundDrawable(getResources()
								.getDrawable(
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));

						// ���ý���Ϊ�ɵ��
						pw_glu_timerange.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
						pw_glu_timerange
								.showAsDropDown(datacurve_glu_timerange_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_timerange);
						lv.setAdapter(new ListViewAdapter(DatacurveGLU.this,
								glu_timerangelist));
						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_glu_timerange_textView
										.setText(glu_timerangelist
												.get(position));
								if (clickPsitionofglu_timerange != position) {
									clickPsitionofglu_timerange = position;
								}

								switch (position) {
								case 0:
									starttime = "0";
									endtime = "0";
									break;
								case 1:
									onepreweek = ct.OnepreweekTimeToString();// �õ�һ��ǰ��ʱ��
									nowweekTime = ct.TimeToString();// �õ���ǰʱ��

									starttime = onepreweek;
									endtime = nowweekTime;
									break;
								case 2:

									onepremonth = ct.OnepremonthTimetoString();// �õ�һ����ǰ��ʱ��
									nowweekTime = ct.TimeToString();

									starttime = onepremonth;
									endtime = nowweekTime;

									break;

								default:
									// datacurve_timerange_imageView.setImageResource(R.drawable.down);
									break;
								}

								setTabSelection(type);

								datacurve_glu_timerange_imageView
										.setImageResource(R.drawable.down);
								pw_glu_timerange.dismiss();
							}
						});
					}

				});
	}

	private void initciortimepop() {// ׷�ٷ�ʽpopwindow��ʼ��
		glu_ciortimelist = getciortimeList();
		// ciortimelist=getciortimeList();
		// ����Ĭ����ʾ��Text
		datacurve_glu_ciortime_textView.setText(glu_ciortimelist.get(0));
		datacurve_glu_ciortime_linear
				.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						datacurve_glu_ciortime_imageView
								.setImageResource(R.drawable.up);
						// ͨ������ע������ע�벼�ָ�View����
						View myView = getLayoutInflater().inflate(
								R.layout.pop_ciortime, null);
						// WindowManager windowManager = getWindowManager();
						// Display display = windowManager.getDefaultDisplay();
						// WindowManager.LayoutParams params = getWindow()
						// .getAttributes();
						// params.alpha = 0.7f;
						// getWindow().setAttributes(params);
						// ͨ��view �Ϳ��ߣ�����PopopWindow
						// pw_ciortime = new PopupWindow(myView,
						// LayoutParams.FILL_PARENT,
						// LayoutParams.FILL_PARENT, true);
						// pw=new PopupWindow(myView);

						pw_glu_ciortime = new PopupWindow(myView,
								LayoutParams.FILL_PARENT,
								LayoutParams.WRAP_CONTENT, true);
						// pw=new PopupWindow(myView);

						pw_glu_ciortime.setBackgroundDrawable(getResources()
								.getDrawable(
								// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
										R.drawable.bg_white));

						// pw_ciortime.setBackgroundDrawable(new
						// ColorDrawable(0x55000000));
						// pw_ciortime.
						// ���ý���Ϊ�ɵ��
						pw_glu_ciortime.setFocusable(true);// ����������Ϊfalse�Ľ��
						// ��window��ͼ��ʾ��myButton����
						pw_glu_ciortime
								.showAsDropDown(datacurve_glu_ciortime_textView);

						ListView lv = (ListView) myView
								.findViewById(R.id.lv_pop_ciortime);
						lv.setAdapter(new ListViewAdapter(DatacurveGLU.this,
								glu_ciortimelist));

						lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								datacurve_glu_ciortime_textView
										.setText(glu_ciortimelist.get(position));
								if (clickPsitionofglu_ciortime != position) {
									clickPsitionofglu_ciortime = position;
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

								setTabSelection(type);

								pw_glu_ciortime.dismiss();
								datacurve_glu_ciortime_imageView
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

	private void initViews() {
		resetBtn();
		shuiqiantab = (LinearLayout) findViewById(R.id.id_tab_top_shuiqian);
		canqiantab = (LinearLayout) findViewById(R.id.id_tab_top_canqian);
		canhoutab = (LinearLayout) findViewById(R.id.id_tab_top_canhou);
		lingchentab = (LinearLayout) findViewById(R.id.id_tab_top_lingchen);
		canqianbtn = (Button) findViewById(R.id.btn_tab_top_canqian);
		canhoubtn = (Button) findViewById(R.id.btn_tab_top_canhou);
		shuiqianbtn = (Button) findViewById(R.id.btn_tab_top_shuiqian);
		lingchenbtn = (Button) findViewById(R.id.btn_tab_top_lingchen);
		shuiqiantab.setOnClickListener(onclicklistener);
		canqiantab.setOnClickListener(onclicklistener);
		canhoutab.setOnClickListener(onclicklistener);
		lingchentab.setOnClickListener(onclicklistener);
	}

	public OnClickListener onclicklistener = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.id_tab_top_canqian:
				canqianbtn.setBackgroundResource(R.drawable.cur_btn_bs);
				canhoubtn.setBackgroundResource(R.drawable.bg_white);
				shuiqianbtn.setBackgroundResource(R.drawable.bg_white);
				lingchenbtn.setBackgroundResource(R.drawable.bg_white);
				type = 0;
				setTabSelection(0);
				break;
			case R.id.id_tab_top_canhou:
				canqianbtn.setBackgroundResource(R.drawable.bg_white);
				canhoubtn.setBackgroundResource(R.drawable.cur_btn_bs);
				shuiqianbtn.setBackgroundResource(R.drawable.bg_white);
				lingchenbtn.setBackgroundResource(R.drawable.bg_white);
				type = 1;
				setTabSelection(1);
				break;
			case R.id.id_tab_top_shuiqian:
				canqianbtn.setBackgroundResource(R.drawable.bg_white);
				canhoubtn.setBackgroundResource(R.drawable.bg_white);
				shuiqianbtn.setBackgroundResource(R.drawable.cur_btn_bs);
				lingchenbtn.setBackgroundResource(R.drawable.bg_white);
				type = 2;
				setTabSelection(2);
				break;
			case R.id.id_tab_top_lingchen:
				canqianbtn.setBackgroundResource(R.drawable.bg_white);
				canhoubtn.setBackgroundResource(R.drawable.bg_white);
				shuiqianbtn.setBackgroundResource(R.drawable.bg_white);
				lingchenbtn.setBackgroundResource(R.drawable.cur_btn_bs);
				type = 3;
				setTabSelection(3);
				break;
			default:
				break;
			}
		}
	};

	@SuppressLint("NewApi")
	private void setTabSelection(int index) {
		
		fragmentManager = getSupportFragmentManager();
		transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
//			((ImageButton) canqiantab.findViewById(R.id.btn_tab_top_canqian))
//					.setImageResource(R.drawable.cur_btn_bmeal_current);
			if (canqian == null) {
				canqian = new ActivityMaincanqian();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.add(R.id.id_content2, canqian);

				transaction.commit();// �����ύ
			} else {
				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				canqian = new ActivityMaincanqian();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, canqian); // ��id_content1���fragment�滻Ϊgaoya
				transaction.commit();// �����ύ
			}
			break;
		case 1:
//			((ImageButton) canhoutab.findViewById(R.id.btn_tab_top_canhou))
//					.setImageResource(R.drawable.cur_btn_ameal_current);
			if (canhou == null) {
				canhou = new ActivityMaincanhou();
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, canhou); // ��id_content2���fragment�滻Ϊcanhou
				transaction.commit();
			} else {
				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				canhou = new ActivityMaincanhou();
				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, canhou); // ��id_content2���fragment�滻Ϊcanhou
				transaction.commit();
			}
			break;

		case 2:
//			((ImageButton) shuiqiantab.findViewById(R.id.btn_tab_top_shuiqian))
//					.setImageResource(R.drawable.cur_btn_bsleep_current);
			if (shuiqian == null) {
				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
				shuiqian = new ActivityMainShuiQian();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);

				transaction.replace(R.id.id_content2, shuiqian); // ��id_content2���fragment�滻Ϊshuiqian
				transaction.commit();

			} else {
				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				shuiqian = new ActivityMainShuiQian();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, shuiqian); // ��id_content2���fragment�滻Ϊshuiqian

				transaction.commit();// �����ύ
			}
			break;

		case 3:
//			((ImageButton) lingchentab.findViewById(R.id.btn_tab_top_lingchen))
//					.setImageResource(R.drawable.cur_btn_bdawn_current);
			if (lingchen == null) {
				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
				lingchen = new ActivityMainlingchen();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, lingchen); // ��id_content2���fragment�滻Ϊshuiqian

				transaction.commit();// �����ύ
			} else {
				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
				lingchen = new ActivityMainlingchen();

				setUserName(userName);
				setStarttime(starttime);
				setEndtime(endtime);
				setTimeorci(timeorci);
				transaction.replace(R.id.id_content2, lingchen); // ��id_content2���fragment�滻Ϊshuiqian

				transaction.commit();// �����ύ
			}
			break;

		}
		// transaction.commit();
	}

	private void resetBtn() {

//		((ImageButton) shuiqiantab.findViewById(R.id.btn_tab_top_shuiqian))
//				.setImageResource(R.drawable.cur_btn_bsleep);
//		((ImageButton) canqiantab.findViewById(R.id.btn_tab_top_canqian))
//				.setImageResource(R.drawable.cur_btn_bmeal);
//		((ImageButton) canhoutab.findViewById(R.id.btn_tab_top_canhou))
//				.setImageResource(R.drawable.cur_btn_ameal);
//		((ImageButton) lingchentab.findViewById(R.id.btn_tab_top_lingchen))
//				.setImageResource(R.drawable.cur_btn_bdawn);
		canqianbtn = (Button) findViewById(R.id.btn_tab_top_canqian);
		canhoubtn = (Button) findViewById(R.id.btn_tab_top_canhou);
		shuiqianbtn = (Button) findViewById(R.id.btn_tab_top_shuiqian);
		lingchenbtn = (Button) findViewById(R.id.btn_tab_top_lingchen);
		canqianbtn.setBackgroundResource(R.drawable.cur_btn_bs);
		canhoubtn.setBackgroundResource(R.drawable.bg_white);
		shuiqianbtn.setBackgroundResource(R.drawable.bg_white);
		lingchenbtn.setBackgroundResource(R.drawable.bg_white);
		
	}

	@SuppressLint("NewApi")
	private void hideFragments(FragmentTransaction transaction) {

		if (shuiqian != null) {
			transaction.hide(shuiqian);
		}
		if (canqian != null) {
			transaction.hide(canqian);
		}
		if (canhou != null) {
			transaction.hide(canhou);
		}
		if (lingchen != null) {
			transaction.hide(lingchen);
		}

	}

	// get set���� fragment�����ʹ��Activity���get�����õ�ֵ
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
}
