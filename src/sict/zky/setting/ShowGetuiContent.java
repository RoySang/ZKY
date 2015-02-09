package sict.zky.setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.main.SysApplication;
import sict.zky.service.Getui_storeService;
import sict.zky.utils.Config;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowGetuiContent extends Activity {

	private int userId;
	private String time;
	private String content;
	private String title;
	private TextView getui_title;
	private TextView getui_time;
	private TextView getui_content;
	private Getui_storeService getui_storeService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getuicontent);
		SysApplication.getInstance().addActivity(this);
		getui_storeService = new Getui_storeService(getApplicationContext());
		getui_title = (TextView) findViewById(R.id.getui_title);
		getui_time=(TextView)findViewById(R.id.getui_time);
		getui_content = (TextView) findViewById(R.id.getui_content);
		Intent intent = getIntent();
		userId=intent.getIntExtra("userId", 0);
		time = intent.getStringExtra("time");
		title=intent.getStringExtra("title");
//		Toast.makeText(getApplicationContext(), userId+" , "+time, 1).show();
//		context = intent.getStringExtra("context");
		content=getui_storeService.getcontentbytimeandtitle(userId,time,title);
		getui_title.setText(title);
		getui_time.setText(time);
		getui_content.setText(content);
		getui_storeService.updatemarkbyuserIdandtimeandtitle(userId, time,title);
		
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

}
