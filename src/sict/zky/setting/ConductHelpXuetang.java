package sict.zky.setting;

import java.util.ArrayList;

import sict.zky.deskclock.R;
import sict.zky.main.SysApplication;
import sict.zky.main.ViewPagerAdapter;
import com.testin.agent.TestinAgent;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ConductHelpXuetang extends Activity {
	// 定义ViewPager对象
			private ViewPager viewPager;

			// 定义ViewPager适配器
			private ViewPagerAdapter vpAdapter;

			// 定义一个ArrayList来存放View
			private ArrayList<View> views;

			//定义各个界面View对象
			private View view1,view2,view3,view4;
			
			// 定义底部小点图片
			private ImageView pointImage0, pointImage1,pointImage2,pointImage3;
			//定义开始按钮对象
			//private Button startBt;
			
			// 当前的位置索引值
			private int currIndex = 0;
			//private CheckBox rem_pw111;
			//private static final String SHAREDPREFERENCES_NAME="first_pref";
			//private SharedPreferences sp;
		private TextView SN;
		private SharedPreferences sp;	
		private String SN_;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_shebeixinxi);
			SysApplication.getInstance().addActivity(this);
//			SN=(TextView)findViewById(R.id.SN);
//			sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
//			SN_=sp.getString("SN", "null");
//			SN.setText(SN_);
			
			initView();

			initData();
		}
		
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
					
					viewPager.setAdapter(vpAdapter);	
			
		}

		private void initView() {
			LayoutInflater mLi = LayoutInflater.from(this);
			view1 = mLi.inflate(R.layout.xuetanghelpdpcument1, null);
			view2 = mLi.inflate(R.layout.xuetanghelpdocument2, null);
			view3 = mLi.inflate(R.layout.xuetanghelpdocument3, null);
			view4 = mLi.inflate(R.layout.xuetanghelpdocument4, null);
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
					pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
					pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
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
		
		
		
	

}
