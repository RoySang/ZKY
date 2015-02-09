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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Knowledge extends Activity {

	private int id;
	private String title;
	private String text;
	private TextView knowledge_title;
	private TextView knowledge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge);
		SysApplication.getInstance().addActivity(this);
		knowledge_title = (TextView) findViewById(R.id.knowledge_title);
		knowledge = (TextView) findViewById(R.id.knowledge);
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);
		title = intent.getStringExtra("title");
		knowledge_title.setText(title);

		Thread searchThread = new Thread(new SearchThread());
		searchThread.start();
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

				knowledge.setText("    " + text);
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "Ê§°Ü",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class SearchThread implements Runnable {


		public void run() {

			String str = SearchThread();
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

	private String SearchThread() {
		String str = "";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/ZKYweb/SearchKnowledgeServlet");
			// URL url = new
			// URL("http://localhost:8080/ZKYweb/SearchKnowledgeServlet");
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/SearchKnowledgeServlet");

			// URL url = new URL(
			// "http://192.168.0.100:8080/ZKYweb/SearchKnowledgeServlet");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj = new JSONObject();
			obj.put("title", title);

			pw.write(obj.toString());
			pw.flush();
			pw.close();

			StringBuffer sb = new StringBuffer("");
			String result = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));

			String temp;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
			}
			br.close();
			result = sb.toString();
			JSONObject obj1 = new JSONObject(result);

			text = obj1.getString("text");
			str = "success";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;
	}
}
