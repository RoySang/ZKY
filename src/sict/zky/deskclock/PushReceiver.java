package sict.zky.deskclock;

import org.json.JSONException;
import org.json.JSONObject;

import sict.zky.domain.Getui_store;
import sict.zky.main.MainTestActivity;
import sict.zky.main.TestSplashActivity;
import sict.zky.service.Getui_storeService;
import sict.zky.setting.SettingActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast; 

import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

public class PushReceiver extends BroadcastReceiver {
	private int userId;
	private int high;
	private int low;
	private int pulse;
	private String uploadTime; //测量数据的上传时间
	
	private String content;
	private Getui_store getui_store;
	private Getui_storeService getui_storeService;
	private String title;
	private String msg;

	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		getui_storeService=new Getui_storeService(context);
		
//		System.out.println("aaaaaaaaaaaaaaa"+bundle.toString());
		Log.d("GetuiSdkDemo", "onReceive() action=" + bundle.getInt("action"));
		switch (bundle.getInt(PushConsts.CMD_ACTION)) {

		case PushConsts.GET_MSG_DATA:
			
			// String appid = bundle.getString("appid");
			byte[] payload = bundle.getByteArray("payload");
			
			String taskid = bundle.getString("taskid");
			String messageid = bundle.getString("messageid");

			boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

			
			if (payload != null) {
				String data = new String(payload);
				
				
				
				
				

				Log.d("GetuiSdkDemo", "Got Payload:" + data);
				try {
					JSONObject obj =new JSONObject(data);
					int  type = obj.getInt("type");
					if(type==0) //接收到gsm测量数据
					{
						userId=obj.getInt("userId");
						high=obj.getInt("high");
						low=obj.getInt("low");
						pulse=obj.getInt("pulse");
						
						uploadTime=obj.getString("uploadTime");
						content=" 舒张压/收缩压 : "+high+"/"+low+" , 脉搏 : "+pulse;
						getui_store=new Getui_store(userId,content,uploadTime,0,"测量血压消息");
						new Thread(new MyThread()).start();
						
						
						TestSplashActivity.high = high;
						TestSplashActivity.low = low;
						TestSplashActivity.pulse = pulse;
						TestSplashActivity.uploadTime = uploadTime;
						TestSplashActivity.userID=userId;
						TestSplashActivity.type = 0;
						
					}
					else if(type == 1) //接收到 健康管理师的指导建议
					{
						userId=obj.getInt("userId");
						title = obj.getString("title");			
						content=  obj.getString("msg");
						uploadTime = obj.getString("time");
						getui_store=new Getui_store(userId,content,uploadTime,0,title);
						new Thread(new MyThread()).start();
						TestSplashActivity.userID=userId;
						TestSplashActivity.title = title;
						TestSplashActivity.msg = content;
						TestSplashActivity.type = 1;
						TestSplashActivity.time =uploadTime;
						
						
					}

					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}



			}else{
				Log.d("GetuiSdkDemo", "error");
			}
			break;
		case PushConsts.GET_CLIENTID:
			
			String cid = bundle.getString("clientid");

			if(!cid.equals("")){
				TestSplashActivity.clientId=cid;
//				Thread clientThread = new Thread(new ClientThread());
//				clientThread.start();
			}
			break;
		case PushConsts.THIRDPART_FEEDBACK:
		
			break;
		default:
			break;
		}
	}
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				if(!getui_storeService.ishavegetui_msg(userId, content, uploadTime)){
					getui_storeService.insertnouserName(getui_store);
					System.out.println("eeeeeeeeeeesuccess!!");
				}
				
				break;


			}
		}
	};

	public class MyThread implements Runnable {
		public void run() {
			// TODO Auto-generated method stub
			
				Message message = null;
				message = new Message();
				message.what = 0;
				handler.sendMessage(message);// 发送消息
//				getui_storeService.insertnouserName(getui_store);
			
		}
	}
}






