package sict.zky.service;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import sict.zky.domain.Pc_data;

public class Pc_dataService {
	private DBOpenHelper dbOpenHelper;

	public Pc_dataService(Context context) {

		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void insert(Pc_data pc_data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sq1 = "insert into pc_data(userId,systolicPressure,diastolicPressure,pulse,uploadTime,userName,screenName,familyMember,upload) values(?,?,?,?,?,?,?,?,?)";
		db.execSQL(
				sq1,
				new Object[] { pc_data.getUserId(),
						pc_data.getSystolicPressure(),
						pc_data.getDiastolicPressure(), pc_data.getPulse(),
						pc_data.getUploadTime(), pc_data.getUserName(),
						pc_data.getScreenName(), pc_data.getFamilyMember(),
						pc_data.getUpload() });
		db.close();
	}

	public void delete(Integer id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_data where id=?";
		db.execSQL(sql, new Object[] { id });
	}

	public void update(Pc_data pc_data) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_data set userId=?,systolicPressure=?,diastolicPressure=?pulse=?,uploadTime=?,userName=?,screenName=?,familyMember=?";
		db.execSQL(
				sql,
				new Object[] { pc_data.getUserId(),
						pc_data.getSystolicPressure(),
						pc_data.getDiastolicPressure(), pc_data.getPulse(),
						pc_data.getUploadTime(), pc_data.getUserName(),
						pc_data.getScreenName(), pc_data.getFamilyMember() });
		db.close();

	}

