package sict.zky.testBPM;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.iwit.bluetoothcommunication.util.encodeUtil;
import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.packet.Packet;
import sict.zky.service.Pc_bgdataService;
import sict.zky.service.Pc_dataService;
import sict.zky.service.Pc_userService;
import sict.zky.service.UserNameAdapter;
import sict.zky.utils.Config;
import sict.zky.utils.CurrentTime;
import sict.zky.utils.ExChange;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StartTestBGMActivity extends Activity
{

	// scan
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler scanHandler;	
	private TextView deviceAddress;
	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 50000;

	private String high;
	private String low;
	private String pul;

	private String thishigh;
	private String thislow;
	private String thispul;

	private String uploadTime;
	private int userId;
	private String userName = "";
	private String screenName_;
	private String GLU;
	private String ThisGLU;
	
	private String timeValue;
	private CurrentTime ct = new CurrentTime();

	private final static String TAG = StartTestBGMActivity.class
			.getSimpleName();

	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

	private TextView mConnectionState;
	private ImageView connection;
	private ImageView  level_image;

	private TextView SYSView;
	private TextView DIAView;
	private TextView PULSEView;
	private TextView TestTimeView;
	private TextView GLUView;
	private TextView GLUHistoryView;
	private TextView processPulseView;
	private ProgressBar progressBar;
	private ProgressDialog mDialog;

	private String mDeviceName;
	private String mDeviceAddress;
	private ExpandableListView mGattServicesList;
	private BluetoothLeService mBluetoothLeService;
	private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
	private boolean mConnected = false;
	private boolean thisTest = true;

	private final String LIST_NAME = "NAME";
	private final String LIST_UUID = "UUID";

	private BluetoothGatt mBluetoothGatt;

	private String sumString = "";
	private String checkString = "";
	private String checkString1 = "";
	private String informationString = "";
	private String startString = "";
	private String resultString = "";
	private String replyPacket = "";
	private String resultofMsg = "";
	private String errorString = "";
	private Double[] historyGLUValue = new Double[20];
	private String[] historyGLUTime = new String[20];
	private int[] historySYSValue = new int[20];
	private int[] historyDIAValue = new int[20];
	private int[] historyPULSEValue = new int[20];
	private String[] historyBPMTime = new String[20];

	private int countBPMResult = 0; // ��¼û���ϴ����ֻ���bpm���������
	private int countBGMResult = 0; // ��¼û���ϴ����ֻ���bgm���������

	private int thisposition = 0;

	private boolean haveinformation = false;

	private int count = -1;

	private String a[];

	private CurrentTime currentTime = new CurrentTime();
	private Packet pk = new Packet();
	private ExChange ex = new ExChange();
	private Spinner starttestbgm_spinner;

	private String getuserName;
	private List<String> getuserNamebyuserId;

	private Pc_data pc_data;
	private Pc_dataService pc_dataService;
	private Pc_bgdata pc_bgdata;
	private Pc_bgdataService pc_bgdataService;
	private Pc_userService pc_userService;

	private int familyMember_;
	private String familyRole;
	private UserNameAdapter userNameAdapter;

	// �������㷨
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(android.os.Message msg)
		{
			if (!errorString.equals(""))
			{

			}

			// �������Ϣ��������05Ӧ���
			if (pk.isInformationPacket(informationString))
			{

				count = -1;
				SampleGattAttributes.sendMessage(mBluetoothGatt, replyPacket); // ����Ӧ���
			}

			// ����ǿ�ʼ��������05Ӧ���
			if (pk.isStartPacket(startString))
			{
				count = 0;
				SampleGattAttributes.sendMessage(mBluetoothGatt, replyPacket);
			}
			
			
			
			
			
			// ����ǹ��̰�������������ֵ�����ҽ�����ֵ�ڽ������ж�̬��ʾ

			if(resultofMsg.length() > 16)
			{
				
		
			if (pk.isProcessPacket(resultofMsg))
				{
					resultofMsg = resultofMsg.substring(6, 16);//���������ܻ�����Խ�磻������
					String s1 = resultofMsg.substring(6, 7);
					String s2 = resultofMsg.substring(7, 8);
					String s3 = resultofMsg.substring(8, 9);
					String s4 = resultofMsg.substring(9, 10);

					String Presure = s3 + s4 + s1 + s2;
					String pre = Integer.toString(Integer.parseInt(Presure, 16));

					int pre1 = Integer.parseInt(pre);

					progressBar.setMax(250);// ���������̶�200
					if(pre1<=250)
					{
						progressBar.setProgress(pre1);// ������ֵ��ʾ�ڽ�������
						processPulseView.setText(pre);
					}
					

				}
			}
			

			// �����BPM�����������05Ӧ��������ҽ������õ� ��ѹ����ѹ������ ���ֵ
			if(resultString.length()>6)
			{
				if (pk.isBPMResultPacket(resultString))
				{
					countBPMResult++;

					SampleGattAttributes.sendMessage(mBluetoothGatt, replyPacket);
					String str = resultString.substring(16, 24);// ��ѹ����ѹ��������Ϣ
					String testTime = resultString.substring(6, 16);

					// ����ʱ��
					String year = Integer.toString(Integer.parseInt(
							testTime.substring(0, 2), 16));
					String month = Integer.toString(Integer.parseInt(
							testTime.substring(2, 4), 16));
					String day = Integer.toString(Integer.parseInt(
							testTime.substring(4, 6), 16));
					String hour = Integer.toString(Integer.parseInt(
							testTime.substring(6, 8), 16));
					String minute = Integer.toString(Integer.parseInt(
							testTime.substring(8, 10), 16));
					 timeValue =  year + "-" + month + "-" + day + " "
							+ hour + ":" + minute + ":" + "00";// ���صĲ���ʱ��

					String s1 = str.substring(0, 1);
					String s2 = str.substring(1, 2);
					String s3 = str.substring(2, 3);
					String s4 = str.substring(3, 4);
					String s5 = str.substring(4, 5);
					String s6 = str.substring(5, 6);
					String s7 = str.substring(6, 7);
					String s8 = str.substring(7, 8);

					String highPresure = s3 + s4 + s1 + s2;
					String lowPresure = s5 + s6;
					String pulse = s7 + s8;

					high = Integer.toString(Integer.parseInt(highPresure, 16));
					low = Integer.toString(Integer.parseInt(lowPresure, 16));
					pul = Integer.toString(Integer.parseInt(pulse, 16));

//					if (thisTest)
//					{
//						
////						SYSView.setText("��ѹ��" + high);
////						DIAView.setText("��ѹ��" + low);
//						if(Integer.valueOf(pul)<100)
//						{
//							PULSEView.setText("������" + pul);
//							PULSEView.setTextColor(0xFF81C53F);
//						}
//						if((Integer.valueOf(pul)>=100))
//						{
//							PULSEView.setText("������" + pul);
//							PULSEView.setTextColor(0xFFB41132);
//						}
//						
//						// TestTimeView.setText("��ǰ����ʱ�䣺" + timeValue);
//						thishigh = high;
//						thislow = low;
//						thispul = pul;
//						
//						//�ı�ͼƬ������������ɫ
//						AnalyseBPMResult analyresulut = new AnalyseBPMResult();
//						int  level = analyresulut.analyse_bpm_result(Integer.valueOf(thishigh), Integer.valueOf(thislow));
//						result(level,thishigh,thislow);
//						
//						
//						
//						
//						
//
//						thisTest = false;
//						if(userId > 0 )
//						{
//							AlertDialog.Builder dialog = new AlertDialog.Builder(
//									StartTestBGMActivity.this);
//							dialog.setTitle("�������")							
//									.setMessage(
//											"	��ѹ��" + high + "\n" + "	��ѹ��" + low + "\n"
//													+ "	������" + pul + "\n")
//									.setPositiveButton("�ϴ���������",
//											new DialogInterface.OnClickListener()
//											{
//
//												public void onClick(
//														DialogInterface dialog,
//														int which)
//												{
//													ct.getCurrentTime();
//													uploadTime = ct.TimeToString();
//
//													mDialog = new ProgressDialog(
//															StartTestBGMActivity.this);
//													mDialog.setTitle("�ύ");
//													mDialog.setMessage("�����ύ�����Ժ�...");
//													mDialog.show();
//													Thread update_pc_dataThread = new Thread(
//															new Update_pc_dataThread());
//													update_pc_dataThread.start();
//												}
//											})
//									.setNegativeButton("ȷ��",
//											new DialogInterface.OnClickListener()
//											{
//
//												public void onClick(
//														DialogInterface dialog,
//														int which)
//												{
//
//													dialog.cancel();// ȡ��������
//												}
//											}).create().show();
//						}
//
//						
//
//					} else
//					{
//
//						historySYSValue[countBPMResult] = Integer.valueOf(high);// ÿ�ΰ�ֵд��historySYSValue����
//						historyDIAValue[countBPMResult] = Integer.valueOf(low);// ÿ�ΰ�ֵд��historyDIAValue����
//						historyPULSEValue[countBPMResult] = Integer.valueOf(pul);// ÿ�ΰ�ֵд��historyPULSEValue����
//						historyBPMTime[countBPMResult] = timeValue; // ÿ�ΰ�ֵд��historyGLUTime����
//
//						// ��ʷ���ݱ��浽���أ�uername�� familymember �ǹ̶��ġ�������
//						pc_data = new Pc_data(userId,
//								historySYSValue[countBPMResult],
//								historyDIAValue[countBPMResult],
//								historyPULSEValue[countBPMResult],
//								historyBPMTime[countBPMResult], userName,
//								screenName_, familyMember_);
//						pc_dataService.insert(pc_data);
//
//						// ��ʷ����Ĭ���ϴ�������,????�ϴ���ʱ�������⣬Ӧ����timevalue����������������
//						Thread update_history_pc_bgdataThread = new Thread(
//								new Update_history_pc_dataThread());
//						update_history_pc_bgdataThread.start();
//
//					}
//
				}
				
			}


			
			// �����BGM�����������05Ӧ��������ҽ������õ�Ѫ��ֵ
			// ��ʷ��¼bugδ���
			if (pk.isBGMResultPacket(resultString))
			{
				countBGMResult++;
				// SampleGattAttributes.sendMessage(mBluetoothGatt, edt_message
				// .getText().toString());
				SampleGattAttributes.sendMessage(mBluetoothGatt, replyPacket);

				String str = resultString.substring(18, 20);
				
				double glu = Math.round(Integer.parseInt(str, 16)/18.0*10)/10.0;				
				GLU = Double.toString(glu);
				
				

				String testTime = resultString.substring(6, 16);

				String year = Integer.toString(Integer.parseInt(
						testTime.substring(0, 2), 16));
				String month = Integer.toString(Integer.parseInt(
						testTime.substring(2, 4), 16));
				String day = Integer.toString(Integer.parseInt(
						testTime.substring(4, 6), 16));
				String hour = Integer.toString(Integer.parseInt(
						testTime.substring(6, 8), 16));
				String minute = Integer.toString(Integer.parseInt(
						testTime.substring(8, 10), 16));

				timeValue = year + "-" + month + "-" + day + " "
						+ hour + ":" + minute + ":" + "00";// ���صĲ���ʱ��

				if (thisTest)
				{
					ThisGLU = GLU;
					GLUView.setText("Ѫ�� : " + GLU);
					//SYSView.setText("Ѫ�ǣ�" + timeValue);
					thisTest = false;
				

					AlertDialog.Builder dialog = new AlertDialog.Builder(
							StartTestBGMActivity.this);
					dialog.setTitle("�������")							
							.setMessage("   Ѫ�ǣ�   " + GLU)
							
							.setNegativeButton("���ϴ�",
									new DialogInterface.OnClickListener()
									{

										public void onClick(
												DialogInterface dialog,
												int which)
										{

											dialog.cancel();// ȡ��������
										}
									})
									.setPositiveButton("�ϴ�",
									new DialogInterface.OnClickListener()
									{

										public void onClick(
												DialogInterface dialog,
												int which)
										{
											ct.getCurrentTime();
											uploadTime = ct.TimeToString();

											mDialog = new ProgressDialog(
													StartTestBGMActivity.this);
											mDialog.setTitle("�ύ");
											mDialog.setMessage("�����ύ�����Ժ�...");
											mDialog.show();

											Thread update_pc_bgdataThread = new Thread(
													new Update_pc_bgdataThread());
											update_pc_bgdataThread.start();

										}
									}).create().show();
				} else
				{
					historyGLUValue[countBPMResult] = Double.valueOf(GLU);// ÿ�ΰ�ֵд��historyGLUValue����

					historyGLUTime[countBPMResult] = timeValue; // ÿ�ΰ�ֵд��historyGLUTime����

					// ��ʷ���ݱ��浽���أ�uername�� familymember �ǹ̶��ġ�������
					pc_bgdata = new Pc_bgdata(userId,
							historyGLUValue[countBPMResult],
							historyGLUTime[countBPMResult], userName,
							screenName_, familyMember_,0,0);//0����ȫ������Ĭ��д����������Ҫ��ʱ���ٸ�
					pc_bgdataService.insert(pc_bgdata);

					// Ĭ���ϴ���ʷ��ûʵ��

					Thread update_history_pc_bgdataThread = new Thread(
							new Update_history_pc_bgdataThread());
					update_history_pc_bgdataThread.start();

				}

			}

		}

		
			
			
			
		

	};

	// Code to manage Service lifecycle. ������������
	private final ServiceConnection mServiceConnection = new ServiceConnection()
	{
		// �����Ѿ�����
	
		public void onServiceConnected(ComponentName componentName,
				IBinder service)
		{
			mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
					.getService();

			if (!mBluetoothLeService.initialize())
			{
				Toast.makeText(getApplicationContext(),
						"Unable to initialize Bluetooth",1).show();
				finish();// ����activity
			}
			// ���ӵ��豸��ַ����������
			if (mBluetoothLeService.connect(mDeviceAddress))
			{
				mBluetoothGatt = mBluetoothLeService.getGatt();
				
			}
		}

		// �����жΣ�ֹͣ����

		public void onServiceDisconnected(ComponentName componentName)
		{
			mBluetoothLeService = null;
			
			
		}
	};

	// ���չ㲥��Ϣ
	private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			
			
