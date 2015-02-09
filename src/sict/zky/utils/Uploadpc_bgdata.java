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

public class Uploadpc_bgdata {
	private Context context;
	private int userId;
	private String userName;
	private String familyRole;
	private String BloodGlucose;
	private int type;

	public Uploadpc_bgdata(Context context) {
		super();
		this.context = context;
	}

	public void uploadpc_bgdata(int userId) {

		Thread uploadpc_bgdataThread = new Thread(new Uploadpc_bgdataThread());
		uploadpc_bgdataThread.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Pc_bgdataService pc_bgdataService = new Pc_bgdataService(
						context);
				pc_bgdataService.updateupload();
				// Toast.makeText(context, "血糖上传同步成功",
				// Toast.LENGTH_LONG).show();

				break;
			case 2:
				// mDialog.cancel();
				Toast.makeText(context, "血糖上传同步失败", Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Uploadpc_bgdataThread implements Runnable {

		public void run() {

			String str = Upload_pcbgdataServer();
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
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

	private String Upload_pcbgdataServer() {
		String str = "";
		Pc_userService pc_userService = new Pc_userService(context);
		Pc_bgdataService pc_bgdataService = new Pc_bgdataService(context);
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		pc_bgdatas = pc_bgdataService.getnotuploadpc_bgdata(0);
		if (pc_bgdatas.size() == 0) {
			str = "0";
		} else {
			try {

				URL url = new URL(Config.IPaddress
						+ "/ZKYweb/Upload_PC_BGDataServlet");
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
				for (Pc_bgdata pc_bgdata : pc_bgdatas) {
					userName = pc_bgdata.getUserName();
					familyRole = pc_userService
							.getfamilyRolebyuserIdanduserName(userId, userName);
					BloodGlucose=String.valueOf(pc_bgdata.getBloodGlucose());
					if(BloodGlucose.indexOf(".")==-1){
						BloodGlucose=BloodGlucose+".0";
					}
					
					JSONObject obj = new JSONObject();
					obj.put("userId", pc_bgdata.getUserId());
					obj.put("bloodGlucose", BloodGlucose);
					obj.put("uploadTime", pc_bgdata.getUploadTime());
					obj.put("userName", userName);
					obj.put("screenName", pc_bgdata.getScreenName());
					obj.put("familyMember", pc_bgdata.getFamilyMember());
					obj.put("uploadType", 0);
					obj.put("familyRole", familyRole);
					obj.put("celiangType", pc_bgdata.getType());

					array.put(obj);
				}
				pw.write(array.toString());
				pw.flush();
				pw.close();
//				System.out.println("eeeee" + array.toString());
				BufferedReader br = new BufferedReader(new InputStreamReader(
						conn.getInputStream(), "UTF-8"));
				StringBuffer sb = new StringBuffer("");
				String result = "";
				String temp;
				while ((temp = br.readLine()) != null) {
					sb.append(temp);
				}
				br.close();
				str = sb.toString();

			} catch (Exception e) {
				e.printStackTrace();
				// System.out.println("333333333333333333333333333"+e.toString());
			}
		}
		// System.out.println(pc_bgdatas.size()+"ppppppppppppppppppppppppppppppppppppp");

		return str;

	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

}
