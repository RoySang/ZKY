package sict.zky.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {

	

	
 
	public DBOpenHelper(Context context) {
		super(context, "zky.db", null, 1);
	}
   


	@Override
	public void onCreate(SQLiteDatabase db) {
		String sq1="CREATE TABLE pc_user(id integer primary key autoincrement ,userId,userName,familyMember,familyRole,upload)";
				
		db.execSQL(sq1);
        String sq2 ="CREATE TABLE pc_data(id integer primary key autoincrement ,userId varchar(20) NULL,systolicPressure int(25) NULL,diastolicPressure int(25) NULL,pulse int(10) NULL,uploadTime String NULL,userName varchar(50) NULL,screenName varchar(50) NULL,familyMember int(5) NULL,upload int(10) NULL)";
        db.execSQL(sq2);
        String sq3="CREATE TABLE pc_bgdata(id integer primary key autoincrement ,userId varchar(20) NULL,userName varchar(25) NULL,screenName varchar(25) NULL,bloodGlucose varchar(20) NULL,uploadTime String NULL,familyMember int(5) NULL,upload int(10) NULL,type)";
        db.execSQL(sq3);
        String sq4 = "CREATE TABLE pc_healthknowledge(id integer primary key autoincrement ,knowledgeID int(11) NULL,title varchar(20) NULL,text varchar(200) NULL,link varchar(20) NULL,agencyID varchar(30) NULL)";
        db.execSQL(sq4);
        String sq5="CREATE TABLE pc_greenchannel(id integer primary key autoincrement ,name varchar(50) NULL,phone1 varchar(30) NULL,phone2 varchar(30) NULL,phone3 varchar(30) NULL,date timestamp NOT NULL,agencyID varchar(30) NULL,type varchar(10) NULL,level varchar(10) NULL)";
        db.execSQL(sq5);
        String sq6="CREATE TABLE getui_store(id integer primary key autoincrement,userId,userName,content,time,type,mark,title)";
        db.execSQL(sq6);

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		

	}

}
