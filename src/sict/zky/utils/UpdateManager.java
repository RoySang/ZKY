package sict.zky.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import sict.zky.deskclock.R;
import sict.zky.main.LoginActivity;
import sict.zky.main.MainActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class UpdateManager {
	private Context mContext;
	private String updateMsg = "�����µ������Ŷ���׿����ذ�~";
	// ���صİ�װ��url
	private String xmlUrl = "http://121.42.32.103:8080/zkysoft/version.xml";
	// private String xmlUrl = "http://121.42.32.103/app/download/version.xml";
	private Dialog noticeDialog;
	private Dialog downloadDialog;
	/* ���ذ���װ·�� */
	private static final String savePath = "/sdcard/zkhyun/Download/";
	// private static final String savePath = "/sdcard/Download/";
	private static final String saveFileName = savePath + "ZKYHealth.apk";
	/* ��������֪ͨuiˢ�µ�handler��msg���� */
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;
	private Thread downLoadThread;
	private boolean interceptFlag = false;
	private int serviceCode;
	private String serviceVersion;
	// ���������XML��Ϣ
	HashMap<String, String> mHashMap;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:

				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	// �ⲿ�ӿ�����Activity����
	public void checkUpdateInfo() {
		Thread checkUpdateThread = new Thread(new CheckUpdateThread());
		checkUpdateThread.start();
		
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				showNoticeDialog();

				break;
			case 1:
				Toast.makeText(mContext, "�������°汾", 1).show();
				break;
			}

		}
	};

	class CheckUpdateThread implements Runnable {

		public void run() {

			boolean ifneedupdate = checkUpdateServer();
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (ifneedupdate) {//��Ҫ����
				msg.what = 0;
				handler.sendMessage(msg);
			} else   {// ����Ҫ����
				msg.what = 1;
				handler.sendMessage(msg);
			} 
		}

	}

	private boolean checkUpdateServer() {
		try {
			int versionCode = getVersionCode(mContext);
			URL url = new URL(xmlUrl);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			InputStream inStream = urlConn.getInputStream();
			ParseXmlService service = new ParseXmlService();
			try {
				mHashMap = service.parseXml(inStream);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null != mHashMap) {
				serviceCode = Integer.valueOf(mHashMap.get("version"));
				serviceVersion=mHashMap.get("appversion");
				if (serviceCode > versionCode) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void showNoticeDialog() {
		// AlertDialog.Builder builder = new Builder(mContext);
		// builder.setTitle("����汾����");
		// builder.setMessage(updateMsg);
		// builder.setPositiveButton("����", new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		showDownloadDialog();
		// }
		// });
		// builder.setNegativeButton("�Ժ���˵", new OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// dialog.dismiss();
		// }
		// });
		// noticeDialog = builder.create();
		// noticeDialog.show();
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("���°汾"+serviceVersion);
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.uploadprogress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.progress);

		builder.setView(v);
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();

		downloadApk();
	}

	private Runnable mdownApkRunnable = new Runnable() {

		public void run() {
			try {
				
						URL apkurl = new URL(mHashMap.get("url")
								+ "ZKYHealth.apk");
						HttpURLConnection conn = (HttpURLConnection) apkurl
								.openConnection();
						conn.connect();
						int length = conn.getContentLength();
						InputStream is = conn.getInputStream();

						File file = new File(savePath);
						if (!file.exists()) {
							file.mkdirs();
						}
						String apkFile = saveFileName;
						File ApkFile = new File(apkFile);
						if (ApkFile.exists()) {
							ApkFile.delete();
						}
						FileOutputStream fos = new FileOutputStream(ApkFile);

						int count = 0;
						byte buf[] = new byte[1024];

						do {
							int numread = is.read(buf);
							count += numread;
							progress = (int) (((float) count / length) * 100);
							// ���½���
							mHandler.sendEmptyMessage(DOWN_UPDATE);
							if (numread <= 0) {
								// �������֪ͨ��װ
								mHandler.sendEmptyMessage(DOWN_OVER);
								break;
							}
							fos.write(buf, 0, numread);
						} while (!interceptFlag);// ���ȡ����ֹͣ����.

						fos.close();
						is.close();
								

			} catch (MalformedURLException e) {
				e.printStackTrace();
				System.out.println("eeeeeeeeeeee" + e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("eeeeeeeeeeee" + e.toString());
			}

		}
	};

	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode

			versionCode = context.getPackageManager().getPackageInfo(
					"sict.zky.deskclock", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/*
	 * ����apk
	 */
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}

	/*
	 * ��װapk
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		mContext.startActivity(i);

	}

}
