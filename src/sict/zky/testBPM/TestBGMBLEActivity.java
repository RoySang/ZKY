package sict.zky.testBPM;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import sict.zky.datacurve.Datacurve;
import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.main.SysApplication;
import sict.zky.packet.Packet;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.utils.Config;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.ListViewAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.testin.agent.TestinAgent;

public class TestBGMBLEActivity extends Activity {
	// ---------------------------------------------------

	private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP����UUID��

	BluetoothAdapter btAdapt;
	// private static BluetoothSocket btSocket ;
	BluetoothSocket btSocket = null;
	BluetoothDevice device = null;

	private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter();

	private String smsg = "null"; // ��ʾ�����ݻ���
	private String fmsg = ""; // ���������ݻ���
	private String msg1 = ""; // ���������ݻ���

	private InputStream is; // ������������������������
	public String filename = ""; // ��������洢���ļ���

	boolean _discoveryFinished = false;
	boolean bRun = true;
	boolean bThread = false;
	private Packet pk = new Packet();
	private boolean ifok = true;
	private int count = 0;

	private ImageView showconn;
	private TextView showdata;
	// private Spinner bpmble_username_spinner;
	// private Spinner bpmble_time_spinner;
	private String getuserName;
	private List<String> getuserNamebyuserId;
	private UserNameAdapter userNameAdapter;
	private ProgressDialog mDialog;

	private Pc_bgdata pc_bgdata;
	private Pc_bgdataService pc_bgdataService;
	private Pc_userService pc_userService;

	// private String uploadTime;
	private int userId;
	private String userName = "";
	private String screenName_;
	private String GLU;
	private int familyMember_;
	private String familyRole;
	private int type = 1;
//	private static String time[] = { "��ǰ", "����", "˯ǰ", "�賿" };
//	private ArrayAdapter<String> timeadapter;

	private int thisposition = 0;
	private String ThisGLU;
	private boolean thisTest = true;
	private String timeValue;
	private String checksum;
	private CurrentTime ct = new CurrentTime();

	private TextView bpmble_username_textView;
	private ImageView bpmble_username_imageView;
	private PopupWindow pw_username;// PopupWindow��������
	private LinearLayout bpmble_username_linear;
	private ArrayList<String> usernamelist;// �û�����ʾ�б�
	int clickPsitionofusername = -1;// �û����б���λ��

	private TextView bpmble_time_textView;
	private ImageView bpmble_time_imageView;
	private PopupWindow pw_time;// PopupWindow��������
	private LinearLayout bpmble_time_linear;
	private ArrayList<String> timelist;// �û�����ʾ�б�
	int clickPsitionoftime = -1;// �û����б���λ��

	// ---------------------------------------------------

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SysApplication.getInstance().addActivity(this);
		setContentView(R.layout.bgmble);

		// ---------------------------------------------------
		showdata = (TextView) findViewById(R.id.showdata);
		showconn = (ImageView) findViewById(R.id.showconn);
		// bpmble_username_spinner = (Spinner)
		// findViewById(R.id.bpmble_username_spinner);
		// bpmble_time_spinner=(Spinner) findViewById(R.id.bpmble_time_spinner);
		Intent intent1 = getIntent();
		userId = intent1.getIntExtra("userId", 0);
		screenName_ = intent1.getStringExtra("screenName");
		userName=screenName_;
		familyMember_ = 0;
		familyRole = "����";

		pc_userService = new Pc_userService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());
		getuserNamebyuserId = pc_userService.getuserName(userId);

		// ��ʼ���ؼ�
		bpmble_username_linear = (LinearLayout) findViewById(R.id.bpmble_username_linear);
		bpmble_username_textView = (TextView) findViewById(R.id.bpmble_username_textView);
		bpmble_username_imageView = (ImageView) findViewById(R.id.bpmble_username_imageView);
		bpmble_time_linear = (LinearLayout) findViewById(R.id.bpmble_time_linear);
		bpmble_time_textView = (TextView) findViewById(R.id.bpmble_time_textView);
		bpmble_time_imageView = (ImageView) findViewById(R.id.bpmble_time_imageView);
		initusernamepop();
		inittimepop();
		// userNameAdapter = new UserNameAdapter(this, getuserNamebyuserId);

		// bpmble_username_spinner.setAdapter(userNameAdapter);
		// bpmble_username_spinner.setOnItemSelectedListener(new
		// OnItemSelected());

