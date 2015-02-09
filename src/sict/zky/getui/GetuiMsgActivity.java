package sict.zky.getui;

import sict.zky.deskclock.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class GetuiMsgActivity extends Activity
{
	
	private int userId;
	private  String title;
	private  String msg;
	private String  time;
	private TextView get_userId, get_title, get_msg, get_receivetime;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getui_msg);
		
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		title = intent.getStringExtra("title");
		msg = intent.getStringExtra("msg");
		time= intent.getStringExtra("time");
		
		
		
		
		
		get_title = (TextView) findViewById(R.id.getui_msg_title);
		get_msg = (TextView) findViewById(R.id.getui_msg);
		get_receivetime = (TextView) findViewById(R.id.getui_msg_receive_time);
		
		get_title.setText(title);
		get_msg.setText(msg);
		get_receivetime.setText(time);
		
	}

}
