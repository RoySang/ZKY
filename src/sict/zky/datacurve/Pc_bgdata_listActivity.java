package sict.zky.datacurve;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import sict.zky.domain.Pc_bgdata;
import sict.zky.input.UPLOAD_PC_BGDATAActivity;
import sict.zky.main.Knowledge;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.utils.Config;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Pc_bgdata_listActivity extends Activity {

	private ListView pc_bgdata_listview;

	private int userId;
	private String userName;
	private int id;
	private String title;
	private String text;
	private SimpleAdapter adapter = null;
	private ProgressDialog mDialog;
	private Pc_bgdataService pc_bgdataService;
	private TextView pc_bgdata_listuserName;
	private ArrayList<HashMap<String, Object>> listItems;
	private HashMap<String, Object> longitem;
	
	private String uploadTime;
	private double bloodGlucose;

	// private SimpleAdapter adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_bgdata_listactivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		userName = intent.getStringExtra("userName");

		pc_bgdata_listview = (ListView) findViewById(R.id.pc_bgdata_listview);
		pc_bgdata_listview.setVerticalScrollBarEnabled(true);
		pc_bgdata_listuserName = (TextView) findViewById(R.id.pc_bgdata_listuserName);
		pc_bgdata_listuserName.setText("当前用户: " + userName);

		initListView();

		// 添加长按点击
		pc_bgdata_listview
				.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
						int mListPos = info.position;
						longitem = listItems.get(mListPos);
						
						menu.setHeaderTitle("操作");
						menu.add(0, 0, 0, "删除");

					}
				});

	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
//		 setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
		switch (item.getItemId()) {
		case 0:

			uploadTime = longitem.get("time").toString();
			bloodGlucose=(Double) longitem.get("bloodGlucose");
			pc_bgdataService.delete(userId,uploadTime,bloodGlucose);
			adapter.notifyDataSetChanged();
			initListView();
			
			Thread delete_pc_bgdataThread = new Thread(new Delete_pc_bgdataThread());
			delete_pc_bgdataThread.start();
//			pc_bgdata_listview.setAdapter(adapter);
//			Toast.makeText(getApplicationContext(), time, 1).show();
		}
		return super.onContextItemSelected(item);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				mDialog.cancel();

				Toast.makeText(Pc_bgdata_listActivity.this, "删除成功", Toast.LENGTH_SHORT)
						.show();


				break;
			case 1:

				Toast.makeText(Pc_bgdata_listActivity.this, "删除本地",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Delete_pc_bgdataThread implements Runnable {

	
		public void run() {
			String str="";
			str = delete_pc_bgdataServer(userId,String.valueOf(bloodGlucose),uploadTime);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if(str!=null){
				if (str.equals("success")) {
					msg.what = 0;
					handler.sendMessage(msg);
				} else {

					msg.what = 1;
					handler.sendMessage(msg);
				}
			}
			

		}

	}

	private String delete_pc_bgdataServer(int userId,String bloodGlucose,String uploadTime) {
		String str="";
		try {
			// URL url = new URL("http://192.168.136.9:8080/ZKYweb/Upload_PC_BGDataServlet");
			// URL url = new URL("http://localhost:8080/ZKYweb/Upload_PC_BGDataServlet");
			URL url = new URL(Config.IPaddress+"/ZKYweb/Delete_PC_BGDataServlet");
//			 URL url = new URL("http://192.168.0.100:8080/ZKYweb/Upload_PC_BGDataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			
//			JSONArray array=new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("uploadTime", uploadTime);
			obj.put("bloodGlucose", bloodGlucose);
			
//			array.put(obj);
			pw.write(obj.toString());
			pw.flush();
			pw.close();

			
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}
	
	
	// 重写SimpleAdapter,使其颜色交替显示
	class OverwriteAdapter extends SimpleAdapter {
		// 颜色
		private int[] colors = { Color.rgb(255, 255, 255),
				Color.rgb(209, 230, 245) };

		public OverwriteAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			view.setBackgroundColor(colors[position % 2]);
			return view;
		}
	}

	private void initListView() {// 设置适配器内容
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());

		List<Pc_bgdata> pc_bgdatas = pc_bgdataService.selectData(userName);

//		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		listItems=new ArrayList<HashMap<String, Object>>();
		for (Pc_bgdata pc_bgdata : pc_bgdatas) {
			// pc_bgdata = pc_bgdatas.get(location)

			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put("time", pc_bgdata.getUploadTime());
			item.put("bloodGlucose", pc_bgdata.getBloodGlucose());
			item.put("pc_bgdatalist_image", R.drawable.bank);
			listItems.add(item);
		}
		// adapter = new SimpleAdapter(this, data, R.layout.pc_bgdata_list_item,
		// new String[] { "time", "bloodGlucose", "pc_bgdatalist_image" },
		// new int[] { R.id.pc_bgdatalist_time,
		// R.id.pc_bgdatalist_bloodGlucose,
		// R.id.pc_bgdatalist_image });
		adapter = new OverwriteAdapter(this, listItems,
				R.layout.pc_bgdata_list_item, new String[] { "time",
						"bloodGlucose", "pc_bgdatalist_image" }, new int[] {
						R.id.pc_bgdatalist_time,
						R.id.pc_bgdatalist_bloodGlucose,
						R.id.pc_bgdatalist_image });
		pc_bgdata_listview.setAdapter(adapter);
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
