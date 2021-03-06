/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sict.zky.testBPM;

import java.util.ArrayList;

import com.testin.agent.TestinAgent;

import sict.zky.deskclock.R;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class BGMDeviceScanActivity extends ListActivity
{
	
	private int userId;
	private String screenName;
	private String userName;
	private int thisposition;
	
	private LeDeviceListAdapter mLeDeviceListAdapter;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean mScanning;
	private Handler mHandler;

	private static final int REQUEST_ENABLE_BT = 1;
	// Stops scanning after 10 seconds.
	private static final long SCAN_PERIOD = 10000;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		//getActionBar().setTitle(R.string.title_devices);// 设置标题：ble device scan
		Intent intent =getIntent();
		userId=intent.getIntExtra("userId", 0);
	       
        screenName=intent.getStringExtra("screenName");
        userName=intent.getStringExtra("userName");
        thisposition=intent.getIntExtra("thisposition",0);
		
		mHandler = new Handler();

		Toast toast = Toast.makeText(this, "首先打开设备，搜索到设备后，在十秒内点击进入进行测量。",
				Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 10);
		toast.show();

		// Use this check to determine whether BLE is supported on the device.
		// Then you can
		// selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE))
		{
			Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		// Initializes a Bluetooth adapter. For API level 18 and above, get a
		// reference to
		// BluetoothAdapter through BluetoothManager.
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

	// 对右上角menu的初始化设置
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		if (!mScanning)
		{

			menu.findItem(R.id.menu_stop).setVisible(false);
			menu.findItem(R.id.menu_scan).setVisible(true);

			menu.findItem(R.id.menu_refresh).setActionView(null);
		} else
		{
			menu.findItem(R.id.menu_stop).setVisible(true);

			menu.findItem(R.id.menu_scan).setVisible(false);
			menu.findItem(R.id.menu_refresh).setActionView(
					R.layout.actionbar_indeterminate_progress);
		}
		return true;
	}

	// 对右上角menu的点击之后的动作
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_scan:
			mLeDeviceListAdapter.clear();
			scanLeDevice(true);
			break;
		case R.id.menu_stop:
			scanLeDevice(false);
			break;
		}
		return true;
	}

	// 从被覆盖到被active ，返回界面后，即搜索设备
	@Override
	protected void onResume()
	{
		super.onResume();

		// Ensures Bluetooth is enabled on the device. If Bluetooth is not
		// currently enabled,
		// fire an intent to display a dialog asking the user to grant
		// permission to enable it.
		if (!mBluetoothAdapter.isEnabled())
		{
			if (!mBluetoothAdapter.isEnabled())
			{
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}

		// Initializes list view adapter.
		mLeDeviceListAdapter = new LeDeviceListAdapter();
		setListAdapter(mLeDeviceListAdapter);
		scanLeDevice(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ENABLE_BT
				&& resultCode == Activity.RESULT_CANCELED)
		{
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		scanLeDevice(false);
		mLeDeviceListAdapter.clear();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
		if (device == null)
			return;
		final Intent intent = new Intent(this, StartTestBGMActivity.class);
		
		intent.putExtra("userId", userId);
		intent.putExtra("screenName", screenName);
		intent.putExtra("userName", userName);
		intent.putExtra("thisposition", thisposition);
		intent.putExtra(StartTestBGMActivity.EXTRAS_DEVICE_NAME,
				device.getName());
		intent.putExtra(StartTestBGMActivity.EXTRAS_DEVICE_ADDRESS,
				device.getAddress());
		
		
		if (mScanning)
		{
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mScanning = false;
		}
		startActivity(intent);
		finish();
	}

	// 搜索时间 10s，线程中进行搜索
	private void scanLeDevice(final boolean enable)
	{
		if (enable)
		{
			// Stops scanning after a pre-defined scan period.
			mHandler.postDelayed(new Runnable()
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
		} else
		{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
		}
		invalidateOptionsMenu();
	}

	// Adapter for holding devices found through scanning.
	private class LeDeviceListAdapter extends BaseAdapter
	{
		private ArrayList<BluetoothDevice> mLeDevices;
		private LayoutInflater mInflator;

		public LeDeviceListAdapter()
		{
			super();
			mLeDevices = new ArrayList<BluetoothDevice>();
			mInflator = BGMDeviceScanActivity.this.getLayoutInflater();
		}

		public void addDevice(BluetoothDevice device)
		{
			if (!mLeDevices.contains(device))
			{
				mLeDevices.add(device);
			}
		}

		public BluetoothDevice getDevice(int position)
		{
			return mLeDevices.get(position);
		}

		public void clear()
		{
			mLeDevices.clear();
		}


		public int getCount()
		{
			return mLeDevices.size();
		}

	
		public Object getItem(int i)
		{
			return mLeDevices.get(i);
		}

		
		public long getItemId(int i)
		{
			return i;
		}

	
		public View getView(int i, View view, ViewGroup viewGroup)
		{
			ViewHolder viewHolder;
			// General ListView optimization code.
			if (view == null)
			{
				view = mInflator.inflate(R.layout.listitem_device, null);
				viewHolder = new ViewHolder();
				viewHolder.deviceAddress = (TextView) view
						.findViewById(R.id.device_address);
				viewHolder.deviceName = (TextView) view
						.findViewById(R.id.device_name);
				view.setTag(viewHolder);
			} else
			{
				viewHolder = (ViewHolder) view.getTag();
			}

			BluetoothDevice device = mLeDevices.get(i);
			final String deviceName = device.getName();
			if (deviceName != null && deviceName.length() > 0)
				viewHolder.deviceName.setText(deviceName);
			else
				viewHolder.deviceName.setText(R.string.unknown_device);
			viewHolder.deviceAddress.setText(device.getAddress());

			return view;
		}
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
					mLeDeviceListAdapter.addDevice(device);
					mLeDeviceListAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	static class ViewHolder
	{
		TextView deviceName;
		TextView deviceAddress;
	}
}