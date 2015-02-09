package sict.zky.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.utils.Config;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class HealthKnowledgeActivity extends Activity {

	private ListView knowledge_listview;

	private int userId;
	private int id;
	private String title;
	private String text;
	private SimpleAdapter adapter=null;
	private ProgressDialog mDialog;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.healthknowledgeactivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		knowledge_listview=(ListView)findViewById(R.id.knowledge_listview);
		knowledge_listview.setVerticalScrollBarEnabled(true);
		knowledge_listview.setOnItemClickListener(new ItemClickListener());

		mDialog = new ProgressDialog(HealthKnowledgeActivity.this);
		mDialog.setTitle("加载");
		mDialog.setMessage("正在加载...");
		mDialog.show();
		Thread showThread = new Thread(new ShowThread());
		showThread.start();
		
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

	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				knowledge_listview.setAdapter(adapter);
				
				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "查询失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class ShowThread implements Runnable {


		public void run() {
			
			String str = ShowThreadServer(String.valueOf(userId));
			Message msg = handler1.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				handler1.sendMessage(msg);
			} else {

				msg.what = 1;
				handler1.sendMessage(msg);
			}

		}

	}

	private String ShowThreadServer(String userId) {
		String str="";
		try {
			URL url=new URL(Config.IPaddress+"/ZKYweb/HealthKnowledgeServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			JSONObject obj1 = new JSONObject();
			obj1.put("userId", userId);
			pw.write(obj1.toString());
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
			
//			 Toast.makeText(getApplicationContext(), result, 1).show();
			JSONArray array = new JSONArray(result);
			

			// System.out.println(result);
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);

				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("id", i+1);
				item.put("title", obj.getString("title"));
				item.put("rightimage", R.drawable.more2);
				data.add(item);

			}
			adapter = new SimpleAdapter(this, data,
					R.layout.knowledge_list, new String[] { "id","title",
							"rightimage" }, new int[] {R.id.knowledgeId, R.id.knowledgetitle,
							R.id.knowledgerightimage });
			
			str="success";
			
		} catch (Exception e) {

			e.printStackTrace();
//			
		}
		
		return str;
		
	}
	
	

	private final class ItemClickListener implements OnItemClickListener {

		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long iddd) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);

			id = Integer.parseInt(item.get("id").toString());
			title=item.get("title").toString();
			
			Intent intent =new Intent();
			intent.setClass(HealthKnowledgeActivity.this, Knowledge.class);
			intent.putExtra("id", id);
			intent.putExtra("title", title);
			startActivity(intent);
			
			
		
		}
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 0:

					break;
				case 1:
					Toast.makeText(getApplicationContext(), "失败",
							Toast.LENGTH_SHORT).show();
					break;

				}

			}
		};

		class OnItemClick implements Runnable {

	
			public void run() {
				
				String str = onItemClickServer();
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
		private String onItemClickServer() {
			String str="";
			try {
				// URL url = new
				// URL("http://192.168.136.9:8080/ZKYweb/SearchKnowledgeServlet");
				// URL url = new
				// URL("http://localhost:8080/ZKYweb/SearchKnowledgeServlet");
				 URL url = new
				 URL(Config.IPaddress+"/ZKYweb/SearchKnowledgeServlet");
				
//				URL url = new URL(
//						"http://192.168.0.100:8080/ZKYweb/SearchKnowledgeServlet");
				
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.addRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.connect();
				PrintWriter pw = new PrintWriter(conn.getOutputStream());

				JSONObject obj = new JSONObject();
				obj.put("id", id);

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
				str="success";
			} catch (Exception e) {
				e.printStackTrace();
			}

			return str;
		}
	}

	
	

	

	public void returntomain(View v) {
		Intent intent = new Intent(HealthKnowledgeActivity.this,
				MainActivity.class);
		intent.putExtra("userId", userId);
		startActivity(intent);
		finish();
	}

}
