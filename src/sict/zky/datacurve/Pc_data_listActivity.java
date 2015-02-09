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

import sict.zky.datacurve.Pc_bgdata_listActivity.Delete_pc_bgdataThread;
import sict.zky.datacurve.Pc_bgdata_listActivity.OverwriteAdapter;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Pc_data_listActivity extends Activity {

	private ListView pc_data_listview;

	private int userId;
	private String userName;
	private SimpleAdapter adapter = null;
	private ProgressDialog mDialog;
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;
	private TextView pc_data_listuserName;
	private ArrayList<HashMap<String, Object>> listItems;
	private HashMap<String, Object> longitem;
	private String uploadTime;
	private String pressure;
	private int high;
	private int low;
	private int pulse;

	// Color c={Color.RED}
	// private SimpleAdapter adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pc_data_listactivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		userName = intent.getStringExtra("userName");

		pc_data_listview = (ListView) findViewById(R.id.pc_data_listview);
		pc_data_listview.setVerticalScrollBarEnabled(true);
		pc_data_listuserName = (TextView) findViewById(R.id.pc_data_listuserName);
		pc_data_listuserName.setText("当前用户: " + userName);

		initListView();

		// 添加长按点击
		pc_data_listview
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
		// setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
		switch (item.getItemId()) {
		case 0:

			uploadTime = longitem.get("time").toString();
			pulse = (Integer) longitem.get("pulse");
			pressure = longitem.get("pressure").toString();
			String[] Array = pressure.split("/");
			high = Integer.parseInt(Array[0]);
			low = Integer.parseInt(Array[1]);
			// Toast.makeText(getApplicationContext(), Array[0]+" , "+Array[1],
			// 1).show();
			// bloodGlucose=(Double) longitem.get("bloodGlucose");

			pc_dataService.delete(userId, high, low, pulse, uploadTime);
			adapter.notifyDataSetChanged();
			initListView();

			Thread delete_pc_dataThread = new Thread(new Delete_pc_dataThread());
			delete_pc_dataThread.start();
			// Toast.makeText(getApplicationContext(), time, 1).show();
		}
		return super.onContextItemSelected(item);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				mDialog.cancel();

				Toast.makeText(Pc_data_listActivity.this, "删除成功",
						Toast.LENGTH_SHORT).show();

				break;
			case 1:

				Toast.makeText(Pc_data_listActivity.this, "删除本地",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Delete_pc_dataThread implements Runnable {

		public void run() {
			String str = "";
			str = delete_pc_dataServer(userId, high, low, pulse, uploadTime);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str != null) {
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

	private String delete_pc_dataServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse, String uploadTime) {
		String str = "nu";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/ZKYweb/Upload_PC_BGDataServlet");
			// URL url = new
			// URL("http://localhost:8080/ZKYweb/Upload_PC_BGDataServlet");
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Delete_PC_DataServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/Upload_PC_BGDataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			// JSONArray array=new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("systolicPressure", systolicPressure);
			obj.put("diastolicPressure", diastolicPressure);
			obj.put("pulse", pulse);
			obj.put("uploadTime", uploadTime);

			// array.put(obj);
			pw.write(obj.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();
//			System.out.println("eeeeeeeeeeee"+str+"nul");
		} catch (Exception e) {
			e.printStackTrace();
//			System.out.println("eeeeeeeeeeee"+e.toString());
		}
		return str;
	}

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
		pc_dataService = new Pc_dataService(getApplicationContext());

		List<Pc_data> pc_datas = pc_dataService.selectData(userName);

		listItems = new ArrayList<HashMap<String, Object>>();
		for (Pc_data pc_data : pc_datas) {
			// pc_bgdata = pc_bgdatas.get(location)

			HashMap<String, Object> item = new HashMap<String, Object>();
			// item.put("userName", "");
			item.put("time", pc_data.getUploadTime());
			item.put(
					"pressure",
					pc_data.getSystolicPressure() + "/"
							+ pc_data.getDiastolicPressure());
			item.put("pulse", pc_data.getPulse());
			listItems.add(item);
		}
		// adapter = new SimpleAdapter(this, data, R.layout.pc_data_list_item,
		// new String[] { "time", "pressure" ,"pulse"}, new int[] {
		// R.id.pc_datalist_time,
		// R.id.pc_datalist_pressure,R.id.pc_datalist_pulse });

		adapter = new OverwriteAdapter(this, listItems,
				R.layout.pc_data_list_item, new String[] { "time", "pressure",
						"pulse" }, new int[] { R.id.pc_datalist_time,
						R.id.pc_datalist_pressure, R.id.pc_datalist_pulse });

		// adapter.getView(position, convertView, parent)
		pc_data_listview.setAdapter(adapter);
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
