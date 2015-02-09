package sict.zky.service;

import java.util.ArrayList;
import java.util.List;

import sict.zky.domain.Pc_bgdata;
import sict.zky.domain.Pc_data;
import sict.zky.domain.Pc_user;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Pc_userService {
	private DBOpenHelper dbOpenHelper;

	public Pc_userService(Context context) {

		this.dbOpenHelper = new DBOpenHelper(context);
	}

	public void insert(Pc_user pc_user) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sq1 = "insert into pc_user(userId,userName,familyMember,familyRole,upload) values(?,?,?,?,?)";
		db.execSQL(sq1,
				new Object[] { pc_user.getUserID(), pc_user.getUserName(),
						pc_user.getFamilyMember(), pc_user.getFamilyRole() ,
						pc_user.getUpload()});
		db.close();
	}

	public void delete(Integer id) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_user where id=?";
		db.execSQL(sql, new Object[] { id });
	}

	public void deletebyuserName(String userName) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例
		String sql = "delete from pc_user where userName=?";
		db.execSQL(sql, new Object[] { userName });
	}

	public void update(Pc_user pc_user) {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_user set userId=?,userName=?,familyMember=?,familyRole=?";
		db.execSQL(sql,
				new Object[] { pc_user.getUserID(), pc_user.getUserName(),
						pc_user.getFamilyMember(), pc_user.getFamilyRole() });
		db.close();

	}

	public List<String> getuserName(int userId) {
		List<String> userNames = new ArrayList<String>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select Distinct(userName) from pc_user where userId="
				+ userId;
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {

			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			// int familyMember =
			// cursor.getInt(cursor.getColumnIndex("familyMember"));
			userNames.add(userName);
		}
		cursor.close();// 关掉cursor
		db.close();
		return userNames;
	}

	public Pc_user find(Integer id1) {

		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_user where id=?";
		Cursor cursor = db.rawQuery(sql, new String[] { id1.toString() });
		if (cursor.moveToFirst()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));

			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			Integer familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String familyRole = cursor.getString(cursor
					.getColumnIndex("familyRole"));

			return new Pc_user(id, userId, userName, familyMember, familyRole);
		}

		db.close();
		cursor.close();

		return null;

	}

	public boolean ishaveUser(Integer Id) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();// 去的数据库操作实例
		Cursor cursor = db.rawQuery("select * from pc_user where userId = "
				+ Id, new String[] {});
		if (cursor.moveToFirst()) {
			// int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			cursor.close();// 关掉cursor
			db.close();
			return true;
		}
		cursor.close();// 关掉cursor
		db.close();
		return false;
	}

	public boolean ishaveUserbyuserName(String userName) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();// 去的数据库操作实例
		String sql = "select *  from pc_user where userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		if (cursor.moveToFirst()) {
			// int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			cursor.close();// 关掉cursor
			db.close();
			return true;
		}
		cursor.close();// 关掉cursor
		db.close();
		return false;
	}

	public boolean ishaveUserbyuserName(int userId, String userName) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();// 去的数据库操作实例
		String sql = "select *  from pc_user where userId=" + userId
				+ " and userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { String.valueOf(userId),
				userName });
		if (cursor.moveToFirst()) {
			// int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			cursor.close();// 关掉cursor
			db.close();
			return true;
		}
		cursor.close();// 关掉cursor
		db.close();
		return false;
	}

	public boolean ishaveUserbyuserName(String userName, int userId) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();// 去的数据库操作实例
		String sql = "select *  from pc_user where userName=? and userId="
				+ userId;
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		if (cursor.moveToFirst()) {
			// int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			cursor.close();// 关掉cursor
			db.close();
			return true;
		}
		cursor.close();// 关掉cursor
		db.close();
		return false;
	}

	public int getCountofother(Integer familyMember) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select count(*) from pc_user where familyMember="
				+ familyMember;
		Cursor cursor = db.rawQuery(sql, null);
		cursor.moveToFirst();
		int result = cursor.getInt(0);
		db.close();
		cursor.close();
		return result;
	}

	public boolean ishaveFamilyMember(int familyMember) {
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();// 去的数据库操作实例
		Cursor cursor = db.rawQuery(
				"select * from pc_user where familyMember = " + familyMember,
				new String[] {});
		if (cursor.moveToFirst()) {
			// int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			cursor.close();// 关掉cursor
			db.close();
			return true;
		}
		cursor.close();// 关掉cursor
		db.close();
		return false;
	}

	public int getfamilyMemberbyuserIdanduserName(int userId,String userName) {
		int familyMember = 0;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select familyMember from pc_user where userId="+userId+" and userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		if (cursor.moveToFirst()) {

			// String userName =
			// cursor.getString(cursor.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			familyMember = cursor.getInt(cursor.getColumnIndex("familyMember"));
			// userNames.add(userName);
		}
		cursor.close();// 关掉cursor
		db.close();
		return familyMember;
	}

	public int getlastfamilyMember(int userId) {
		int familyMember = 0;
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select familyMember from pc_user where userId=" + userId
				+ " order by familyMember desc";

		Cursor cursor = db.rawQuery(sql, new String[] {});
		if (cursor.moveToFirst()) {

			// String userName =
			// cursor.getString(cursor.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			familyMember = cursor.getInt(cursor.getColumnIndex("familyMember"));
			// userNames.add(userName);
		}
		cursor.close();// 关掉cursor
		db.close();
		return familyMember;
	}

	public String getuserNamebyfamilyMember(int familyMember, int userId) {
		String userName = "";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select userName from pc_user where familyMember="
				+ familyMember + " and userId=" + userId;
		Cursor cursor = db.rawQuery(sql, new String[] {});
		if (cursor.moveToFirst()) {

			// String userName =
			// cursor.getString(cursor.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			userName = cursor.getString(cursor.getColumnIndex("userName"));
			// userNames.add(userName);
		}
		cursor.close();// 关掉cursor
		db.close();
		return userName;
	}

	public String getfamilyRolebyuserIdanduserName(int userId,String userName) {
		String familyRole = "";
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select familyRole from pc_user where userId="+userId+" and userName=?";
		Cursor cursor = db.rawQuery(sql, new String[] { userName });
		if (cursor.moveToFirst()) {

			// String userName =
			// cursor.getString(cursor.getColumnIndex("userName"));
			// String screenName =
			// cursor.getString(cursor.getColumnIndex("screenName"));
			familyRole = cursor.getString(cursor.getColumnIndex("familyRole"));
			// userNames.add(userName);
		}
		cursor.close();// 关掉cursor
		db.close();
		return familyRole;
	}

	public List<Pc_user> getScrollData(int offset, int maxResult) {
		List<Pc_user> pc_user = new ArrayList<Pc_user>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from pc_user order by id asc limit ?,?",
				new String[] { String.valueOf(offset),
						String.valueOf(maxResult) });
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));

			Integer familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String familyRole = cursor.getString(cursor
					.getColumnIndex("familyRole"));
			new Pc_user(id, userId, userName, familyMember, familyRole);
		}
		cursor.close();// 关掉cursor
		db.close();
		return null;

	}

	public long getCount() {
		return 0;
	}

	public List<Pc_user> selectAll(int ID) {
		List<Pc_user> pc_users = new ArrayList<Pc_user>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_user where userId=" + ID
				+ " order by familyMember";
		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			Integer id = cursor.getInt(cursor.getColumnIndex("id"));
			Integer userId = cursor.getInt(cursor.getColumnIndex("userId"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));
			Integer familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String familyRole = cursor.getString(cursor
					.getColumnIndex("familyRole"));
			pc_users.add(new Pc_user(id, userId, userName, familyMember,
					familyRole));
			// Log.v("AHIUH",pc_datas.get(1).getUploadTime());
		}
		cursor.close();
		db.close();
		return pc_users;
	}

	public void updateupload() {
		SQLiteDatabase db = dbOpenHelper.getWritableDatabase();// 取得数据库操作实例SQLiteDatabase
		String sql = "update pc_user set upload=1 where upload =0";
		db.execSQL(sql, new Object[] {});
		db.close();

	}

	public List<Pc_user> getnotuploadpc_user(int upload) {
		List<Pc_user> pc_users = new ArrayList<Pc_user>();
		SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		String sql = "select * from pc_user where upload=" + upload;

		// List<Pc_bgdata> pc_bgdatas = new ArrayList<Pc_bgdata>();
		// SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
		// String sql = "select * from pc_bgdata where upload=" + upload;

		Cursor cursor = db.rawQuery(sql, new String[] {});
		while (cursor.moveToNext()) {
			int userId = cursor.getInt(cursor.getColumnIndex("userId"));
			String userName = cursor.getString(cursor
					.getColumnIndex("userName"));

			int familyMember = cursor.getInt(cursor
					.getColumnIndex("familyMember"));
			String familyRole = cursor.getString(cursor
					.getColumnIndex("familyRole"));

			pc_users.add(new Pc_user(userId, userName, familyMember, familyRole));
			// Log.v("AHIUH",pc_datas.get(1).getUploadTime());
		}
		cursor.close();
		db.close();
		return pc_users;
	}

}
