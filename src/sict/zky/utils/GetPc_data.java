package sict.zky.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_data;
import sict.zky.domain.Pc_user;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class GetPc_data {
	private Context context;
	private int userId;
	private boolean downloaded;
	
	
	
	private ProgressBar progressBar;
//	private ProgressDialog mDialog;
	
	

	public GetPc_data(Context context) {
		super();
		this.context = context;
//		mDialog = new ProgressDialog(context);
	}
	

	public GetPc_data(Context context, boolean downloaded) {
		super();
		this.context = context;
		this.downloaded = downloaded;
	}


	public boolean getpc_data(int userId) {
		
		Thread get_pcdataThread = new Thread(new Get_pcdataThread());
		
		get_pcdataThread.start();		
		
		return downloaded;
		
		
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
//				mDialog.cancel();
//				progressBar.findViewById(R.id.round_progressbar);
//				progressBar.setVisibility(View.INVISIBLE);
				downloaded=true;
				setDownloaded(true);
				Toast.makeText(context, "ÏÂÔØÑªÑ¹Í¬²½³É¹¦", Toast.LENGTH_SHORT).show();
				
				
				
				break;
			case 1:
//				mDialog.cancel();
				downloaded=true;
				setDownloaded(true);
				Toast.makeText(context, "ÏÂÔØÑªÑ¹Í¬²½Ê§°Ü", Toast.LENGTH_SHORT).show();
				
				break;

			}

		}
	};

	class Get_pcdataThread implements Runnable {

		
		public void run() {

			String str = Get_pcdataServer(userId);
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

	private String Get_pcdataServer(int userId) {
		String str = "";
		try {
			Pc_userService pc_userService = new Pc_userService(context);
			Pc_dataService pc_dataService = new Pc_dataService(context);
			Pc_data pc_data;
			URL url = new URL(Config.IPaddress + "/ZKYweb/GetPc_dataServlet");
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

			// Toast.makeText(getApplicationContext(), result, 1).show();
			JSONArray array = new JSONArray(result);

//			System.out.println(array.toString());
			// List<HashMap<String, Object>> data = new
			// ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj1 = array.getJSONObject(i);
				// System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
				// + obj1.toString());
				// HashMap<String, Object> item = new HashMap<String, Object>();
				// item.put("systolicPressure",
				// obj1.getInt("systolicPressure"));
				// item.put("diastolicPressure",
				// obj1.getInt("diastolicPressure"));
				// item.put("pulse", obj1.getInt("pulse"));
				// item.put("uploadTime", obj1.getString("uploadTime"));
				// item.put("screenName", obj1.getString("screenName"));
				// item.put("familyMember", obj1.getInt("familyMember"));
				int systolicPressure = obj1.getInt("systolicPressure");
				int diastolicPressure = obj1.getInt("diastolicPressure");
				int pulse = obj1.getInt("pulse");
				String screenName = obj1.getString("screenName");
				String uploadTime = obj1.getString("uploadTime");
				int familyMember_ = obj1.getInt("familyMember");

				switch (familyMember_) {
				case 0:

					// + familyMember_ + "");
					// pc_data=new Pc_data(userId,
					// obj1.getInt("systolicPressure"),
					// obj1.getInt("diastolicPressure"), obj1.getInt("pulse"),
					// obj1.getString("uploadTime"),
					// obj1.getString("screenName"),
					// obj1.getString("screenName"), familyMember_);
					// pc_data = new Pc_data(userId,
					// (Integer) item.get("systolicPressure"),
					// (Integer) item.get("diastolicPressure"),
					// (Integer) item.get("pulse"),
					// (String) item.get("uploadTime"),
					// (String) item.get("screenName"),
					// (String) item.get("screenName"), familyMember_);

					pc_data = new Pc_data(userId, systolicPressure,
							diastolicPressure, pulse, uploadTime, screenName,
							screenName, familyMember_);
					// Pc_dataService pc_dataService0=new
					// Pc_dataService(GetPc_dataActivity.this);
					pc_dataService.insert(pc_data);
					// System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
					// + systolicPressure + "");
					break;
				case 1:
					if (!pc_userService.ishaveFamilyMember(1)) {
						pc_userService
								.insert(new Pc_user(userId, "¸¸Ç×", 1, "¸¸Ç×",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"¸¸Ç×", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 2:
					if (!pc_userService.ishaveFamilyMember(2)) {
						pc_userService
								.insert(new Pc_user(userId, "Ä¸Ç×", 2, "Ä¸Ç×",1));
					}
					// Pc_dataService pc_dataService1=new
					// Pc_dataService(GetPc_dataActivity.this);
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"Ä¸Ç×", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					break;
				case 3:
					if (!pc_userService.ishaveFamilyMember(3)) {
						pc_userService
								.insert(new Pc_user(userId, "ÅäÅ¼", 3, "ÅäÅ¼",1));
					}
					// Pc_dataService pc_dataService2=new
					// Pc_dataService(GetPc_dataActivity.this);
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"ÅäÅ¼", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 4:
					if (!pc_userService.ishaveFamilyMember(4)) {
						pc_userService
								.insert(new Pc_user(userId, "×ÓÅ®", 4, "×ÓÅ®",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"×ÓÅ®", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 5:
					if (!pc_userService.ishaveFamilyMember(5)) {
						pc_userService
								.insert(new Pc_user(userId, "×æ¸¸", 5, "×æ¸¸",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"×æ¸¸", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 6:
					if (!pc_userService.ishaveFamilyMember(6)) {
						pc_userService
								.insert(new Pc_user(userId, "×æÄ¸", 6, "×æÄ¸",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"×æÄ¸", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 7:
					if (!pc_userService.ishaveFamilyMember(7)) {
						pc_userService
								.insert(new Pc_user(userId, "ÐÖµÜ", 7, "ÐÖµÜ",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"ÐÖµÜ", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				case 8:
					if (!pc_userService.ishaveFamilyMember(8)) {
						pc_userService
								.insert(new Pc_user(userId, "½ãÃÃ", 8, "½ãÃÃ",1));
					}
					pc_data = new Pc_data(userId, systolicPressure,diastolicPressure,
							pulse, uploadTime,
							"½ãÃÃ", screenName, familyMember_);
					pc_dataService.insert(pc_data);
					pc_dataService.insert(pc_data);
					break;
				default:
					break;
				}
			}

			str = "success";

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println("eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
					+ e.toString());
			
		}

		return str;

	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public boolean isDownloaded()
	{
		return downloaded;
	}

	public void setDownloaded(boolean downloaded)
	{
		this.downloaded = downloaded;
	}

}
