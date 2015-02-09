package sict.zky.setting;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.main.SysApplication;
import sict.zky.main.TestSplashActivity;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;



public class AboutusActivity extends Activity
{
	private TextView version;
	private TextView clientId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutus_activity);
		SysApplication.getInstance().addActivity(this);
		version=(TextView)findViewById(R.id.version);
		clientId=(TextView)findViewById(R.id.clientId);
		PackageManager manager;

		PackageInfo info = null;

		manager = this.getPackageManager();
		
		try {

			info = manager.getPackageInfo(this.getPackageName(), 0);

			} catch (NameNotFoundException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

			}
		String str="°æ±¾ºÅ£º "+String.valueOf(info.versionName)+"/"+String.valueOf(info.versionCode);

		version.setText(str);
		clientId.setText("clientId:"+TestSplashActivity.clientId);
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
	


}
