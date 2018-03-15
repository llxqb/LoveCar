package com.bhxx.lovecar.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bhxx.lovecar.beans.ZiXunRightItemBean;
import com.bhxx.lovecar.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class ZiXunItemDao {
    public static final String TABLE_NAME = "zixuntype";// 表名
    public static final String COLUMN_ID = "_id";// 主键自动增长
    public static final String COLUMN_TYPE = "type";// id标示
    public static final String COLUMN_CONTENT = "content";// 类型名
    public static final String COLUMN_CONTENTID = "contentId";// 类型Id
//    public static final String COLUMN_USERID = "userid";// 用户

    /**
     * 插入一条数据
     *
     * @param type
     * @return
     */
    public static boolean insertType(ZiXunRightItemBean type) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ZiXunItemDao.COLUMN_TYPE, type.getType());
        values.put(ZiXunItemDao.COLUMN_CONTENT, type.getContent());
        values.put(ZiXunItemDao.COLUMN_CONTENTID, type.getContentId());
//        values.put(ZiXunItemDao.COLUMN_USERID,userid);

        return db.insert(ZiXunItemDao.TABLE_NAME, null, values) != -1;
    }

    /**
     * 以集合形式插入
     *
     * @param types
     */
    public static void insertTypes(List<ZiXunRightItemBean> types) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        db.beginTransaction();

        try {
            for (int i = 0; i < types.size(); i++) {
                ZiXunRightItemBean type = types.get(i);
                LogUtils.i("type.getContentId()=" + type.getContentId());
                ContentValues values = new ContentValues();
                values.put(ZiXunItemDao.COLUMN_TYPE, type.getType());
                values.put(ZiXunItemDao.COLUMN_CONTENT, type.getContent());
                values.put(ZiXunItemDao.COLUMN_CONTENTID, type.getContentId());
//                values.put(ZiXunItemDao.COLUMN_USERID,userid);
                db.insert(ZiXunItemDao.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * @return
     */
    public static void clearTable() {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getWritableDatabase();

        db.beginTransaction();

        try {
            db.execSQL("DELETE FROM " + ZiXunItemDao.TABLE_NAME);
            db.execSQL("DELETE FROM " + "sqlite_sequence");

            // db.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = " +
            // TABLE_NAME);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 清除表并插入新数据
     *
     * @param types
     * @return
     */
    public static void clearAndInsert(List<ZiXunRightItemBean> types) {

        clearTable();
        insertTypes(types);
    }

    public static List<ZiXunRightItemBean> queryAllType(String z_type) {

        SQLiteDatabase db = SqlLiteHelper.getInstance().getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE type=" + "'" + z_type + "'", null);

        List<ZiXunRightItemBean> types = new ArrayList<ZiXunRightItemBean>();
        while (cursor.moveToNext()) {
            ZiXunRightItemBean type = new ZiXunRightItemBean();
            type.setContentId(cursor.getInt(cursor
                    .getColumnIndex(ZiXunItemDao.COLUMN_CONTENTID)));
            type.setContent(cursor.getString(cursor
                    .getColumnIndex(ZiXunItemDao.COLUMN_CONTENT)));
            type.setType(cursor.getString(cursor
                    .getColumnIndex(ZiXunItemDao.COLUMN_TYPE)));
            types.add(type);
        }
        return types;
    }

}
