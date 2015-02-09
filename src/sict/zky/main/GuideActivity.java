package sict.zky.main ;

import java.util.ArrayList;

import sict.zky.deskclock.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.igexin.sdk.PushManager;
import com.testin.agent.TestinAgent;
/**
 * @author yangyu
 *	功能描述：主程序入口activity
 */
public class GuideActivity extends Activity {
	// 定义ViewPager对象
	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	//定义各个界面View对象
	private View view1,view2,view3,view4,view5,view6;
	
	// 定义底部小点图片
	private ImageView pointImage0, pointImage1, pointImage2, pointImage3,pointImage4, pointImage5;

	//定义开始按钮对象
	private Button startBt;
	
	// 当前的位置索引值
	private int currIndex = 0;
	private CheckBox rem_pw111;
	private static final String SHAREDPREFERENCES_NAME="first_pref";
	private SharedPreferences sp;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mainguide);
		SysApplication.getInstance().addActivity(this);
		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
	   PushManager.getInstance().initialize(this.getApplicationContext());
		
		initView();

		initData();
		
		
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

	/**
	 * 初始化组件
	 */
	private void initView() {
		//实例化各个界面的布局对象 
		LayoutInflater mLi = LayoutInflater.from(this);
		view1 = mLi.inflate(R.layout.guide_view01, null);
		view2 = mLi.inflate(R.layout.guide_view02, null);
		view3 = mLi.inflate(R.layout.guide_view03, null);
		view4 = mLi.inflate(R.layout.guide_view04, null);
		view5 = mLi.inflate(R.layout.guide_view05, null);
		view6 = mLi.inflate(R.layout.guide_view06, null);
				
		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewpager);

		// 实例化ArrayList对象
		views = new ArrayList<View>();
		
		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);
     
		// 实例化底部小点图片对象
		pointImage0 = (ImageView) findViewById(R.id.page0);
		pointImage1 = (ImageView) findViewById(R.id.page1);
		pointImage2 = (ImageView) findViewById(R.id.page2);
		pointImage3 = (ImageView) findViewById(R.id.page3);
		pointImage4 = (ImageView) findViewById(R.id.page4);
		pointImage5 = (ImageView) findViewById(R.id.page5);
		
		//实例化开始按钮
		startBt = (Button) view6.findViewById(R.id.startBtn);
		
		
//	   rem_pw111 = (CheckBox) view1.findViewById(R.id.cb_mima111);
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 设置监听
		 
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		// 设置适配器数据
		vpAdapter.notifyDataSetChanged();
		

		//将要分页显示的View装入数组中		
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		views.add(view5);
		views.add(view6);
		
		viewPager.setAdapter(vpAdapter);				
		// 给开始按钮设置监听
		startBt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				 startbutton();
			}
		});
		
//		rem_pw111.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
//				if (rem_pw111.isChecked()) {
//					SharedPreferences preferences = getSharedPreferences(
//							SHAREDPREFERENCES_NAME,MODE_PRIVATE);
//					 Editor editor =  preferences .edit();
//					 editor.putBoolean("isClip", false);
//					 editor.commit();
////					Intent intent = new Intent();
////					intent.setClass(GuideActivity.this,MainActivity.class);
////					intent.putExtra("userId", sp.getInt("USER_ID", -1));
////					intent.putExtra("screenName", sp.getString("USER_NAME", ""));
////					startActivity(intent);
//					
//					
//				}else {
//					
//					
//				}
//
//			}
//		});
     
		
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {
		
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			case 1:
				pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			case 2:
				pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			case 3:
				pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			case 4:
				pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				pointImage5.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			case 5:
				pointImage5.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
				pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
				break;
			}
			currIndex = position;
			// animation.setFillAfter(true);// True:图片停在动画结束位置
			// animation.setDuration(300);
			// mPageImg.startAnimation(animation);
		}

		
		public void onPageScrollStateChanged(int arg0) {

		}

		
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}
	
	/**
	 * 相应按钮点击事件
	 */
	private void startbutton() {  
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME,MODE_PRIVATE);
		 Editor editor =  preferences .edit();
		 editor.putBoolean("isClip", false);
		 editor.commit();
      	Intent intent = new Intent();
		intent.setClass(GuideActivity.this,MainActivity.class);
		intent.putExtra("userId", sp.getInt("USER_ID", -1));
		intent.putExtra("screenName", sp.getString("USER_NAME", ""));
		startActivity(intent);
		this.finish();
      }
//	private void clipbutton(){
//		SharedPreferences preferences = getSharedPreferences(
//				SHAREDPREFERENCES_NAME,MODE_PRIVATE);
//		Editor editor = preferences.edit();
//		editor.putBoolean("isClip", false);
//		editor.commit();
//		 
//		Intent intent = new Intent();
//		intent.setClass(GuideActivity.this,MainActivity.class);
//		intent.putExtra("userId", sp.getInt("USER_ID", -1));
//		intent.putExtra("screenName", sp.getString("USER_NAME", ""));
//		startActivity(intent);
//		this.finish();
//	}
	
}
