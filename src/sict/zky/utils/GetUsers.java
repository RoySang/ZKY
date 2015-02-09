package sict.zky.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_data;
import sict.zky.domain.Pc_user;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.utils.GetPc_data.Get_pcdataThread;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class GetUsers {
	private Context context;
	private int userId;
	private Pc_userService pc_userService;
	private Pc_user pc_user;

	public GetUsers(Context context) {
		super();
		this.context = context;
	}

	public void getUsers(int userId) {

		Thread getusers = new Thread(new GetUsersThread());
		getusers.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Pc_dataService pc_dataService = new Pc_dataService(context);
				pc_dataService.updateupload();
				// mDialog.cancel();
				Toast.makeText(context, "血压上传同步成功", Toast.LENGTH_SHORT).show();

				break;
			case 2:
				// mDialog.cancel();
				Toast.makeText(context, "血压上传同步失败", Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class GetUsersThread implements Runnable {

		public void run() {

			String str = GetUsersServer(userId);
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				Pc_dataService pc_dataService = new Pc_dataService(context);
				pc_dataService.updateupload();
				msg.what = 0;
				handler.sendMessage(msg);
			} else if (str.equals("0")) {

				msg.what = 1;
				handler.sendMessage(msg);
			} else {
				msg.what = 2;
				handler.sendMessage(msg);

			}

		}
	}

	private String GetUsersServer(int userId) {
		String str = "";

		try {

			URL url = new URL(Config.IPaddress + "/ZKYweb/GetUsersServlet");
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

			JSONArray array = new JSONArray(result);

			List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return str;

	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}