//			Toast.makeText(getApplicationContext(), action, 0).show();
			// ��������������ӣ�����״̬Ϊ���ӣ�����ˢ��
			if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action))
			{
				mConnected = true;

//				connection.setImageDrawable(getResources().getDrawable(
//						R.drawable.already_connect));
				// updateConnectionState(R.string.connected);//
				// ��ʾstate��connected

				// invalidateOptionsMenu();// ˢ��ѡ��˵�����

			}
			// �����������û�����ӣ���������״̬Ϊ�Ͽ�������ˢ��
			else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action))
			{
				mConnected = false;
				
				
				//�Ͽ�����
				mBluetoothLeService.disconnect();
				
				
				
				//�Ͽ�����
				
//			    mBluetoothGatt.close();
//			    mBluetoothGatt = null;

				connection.setImageDrawable(getResources().getDrawable(
						R.drawable.no_connect));
				
				
				//mBluetoothLeService.disconnect();
				// updateConnectionState(R.string.disconnected);//
				// ��ʾstate��disconnected
				// invalidateOptionsMenu();
				// clearUI();
				
				
				
				// ���뼴��ʼ�����豸��50s
				if (!mBluetoothAdapter.isEnabled())
				{
					if (!mBluetoothAdapter.isEnabled())
					{
						Intent enableBtIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}
				}
				scanLeDevice(true);
				
				
				
				
				
			}
			// ����������񱻷��֣���ʾ����uuid
			else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
					.equals(action))
			{
				// ui��ʾ���еķ��������uuid
				if (mBluetoothGatt != null)
				{
					// displayGattServices(mBluetoothGatt.getServices());
				}

			}

			// ��������������ݿ��ã������豸�㲥���������ݣ����д���
			else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action))
			{
				byte[] notify = intent
						.getByteArrayExtra(BluetoothLeService.EXTRA_NOTIFY_DATA);
				
				

				// ͨ��message���ͳ�һ�������İ�����handler��������
				if (notify != null)
				{

					
					count++;
					// �����������յ�����
					StringBuilder builder = new StringBuilder(notify.length);
					StringBuilder builder1 = new StringBuilder(notify.length);
					for (byte b : notify)
					{
						builder.append(String.format("%02X ", b));
						builder1.append(String.format("%02X", b));

					}
					checkString1 += builder1.toString();
					checkString += builder1.toString();

					// �ж��Ƿ��ǹ��̰����õ����̰�����ֵ
					int ii = checkString1.indexOf("550802");
					if (ii != -1)
					{
						checkString1 = checkString1.substring(ii);// �ӹ��̰���ͷ���գ���֤���ݰ��ǹ��̰�
						haveinformation = true;
					}
					// ���۹�����һ�����̰�����ȡһ���������̰�
					if (haveinformation && checkString1.length() > 16)
					{
						resultofMsg = checkString1.substring(0, 16) + " ,";
						checkString1 = checkString1.substring(16);// ���̰�
					}

					// �������16�ֽڣ�˵������Ϣ��
					if (builder1.toString().length() == 32)
					{
						informationString = builder1.toString();

//						Toast.makeText(getApplicationContext(),
//								informationString, 1).show();
						
						
			

					} else
					{
						informationString = "Itisnotinf!";
					}

					// �������13��˵���ǣ�������
					if (builder1.toString().length() == 26)
					{
						errorString = builder1.toString();
					}

					// �������6��˵���ǿ�ʼ��
					if (builder1.toString().length() == 12)
					{
						startString = builder1.toString();
					} else
					{
						startString = "Itisnotsta!";
					}
					resultString = builder1.toString();

					mHandler.sendEmptyMessage(0);

				}
				// ��ʱ���� else
				else
				{
					// ������������������
					byte[] read = intent
							.getByteArrayExtra(BluetoothLeService.EXTRA_READ_DATA);
					String s2 = encodeUtil.decodeMessage(read);
					StringBuilder builder2 = new StringBuilder(read.length);
					for (byte b : read)
					{
						builder2.append(String.format("%02X ", b));
					}

				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);		
		setContentView(R.layout.starttestbgmactivity);

		// scan
		scanHandler = new Handler();

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE))
		{
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

		// Checks if Bluetooth is supported on the device.
		if (mBluetoothAdapter == null)
		{
			Toast.makeText(this, R.string.error_bluetooth_not_supported,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}

		Intent intent = getIntent();

		userId = intent.getIntExtra("userId", 0);
		screenName_ = intent.getStringExtra("screenName");
		familyMember_ = 0; 
		
		

//		thisposition = intent.getIntExtra("thisposition", 0);

		starttestbgm_spinner = (Spinner) findViewById(R.id.starttestbgm_spinner);

		connection = (ImageView) findViewById(R.id.showconnection);

		pc_userService = new Pc_userService(getApplicationContext());
		pc_dataService = new Pc_dataService(getApplicationContext());
		pc_bgdataService = new Pc_bgdataService(getApplicationContext());
		getuserNamebyuserId = pc_userService.getuserName(userId);
		userNameAdapter = new UserNameAdapter(this, getuserNamebyuserId);
		// // time_spinner.setAdapter(null);
		starttestbgm_spinner.setAdapter(userNameAdapter);
		starttestbgm_spinner.setSelection(thisposition, true);
		// starttestbgm_spinner.
		starttestbgm_spinner.setOnItemSelectedListener(new OnItemSelected());

		userName = (String) starttestbgm_spinner
				.getItemAtPosition(thisposition);

		// �õ��豸����
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
		// �õ��豸��ַ
		mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

		

		// getActionBar().setTitle(mDeviceName);// ��������Ϊ�豸����
		// getActionBar().setDisplayHomeAsUpEnabled(true);//
		// �����Ͻ�ͼ�����߼���һ�����ص�ͼ�꣬���Է��ص��ϸ�����
		Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
		bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE); // ����������
		currentTime.getCurrentTime(); // �õ�����ʱ��
		currentTime.TimeToHex();// ת����05����ʽ
		replyPacket = pk.ReplyPacket(); // 05Ӧ���

		SYSView = (TextView) findViewById(R.id.SYSView);
		DIAView = (TextView) findViewById(R.id.DIAView);
		// TestTimeView = (TextView) findViewById(R.id.TestTimeView);
		PULSEView = (TextView) findViewById(R.id.PULSEView);
		GLUView = (TextView) findViewById(R.id.GLUView);
		// GLUHistoryView = (TextView) findViewById(R.id.GLUHistoryView);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		
		processPulseView = (TextView) findViewById(R.id.processPluseView);
		
		level_image = (ImageView) findViewById(R.id.level_image);

		// ���뼴��ʼ�����豸��50s
		if (!mBluetoothAdapter.isEnabled())
		{
			if (!mBluetoothAdapter.isEnabled())
			{
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
		scanLeDevice(true);

	}
	
	


	private final class OnItemSelected implements OnItemSelectedListener
	{

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id)
		{
			getuserName = parent.getItemAtPosition(position).toString();
			userName = getuserName;
//			Toast.makeText(getApplicationContext(),
//					userName + " , " + position, 1).show();
			familyMember_ = pc_userService
					.getfamilyMemberbyuserIdanduserName(userId,getuserName);
			familyRole=pc_userService.getfamilyRolebyuserIdanduserName(userId, getuserName);
			thisposition = position;
		}

		
		public void onNothingSelected(AdapterView<?> parent)
		{
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();

//		if (!mBluetoothAdapter.isEnabled())
//		{
//			if (!mBluetoothAdapter.isEnabled())
//			{
//				Intent enableBtIntent = new Intent(
//						BluetoothAdapter.ACTION_REQUEST_ENABLE);
//				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//			}
//		}
//		scanLeDevice(true);

		registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
		if (mBluetoothLeService != null)
		{
			final boolean result = mBluetoothLeService.connect(mDeviceAddress);
		}
	}

	// scan
	private void scanLeDevice(final boolean enable)
	{
		if (enable)
		{
			// Stops scanning after a pre-defined scan period.
			scanHandler.postDelayed(new Runnable()
			{
			
				public void run()
				{
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					invalidateOptionsMenu();
				}
			}, SCAN_PERIOD);

			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			
			
			
			connection.setImageDrawable(getResources().getDrawable(
					 R.drawable.searching));
			
			
		} else
		{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback()
	{


		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord)
		{
			runOnUiThread(new Runnable()
			{
		
				public void run()
				{
					mDeviceAddress = device.getAddress();

					
					if (!mDeviceAddress.equals("") && device.getName().indexOf("Bioland-BGM")>=0)
						
						//if (!mDeviceAddress.equals("") )
					{
						if (mScanning)
						{
							mBluetoothAdapter.stopLeScan(mLeScanCallback);
							mScanning = false;
						}
						if(mBluetoothLeService!=null)
						{
							mBluetoothLeService.connect(mDeviceAddress);
							mBluetoothGatt = mBluetoothLeService.getGatt();
							
							connection.setImageDrawable(getResources().getDrawable(
									R.drawable.already_connect));
							
							
							
							
						}
						

					}
					


//					Toast.makeText(
//							getApplicationContext(),
//							"name" + device.getName() + device.getAddress()
//									+ "name", 1).show();
					

				}
			});
		}
	};

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mGattUpdateReceiver);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//unbindService(mServiceConnection);
		mBluetoothLeService = null;
	}
	
	



	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		super.onBackPressed();
		//unregisterReceiver(mGattUpdateReceiver);
		unbindService(mServiceConnection);
		mBluetoothLeService = null;
		
		if (mBluetoothGatt == null) {
	        return;
	    }
	    mBluetoothGatt.close();
	    mBluetoothGatt = null;
	}


	
