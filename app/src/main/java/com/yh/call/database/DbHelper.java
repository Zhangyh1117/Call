package com.yh.call.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;

/**
 * Created by YH on 2016/8/30.
 */
public class DbHelper extends SQLiteOpenHelper implements Serializable {

    private static String name = "TopContacts.db";//表示数据库的文件名
    private static String nameOfDatabase = "person";//表示数据库名称
    private static int version = 1;//表示数据库的版本号

    public static String getName() {
        return name;
    }

    public static String getNameOfDatabase() {
        return nameOfDatabase;
    }

    public static int getVersion() {
        return version;
    }

    public DbHelper(Context context) {
        super(context, name, null, version);
    }

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //数据库第一次被创建时onCreate会被调用
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table if not exists " + getNameOfDatabase() + " " + "(" +
                "id integer primary key autoincrement, " +
                "name varchar(64), " +
                "phone varchar(20), " +
                "position varchar(10)" +
                ")";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
