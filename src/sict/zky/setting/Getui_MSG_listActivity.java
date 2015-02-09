package sict.zky.setting;

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
import sict.zky.domain.Getui_store;
import sict.zky.domain.Pc_bgdata;
import sict.zky.input.UPLOAD_PC_BGDATAActivity;
import sict.zky.main.HealthKnowledgeActivity;
import sict.zky.main.Knowledge;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Getui_storeService;
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

public class Getui_MSG_listActivity extends Activity {

	private ListView getui_listview;

	private int userId;
	private String userName;
	private String content;
	private String time;
	private String title;
	private SimpleAdapter adapter = null;

	private Getui_storeService getui_storeService;
	private TextView pc_bgdata_listuserName;
	private ArrayList<HashMap<String, Object>> listItems;
	private HashMap<String, Object> longitem;


	// private SimpleAdapter adapter=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getui_listactivity);
		SysApplication.getInstance().addActivity(this);
		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
//		userName = intent.getStringExtra("userName");

		// Toast.makeText(getApplicationContext(), ""+userId, 1).show();

		getui_listview = (ListView) findViewById(R.id.getui_listview);
		getui_listview.setVerticalScrollBarEnabled(true);

		initListView();

		// 添加长按点击
		getui_listview
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
		// 添加点击事件
		getui_listview.setOnItemClickListener(new ItemClickListener());

	}

	// 点击事件
	private final class ItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
			time=item.get("time").toString();
			title=item.get("title").toString();
//			Toast.makeText(getApplicationContext(), time, 1).show();
			Intent intent =new Intent();
			intent.setClass(Getui_MSG_listActivity.this, ShowGetuiContent.class);			
			intent.putExtra("userId", userId);
			intent.putExtra("time", time);
			intent.putExtra("title", title);
			startActivity(intent);
		}

	}

	// 长按菜单响应函数
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");
		switch (item.getItemId()) {
		case 0:
			time = longitem.get("time").toString();
			getui_storeService.deletebytime(time);
			// pc_bgdataService.delete(userId,uploadTime,bloodGlucose);
			adapter.notifyDataSetChanged();
			initListView();

		}
		return super.onContextItemSelected(item);
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
		getui_storeService = new Getui_storeService(getApplicationContext());

		List<Getui_store> getui_stores = getui_storeService.selectData(userId);

		// Toast.makeText(getApplicationContext(), ""+getui_stores.size(),
		// 1).show();
		// List<HashMap<String, Object>> data = new ArrayList<HashMap<String,
		// Object>>();
		listItems = new ArrayList<HashMap<String, Object>>();
		for (Getui_store getui_store : getui_stores) {
			// pc_bgdata = pc_bgdatas.get(location)

			HashMap<String, Object> item = new HashMap<String, Object>();
			// item.put("content", getui_store.getContent());
			item.put("time", getui_store.getTime());
			item.put("title", getui_store.getTitle());
			int mark = getui_store.getMark();
			// System.out.println("eeeeeeeee"+mark);
			if (mark == 0) {
				item.put("getui_list_image", R.drawable.readpoint);
			}

			listItems.add(item);
		}

		adapter = new OverwriteAdapter(this, listItems,
				R.layout.getui_list_item, new String[] { "time","title",
						"getui_list_image" }, new int[] { R.id.getui_list_time,
						R.id.getui_list_title,R.id.getui_list_image });
		getui_listview.setAdapter(adapter);
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

	@Override
	protected void onResume() {
		initListView();
		super.onResume();
	}

}
