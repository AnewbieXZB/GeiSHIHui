package com.example.s.why_no.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBSearchHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "lws.db";
	private static final int DATABASE_VERSION = 1;

	public DBSearchHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		/**
		 * 历史记录表
		 */
		  db.execSQL("CREATE TABLE IF NOT EXISTS t_historywords" +
	                "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				  	"historyword VARCHAR," +
				  	" updatetime LONG)");

		/**
		 * 搜索的商品 表
		 */
		db.execSQL("CREATE TABLE IF NOT EXISTS shop_collect" +
				"( id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"uid varchar," +
				"uname text," +
				"uimg text," +
				"details text," +
				"classification text," +
				"extension text," +
				"price float DEFAULT NULL," +
				"volume double DEFAULT NULL," +
				"wangwang text," +
				"wwid text," +
				"shopname text," +
				"platform text," +
				"yhid text," +
				"total text," +
				"surplus text," +
				"denomination text," +
				"starttime date DEFAULT NULL," +
				"endtime date DEFAULT NULL," +
				"yhurl text," +
				"tgurl text," +
				"roll float DEFAULT NULL," +
				"minimum float DEFAULT NULL," +
				"discount INTEGER DEFAULT NULL," +
				"ification varchar DEFAULT NULL," +
				"top varchar DEFAULT NULL," +
				"heat varchar DEFAULT NULL," +
				"off INTEGER DEFAULT '0'," +
				"text text" +
				")");

//			db.execSQL(
//					"CREATE TABLE IF NOT EXISTS shop_collect (" +
//					"  id int(15) PRIMARY KEY  AUTOINCREMENT," +
//					"  uid varchar(50) NOT NULL DEFAULT ''," +
//					"  uname text," +
//					"  uimg text," +
//					"  details text," +
//					"  classification text," +
//					"  extension text," +
//					"  price float DEFAULT NULL," +
//					"  volume double DEFAULT NULL,"
//					"  wangwang text," +
//					"  wwid text," +
//					"  shopname text," +
//					"  platform text," +
//					"  yhid text," +
//					"  total text," +
//					"  surplus text," +
//					"  denomination text," +
//					"  starttime date DEFAULT NULL," +
//					"  endtime date DEFAULT NULL," +
//					"  yhurl text," +
//					"  tgurl text," +
//					"  roll float DEFAULT NULL," +
//					"  minimum float DEFAULT NULL," +
//					"  discount int(5) DEFAULT NULL," + //////////////////////////
//					"  ification varchar(15) DEFAULT NULL," +
//					"  top varchar(15) DEFAULT NULL," +
//					"  heat varchar(15) DEFAULT NULL," +
//					"  off int(10) DEFAULT '0'," +
//					"  text text" +
//					") ");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		

	}

}
