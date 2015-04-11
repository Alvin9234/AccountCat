package com.alvin.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "db_accountCat.db";
    private final static int VERSION = 1;
    public SQLiteDatabase dbConn;

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        dbConn = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists tb_count(_id integer primary key autoincrement ,type,money,remark,year TEXT,month TEXT,day TEXT,week TEXT,article)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists tb_count");
            //db.execSQL("drop table if exists tb_expenditure");
            onCreate(db);
        }
    }

    /**
     * @param sql
     * @param selectionArgs
     * @return Cursor
     * @作用：执行带占位符的select语句，查询数据，返回Cursor
     */
    public Cursor selectCursor(String sql, String[] selectionArgs) {
        return dbConn.rawQuery(sql, selectionArgs);
    }

    /**
     * @param sql
     * @param selectionArgs
     * @return int
     * @作用：执行带占位符的select语句，返回结果集的个数
     */
    public int selectCount(String sql, String[] selectionArgs) {
        Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
        if (cursor != null) {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            return 0;
        }
    }

    /**
     * @param sql
     * @param selectionArgs
     * @return List<Map<String, Object>>
     * @作用：执行带占位符的select语句，返回多条数据，放进List集合中。
     */
    public List<Map<String, Object>> selectList(String sql,
                                                String[] selectionArgs) {
        Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
        return cursorToList(cursor);
    }

    /**
     * @return List<Map<String, Object>>集合
     * @作用：将Cursor对象转成List集合
     */
    public List<Map<String, Object>> cursorToList(Cursor cursor) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        String[] arrColumnName = cursor.getColumnNames();
        while (cursor.moveToNext()) {
            Map<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < arrColumnName.length; i++) {
                Object cols_value = cursor.getString(i);
                // switch (cursor.getType(i)) {
                // case 1:
                // cols_value = cursor.getInt(i);
                // break;
                // case 2:
                // cols_value = cursor.getFloat(i);
                // break;
                // case 3:
                // cols_value = cursor.getString(i);
                // break;
                // case 4:
                // cols_value = cursor.getBlob(i);
                // break;
                // default:
                // break;
                // }
                map.put(arrColumnName[i], cols_value);
            }
            list.add(map);
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * @param sql
     * @param bindArgs
     * @return boolean
     * @作用：执行带占位符的update、insert、delete语句，更新数据库，返回true或false
     */
    public boolean execData(String sql, Object[] bindArgs) {
        try {
            dbConn.execSQL(sql, bindArgs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void destroy() {
        if (dbConn != null) {
            dbConn.close();
        }
    }
}
