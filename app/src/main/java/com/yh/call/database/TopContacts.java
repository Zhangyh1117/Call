package com.yh.call.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YH on 2016/8/30.
 */
public class TopContacts implements DatabaseService,Serializable{

    //private static final long serialVersionUID = -7060210544600464481L;

    private DbHelper dbHelper;

    public TopContacts(Context context) {
        dbHelper = new DbHelper(context,DbHelper.getName(),null,DbHelper.getVersion());
    }

    public static void DeleteDatabase(Context context){
        context.deleteDatabase(DbHelper.getName());
        Log.d("Call", "delete old databsse done");
    }

    @Override
    public boolean add(Object[] params) {//增加
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            String sql = "insert into " + DbHelper.getNameOfDatabase() + " (name,phone,position) values(?,?,?)";
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(sql,params);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }

        return flag;
    }

    @Override
    public boolean delete(Object[] params) {//删除
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            String sql = "delete from " + DbHelper.getNameOfDatabase() + " where position = ?";
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(sql,params);
            flag= true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }

        return flag;
    }

    @Override
    public boolean update(Object[] params) {//修改
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = null;
        try {
            //String sql = "update " + DbHelper.getNameOfDatabase() + " set name = ?,phone = ? where position = ?";
            String sql = "update " + DbHelper.getNameOfDatabase() + " set position = ? where name = ?";
            sqLiteDatabase = dbHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(sql,params);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }

        return flag;
    }

    @Override
    public Map<String, String> view(String[] selectionArgs) {//查询符合要求的记录
        Map<String,String> map = new HashMap<String, String>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            String sql = "select * from " + DbHelper.getNameOfDatabase() + " where position = ?";
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(sql,selectionArgs);
            int colums = cursor.getColumnCount();//获得数据库列的个数
            while (cursor.moveToNext()){
                for (int i = 0;i < colums;i++){
                    String colsName = cursor.getColumnName(i);
                    String colsValue = cursor.getString(cursor.getColumnIndex(colsName));
                    if (colsValue == null){
                        colsValue = "";
                    }
                    map.put(colsName,colsValue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }

        return map;
    }

    @Override
    public List<Map<String, String>> listMaps(String[] selecionArgs) {//查询所有记录
        List<Map<String,String>> list = new ArrayList<Map<String, String>>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            String sql = "select * from " + DbHelper.getNameOfDatabase();
            sqLiteDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(sql,selecionArgs);
            int colums = cursor.getColumnCount();
            while (cursor.moveToNext()){
                Map<String,String> map = new HashMap<String, String>();
                for (int i = 0;i < colums;i++){
                    String colsName = cursor.getColumnName(i);
                    String colsValue = cursor.getString(cursor.getColumnIndex(colsName));
                    if (colsValue == null){
                        colsValue = "";
                    }
                    map.put(colsName,colsValue);
                }
                list.add(map);
            }
            Log.d("Call", "cursor.getCount():" + cursor.getCount());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }
        Log.d("Call", list.toString());

        return list;
    }

    //public ArrayList<Map<String,String>> initList(String[] selec)

    @Override
    public boolean add() {
        return false;
    }

    @Override
    public boolean delete(String whereClause, String[] whereArgs) {
        return false;
    }

    @Override
    public boolean update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        boolean flag = false;
        SQLiteDatabase sqLiteDatabase = null;
        int count = 0;//影响数据库的行数
        try {
            sqLiteDatabase = dbHelper.getWritableDatabase();
            count = sqLiteDatabase.update(DbHelper.getNameOfDatabase(),contentValues,whereClause,whereArgs);
            flag = count > 0? true : false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null){
                sqLiteDatabase.close();
            }
        }

        return flag;
    }

    @Override
    public Map<String, String> view(String selection, String[] selectionArgs) {
        return null;
    }

    @Override
    public List<Map<String, String>> listMaps(String[] columns,String selection,String[] selectionArgs,String orderBy) {
        List<Map<String,String>> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try {
            sqLiteDatabase = dbHelper.getReadableDatabase();
            cursor = sqLiteDatabase.query(false, DbHelper.getNameOfDatabase(), columns, selection, selectionArgs,
                    null, null, orderBy, null);
            int colums = cursor.getColumnCount();
            while (cursor.moveToNext()){
                Map<String,String> map = new HashMap<String, String>();
                for (int i = 0;i < colums;i++){
                    String colsName = cursor.getColumnName(i);
                    String colsValue = cursor.getString(cursor.getColumnIndex(colsName));
                    if (colsValue == null){
                        colsValue = "";
                    }
                    map.put(colsName,colsValue);
                }
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return list;
    }

}
