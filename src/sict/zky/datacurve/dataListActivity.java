package sict.zky.datacurve;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.setting.UserInfoActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class dataListActivity extends TabActivity {
	private TabHost th;
	private int userId;
	private String userName;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// 使用系统的layout文件初始化界面结构

		// getActionBar().setTitle(inputActivity);

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", -1);
		userName = intent.getStringExtra("userName");
		if (userId < 0) {
			Toast.makeText(getApplicationContext(), "请先登录", 1).show();
			Intent intent0 = new Intent(dataListActivity.this,
					LoginActivity.class);
			startActivity(intent0);
			finish();
		} else {
			setContentView(R.layout.inputdata);
			th = this.getTabHost();// 得到tabhost控件
			LayoutInflater.from(this).inflate(R.layout.userfamilydata, th.getTabContentView(), true);
		    RelativeLayout tabStyle1 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.table_style, null);
		    TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
		    text1.setText("血压");
//		    text1.setTextColor(Color.RED);
//		    text1.setTextSize(30);
		    RelativeLayout tabStyle2 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.table_style, null);
		    TextView text2 = (TextView) tabStyle2.findViewById(R.id.tab_label);
		    text2.setText("血糖");
		    
			TabWidget tabWidget = this.getTabWidget();
			TabHost.TabSpec tabSpec1 = th.newTabSpec("tab1");
			//TabHost.TabSpec tabSpec1 = th.newTabSpec("tab1");
			tabSpec1.setIndicator(tabStyle1);
			//tabSpec1.setContent(R.id.Tab1);
			Intent intent1 = new Intent();
			intent1.setClass(this, Pc_data_listActivity.class);
			intent1.putExtra("userId", userId);
			intent1.putExtra("userName", userName);
			tabSpec1.setContent(intent1);

			TabHost.TabSpec tabSpec2 = th.newTabSpec("tab2");
			tabSpec2.setIndicator(tabStyle2);
			//tabSpec2.setContent(R.id.Tab2);
			
			
			Intent intent2 = new Intent();
			intent2.setClass(this, Pc_bgdata_listActivity.class);
			intent2.putExtra("userId", userId);
			intent2.putExtra("userName", userName);
			tabSpec2.setContent(intent2);
			//tabSpec2.setIndicator("血糖");

			// Toast.makeText(getApplicationContext(), th.getChildCount()+"",
			// 1).show();
			// for(int i=0;i<tabWidget.getChildCount();i++){
			// TextView
			// tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
			// tv.setTextSize(50);
			// tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
			// }

			th.addTab(tabSpec1);
			th.addTab(tabSpec2);
			th.setCurrentTab(0);
		}

	}
	@Override
	protected void onStart()
	{
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		TestinAgent.onStop(this);
	}

	@Override
	protected void onRestart() {
		if (userId < 0) {
			Intent intent0 = new Intent(dataListActivity.this,
					MainActivity.class);
			startActivity(intent0);
		}
		super.onRestart();
	}

}
