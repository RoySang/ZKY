package sict.zky.setting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.testin.agent.TestinAgent;
import sict.zky.datacurve.Pc_bgdata_listActivity;
import sict.zky.datacurve.Pc_data_listActivity;
import sict.zky.datacurve.dataListActivity;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_user;
import sict.zky.input.inputActivity;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import sict.zky.main.SysApplication;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.testBPM.StartTestBPMActivity;
import sict.zky.utils.Config;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.GetPc_dataActivity;
import sict.zky.utils.GetPc_data;
import sict.zky.utils.Uploadpc_bgdata;
import sict.zky.utils.Uploadpc_data;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class UserInfoActivity extends ListActivity {

	private ListView user_listView;
	private Pc_userService pc_userService;
	private int p;
	private int userId;
	private ArrayList<HashMap<String, Object>> listItems;
	private SimpleAdapter listItemAdapter;
	private HashMap<String, Object> longitem;
	public int MID;
	private long firstTime = 0;
	private Pc_dataService pc_dataService;
	private Pc_bgdataService pc_bgdataService;
	private GetPc_data getpc_data;
	// private GetPc_bgdata getpc_bgdata;
	private GetPc_dataActivity getpc_dataActivity;
	private String screenName;
	private String userName;

	private ProgressDialog mDialog;
	private Uploadpc_data uploadpc_data;
	private Uploadpc_bgdata uploadpc_bgdata;
	private CurrentTime ct = new CurrentTime();
	private String currentTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		SysApplication.getInstance().addActivity(this);

		setContentView(R.layout.userinfoactivity1);
		// getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
		// R.layout.userinfo_title);
		// getListView().setBackgroundColor(Color.WHITE);

		Intent intent = getIntent();
		userId = intent.getIntExtra("userId", 0);
		screenName = intent.getStringExtra("screenName");
		// Toast.makeText(getApplicationContext(), userId+", userInfor",
		// 1).show();
		// if(userId<0){
		// Toast.makeText(getApplicationContext(), "���ȵ�¼", 1).show();
		// Intent intent0=new Intent(UserInfoActivity.this,LoginActivity.class);
		// startActivity(intent0);
		// finish();
		// }else{
		pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());
		getpc_data = new GetPc_data(getApplicationContext());
		uploadpc_data = new Uploadpc_data(getApplicationContext());
		uploadpc_bgdata = new Uploadpc_bgdata(getApplicationContext());

		// getpc_dataActivity=new GetPc_dataActivity(getApplicationContext());

		// user_listView = (ListView) this.findViewById(R.id.user_listview);
		// show();
		// user_listView.setOnItemClickListener(new ItemClickListener());
		// listItemAdapter.notifyDataSetChanged();
		// listItemAdapter.notifyDataSetChanged();

		initListView();
		this.setListAdapter(listItemAdapter);
		ItemOnLongClick1();
		// }

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

	private void ItemOnLongClick1() {
		// ע��setOnCreateContextMenuListener��������onContextItemSelected����ʹ�õ�
		UserInfoActivity.this.getListView().setOnCreateContextMenuListener(
				new OnCreateContextMenuListener() {

					public void onCreateContextMenu(ContextMenu menu, View v,
							ContextMenuInfo menuInfo) {
						final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
						int mListPos = info.position;
						longitem = listItems.get(mListPos);
						
						menu.setHeaderTitle("����");
						menu.add(0, 0, 0, "ɾ��");
						// menu.add(0, 1, 0, "�ղ�");
						// menu.add(0, 2, 0, "�Ա�");
					}
				});
	}

	// �����˵���Ӧ����
	public boolean onContextItemSelected(MenuItem item) {

		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		MID = (int) info.id;// �����info.id��Ӧ�ľ������ݿ���_id��ֵ

		switch (item.getItemId()) {
		case 0:
			// ɾ������
//			Toast.makeText(UserInfoActivity.this, "ɾ��", Toast.LENGTH_SHORT)
//					.show();
			// UserInfoActivity.this.getListView().getAdapter().g
			userName = longitem.get("userName").toString();

			pc_userService.deletebyuserName(userName);
			listItemAdapter.notifyDataSetChanged();
			initListView();
			UserInfoActivity.this.setListAdapter(listItemAdapter);
			Thread delete_pc_userThread = new Thread(new Delete_pc_userThread());
			delete_pc_userThread.start();
			break;

		// case 1:
		// // ɾ������
		// break;
		//
		// case 2:
		// // ɾ��ALL����
		// break;

		default:
			break;
		}

		return super.onContextItemSelected(item);

	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				mDialog.cancel();

				Toast.makeText(UserInfoActivity.this, "ɾ���ɹ�",
						Toast.LENGTH_SHORT).show();

				break;
			case 1:

				Toast.makeText(UserInfoActivity.this, "ɾ������",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Delete_pc_userThread implements Runnable {

		public void run() {
			String str = "";
			str = delete_pc_dataServer(userId, userName);
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

	private String delete_pc_dataServer(int userId, String userName) {
		String str = "";
		try {
			// URL url = new
			// URL("http://192.168.136.9:8080/ZKYweb/Upload_PC_BGDataServlet");
			// URL url = new
			// URL("http://localhost:8080/ZKYweb/Upload_PC_BGDataServlet");
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Delete_PC_UserServlet");
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
			obj.put("userName", userName);

			// array.put(obj);
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
	
	

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		HashMap<String, Object> item = listItems.get(position);
		// Toast.makeText(UserInfoActivity.this,
		// listItems.get(position).toString(), 1).show();
		if (item.get("userName") != null && !item.get("userName").equals("")) {
			Intent intent = new Intent(UserInfoActivity.this,
					dataListActivity.class);
			// Toast.makeText(getApplicationContext(),
			// item.get("userName").toString(), 1).show();
			intent.putExtra("userName", item.get("userName").toString());
			intent.putExtra("userId", userId);
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(), "���¼", 1).show();
			Intent intent0 = new Intent(UserInfoActivity.this,
					LoginActivity.class);
			startActivity(intent0);
			// finish();
		}

	}

	private void initListView() {// ��������������
		List<Pc_user> pc_users = pc_userService.selectAll(userId);
		listItems = new ArrayList<HashMap<String, Object>>();
		for (Pc_user pc_user : pc_users) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", R.drawable.head); // ͼƬ
			map.put("userName", pc_user.getUserName());// userName
			map.put("familyRole", pc_user.getFamilyRole());
			map.put("rightimage", R.drawable.more2);
			listItems.add(map);
		}
		listItemAdapter = new SimpleAdapter(this, listItems,
				R.layout.userinfo_item, new String[] { "ItemImage", "userName",
						"familyRole", "rightimage" }, new int[] {
						R.id.ItemImage, R.id.user_name, R.id.familyRole,
						R.id.rightimage });
	}

	public void adduser(View v) {
		if (userId < 0) {
			Toast.makeText(getApplicationContext(), "���¼", 1).show();
			Intent intent0 = new Intent(UserInfoActivity.this,
					LoginActivity.class);
			startActivity(intent0);
		} else {
			Intent intent = new Intent();
			intent.setClass(UserInfoActivity.this, AdduserActivity.class);
			intent.putExtra("userId", userId);
			startActivity(intent);
		}

	}

	// public void refresh(View v) throws InterruptedException {
	//
	// ct.getCurrentTime();
	// currentTime = ct.TimeToString();
	// if(userId>0){
	// Toast.makeText(getApplicationContext(), "����ˢ��...", 1).show();
	// if (pc_dataService.ishavepc_databyuserId(userId) ) {
	// getpc_data.setUserId(userId);
	// getpc_data.getpc_data(userId);
	// listItemAdapter.notifyDataSetChanged();
	// initListView();
	// UserInfoActivity.this.setListAdapter(listItemAdapter);
	// }
	//
	// �����¼С��10������
	// if (pc_bgdataService.ishavepc_bgdatabyuserId(userId) ) {
	// getpc_bgdata.setUserId(userId);
	// getpc_bgdata.getpc_bgdata(userId);
	// listItemAdapter.notifyDataSetChanged();
	// initListView();
	// UserInfoActivity.this.setListAdapter(listItemAdapter);
	// }
	//
	//
	// uploadpc_data.setUserId(userId);
	// uploadpc_data.uploadpc_data(userId);
	//
	// uploadpc_bgdata.setUserId(userId);
	// uploadpc_bgdata.uploadpc_bgdata(userId);
	//
	//
	// new Handler().postDelayed(new Runnable(){
	// public void run() {
	// ������ִ����ʱ����
	// listItemAdapter.notifyDataSetChanged();
	// initListView();
	// UserInfoActivity.this.setListAdapter(listItemAdapter);
	// }
	// }, 8000);
	// }else{
	// Toast.makeText(getApplicationContext(), "���¼", 1).show();
	// Intent intent0 = new Intent(UserInfoActivity.this,
	// LoginActivity.class);
	// startActivity(intent0);
	// }
	//
	// }

	// @Override
	// protected void onRestart() {
	// listItemAdapter.notifyDataSetChanged();
	// initListView();
	// UserInfoActivity.this.setListAdapter(listItemAdapter);
	// super.onRestart();
	// }

	// @Override
	// protected void onStart() {
	// listItemAdapter.notifyDataSetChanged();
	// initListView();
	// UserInfoActivity.this.setListAdapter(listItemAdapter);
	// super.onStart();
	// }

	@Override
	protected void onResume() {
		listItemAdapter.notifyDataSetChanged();
		initListView();
		UserInfoActivity.this.setListAdapter(listItemAdapter);
		super.onResume();
	}

	// ˫���˳�
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "�ٰ�һ�η��ؼ��˳��п��ƽ���", Toast.LENGTH_SHORT)
						.show();
				firstTime = secondTime;
				return true;
			} else {
				SysApplication.getInstance().exit();
			}
			break;
		}
		return super.onKeyUp(keyCode, event);
	}
}
