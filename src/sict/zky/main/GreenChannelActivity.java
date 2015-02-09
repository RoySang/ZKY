package sict.zky.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.utils.Config;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class GreenChannelActivity extends Activity {
	private TextView agencyNameText;
	private TextView mobileText1,mobileText2,mobileText3;
	private Button phoneButton1,phoneButton2,phoneButton3;
	private int userId;
	private String agencyID;
	private String agencyName;
	private String phone1;
	private String phone2;
	private String phone3;
	private ProgressDialog mDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.greenchannelactivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent=getIntent();
		userId=intent.getIntExtra("userId", 0);
		agencyNameText=(TextView)findViewById(R.id.greenchannelagencyName);
		mobileText1=(TextView)findViewById(R.id.greenchannelmobile1);
		phoneButton1=(Button)this.findViewById(R.id.callthedoctor1);
		mobileText2=(TextView)findViewById(R.id.greenchannelmobile2);
		phoneButton2=(Button)this.findViewById(R.id.callthedoctor2);
		mobileText3=(TextView)findViewById(R.id.greenchannelmobile3);
		phoneButton3=(Button)this.findViewById(R.id.callthedoctor3);

		
		
		mDialog = new ProgressDialog(GreenChannelActivity.this);
		mDialog.setTitle("查询");
		mDialog.setMessage("正在查询，请等待...");
		mDialog.show();
		Thread greenChannelThread = new Thread(new GreenChannelThread());
		greenChannelThread.start();
		
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
	
	
	
	
	
	
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				agencyNameText.setText(agencyName);
				mobileText1.setText(phone1);
				mobileText2.setText(phone2);
				mobileText3.setText(phone3);

				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "查询失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class GreenChannelThread implements Runnable {


		public void run() {
			
			String str = greenChannelServer(String.valueOf(userId));
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else {

				msg.what = 1;
				handler.sendMessage(msg);
			}

		}

	}

	private String greenChannelServer(String userId) {
		String str="";
		try {
			// URL url = new URL("http://192.168.136.9:8080/ZKYweb/GreenChannelServlet");
			// URL url = new URL("http://localhost:8080/ZKYweb/GreenChannelServlet");
			URL url = new URL(Config.IPaddress+"/ZKYweb/GreenChannelServlet");
//			 URL url = new URL("http://192.168.0.100:8080/ZKYweb/GreenChannelServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj = new JSONObject();
			obj.put("userId", userId);

			pw.write(obj.toString());
			pw.flush();
			pw.close();

			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuffer sb = new StringBuffer("");
			String result = "";
			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			result = sb.toString();
			
			JSONObject jobj = new JSONObject(result);
			if(jobj!=null){
				str="success";
				agencyName=jobj.getString("agencyName");
				phone1=jobj.getString("phone1");
				phone2=jobj.getString("phone2");
				phone3=jobj.getString("phone3");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}


	
	//拨打电话
	public void callthedoctor1(View v){
		String number=mobileText1.getText().toString();
		Intent intent =new Intent();
		intent.setAction("android.intent.action.CALL");
		//intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}
	
	public void callthedoctor2(View v){
		String number=mobileText2.getText().toString();
		Intent intent =new Intent();
		intent.setAction("android.intent.action.CALL");
		//intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}
	public void callthedoctor3(View v){
		String number=mobileText3.getText().toString();
		Intent intent =new Intent();
		intent.setAction("android.intent.action.CALL");
		//intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("tel:"+number));
		startActivity(intent);
	}
	public void returntomain(View v){
		Intent intent =new Intent(GreenChannelActivity.this,MainActivity.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
		finish();
	}
	
}