	public Pc_data find(Integer id1) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { id1.toString() });
		if (cursor.moveToFirst()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			String screenName = cursor.getString(cursor
					.getColumnIndex("screenName"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			return new Pc_data(id, userId, systolicPressure, diastolicPressure,
					pulse, uploadTime, userName, screenName, familyMember);
		}

		db.close();
		cursor.close();

		return null;
	}

	// public boolean ishavepc_data(String userName){
	//
	// SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
	// String sql="select * from pc_data where userName=?";
	// Cursor cursor = db.rawQuery(sql, new String[] {userName});
	// if(cursor.moveToFirst()){
	// db.close();
	// cursor.close();
	//
	// return true;
	// }
	//
	// db.close();
	// cursor.close();
	//
	// return false;
	// }

	public boolean ishavepc_databyuserId(int userId) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select count(*) from pc_data where userId=" + userId;
		Cursor cursor = db.rawQuery(sql, new String[] {});
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		if (result < 10) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	public boolean ishavepc_data(int userId, String userName,
			int systolicPressure, int diastolicPressure, int pulse,
			String uploadTime) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userId=? and userName=? and systolicPressure=? and diastolicPressure=? and pulse=? and uploadTime=?";
		Cursor cursor = db.rawQuery(
				sql,
				new String[] { String.valueOf(userId), userName,
						String.valueOf(systolicPressure),
						String.valueOf(diastolicPressure),
						String.valueOf(pulse), uploadTime });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	// 判断是否有这条记录
	public boolean ishavepc_data(int userId, int systolicPressure,
			int diastolicPressure, int pulse, String uploadTime) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userId=?  and systolicPressure=? and diastolicPressure=? and pulse=? and uploadTime=?";
		Cursor cursor = db.rawQuery(
				sql,
				new String[] { String.valueOf(userId),
						String.valueOf(systolicPressure),
						String.valueOf(diastolicPressure),
						String.valueOf(pulse), uploadTime });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	public List<String> getuserName(int userId) {
		List<String> userNames = new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select Distinct(userName) from pc_data where userId=?";
		Cursor cursor = db.rawQuery(sql,
				new String[] { String.valueOf(userId) });
		while (cursor.moveToNext()) {

			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			userNames.add(userName);
		}

		db.close();
		cursor.close();

		return userNames;

	}

	public List<Pc_data> getScrollData(int offset, int maxResult) {
		List<Pc_data> pc_data = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from pc_data order by id asc limit ?,?",
				new String[] { String.valueOf(offset),
						String.valueOf(maxResult) });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			String screenName = cursor.getString(cursor
					.getColumnIndex("screenName"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			pc_data.add(new Pc_data(id, userId, systolicPressure,
					diastolicPressure, pulse, uploadTime, userName, screenName,
					familyMember));
		}
		return pc_data;

	}

	public List<Pc_data> selectOther() {// 从数据库中读取数据用于画图
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_datas;
	}

	public List<Pc_data> selectDatabynameandtimerange(String userName,
			String startTime, String endTime) {// 通过userName,从数据库中读取数据用于画图
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userName=? and uploadTime >? and uploadTime<? "
				+ "and systolicPressure<250 and systolicPressure>50 "
				+ "and diastolicPressure<250 and diastolicPressure>50 "
				+ "and pulse<250 and pulse>50 "
				+ "order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName, startTime,
				endTime });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_datas;
	}
	
	public List<Pc_data> selectDatabynameandtimerangeandtype(String userName,
			String startTime, String endTime,int type) {// 通过userName,从数据库中读取数据用于画图
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userName=? and uploadTime >? and uploadTime<? "
				+ "and type="+type
				+ " and systolicPressure<250 and systolicPressure>50 "
				+ "and diastolicPressure<250 and diastolicPressure>50 "
				+ "and pulse<250 and pulse>50 "
				+ "order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName, startTime,
				endTime });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_datas;
	}
	

	public List<Pc_data> selectData(String userName) {// 通过userName,从数据库中读取数据用于画图
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userName=?  "
				+ "and systolicPressure<250 and systolicPressure>50 "
				+ "and diastolicPressure<250 and diastolicPressure>50 "
				+ "and pulse<250 and pulse>50 "
				+ "order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_datas;
	}

	public List<Pc_data> selectDatabyuserNameandtype(String userName,int type) {// 通过userName和type,从数据库中读取数据用于画图
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where userName=?  and type="+type
				+ " and systolicPressure<250 and systolicPressure>50 "
				+ "and diastolicPressure<250 and diastolicPressure>50 "
				+ "and pulse<250 and pulse>50 "
				+ "order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_datas;
	}
	
	
	public List<Pc_data> selectAll() {
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			pc_datas.add(new Pc_data(id, uploadTime, systolicPressure,
					diastolicPressure, pulse, familyMember));
			// Log.v("AHIUH",pc_datas.get(1).getUploadTime());
		}
		cursor.close();
		db.close();
		return pc_datas;
	}

	// 检索得到未上传的数据
	public List<Pc_data> getnotuploadpc_data(int upload) {
		List<Pc_data> pc_datas = new ArrayList<Pc_data>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_data where upload=0";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			int systolicPressure = cursor.getInt(cursor
					.getColumnIndex("systolicPressure"));
			int diastolicPressure = cursor.getInt(cursor
					.getColumnIndex("diastolicPressure"));
			int pulse = cursor.getInt(cursor.getColumnIndex("pulse"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			String screenName = cursor.getString(cursor
					.getColumnIndex("screenName"));
			pc_datas.add(new Pc_data(userId, systolicPressure,
					diastolicPressure, pulse, uploadTime, userName, screenName,
					familyMember));
			// Log.v("AHIUH",pc_datas.get(1).getUploadTime());
		}
		cursor.close();
		db.close();
		return pc_datas;
	}

	public void updateupload() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_data set upload=1 where upload =0";
		db.execSQL(sql, new Object[] {});
		db.close();

	}

	public void updatefamilyMember(int userId, String userName,
			int systolicPressure, int diastolicPressure, int pulse,
			String uploadTime, int familyMember) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_data set userName='" + userName
				+ "',familyMember=" + familyMember + " where userId =" + userId
				+ " and systolicPressure=" + systolicPressure
				+ " and diastolicPressure=" + diastolicPressure + " and pulse="
				+ pulse + " and uploadTime='" + uploadTime + "'";
		db.execSQL(sql, new String[] {});
		db.close();

	}

	public void delete(int userId, int systolicPressure, int diastolicPressure,
			int pulse, String uploadTime) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_data where userId= ?  and systolicPressure=? "
				+ "and diastolicPressure=? and pulse =? and uploadTime=?";
		db.execSQL(sql, new Object[] { userId, systolicPressure,
				diastolicPressure, pulse, uploadTime });

	}
}