//	// ��menu��ѡ���,���ѡ��connet���������������dis���Ͽ�������
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item)
//	{
//		switch (item.getItemId())
//		{
//		case R.id.menu_connect:
//			mBluetoothLeService.connect(mDeviceAddress);
//			return true;
//		case R.id.menu_disconnect:
//			mBluetoothLeService.disconnect();
//			return true;
//		case android.R.id.home:
//			onBackPressed();
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

//	// ��������״̬
//	private void updateConnectionState(final int resourceId)
//	{
//		runOnUiThread(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//
//				mConnectionState.setText(resourceId);
//			}
//		});
//	}

	// Demonstrates how to iterate through the supported GATT
	// Services/Characteristics.
	// In this sample, we populate the data structure that is bound to the
	// ExpandableListView
	// on the UI.
	// ��ʾservice��listview
//	private void displayGattServices(List<BluetoothGattService> gattServices)
//	{
//		if (gattServices == null)
//			return;
//		String uuid = null;
//		String unknownServiceString = getResources().getString(
//				R.string.unknown_service);
//		String unknownCharaString = getResources().getString(
//				R.string.unknown_characteristic);
//		ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
//		ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
//		mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
//
//		SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//				this, gattServiceData,
//				android.R.layout.simple_expandable_list_item_2, new String[] {
//						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
//						android.R.id.text2 }, gattCharacteristicData,
//				android.R.layout.simple_expandable_list_item_2, new String[] {
//						LIST_NAME, LIST_UUID }, new int[] { android.R.id.text1,
//						android.R.id.text2 });
//		mGattServicesList.setAdapter(gattServiceAdapter);
//	}

	private static IntentFilter makeGattUpdateIntentFilter()
	{
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
		intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
		intentFilter
				.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
		intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
		return intentFilter;
	}

	// ble

	// @Override
	// public void onClick(View v)
	// {
	//
	// }
	Handler bphistoryhandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				// mDialog.cancel();
				Toast.makeText(StartTestBGMActivity.this, "�ϴ��ɹ���",
						Toast.LENGTH_SHORT).show();

				// userName.setText(userName_);
				// bloodGlucose.setText(bloodGlucose_);

				// familyMember.setText(String.valueOf(familyMember_));

				break;
			case 1:
				// mDialog.cancel();
				Toast.makeText(getApplicationContext(), "�ϴ�ʧ��",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Update_history_pc_dataThread implements Runnable
	{

		
		public void run()
		{

			String str = update_history_pc_dataServer(userId,
					historySYSValue[countBPMResult],
					historyDIAValue[countBPMResult],
					historyPULSEValue[countBPMResult],0,
					historyBPMTime[countBPMResult], 0,screenName_, screenName_, 0);
			Message msg = bphistoryhandler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success"))
			{
				msg.what = 0;
				bphistoryhandler.sendMessage(msg);
			} else
			{

				msg.what = 1;
				bphistoryhandler.sendMessage(msg);
			}

		}

	}

	private String update_history_pc_dataServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse,int oxygen, String uploadTime,
			int uploadType,String userName, String screenName, int familyMember)
	{
		String str = "";
		try
		{
			
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Upload_PC_DataServlet");
			// URL url = new
			// URL("http://121.42.32.103:80/ZKYweb/Upload_PC_DataServlet");

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
			obj.put("systolicPressure", systolicPressure);
			obj.put("diastolicPressure", diastolicPressure);
			obj.put("pulse", pulse);
			obj.put("uploadTime", uploadTime);
			obj.put("userName", userName);
			obj.put("screenName", screenName);
			obj.put("familyMember", familyMember);
			obj.put("oxygen", oxygen);
			obj.put("uploadType", uploadType);
			
			array.put(obj);

			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				mDialog.cancel();

				Toast.makeText(StartTestBGMActivity.this, "�ϴ��ɹ���",
						Toast.LENGTH_SHORT).show();
				
				
				Pc_data pc_data1 = new Pc_data(userId, Integer.parseInt(high),
						Integer.parseInt(low), Integer.parseInt(pul),
						timeValue, userName, screenName_, familyMember_  , 1);
				pc_dataService.insert(pc_data1);
				
				
				

				

				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "�ϴ�ʧ��",
						Toast.LENGTH_SHORT).show();
				
				Pc_data pc_data2 = new Pc_data(userId, Integer.parseInt(high),
						Integer.parseInt(low), Integer.parseInt(pul),
						timeValue, userName, screenName_, familyMember_  , 0);
				pc_dataService.insert(pc_data2);
				break;

			}

		}
	};

	class Update_pc_dataThread implements Runnable
	{

		
		public void run()
		{
			String str = "";

			 str = update_pc_dataServer(userId,
					Integer.parseInt(thishigh), Integer.parseInt(thislow),
					Integer.parseInt(thispul),0, uploadTime, 0,userName,
					screenName_, familyMember_ );
			Message msg = handler.obtainMessage();
			// if (str.trim().equals("success")) {
			//if (str.equals("success"))
			
				if (str.equals("success"))
				{
					msg.what = 0;
					handler.sendMessage(msg);
				} else
				{

					msg.what = 1;
					handler.sendMessage(msg);
				}
			
			
			

		}

	}

	private String update_pc_dataServer(int userId, int systolicPressure,
			int diastolicPressure, int pulse,int oxygen, String uploadTime,
			int uploadType,String userName, String screenName, int familyMember)
	{
		String str = "";
		try
		{
			
			URL url = new URL(Config.IPaddress
					+ "/ZKYweb/Upload_PC_DataServlet");
			// URL url = new
			// URL("http://121.42.32.103:80/ZKYweb/Upload_PC_DataServlet");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			PrintWriter pw = new PrintWriter(conn.getOutputStream());
			
			JSONArray array =new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("systolicPressure", systolicPressure);
			obj.put("diastolicPressure", diastolicPressure);
			obj.put("pulse", pulse);
			obj.put("uploadTime", uploadTime);
			obj.put("userName", userName);
			obj.put("screenName", screenName);
			obj.put("familyMember", familyMember);
			obj.put("oxygen", oxygen);
			obj.put("uploadType", uploadType);

			array.put(obj);
			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	Handler bghandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				mDialog.cancel();
				Toast.makeText(StartTestBGMActivity.this, "�ϴ��ɹ���",
						Toast.LENGTH_SHORT).show();
				
				Pc_bgdata pc_bgdata1 = new Pc_bgdata(userId, Double.valueOf(ThisGLU),
						timeValue, userName, screenName_, familyMember_ , 1,0);	//1�����������ϴ���0����Ѫ��������ȫ��				
				pc_bgdataService.insert(pc_bgdata1);

				

				break;
			case 1:
				mDialog.cancel();
				Toast.makeText(getApplicationContext(), "�ϴ�ʧ��",
						Toast.LENGTH_SHORT).show();
				
				Pc_bgdata pc_bgdata2 = new Pc_bgdata(userId, Double.valueOf(ThisGLU),
						timeValue, userName, screenName_, familyMember_,0,0);					
				pc_bgdataService.insert(pc_bgdata2);
				break;

			}

		}
	};

	class Update_pc_bgdataThread implements Runnable
	{

		public void run()
		{

			// String str =
			// update_pc_bgdataServer(userId,GLU,uploadTime,userName,screenName_,familyMember_);
			String str = update_pc_bgdataServer(userId, ThisGLU, uploadTime,0,
					userName, screenName_, familyMember_ );

			Message msg = bghandler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success"))
			{
				msg.what = 0;
				bghandler.sendMessage(msg);
			} else
			{

				msg.what = 1;
				bghandler.sendMessage(msg);
			}

		}

	}

	private String update_pc_bgdataServer(int userId, String bloodGlucose,
			String uploadTime, int uploadType ,String userName, String screenName,
			int familyMember)
	{
		
		
		String str = "";
		try
		{
			
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
			
			array.put(obj);

			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	Handler bghistoryhandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0:
				// mDialog.cancel();
				Toast.makeText(StartTestBGMActivity.this, "�ϴ��ɹ���",
						Toast.LENGTH_SHORT).show();

				// userName.setText(userName_);
				// bloodGlucose.setText(bloodGlucose_);

				// familyMember.setText(String.valueOf(familyMember_));

				break;
			case 1:
				// mDialog.cancel();
				Toast.makeText(getApplicationContext(), "�ϴ�ʧ��",
						Toast.LENGTH_SHORT).show();
				break;

			}

		}
	};

	class Update_history_pc_bgdataThread implements Runnable
	{

		public void run()
		{

			// String str =
			// update_pc_bgdataServer(userId,GLU,uploadTime,userName,screenName_,familyMember_);

			// ��ʷ��¼һ�ɱ�����Ĭ���û���
			String str = update_history_pc_bgdataServer(userId,
					String.valueOf(historyGLUValue[countBPMResult]),
					historyGLUTime[countBPMResult], 0, screenName_, screenName_ ,familyMember_);

			Message msg = bghistoryhandler.obtainMessage();
			// if (str.trim().equals("success")) {
			if (str.equals("success"))
			{
				msg.what = 0;
				bghistoryhandler.sendMessage(msg);
			} else
			{

				msg.what = 1;
				bghistoryhandler.sendMessage(msg);
			}

		}

	}

	private String update_history_pc_bgdataServer(int userId,
			String bloodGlucose, String uploadTime,int uploadType, String userName,
			String screenName, int familyMember)
	{
		String str = "";
		try
		{
			
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

			
			JSONArray  array = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("userId", userId);
			obj.put("userName", userName);
			obj.put("screenName", screenName);
			obj.put("uploadTime", uploadTime);
			obj.put("familyMember", familyMember);
			obj.put("bloodGlucose", bloodGlucose);
            obj.put("uploadType", uploadType);
			
			array.put(obj);

			pw.write(array.toString());
			pw.flush();
			pw.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			str = br.readLine();

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return str;
	}

	@Override
    protected void onStart() {
          super.onStart();
          TestinAgent.onStart(this);
}
	@Override
	protected void onStop() {
		
		super.onStop();
		mBluetoothLeService = null;
		
		TestinAgent.onStop(this);
	}

}
