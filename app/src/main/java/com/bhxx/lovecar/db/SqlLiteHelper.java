package com.bhxx.lovecar.db;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bhxx.lovecar.application.App;


/**
 * sqlite 数据库
 *
 */
public class SqlLiteHelper extends SQLiteOpenHelper {
	private static final String name = "zixuntype.db";

	private static class SingletonClass {

		private static final SqlLiteHelper instance = new SqlLiteHelper();
	}

	public static SqlLiteHelper getInstance() {

		return SingletonClass.instance;
	}

	/**
	 * 在SQLiteOpenHelper的子类当中，必须有这个构造函数
	 *
	 *            当前的Activity
	 *            表的名字（而不是数据库的名字，这个类是用来操作数据库的）
	 *            用来在查询数据库的时候返回Cursor的子类，传空值
	 *            当前的数据库的版本，整数且为递增的数
	 */
	public SqlLiteHelper() {
		super(App.app, name, null, 1);
	}

	/**
	 * 该函数是在第一次创建数据库时执行，只有当其调用getreadabledatebase()
	 * 或者getwrittleabledatebase()而且是第一创建数据库是才会执行该函数
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		db.beginTransaction();
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ ZiXunItemDao.TABLE_NAME + " ("
					+ ZiXunItemDao.COLUMN_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ ZiXunItemDao.COLUMN_CONTENT + " TEXT,"
					+ ZiXunItemDao.COLUMN_CONTENTID + " TEXT,"
//					+ ZiXunItemDao.COLUMN_USERID + " TEXT,"
					+ ZiXunItemDao.COLUMN_TYPE + " TEXT)");

			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			db.endTransaction();
		}

	}

	/**
	 * 更新版本
	 * @param db
	 * @param oldVersion
	 * @param newVersion
     */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + ZiXunItemDao.TABLE_NAME;
		db.execSQL(sql);
		this.onCreate(db);
	}


}
