package sict.zky.main;

import sict.zky.deskclock.GetuiSdkDemoActivity;
import sict.zky.deskclock.R;
import sict.zky.getui.GetuiMsgActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.testin.agent.TestinAgent;

public class TestSplashActivity extends Activity
{
	boolean isClip = false;
	// boolean isClip = true;
	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	private static final int GO_SHOW = 1002;
	private static final int GO_LOGIN = 1003;
	private static final int GO_SHOW_MSG =1004;
	private static final long SPLASH_DELAY_MILLIS = 500;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";
	private SharedPreferences sp;
	public static String clientId = "";
	private String appkey = "";
	private String appsecret = "";
	private String appid = "";
	private String SN = "";
	private String cid;
	private int userId;

	// private int check;
	private SharedPreferences rememberclientId;

	public static int type = -1; //消息类型  0gsm测量血压 1指导消息
	public static int high;
	public static int low;
	public static int pulse;
	public static String uploadTime;
	public static String time;
	
	public static int userID;
	public static String title; // 推送指导标题
	public static String msg; // 推送指导内容

	public TestSplashActivity()
	{
		super();
	}

	// private int userId;
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case GO_HOME:
				// System.out.println("上传++++goHome()");
				goHome();
				break;
			case GO_GUIDE:
				// System.out.println("上传++++goGuide()");
				goGuide();
				break;
			case GO_SHOW:

				goShow();
				break;

			case GO_SHOW_MSG:

				goShowMsg();
				break;
			case GO_LOGIN:

				goLogin();
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// 初始化推送
		PushManager.getInstance().initialize(this.getApplicationContext());

		// 从AndroidManifest.xml的meta-data中读取SDK配置信息
		String packageName = getApplicationContext().getPackageName();
		ApplicationInfo appInfo;
		try
		{
			appInfo = getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_META_DATA);
			if (appInfo.metaData != null)
			{

				appid = appInfo.metaData.getString("PUSH_APPID");
				appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
				appkey = (appInfo.metaData.get("PUSH_APPKEY") != null) ? appInfo.metaData
						.get("PUSH_APPKEY").toString() : null;
			}

		} catch (NameNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		View view = View
				.inflate(TestSplashActivity.this, R.layout.splash, null);
		setContentView(view);
		Animation animation = new AlphaAnimation(0.1f, 1.0f);
		animation.setDuration(1000);
		view.setAnimation(animation);

		sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
		rememberclientId = this.getSharedPreferences("rememberclientId",
				Context.MODE_WORLD_READABLE);

		cid = rememberclientId.getString("clientId", "");
		userId = sp.getInt("USER_ID", -1);
		// Toast.makeText;(getApplicationContext(), userId + " , " + auto_login,
		// 1)
		// .show()

		// Toast.makeText(getApplicationContext(), sp.getInt("USER_ID",
		// 0)+" , TestSplashActivity", 1).show();

		// System.out.println("上传+++"+high+" , "+low+" , "+pulse+" , ");
		animation.setAnimationListener(new AnimationListener()
		{

			public void onAnimationStart(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			public void onAnimationRepeat(Animation animation)
			{
				// TODO Auto-generated method stub

			}

			public void onAnimationEnd(Animation animation)
			{
				init();
			}
		});
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

	private void init()
	{
		SharedPreferences preferences = getSharedPreferences(
				SHAREDPREFERENCES_NAME, MODE_PRIVATE);
		// isClip = preferences.getBoolean("isClip", true);
		if (!isClip)
		{
			
			
			if(type ==-1)
			{
				mHandler.sendEmptyMessageDelayed(GO_HOME,
						SPLASH_DELAY_MILLIS);
			}
			else
			{
				if (userId > 0 )
				{
					if(type==0 )
					{
						mHandler.sendEmptyMessageDelayed(GO_SHOW,
								SPLASH_DELAY_MILLIS);
					}
					else if(type ==1)
					{
						mHandler.sendEmptyMessageDelayed(GO_SHOW_MSG,
								SPLASH_DELAY_MILLIS);						
					}
					
					
					
				} else
				{
					mHandler.sendEmptyMessageDelayed(GO_LOGIN,
							SPLASH_DELAY_MILLIS);
				}
				
			}


		} else
		{
			mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}

	}

	private void goHome()
	{
		Intent intent = new Intent(TestSplashActivity.this, MainActivity.class);
		intent.putExtra("userId", sp.getInt("USER_ID", -1));
		intent.putExtra("screenName", sp.getString("USER_NAME", ""));
		TestSplashActivity.this.startActivity(intent);
		TestSplashActivity.this.finish();

	}

	private void goGuide()
	{
		Intent intent = new Intent(TestSplashActivity.this, MainActivity.class);
		intent.putExtra("userId", sp.getInt("USER_ID", -1));
		intent.putExtra("screenName", sp.getString("USER_NAME", ""));
		TestSplashActivity.this.startActivity(intent);
		TestSplashActivity.this.finish();
	}

	private void goLogin()
	{
		Intent intent = new Intent(TestSplashActivity.this, LoginActivity.class);
		// intent.putExtra("userId", sp.getInt("USER_ID", -1));
		// intent.putExtra("screenName", sp.getString("USER_NAME", ""));
		// intent.putExtra("check", 1);
		TestSplashActivity.this.startActivity(intent);
		Toast.makeText(getApplicationContext(), "登录后可以查看消息和选择用户", 1).show();
		TestSplashActivity.this.finish();
	}

	private void goShow()
	{
		Intent intent1 = new Intent(TestSplashActivity.this,
				GetuiSdkDemoActivity.class);
		intent1.putExtra("userId", userID);
		intent1.putExtra("screenName", sp.getString("USER_NAME", ""));
		intent1.putExtra("high", high);
		intent1.putExtra("low", low);
		intent1.putExtra("pulse", pulse);
		intent1.putExtra("uploadTime", uploadTime);

		high = 0;
		low = 0;
		pulse = 0;
		uploadTime = "";
		TestSplashActivity.this.startActivity(intent1);
		finish();
	}

	private void goShowMsg()
	{
		Intent intent2 = new Intent(TestSplashActivity.this,
				GetuiMsgActivity.class);
		intent2.putExtra("userId", userID);
		intent2.putExtra("time", time);
		intent2.putExtra("title", title);
		intent2.putExtra("msg", msg);
		Toast.makeText(getApplicationContext(), title, 1).show();

		title = "";
		msg = "";
		uploadTime = "";
		TestSplashActivity.this.startActivity(intent2);
		finish();
	}

	public static int getHigh()
	{
		return high;
	}

	public static void setHigh(int high)
	{
		TestSplashActivity.high = high;
	}

	public static int getLow()
	{
		return low;
	}

	public static void setLow(int low)
	{
		TestSplashActivity.low = low;
	}

	public static int getPulse()
	{
		return pulse;
	}

	public static void setPulse(int pulse)
	{
		TestSplashActivity.pulse = pulse;
	}

	public static String getUploadTime()
	{
		return uploadTime;
	}

	public static void setUploadTime(String uploadTime)
	{
		TestSplashActivity.uploadTime = uploadTime;
	}

}