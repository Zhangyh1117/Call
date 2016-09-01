package com.yh.call.database;

import android.content.ContentValues;

import java.util.List;
import java.util.Map;

/**
 * Created by YH on 2016/8/30.
 */
public interface DatabaseService {

    public boolean add(Object[] params);

    public boolean delete(Object[] params);

    public boolean update(Object[] params);

    public Map<String,String> view(String[] selectionArgs);

    public List<Map<String,String>> listMaps(String[] selecionArgs);

    public boolean add();

    public boolean delete(String whereClause,String[] whereArgs);

    public boolean update(ContentValues contentValues,String whereClause,String[] whereArgs);

    public Map<String,String> view(String selection,String[] selectionArgs);

    public List<Map<String,String>> listMaps(String[] columns,String selection,String[] selectionArgs,String orderBy);
}
