package sict.zky.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class Uploadpc_data {
	private Context context;
	private int userId;
	private String userName;
	private String familyRole;

	public Uploadpc_data(Context context) {
		super();
		this.context = context;
	}

	public void uploadpc_data(int userId) {

		Thread uploadpc_dataThread = new Thread(new Uploadpc_dataThread());
		uploadpc_dataThread.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Pc_dataService pc_dataService = new Pc_dataService(context);
				pc_dataService.updateupload();
				// mDialog.cancel();
//				Toast.makeText(context, "血压上传同步成功", Toast.LENGTH_SHORT).show();

				break;
			case 2:
				// mDialog.cancel();
				Toast.makeText(context, "血压上传同步失败", Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Uploadpc_dataThread implements Runnable {

		public void run() {

			String str = Upload_pcdataServer();
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

	private String Upload_pcdataServer() {
		String str = "";
		Pc_userService pc_userService = new Pc_userService(context);
		Pc_dataService pc_dataService = new Pc_dataService(context);
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		pc_datas = pc_dataService.getnotuploadpc_data(0);
		if (pc_datas.size() == 0) {
			str = "0";
		} else {
			try {

				URL url = new URL(Config.IPaddress
						+ "/ZKYweb/Upload_PC_DataServlet");
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.addRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.connect();
				PrintWriter pw = new PrintWriter(conn.getOutputStream());
				JSONArray array = new JSONArray();
				for (Pc_data pc_data : pc_datas) {
					userName = pc_data.getUserName();
					familyRole = pc_userService
							.getfamilyRolebyuserIdanduserName(userId, userName);

					JSONObject obj = new JSONObject();
					obj.put("userId", pc_data.getUserId());
					obj.put("systolicPressure", pc_data.getSystolicPressure());
					obj.put("diastolicPressure", pc_data.getDiastolicPressure());
					obj.put("pulse", pc_data.getPulse());
					obj.put("uploadTime", pc_data.getUploadTime());
					obj.put("userName", userName);
					obj.put("screenName", pc_data.getScreenName());
					obj.put("familyMember", pc_data.getFamilyMember());
					obj.put("oxygen", 0);
					obj.put("uploadType", 0);
					obj.put("familyRole", familyRole);

					array.put(obj);

				}
				pw.write(array.toString());
				pw.flush();
				pw.close();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				StringBuffer sb = new StringBuffer("");
				// String result = "";
				String temp;
				while ((temp = br.readLine()) != null) {
					sb.append(temp);
				}
				br.close();
				str = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
