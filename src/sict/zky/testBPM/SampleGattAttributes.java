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

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.iwit.bluetoothcommunication.util.encodeUtil;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

/**
 * This class includes a small subset of standard GATT attributes for
 * demonstration purposes.
 */
public class SampleGattAttributes
{
	// 注：下面的UUID是通过连接设备后打印所得
	/**
	 * 主服务uuid，gatt服务由BluetoothGattService类代表
	 */
	public static String GATT_SERVICE_PRIMARY = "00001000-0000-1000-8000-00805f9b34fb";
	/**
	 * 特性句柄由BluetoothGattCharacteristic类代表 发送数据的特性uuid
	 */
	public static String CHARACTERISTIC_WRITEABLE = "00001001-0000-1000-8000-00805f9b34fb";
	/**
	 * notify特性uuid
	 */
	public static String CHARACTERISTIC_NOTIFY = "00001002-0000-1000-8000-00805f9b34fb";
	public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
	/**
	 * 主动读取数据的特性uuid
	 */
	public static String CHARACTERISTIC_READABLE = "00001003-0000-1000-8000-00805f9b34fb";

	/**
	 * message必须为16进制数的字符串，每次最多17字节
	 * 
	 * @return
	 */
	public static boolean sendMessage(BluetoothGatt gatt, String message)
	{
		if (gatt == null || message == null || message.length() == 0)
			return false;

		// 未知uuid的情况下要通过gatt.getServices()循环遍历
		BluetoothGattService service = gatt.getService(UUID
				.fromString(GATT_SERVICE_PRIMARY));

		if (service == null)
			return false;

		// 获取writeable特性，未知uuid的情况下要通过service.getCharacteristics()循环遍历
		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(CHARACTERISTIC_WRITEABLE));

		if (characteristic == null)
			return false;

		byte[] data = encodeUtil.hexToBytes(message);
		// byte[] sendData = encodeUtil.encodeMessage(data);
		characteristic.setValue(data);
		// 写入方式要视情况而定
		characteristic
				.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
		return gatt.writeCharacteristic(characteristic);
	}

	/**
	 * notify特性即characteristic.getProperties() ==
	 * BluetoothGattCharacteristic.PROPERTY_NOTIFY
	 * 拿到notify特性，使能notify，（被动）实时接收从机数据
	 */
	public static boolean notify(BluetoothGatt gatt)
	{
		if (gatt == null)
			return false;

		BluetoothGattService service = gatt.getService(UUID
				.fromString(GATT_SERVICE_PRIMARY));

		if (service == null)
			return false;

		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(CHARACTERISTIC_NOTIFY));
		if (characteristic != null)
		{
			BluetoothGattDescriptor descriptor = characteristic
					.getDescriptor(UUID
							.fromString(CLIENT_CHARACTERISTIC_CONFIG));
			descriptor
					.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
			gatt.writeDescriptor(descriptor);
		}

		return gatt.setCharacteristicNotification(characteristic, true);
	}

	/**
	 * 拿到readable特性，（主动）读取从机数据
	 */
	public static boolean readMessage(BluetoothGatt gatt)
	{
		if (gatt == null)
			return false;

		BluetoothGattService service = gatt.getService(UUID
				.fromString(GATT_SERVICE_PRIMARY));

		if (service == null)
			return false;

		BluetoothGattCharacteristic characteristic = service
				.getCharacteristic(UUID.fromString(CHARACTERISTIC_READABLE));

		return gatt.readCharacteristic(characteristic);
	}

}
