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

import sict.zky.domain.Pc_bgdata;
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

public class Uploadpc_user {
	private Context context;
	private int userId;

	public Uploadpc_user(Context context) {
		super();
//		System.out.println("eeeeeeeeeeeeeeeeeeeupload_pcusers");
		this.context = context;
	}

	public void uploadpc_user(int userId) {
//		System.out.println("eeeeeeeeeeeeeeeeeee123123");
		Thread uploadpc_userThread = new Thread(new Uploadpc_Thuserread());
		uploadpc_userThread.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Pc_userService pc_userService=new Pc_userService(context);
				pc_userService.updateupload();
//				Toast.makeText(context, "家庭成员上传同步成功", Toast.LENGTH_LONG).show();
				System.out.println("家庭成员上传同步成功");
				break;
			case 1:
				System.out.println("无需上传");
			break;
			case 2:
				// mDialog.cancel();
//				Toast.makeText(context, "家庭成员同步失败", Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Uploadpc_Thuserread implements Runnable {

	
		public void run() {

			String str = Upload_pcuserServer();
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else if(str.equals("0")){

				msg.what = 1;
				handler.sendMessage(msg);
			}else{
				msg.what = 2;
				handler.sendMessage(msg);
			}

		}

	}

	private String Upload_pcuserServer() {
		String str = "false";
		// Pc_userService pc_userService = new Pc_userService(context);
		Pc_userService pc_userService = new Pc_userService(context);
		List<Pc_user> pc_users = new ArrayList<Pc_user>();
		pc_users = pc_userService.getnotuploadpc_user(0);
//		pc_users=pc_userService.selectAll(userId);
		if(pc_users.size()==0){
			str="0";
		}
		else{
			try {
				URL url = new URL(Config.IPaddress + "/ZKYweb/Upload_UsersServlet");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.addRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				conn.connect();
				PrintWriter pw = new PrintWriter(conn.getOutputStream());
				JSONArray array = new JSONArray();
				for (Pc_user pc_user : pc_users) {

					JSONObject obj = new JSONObject();
					obj.put("userId", pc_user.getUserId());
					obj.put("userName", pc_user.getUserName());
					obj.put("familyMember", pc_user.getFamilyMember());
					obj.put("familyRole", pc_user.getFamilyRole());

					array.put(obj);
				}
					pw.write(array.toString());
//					System.out.println("eeeeeeeeeee"+array.toString());
					pw.flush();
					pw.close();

					BufferedReader br = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "UTF-8"));
					StringBuffer sb = new StringBuffer("");
//					String result = "";
					String temp;
					while ((temp = br.readLine()) != null) {
						sb.append(temp);
					}
					br.close();
				str = sb.toString();
//				System.out.println("eeeeeeeeeeeeeee"+str);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("eeeeeeeeeeeeeee"+e.toString());
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
