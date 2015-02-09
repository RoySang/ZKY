package sict.zky.service;

import java.util.ArrayList;
import java.util.List;

import sict.zky.domain.Getui_store;
import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_user;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Getui_storeService {
	private DBOpenHelper dbOpenHelper;

	public Getui_storeService(Context context) {

		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void insertnouserName(Getui_store getui_store) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sq1 = "insert into getui_store(userId,content,time,mark,title) values(?,?,?,?,?)";
		db.execSQL(
				sq1,
				new Object[] { getui_store.getUserId(),
						getui_store.getContent(), getui_store.getTime(),
						getui_store.getMark(),getui_store.getTitle() });
		db.close();
	}
	


	public void insertAll(Getui_store getui_store) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sq1 = "insert into getui_store(userId,userName,content,time,mark,title) values(?,?,?,?,?,?)";
		db.execSQL(
				sq1,
				new Object[] { getui_store.getUserId(),
						getui_store.getUserName(), getui_store.getContent(),
						getui_store.getTime(), getui_store.getMark(),getui_store.getTitle() });
		db.close();
	}

	public void delete(Integer id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from getui_store where id=?";
		db.execSQL(sql, new Object[] { id });
	}

	public void deletebycontent(String content) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from getui_store where content=?";
		db.execSQL(sql, new Object[] { content });
	}

	public void deletebytime(String time) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from getui_store where time=?";
		db.execSQL(sql, new Object[] { time });
	}

	public void deletebymark(int mark) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from getui_store where mark=" + mark;
		db.execSQL(sql, new Object[] {});
	}

//	public void updateuserIdbyuserIdandtime(int userId, String time) {
//		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
//		String sql = "update getui_store set mark=1 where userId=" + userId
//				+ " and time=?";
//		db.execSQL(sql, new Object[] { time });
//		db.close();
//	}
	
	public void updatemarkbyuserIdandtime(int userId, String time) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update getui_store set mark=1 where userId=" + userId
				+ " and time=?";
		db.execSQL(sql, new Object[] { time });
		db.close();
	}
	public void updatemarkbyuserIdandtimeandtitle(int userId, String time,String title) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update getui_store set mark=1 where userId=" + userId
				+ " and time=? and title=?";
		db.execSQL(sql, new Object[] { time,title });
		db.close();
	}

	public void updateuserNamebyuserIdandtime(int userId, String userName,
			String time) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update getui_store set userName=? where userId=" + userId
				+ " and time=?";
		db.execSQL(sql, new Object[] { userName, time });
		db.close();
	}

	public String getcontentbytime(int userId,String time) {
		String content = "";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select content from getui_store where time=? and userId="+userId;
		Cursor cursor = db.rawQuery(sql, new String[] { time });
		if (cursor.moveToFirst()) {
			content = cursor.getString(cursor.getColumnIndex("content"));

		}
		cursor.close();// 关掉cursor
		db.close();
		return content;
	}
	
	public String getcontentbytimeandtitle(int userId,String time,String title) {
		String content = "";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select content from getui_store where time=? and title=? and userId="+userId;
		Cursor cursor = db.rawQuery(sql, new String[] { time ,title});
		if (cursor.moveToFirst()) {
			content = cursor.getString(cursor.getColumnIndex("content"));

		}
		cursor.close();// 关掉cursor
		db.close();
		return content;
	}

	public List<Getui_store> selectData(int userId) {// 从数据库中读取数据
		List<Getui_store> getui_stores = new ArrayList<Getui_store>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from getui_store where userId="+userId;
		Cursor cursor = db.rawQuery(sql, new String[] { });
		while (cursor.moveToNext()) {
			String content = cursor.getString(cursor
					.getColumnIndex("content"));
			String time = cursor.getString(cursor.getColumnIndex("time"));
			Integer mark = cursor.getInt(cursor
					.getColumnIndex("mark"));
			
			String  title = cursor.getString(cursor
					.getColumnIndex("title"));
			
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			getui_stores.add(new Getui_store(content, time, mark,title));

		}
		// Log.v("AHIUH",pc_datas.get(1).getUploadTime());

		cursor.close();
		db.close();
		return getui_stores;
	}
	
	public boolean ishavegetui_msg(int userId, String content,
			String time) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from getui_store where userId="+userId+"  and content=? and  time=?";
		Cursor cursor = db.rawQuery(sql, new String[] { content,time});
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}

		db.close();
		cursor.close();

		return false;
	}
	
	public boolean ishavenoreadmsg(int userId) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from getui_store where mark=0 and userId="+userId;
		Cursor cursor = db.rawQuery(sql, new String[] { });
		if (cursor.moveToFirst()) {
			db.close();
			cursor.close();

			return true;
		}
		

		db.close();
		cursor.close();

		return false;
	}
}
