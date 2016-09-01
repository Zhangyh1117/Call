package com.yh.call;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yh.call.database.TopContacts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YH on 2016/8/30.
 */
public class Action implements Serializable {
    /**
     * 将特定内容加入到ListView中，并显示出来
     * @param context:当前环境
     * @param list:存储数据的list
     * @param listView:要显示的ListView
     * @param titleKey:标题的键，此处标题为姓名
     * @param titleValue:标题的值
     * @param contentKey:内容的键，此处内容为电话号码
     * @param contentValue:内容的值
     */
    public static void showMessageOnListView(Context context,List<Map<String,String>> list,ListView listView,
                                             String titleKey, String titleValue, String contentKey, String contentValue){
        HashMap<String,String> map = new HashMap<String, String>();
        map.put(titleKey,titleValue);
        map.put(contentKey,contentValue);
        list.add(map);
        SimpleAdapter adapter = new SimpleAdapter(context,list,android.R.layout.simple_list_item_2,
                new String[]{titleKey,contentKey},new int[] {android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
        //Log.d("Call", "listView.getCount():" + listView.getCount());
    }

    /**
     * 初始化ListView，将数据库中的信息用ListView显示出来
     * @param context:当前环境
     * @param arrayList:从数据库中提取的内容
     * @param listView:要显示的ListView
     * @param titleKey:标题的键，此处标题为姓名
     * @param contentKey:内容的键，此处内容为电话号码
     */
    public static void initListView(Context context,List<Map<String,String>> arrayList, ListView listView,
                                    String titleKey,String contentKey){
        //Log.d("Call", "arrayList---->" + arrayList.toString());
        SimpleAdapter adapter = new SimpleAdapter(context,arrayList,android.R.layout.simple_list_item_2,
                new String[]{titleKey,contentKey},new int[] {android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
        //Log.d("Call", "listView.getCount():" + listView.getCount());
    }

    /**
     * 显示ListView，使其可以进行多选
     * @param context:当前环境
     * @param arrayList:从数据库中提取的内容
     * @param listView:要显示的ListView
     * @param titleKey:标题的键，此处标题为姓名
     * @param contentKey:内容的键，此处内容为电话号码
     */
    public static void choiceListView(Context context,List<Map<String,String>> arrayList, ListView listView, String titleKey,
                                    String contentKey){
        //Log.d("Call", "arrayList---->" + arrayList.toString());
        SimpleAdapter adapter = new SimpleAdapter(context,arrayList,android.R.layout.simple_list_item_multiple_choice,//simple_list_item_2,
                new String[]{titleKey,contentKey},new int[] {android.R.id.text1,android.R.id.text2});
        adapter.hasStableIds();
        listView.setAdapter(adapter);
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        //Log.d("Call", "listView.getCount():" + listView.getCount());
    }

    /**
     * 打开电话本
     * @param data:上一级函数中的data
     * @param context:当前环境
     * @param name:姓名
     * @param phone:电话
     * @param showMessageListView:要显示的ListView
     * @param topContacts:数据库类
     */
    public static List<Map<String,String>> openContacts(Intent data,Context context,String name,String phone,ListView showMessageListView,
                                    TopContacts topContacts){
        if (data == null){
            return null;
        }
        Uri result = data.getData();
        String contactId = result.getLastPathSegment();

        //得到名称
        String[] projection = new String[] {Contacts.People._ID, Contacts.People.NAME,
                Contacts.People.NUMBER};
        Cursor cursor = null;          //order by
        try {
            cursor = context.getContentResolver().query(Contacts.People.CONTENT_URI,
                    projection,                     //select sentence
                    Contacts.People._ID + " = ?",   //where sentence
                    new String[]{contactId},        //where values
                    Contacts.People.NAME);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请授予权限", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (cursor.moveToFirst()){
            name = cursor.getString(cursor.getColumnIndex(Contacts.People.NAME));
        }

        //得到电话
        projection = new String[] {Contacts.Phones.PERSON_ID,Contacts.Phones.NUMBER};
        cursor = context.getContentResolver().query(Contacts.Phones.CONTENT_URI,
                projection,                         //select sentence
                Contacts.Phones.PERSON_ID + " = ?", //where sentence
                new String[] {contactId},           //where values
                Contacts.Phones.NAME);              //order by

        if (cursor.moveToFirst()){
            phone = cursor.getString(cursor.getColumnIndex(Contacts.Phones.NUMBER));
        }

        //显示
        //showPhone.setText(name + ":" + phone);
        //showMessageOnListView(showMessageListView,"name",name,"phone",phone);
        //TopContacts topContacts = new TopContacts(MainActivity.this);
        Object[] params = {name,phone,showMessageListView.getCount()};
        //Object[] params = {"zhangsan","123456","3"};
        if (topContacts.view(new String[]{name}).isEmpty()) {//若返回值为空，表示在数据库中没有找到同名的项目，则添加到数据库中
            boolean dbAddFlag = topContacts.add(params);
            Log.d("Call", "dbAddFlag:" + dbAddFlag);
        }
        else {//否则提示该条目已存在
            Toast.makeText(context, "此人已存在", Toast.LENGTH_SHORT).show();
        }
        //Log.d("Call", "dbAddFlag:" + dbAddFlag);
        //List<Map<String,String>> list = topContacts.listMaps(null);
        //Log.d("Call", list.toString());
        String[] columns = {"name","phone"};
        //String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = "position";
        //String orderBy = null;
        List<Map<String,String>> initList = topContacts.listMaps(columns,selection,selectionArgs,orderBy);
        //Log.d("Call", initList.toString());
        Action.initListView(context,initList,showMessageListView,"name","phone");

        return initList;
    }

    /**
     * 读取数据库中的内容，并显示出来
     * @param topContacts:数据库名称
     * @param context:当前环境
     * @param listView:要显示的ListView
     * @return:从数据库读取的信息
     */
    public static List<Map<String,String>> showDatabase(TopContacts topContacts,Context context,ListView listView) {
        String[] columns = {"name", "phone"};
        //String[] columns = null;
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = "position";
        //String orderBy = null;
        List<Map<String, String>> initList = topContacts.listMaps(columns, selection, selectionArgs, orderBy);
        //Log.d("Call", initList.toString());
        Action.initListView(context, initList, listView, "name", "phone");

        return initList;
    }

    /**
     * 更新数据库中position的值，使其和ListView中显示的位置相符
     * @param listView:要显示的ListView
     * @param topContacts:要修改的数据库
     * @return:成功返回true，失败返回false
     */
    public static boolean updatePosition(ListView listView,TopContacts topContacts){
        if ((listView == null)||(topContacts == null)){
            return false;
        }
        for (int i = 0;i < listView.getCount();i++) {
            HashMap<String,String> map = (HashMap<String, String>) listView.getItemAtPosition(i);
            String name = map.get("name");
            //phone = map.get("phone");
            //Log.d("Call", name);
            //Log.d("Call", phone);
            Object[] p = new String[]{String.valueOf(i),name };
            //boolean flag = topContacts.update(p);
            //Log.d("Call", "topContacts.update(p):" + flag);
            if (topContacts.update(p) == false){
                return false;
            }
        }

        return true;
    }
}

