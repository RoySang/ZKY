package sict.zky.datacurve;

import sict.zky.deskclock.R;
import sict.zky.input.UPLOAD_PC_BGDATAActivity;
import sict.zky.input.UPLOAD_PC_DATAActivity;
import sict.zky.input.inputActivity;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.setting.SettingofVisitorActivity;
import sict.zky.setting.UserInfoActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainDatacurve extends TabActivity
{
	private RadioGroup group;
	private TabHost tabHost;
	public static final String TAB_xueya = "xueya";
	public static final String TAB_xuetang = "xuetang";
	private Button xueyaButton,xuetangButton;
	private int userId;
	private String screenName;
	private TabHost th;
	private long firstTime = 0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 Intent intent=getIntent();
	        userId=intent.getIntExtra("userId", -1);
	        screenName=intent.getStringExtra("screenName");
	        if(userId<0){
	        	Toast.makeText(getApplicationContext(), "请先登录", 1).show();
	        	Intent intent0=new Intent(MainDatacurve.this,LoginActivity.class);
	        	startActivity(intent0);
	        	finish();
	        }else{
	        	setContentView(R.layout.inputdata);
	        	 th = this.getTabHost();//得到tabhost控件
	        	 LayoutInflater.from(this).inflate(R.layout.userfamilydata, th.getTabContentView(), true);
	  		    RelativeLayout tabStyle1 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.table_style, null);
	  		    TextView text1 = (TextView) tabStyle1.findViewById(R.id.tab_label);
	  		    text1.setText("血压");
//	  		    text1.setTextColor(Color.RED);
//	  		    text1.setTextSize(30);
	  		    RelativeLayout tabStyle2 = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.table_style, null);
	  		    TextView text2 = (TextView) tabStyle2.findViewById(R.id.tab_label);
	  		    text2.setText("血糖");
	  		    
	          	
	               TabWidget tabWidget =this.getTabWidget();
	               TabHost.TabSpec tabSpec1 = th.newTabSpec("tab1");
	   			//TabHost.TabSpec tabSpec1 = th.newTabSpec("tab1");
	   			tabSpec1.setIndicator(tabStyle1);
	       		Intent intent1 = new Intent();
	       		intent1.setClass(this, DatacurvePressure.class);
	       		intent1.putExtra("userId", userId);
	       		intent1.putExtra("screenName", screenName);
	       		tabSpec1.setContent(intent1);
	       		
	       		TabHost.TabSpec tabSpec2 = th.newTabSpec("tab2");
	  			tabSpec2.setIndicator(tabStyle2);
	  			
	       		Intent intent2 = new Intent();
	       		intent2.setClass(this, DatacurveGLU.class);
	       		intent2.putExtra("userId", userId);
	       		intent2.putExtra("screenName", screenName);
	       		tabSpec2.setContent(intent2);
	       		//tabSpec2.setIndicator("血糖");
//	       		Toast.makeText(getApplicationContext(), th.getChildCount()+"", 1).show();
//	       		for(int i=0;i<tabWidget.getChildCount();i++){
//	       			TextView tv=(TextView)tabWidget.getChildAt(i).findViewById(android.R.id.title);
//	       			tv.setTextSize(50);
//	       			tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
//	       		}
	       		
	       		th.addTab(tabSpec1);
	       		th.addTab(tabSpec2);
	       		th.setCurrentTab(0);
	          }
	         
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