//		timeadapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, time);
//		timeadapter
//				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// bpmble_time_spinner.setAdapter(timeadapter);//ʱ�������б�������
		// bpmble_time_spinner.setPrompt("��ѡ��ʱ��:");
		// bpmble_time_spinner
		// .setOnItemSelectedListener(new OnItemSelectedListener() {//����¼�
		//
		// public void onItemSelected(AdapterView<?> parent,
		// View view, int position, long id) {
		// switch (position) {
		//
		// case 0:
		// type = 1;
		// break;
		// case 1:
		// type = 2;
		// break;
		// case 2:
		// type = 3;
		// break;
		// case 3:
		// type = 4;
		// break;
		// default:
		// break;
		// }
		//
		// }
		//
		// public void onNothingSelected(AdapterView<?> parent) {
		// // TODO Auto-generated method stub
		//
		// }
		// });

		btAdapt = BluetoothAdapter.getDefaultAdapter();// ��ʼ��������������

		// ע��Receiver����ȡ�����豸��صĽ��
		String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
		IntentFilter intent = new IntentFilter();
		intent.addAction(BluetoothDevice.ACTION_FOUND);
		intent.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		intent.addAction(ACTION_PAIRING_REQUEST);
		intent.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
		intent.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(searchDevices, intent);

		// ���û�д�����������������Ƿ���Ҫ���̴߳������������ǣ�
		new Thread() {
			public void run() {
				if (btAdapt.isEnabled() == false) {
					btAdapt.enable();
				}
			}
		}.start();

		// if (btAdapt.isEnabled() == false)
		// {
		// btAdapt.enable();
		// }

		// ���socket�������ӣ��ر�socket
		if (btSocket != null) {

			// �ر�����socket
			try {

				btSocket.close();
				btSocket = null;
				bRun = false;

			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), e.toString(), 1).show();
			}

		}
		// ���������������ӵ��߳�
		new Thread(new MyThread()).start();
	}

	private void initusernamepop() {// �û���popwindow��ʼ��
		usernamelist = getusernameList();
		// final int size = getuserNamebyuserId.size() + 1;
		// ����Ĭ����ʾ��Text
		bpmble_username_textView.setText(usernamelist.get(0));
		bpmble_username_linear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				bpmble_username_imageView.setImageResource(R.drawable.up);
				// ͨ������ע������ע�벼�ָ�View����
				View myView = getLayoutInflater().inflate(
						R.layout.pop_username, null);
				// ͨ��view �Ϳ��ߣ�����PopopWindow
				pw_username = new PopupWindow(myView, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				// pw=new PopupWindow(myView);
				// pw_username.setOutsideTouchable(true);
				pw_username.setBackgroundDrawable(getResources().getDrawable(
				// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
						R.drawable.bg_white));

				// ���ý���Ϊ�ɵ��
				pw_username.setFocusable(true);// ����������Ϊfalse�Ľ��
				// ��window��ͼ��ʾ��myButton����
				pw_username.showAsDropDown(bpmble_username_textView);

				ListView lv = (ListView) myView
						.findViewById(R.id.lv_pop_username);
				lv.setAdapter(new ListViewAdapter(TestBGMBLEActivity.this,
						usernamelist));
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						bpmble_username_textView.setText(usernamelist
								.get(position));
						if (clickPsitionofusername != position) {
							clickPsitionofusername = position;
						}
						// getuserName =
						// parent.getItemAtPosition(position).toString();
						getuserName = usernamelist.get(position);
						userName = getuserName;
bpmble_username_imageView
										.setImageResource(R.drawable.down);
						pw_username.dismiss();
					}
				});
			}

		});
	}

	public ArrayList<String> getusernameList() {
		ArrayList<String> list = (ArrayList<String>) getuserNamebyuserId;
		return list;

	}

	private void inittimepop() {// �¼�popwindow��ʼ��
		timelist = gettimeList();
		// final int size = getuserNamebyuserId.size() + 1;
		// ����Ĭ����ʾ��Text
		bpmble_time_textView.setText(timelist.get(0));
		bpmble_time_linear.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				bpmble_time_imageView.setImageResource(R.drawable.up);
				// ͨ������ע������ע�벼�ָ�View����
				View myView = getLayoutInflater().inflate(R.layout.pop_time,
						null);
				// ͨ��view �Ϳ��ߣ�����PopopWindow
				pw_time = new PopupWindow(myView, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				// pw=new PopupWindow(myView);
				// pw_username.setOutsideTouchable(true);
				pw_time.setBackgroundDrawable(getResources().getDrawable(
				// �˴�Ϊpopwindow ���ñ�����ͬ����������ⲿ����popwindow��ʧ
						R.drawable.bg_white));

				// ���ý���Ϊ�ɵ��
				pw_time.setFocusable(true);// ����������Ϊfalse�Ľ��
				// ��window��ͼ��ʾ��myButton����
				pw_time.showAsDropDown(bpmble_time_textView);

				ListView lv = (ListView) myView.findViewById(R.id.lv_pop_time);
				lv.setAdapter(new ListViewAdapter(TestBGMBLEActivity.this,
						timelist));
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						bpmble_time_textView.setText(timelist.get(position));
						if (clickPsitionoftime != position) {
							clickPsitionoftime = position;
						}
						switch (position) {
						case 0:
							type = 1;
							break;
						case 1:
							type = 2;
							break;
						case 2:
							type = 3;
							break;
						case 3:
							type = 4;
							break;
						default:
							break;
						}
bpmble_time_imageView
										.setImageResource(R.drawable.down);
						pw_time.dismiss();
					}
				});
			}

		});
	}

	public ArrayList<String> gettimeList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("��ǰ");
		list.add("�ͺ�");
		list.add("˯ǰ");
		list.add("�賿");
		return list;

	}

	// �����б����¼�
	private final class OnItemSelected implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			getuserName = parent.getItemAtPosition(position).toString();
			userName = getuserName;
			familyMember_ = pc_userService.getfamilyMemberbyuserIdanduserName(
					userId, getuserName);
			familyRole = pc_userService.getfamilyRolebyuserIdanduserName(
					userId, getuserName);
			thisposition = position;
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}
	}

	Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:

				showconn.setImageDrawable(getResources().getDrawable(
						R.drawable.searching));
				break;
			}
		}
	};

	public class MyThread implements Runnable {

		public void run() {

			while (ifok) {
				if (btAdapt.isDiscovering())
					btAdapt.cancelDiscovery();

				btAdapt.startDiscovery();
				count++;

				Message message = null;
				message = new Message();
				if (ifok == false) {
					message.what = 0;
					handler1.sendMessage(message);// ������Ϣ
				} else {
					message.what = 1;
					handler1.sendMessage(message);// ������Ϣ
				}
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		}
	}

	// �������ӹ㲥
	private BroadcastReceiver searchDevices = new BroadcastReceiver() {

		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (intent.getAction().equals(
					"android.bluetooth.device.action.PAIRING_REQUEST")) {
				BluetoothDevice btDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				try {
					ClsUtils.setPin(btDevice.getClass(), btDevice, "1234"); // �ֻ��������ɼ������
					ClsUtils.createBond(btDevice.getClass(), btDevice);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {// �����豸ʱ��ȡ���豸��MAC��ַ
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (device.getBondState() == BluetoothDevice.BOND_NONE) {

					try {
						if (device != null && !device.getName().equals(""))// ������������豸�����
						{
							if (device.getName().toString().indexOf("BGM") >= 0) {
								ClsUtils.setPin(device.getClass(), device,
										"1234"); // �ֻ��������ɼ������
								ClsUtils.createBond(device.getClass(), device);
								// ClsUtils.cancelPairingUserInput(device.getClass(),
								// device);
							}
						}
					} catch (Exception e) {

						e.printStackTrace();
					}

					// if (device != null)
					// {
					// if (device.getName().toString().indexOf("BGM") >= 0)
					// {
					// device = btAdapt.getRemoteDevice(device
					// .getAddress());
					//
					// ClsUtils.pair(device.getAddress(), "1234");
					//
					// ifok = false;
					// }
					// } else
					// {
					// Toast.makeText(getApplicationContext(),
					// "δ������Ѫ���豸,���˳����������ؽ�", 1).show();
					// }
				}

				if (device.getBondState() == BluetoothDevice.BOND_BONDED) {

					if (device != null) {
						if (device.getName().toString().indexOf("BGM") >= 0) {

							device = btAdapt.getRemoteDevice(device
									.getAddress());
							connect(device);// �����豸

						}
					}
				}
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:

					Log.d("BlueToothTestActivity", "�������......");
					break;
				case BluetoothDevice.BOND_BONDED:

					if (btAdapt.isDiscovering())
						btAdapt.cancelDiscovery();
					btAdapt.startDiscovery();

					break;
				case BluetoothDevice.BOND_NONE:
					Log.d("BlueToothTestActivity", "ȡ�����");
				default:
					break;
				}
			}

		}
	};

	private void connect(BluetoothDevice btDev) {
		try {
			// ClsUtils.cancelPairingUserInput(device.getClass(), device);
		} catch (Exception e) {

			e.printStackTrace();
		}
		// final UUID SPP_UUID = UUID
		// .fromString("00001101-0000-1000-8000-00805F9B34FB");
		// UUID uuid = SPP_UUID;
		try {
			btDev = btAdapt.getRemoteDevice(btDev.getAddress());
			btSocket = device.createRfcommSocketToServiceRecord(UUID
					.fromString(MY_UUID));

			btAdapt.cancelDiscovery();

			btSocket.connect();
			Toast.makeText(this, "���ӳɹ���",
					Toast.LENGTH_SHORT).show();

			showconn.setImageDrawable(getResources().getDrawable(
					R.drawable.already_connect));

			int i = 0;
			int n = 0;
			try {

				OutputStream os = btSocket.getOutputStream(); // �������������
				// byte[] bos =
				// edit0.getText().toString().getBytes();

				// ������Ϣ��1234 ��Ѫ����
				byte[] bos = "1234".getBytes();
				for (i = 0; i < bos.length; i++) {
					if (bos[i] == 0x0a)
						n++;
				}

				byte[] bos_new = new byte[bos.length + n];
				n = 0;
				for (i = 0; i < bos.length; i++) { // �ֻ��л���Ϊ0a,�����Ϊ0d
													// 0a���ٷ���
					if (bos[i] == 0x0a) {
						bos_new[n] = 0x0d;
						n++;
						bos_new[n] = 0x0a;
					} else {
						bos_new[n] = bos[i];
					}
					n++;
				}

				os.write(bos_new);

			} catch (IOException e) {

			}
			// �򿪽����߳�
			try {
				is = btSocket.getInputStream(); // �õ���������������
			} catch (IOException e) {
				return;
			}
			if (bThread == false) {
				ReadThread.start();
				bThread = true;
			} else {
				bRun = true;
			}

		} catch (IOException e) {
			// try
			// {
			// Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			// btSocket.close();
			// btSocket = null;
			// } catch (IOException ee)
			// {
			// Toast.makeText(this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
			// }
			//
			// return;
		}
	}

	// ��Ϣ�������
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String start = smsg.substring(0, 2);
			String SN = "";
			int bloodGlucose = 0;
			float bloodGlucoseF = 0.0f;
			Double bloodGlucoseB = 0.0;
			String time = "";
			// String checksum1 = "";

			if (start.equals("5A") && pk.isInformationPacketofBGM(smsg)) {
				ifok = false; // ��ֹ�����������߳�
				SN = smsg.substring(11).substring(0, 9); // ������

				bloodGlucose = Integer.parseInt(smsg.substring(26).substring(0,
						3));

				bloodGlucoseB = Math.round(bloodGlucose / 18.0 * 10) / 10.0;
				time = smsg.substring(29).substring(0, 2) + "-"
						+ smsg.substring(31).substring(0, 2) + "-"
						+ smsg.substring(33).substring(0, 2) + " "
						+ smsg.substring(35).substring(0, 2) + ":"
						+ smsg.substring(37).substring(0, 2) + ":00";// ʱ��
				checksum = smsg.substring(39).substring(0, 4);
				setTitle("�������");

				timeValue = time;

				if (thisTest) {
					ThisGLU = String.valueOf(bloodGlucoseB);
					thisTest = false;

					mDialog = new ProgressDialog(TestBGMBLEActivity.this);
					mDialog.setTitle("����");
					mDialog.setMessage("���ڱ��棬���Ժ�...");
					mDialog.show();
					Thread update_pc_bgdataThread = new Thread(
							new Update_pc_bgdataThread());
					update_pc_bgdataThread.start();

					showdata.setText("����Ѫ��ֵ: " + ThisGLU);
					showdata.setTextColor(Color.RED);
					showconn.setVisibility(View.GONE);

					// connect(checksum);
					// socketconnect(checksum);
				} else {
					userName = screenName_;

					Pc_bgdata pc_bgdata = new Pc_bgdata(userId,
							Double.valueOf(bloodGlucoseB), time, userName,
							screenName_, 0, 0, 0);// ���һ��0��ʾѪ������ ���޸�
					pc_bgdataService.insert(pc_bgdata);
					socketconnect(checksum);

				}

			}
		}
	};

	Handler bghandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				mDialog.cancel();
				Toast.makeText(TestBGMBLEActivity.this, "����ɹ���",
						Toast.LENGTH_SHORT).show();

				Pc_bgdata pc_bgdata1 = new Pc_bgdata(userId,
						Double.valueOf(ThisGLU), timeValue, userName,
						screenName_, familyMember_, 1, 0);// ���һ��0��ʾѪ������ ���޸�
				pc_bgdataService.insert(pc_bgdata1);
				socketconnect(checksum);
				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "����������",
						Toast.LENGTH_SHORT).show();

				Pc_bgdata pc_bgdata2 = new Pc_bgdata(userId,
						Double.valueOf(ThisGLU), timeValue, userName,
						screenName_, familyMember_, 0, 0);// ���һ��0��ʾѪ������ ���޸�
				pc_bgdataService.insert(pc_bgdata2);
				socketconnect(checksum);
				break;

			}

		}
	};

	class Update_pc_bgdataThread implements Runnable {

		public void run() {

			String str = update_pc_bgdataServer(userId, ThisGLU, timeValue, 0,
					userName, screenName_, familyMember_, familyRole, type);

			Message msg = bghandler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success")) {
				msg.what = 0;
				bghandler.sendMessage(msg);
			} else {

				msg.what = 1;
				bghandler.sendMessage(msg);
			}

		}

	}

	private String update_pc_bgdataServer(int userId, String bloodGlucose,
			String uploadTime, int uploadType, String userName,
			String screenName, int familyMember, String familyRole, int type) {

		String str = "";
		try {
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Upload_PC_BGDataServlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());

			JSONArray array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("userName", userName);
			obj.put("screenName", screenName);
			obj.put("uploadTime", uploadTime);
			obj.put("familyMember", familyMember);
			obj.put("bloodGlucose", bloodGlucose);
			obj.put("uploadType", uploadType);
			obj.put("familyRole", familyRole);
			obj.put("celiangType", type);
			System.out.println("eeeee" + obj.toString());
			array.put(obj);

			pw.write(array.toString());
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

	// ���������߳�
	Thread ReadThread = new Thread() {
		public void run() {
			int num = 0;
			byte[] buffer = new byte[1024];
			byte[] buffer_new = new byte[1024];
			int i = 0;
			int n = 0;
			bRun = true;
			// �����߳�
			while (true) {
				try {
					while (is.available() == 0) {
						while (bRun == false) {
						}
					}
					while (true) {
						num = is.read(buffer); // ��������
						n = 0;

						String s0 = new String(buffer, 0, num);
						// fmsg += s0; // �����յ�����
						for (i = 0; i < num; i++) {
							if ((buffer[i] == 0x0d) && (buffer[i + 1] == 0x0a)) {
								buffer_new[n] = 0x0a;
								i++;
							} else {
								buffer_new[n] = buffer[i];
							}
							n++;
						}
						String s = new String(buffer_new, 0, n);
						fmsg += s; // д����ջ���
						// msg1 += s + " , ";
						smsg = "";
						if (fmsg.length() >= 43) {
							smsg = fmsg.substring(0, 43);
							fmsg = fmsg.substring(43);
						}

						if (is.available() == 0)
							break; // ��ʱ��û�����ݲ�����������ʾ
					}
					// ������ʾ��Ϣ��������ʾˢ��
					if (smsg.length() > 0)
						handler.sendMessage(handler.obtainMessage());
				} catch (IOException e) {
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.unregisterReceiver(searchDevices);

		// if(btAdapt!=null)
		// {
		// btAdapt.disable(); // �ر���������
		// }

	}

	public void showMessage(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
	}

	private void socketconnect() {
		int i = 0;
		int n = 0;
		try {

			OutputStream os = btSocket.getOutputStream(); // �������������
			// byte[] bos =
			// edit0.getText().toString().getBytes();

			// ������Ϣ��1234 ��Ѫ����
			byte[] bos = "1234".getBytes();
			for (i = 0; i < bos.length; i++) {
				if (bos[i] == 0x0a)
					n++;
			}

			byte[] bos_new = new byte[bos.length + n];
			n = 0;
			for (i = 0; i < bos.length; i++) { // �ֻ��л���Ϊ0a,�����Ϊ0d
												// 0a���ٷ���
				if (bos[i] == 0x0a) {
					bos_new[n] = 0x0d;
					n++;
					bos_new[n] = 0x0a;
				} else {
					bos_new[n] = bos[i];
				}
				n++;
			}

			os.write(bos_new);

		} catch (IOException e) {
			// Toast.makeText(getApplicationContext(), e.toString(), 1).show();

			// System.out.println("eeeeeeeeeee" + e.toString());
		}
		// �򿪽����߳�
		try {
			is = btSocket.getInputStream(); // �õ���������������
		} catch (IOException e) {
			return;
		}
		if (bThread == false) {
			ReadThread.start();
			bThread = true;
		} else {
			bRun = true;
		}
	}

	// �����豸֮�佨��socket����
	private void socketconnect(String str) {
		int i = 0;
		int n = 0;
		try {

			OutputStream os = btSocket.getOutputStream(); // �������������
			// byte[] bos =
			// edit0.getText().toString().getBytes();

			// ������Ϣ��1234 ��Ѫ����
			byte[] bos = str.getBytes();
			for (i = 0; i < bos.length; i++) {
				if (bos[i] == 0x0a)
					n++;
			}

			byte[] bos_new = new byte[bos.length + n];
			n = 0;
			for (i = 0; i < bos.length; i++) { // �ֻ��л���Ϊ0a,�����Ϊ0d
												// 0a���ٷ���
				if (bos[i] == 0x0a) {
					bos_new[n] = 0x0d;
					n++;
					bos_new[n] = 0x0a;
				} else {
					bos_new[n] = bos[i];
				}
				n++;
			}

			os.write(bos_new);

		} catch (IOException e) {

		}
		// �򿪽����߳�
		try {
			is = btSocket.getInputStream(); // �õ���������������
		} catch (IOException e) {
			return;
		}
		if (bThread == false) {
			ReadThread.start();
			bThread = true;
		} else {
			bRun = true;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		TestinAgent.onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
