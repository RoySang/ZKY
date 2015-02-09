package sict.zky.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.main.HealthKnowledgeActivity.ShowThread;
import sict.zky.service.AdvertisementAdapter;
import sict.zky.utils.Config;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ImageActivity extends Activity {
	private ImageView image;
	private ImageView[] vItem = null;
	private EditText editText = null;
	private static Bitmap[] mImageIds = new Bitmap[4];

	private int id;
	private String name;
	private String picLink;
	private String Link;
	private String text;

	private ListView AdvertisementList;
	private SimpleAdapter adapter = null;
	private AdvertisementAdapter listItemAdapter;

	private String[] nameArray = new String[64];
	private String[] picLinkArray = new String[64];
	private String[] LinkArray = new String[64];
	private String[] textArray = new String[64];
	private Bitmap[] bitmap = new Bitmap[64];
	private long firstTime = 0;
	private ProgressDialog mDialog;
	private int count = 0;
	private int userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.advertisement);
		SysApplication.getInstance().addActivity(this);
		
		Intent intent =getIntent();
		userId=intent.getIntExtra("userId", -1);

		AdvertisementList = (ListView) findViewById(R.id.AdvertisementList);
		AdvertisementList.setVerticalScrollBarEnabled(true);
		AdvertisementList.setOnItemClickListener(new ItemClickListener());
		// AdvertisementList.setDivider(null); //去掉黑线

		// ArrayList<HashMap<String, Object>> listItem = new
		// ArrayList<HashMap<String, Object>>();

		mDialog = new ProgressDialog(ImageActivity.this);
		mDialog.setTitle("加载");
		mDialog.setMessage("正在努力加载，请稍等...");
		mDialog.show();

		Thread imageThread = new Thread(new ImageThread());
		imageThread.start();

	}
	
	
	@Override
	protected void onStart()
	{
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop()
	{
		super.onStop();
		TestinAgent.onStop(this);
	}
	
	
	private final class ItemClickListener implements OnItemClickListener {

		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long iddd) {
			HashMap<String, Object> item = (HashMap<String, Object>) parent
					.getItemAtPosition(position);
//			Uri uri = Uri.parse("http://www.baidu.com");
//			System.out.println("eeeeeeeeee"+LinkArray[position]);
			if(LinkArray[position].equals("")||LinkArray[position].indexOf("http://")!=0){
				LinkArray[position]="http://www.zkhyun.com";
			}
			Uri uri = Uri.parse(LinkArray[position]);
			Intent it = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(it);
//			id = Integer.parseInt(item.get("id").toString());
//			title=item.get("title").toString();
//			Intent intent =new Intent();
//
//			intent.setClass(HealthKnowledgeActivity.this, Knowledge.class);
//			intent.putExtra("id", id);
//			intent.putExtra("title", title);
//			startActivity(intent);
			
		}
	}

	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			listItemAdapter.notifyDataSetChanged();

		}
	};

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				// AdvertisementList.setAdapter(adapter);
				AdvertisementList.setAdapter(listItemAdapter);

				// 新开线程循环下载图片
				new Thread() {

					@SuppressWarnings("unchecked")
					public void run() {

						int i = 0;
						while (i < count) {

							try {

								// URL url1 = new URL(picLinkArray[i]);
								// HttpURLConnection conn1 = (HttpURLConnection)
								// url1
								// .openConnection();
								// InputStream is = conn1.getInputStream();
								// bitmap[i] = BitmapFactory.decodeStream(is);

								URL uri = new URL(picLinkArray[i]);
								// URLConnection conn = uri.openConnection();
								HttpURLConnection conn = (HttpURLConnection) uri
										.openConnection();
								conn.connect();
								InputStream is = conn.getInputStream();
								Bitmap bmp = BitmapFactory.decodeStream(is);
								is.close();
								HashMap<String, Object> map = (HashMap<String, Object>) listItemAdapter
										.getItem(i);

								map.put("AdvertisementImage", bmp);
								handler1.sendEmptyMessage(0);// 这个是更新通知就是listItemAdapter.notifyDataSetChanged();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							i++;
						}
					}
				}.start();

				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "查询失败",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class ImageThread implements Runnable {

		public void run() {

			String str = ImageThreadServer(String.valueOf(userId));
			Message msg = handler.obtainMessage();
			if (str.equals("success")) {
				msg.what = 0;
				handler.sendMessage(msg);
			} else {

				msg.what = 1;
				handler.sendMessage(msg);
			}

		}

	}

	private String ImageThreadServer(String userId) {
		String str = "false";
		try {
			URL url = new URL(Config.IPaddress + "/ZKYweb/AdvertisementServlet");
			// URL url = new
			// URL("http://192.168.0.100:8080/ZKYweb/AdvertisementServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONObject obj1=new JSONObject();
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

			// Toast.makeText(getApplicationContext(), array.toString(),
			// 1).show();

			// System.out.println(result);
			List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = array.getJSONObject(i);
				nameArray[i] = obj.getString("name");

				picLinkArray[i] = obj.getString("picLink");
				LinkArray[i] = obj.getString("Link");
				textArray[i] = obj.getString("text");

				count++;
				// Toast.makeText(getApplicationContext(), array.toString(),
				// 1).show();

				// URL url1 = new URL(picLinkArray[i]);
				// HttpURLConnection conn1 = (HttpURLConnection) url1
				// .openConnection();
				// InputStream is = conn1.getInputStream();
				// bitmap[i] = BitmapFactory.decodeStream(is);
				// mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
				// BitmapDrawable bd = (BitmapDrawable)
				// bitmap2Drawable(bitmap[i]);
				// BitmapDrawable bd=(BitmapDrawable)
				// bitmap2Drawable(bitmap[i]);
				// Drawable drawable = new BitmapDrawable(bitmap[i]);
				// System.out.println("eeeeeeeee"+nameArray[i]+","+textArray[i]+" , "+picLinkArray[i]);
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put("AdvertisementImage", R.drawable.z);
				item.put("AdvertisementName", nameArray[i]);
				item.put("AdvertisementXinghao", textArray[i]);
				data.add(item);

			}
			// adapter = new SimpleAdapter(this, data,
			// R.layout.advertisement_item, new String[] {
			// "AdvertisementImage", "AdvertisementName",
			// "AdvertisementXinghao" }, new int[] {
			// R.id.AdvertisementImage, R.id.AdvertisementName,
			// R.id.AdvertisementXinghao });

			listItemAdapter = new AdvertisementAdapter(this, data,
					R.layout.advertisement_item, new String[] {
							"AdvertisementImage", "AdvertisementName",
							"AdvertisementXinghao" }, new int[] {
							R.id.AdvertisementImage, R.id.AdvertisementName,
							R.id.AdvertisementXinghao });

			str = "success";
		} catch (Exception e) {

			e.printStackTrace();
//			System.out.println("eeeeeeeeeeee" + e.toString());
		}
		return str;

	};

	// class Astake extends AsyncTask<Void, Integer, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	//
	// try {
	// int i = 0;
	// while (i < mImageIds.length) {
	// Thread.sleep(3000);
	// publishProgress(i);
	// i++;
	// if (i >= mImageIds.length) {
	// i = 0;
	// }
	// }
	// while (i < textArray.length) {
	// Thread.sleep(3000);
	// publishProgress(i);
	// i++;
	// if (i >= textArray.length) {
	// i = 0;
	// }
	//
	// }
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	//
	// return null;
	// }

	// @Override
	// protected void onProgressUpdate(Integer... values) {
	// // TODO Auto-generated method stub
	// super.onProgressUpdate(values);
	// for (int i = 0; i < mImageIds.length; i++) {
	// vItem[i].setImageDrawable(getBaseContext().getResources()
	// .getDrawable(R.drawable.grayicon));
	//
	// }
	// image.setImageBitmap(mImageIds[values[0]]);
	// // editText.setText(textArray[values[0]]);
	// vItem[values[0]].setImageDrawable(getBaseContext().getResources()
	// .getDrawable(R.drawable.greenicon));
	// super.onProgressUpdate(values);
	// }
	//
	// }

	/**
	 * Bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	// 双击退出
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "再按一次返回键退出中科云健康", Toast.LENGTH_SHORT).show();
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
