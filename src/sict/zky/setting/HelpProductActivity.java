package sict.zky.setting;

import java.util.ArrayList;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.main.SysApplication;
import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpProductActivity extends Activity {

	private ViewPager viewPager;
	private ArrayList<View> pageViews;
	private ImageView imageView;
	private ImageView[] imageViews;
	// 包裹滑动图片LinearLayout
	private ViewGroup main;
	// 包裹小圆点的LinearLayout
	private ViewGroup group;
	@SuppressWarnings("unused")
	private TextView content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		// 设置无标题窗口
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int[] img = new int[] { R.drawable.help_p1, R.drawable.help_p2, R.drawable.help_p3,
				R.drawable.help_p4, R.drawable.help_p5,R.drawable.help_p6 };
		LayoutInflater inflater = getLayoutInflater();
		pageViews = new ArrayList<View>();
		for (int i = 0; i < img.length; i++) {
			LinearLayout layout = new LinearLayout(this);
			LayoutParams ltp = new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT);
			final ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.CENTER_INSIDE);
			imageView.setImageResource(img[i]);
			imageView.setPadding(15, 30, 15, 30);
			layout.addView(imageView, ltp); 
			pageViews.add(layout);
		}
		imageViews = new ImageView[pageViews.size()];
		main = (ViewGroup) inflater.inflate(R.layout.helpdocument, null);
		group = (ViewGroup) main.findViewById(R.id.viewGroup);
		viewPager = (ViewPager) main.findViewById(R.id.guidePages);
		//content = (TextView) findViewById(R.id.photo_content);
		
		/**
		 * 有几张图片 下面就显示几个小圆点
		 */

		for (int i = 0; i < pageViews.size(); i++) {
			LinearLayout.LayoutParams margin = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			//设置每个小圆点距离左边的间距
			margin.setMargins(10, 0, 0, 0);
			imageView = new ImageView(HelpProductActivity.this);
			//设置每个小圆点的宽高
			imageView.setLayoutParams(new LayoutParams(15, 15));
			imageViews[i] = imageView;
			if (i == 0) {
				// 默认选中第一张图片
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				//其他图片都设置未选中状态
				imageViews[i]
						.setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
			group.addView(imageViews[i], margin);
		}
		setContentView(main);
		//给viewpager设置适配器
		viewPager.setAdapter(new GuidePageAdapter());
		//给viewpager设置监听事件
		viewPager.setOnPageChangeListener(new GuidePageChangeListener());
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
	// 指引页面数据适配器
	class GuidePageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(pageViews.get(arg1));
			return pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}
	}

	// 指引页面更改事件监听器
	class GuidePageChangeListener implements OnPageChangeListener {

	
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}


		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		
		public void onPageSelected(int arg0) {
			//遍历数组让当前选中图片下的小圆点设置颜色
			for (int i = 0; i < imageViews.length; i++) {
				imageViews[arg0]
						.setBackgroundResource(R.drawable.page_indicator_focused);

				if (arg0 != i) {
					imageViews[i]
							.setBackgroundResource(R.drawable.page_indicator_unfocused);
				}
			}
		}
	}
}
