package sict.zky.service;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;

public class Pc_bgdataService {
	private DBOpenHelper dbOpenHelper;

	public Pc_bgdataService(Context context) {

		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void insert(Pc_bgdata pc_bgdata) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
		String sq1 = "insert into pc_bgdata(userId,bloodGlucose,uploadTime,userName,screenName,familyMember,upload,type) values(?,?,?,?,?,?,?,?)";
		db.execSQL(
				sq1,
				new Object[] { pc_bgdata.getUserId(),
						pc_bgdata.getBloodGlucose(), pc_bgdata.getUploadTime(),
						pc_bgdata.getUserName(), pc_bgdata.getScreenName(),
						pc_bgdata.getFamilyMember(), pc_bgdata.getUpload(),
						pc_bgdata.getType() });
		db.close();

	}

	public void delete(Integer id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_bgdata where id=?";
		db.execSQL(sql, new Object[] { id });

	}

	public void update(Pc_bgdata pc_bgdata) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_bgdata set userId=?,bloodGlucose=?,uploadTime=?,userName=?,screenName=?,familyMember=?";
		db.execSQL(
				sql,
				new Object[] { pc_bgdata.getUserId(),
						pc_bgdata.getBloodGlucose(), pc_bgdata.getUploadTime(),
						pc_bgdata.getUserName(), pc_bgdata.getScreenName(),
						pc_bgdata.getFamilyMember() });
		db.close();

	}

	public Pc_bgdata find(Integer id1) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { id1.toString() });
		if (cursor.moveToFirst()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			String screenName = cursor.getString(cursor
					.getColumnIndex("screenName"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			return new Pc_bgdata(id, userId, bloodGlucose, uploadTime,
					userName, screenName, familyMember, type);
		}

		db.close();
		cursor.close();

		return null;

	}

	public List<Pc_bgdata> getScrollData(int offset, int maxResult) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from pc_bgdata order by id asc limit ?,?",
				new String[] { String.valueOf(offset),
						String.valueOf(maxResult) });

		return null;

	}

	public long getCount() {
		return 0;
	}

	public List<Pc_bgdata> selectOther() {// 从数据库中读取数据用于画图
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_bgdatas.add(new Pc_bgdata(id, bloodGlucose, uploadTime));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public boolean ishavepc_bgdata(String userName) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	public boolean ishavepc_bgdatabyuserId(int userId) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select count(*) from pc_bgdata where userId=" + userId;
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

	public boolean ishavepc_bgdata(int userId, String userName,
			Double bloodGlucose, String uploadTime) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where userId=? and userName=? and bloodGlucose=? and uploadTime=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(userId),
				userName, String.valueOf(bloodGlucose), uploadTime });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	public List<Pc_bgdata> selectData(String userName) {// 从数据库中读取数据用于画图
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where userName=?  order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			Integer type = cursor.getInt(cursor.getColumnIndex("type"));

			pc_bgdatas.add(new Pc_bgdata(id, bloodGlucose, uploadTime, type));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public List<Pc_bgdata> selectDatabytype(String userName, int type) {// 从数据库中读取数据用于画图
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where userName=? "
				+ " and  type= " + type
				// + "and bloodGlucose>2 and bloodGlucose<16 "
				+ " order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_bgdatas.add(new Pc_bgdata(id, bloodGlucose, uploadTime));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public List<Pc_bgdata> selectDatabynameandtimerange(String userName,
			String startTime, String endTime) {// 通过userName,从数据库中读取数据用于画图
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from Pc_bgdata where userName=? and uploadTime >? and uploadTime<? order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName, startTime,
				endTime });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			Integer type = cursor.getInt(cursor.getColumnIndex("type"));

			pc_bgdatas.add(new Pc_bgdata(id, bloodGlucose, uploadTime, type));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	// 通过用户名，时间范围，类型得到血糖数据
	public List<Pc_bgdata> selectDatabynameandtimerangeandtype(String userName,
			String startTime, String endTime, int type) {// 通过userName,从数据库中读取数据用于画图
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from Pc_bgdata where userName=? and uploadTime >? and uploadTime<? "
				+ " and type  =" + type + " order by uploadTime";
		Cursor cursor = db.rawQuery(sql, new String[] { userName, startTime,
				endTime });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			pc_bgdatas.add(new Pc_bgdata(id, bloodGlucose, uploadTime));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public List<Pc_bgdata> selectAll(Integer id) {
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));
			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			pc_bgdatas.add(new Pc_bgdata(userId, uploadTime, bloodGlucose,
					familyMember));
		}
		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public List<Pc_bgdata> getnotuploadpc_bgdata(int upload) {
		List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where upload=" + upload;
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			Double bloodGlucose = cursor.getDouble(cursor
					.getColumnIndex("bloodGlucose"));

			String uploadTime = cursor.getString(cursor
					.getColumnIndex("uploadTime"));
			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			String screenName = cursor.getString(cursor
					.getColumnIndex("screenName"));
			int type = cursor.getInt(cursor.getColumnIndex("type"));
			pc_bgdatas.add(new Pc_bgdata(userId, bloodGlucose, uploadTime,
					userName, screenName, familyMember, 0, type));
			// Log.v("AHIUH",pc_datas.get(1).getUploadTime());
		}
		cursor.close();
		db.close();
		return pc_bgdatas;
	}

	public void updateupload() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_bgdata set upload=1 where upload =0";
		db.execSQL(sql, new Object[] {});
		db.close();

	}

	public boolean ishavepc_bgdata(int userId, Double bloodGlucoseS,
			String uploadTime) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_bgdata where userId=?  and bloodGlucose=? and  uploadTime=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(userId),
				String.valueOf(bloodGlucoseS), uploadTime });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}

	public void delete(int userId, String uploadTime, double bloodGlucose) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_bgdata where userId= ? and uploadTime=? and bloodGlucose=?";
		db.execSQL(sql, new Object[] { userId, uploadTime, bloodGlucose });

	}
}
