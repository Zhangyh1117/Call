package com.yh.call;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.yh.call.database.TopContacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by YH on 2016/8/30.
 */
public class ListViewForDelete extends AppCompatActivity implements Serializable {

    private ListView showMessageListView;
    private Button confirmButton;
    private Button cancelButton;
    private TopContacts topContacts;
    private ArrayList<Map<String, String>> listForListView = new ArrayList<Map<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        listForListView = (ArrayList<Map<String,String>>) getIntent().getSerializableExtra("db") ;//从Intent中获取存储信息的List
        topContacts = MainActivity.getTopContacts();
        //Log.d("CallDelete", listForListView.toString());

        showMessageListView = (ListView)findViewById(R.id.list_delete) ;
        confirmButton = (Button)findViewById(R.id.confirm_button) ;
        cancelButton = (Button)findViewById(R.id.cancel_button) ;

        //Log.d("Call", "listView.getCount():" + showMessageListView.getCount());*/
        Action.choiceListView(this,listForListView,showMessageListView,"name","phone");//将Intent中获取的list显示到当前的ListView中

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] pos = showMessageListView.getCheckItemIds();//获取ListView中被选中的条目的ID
                String[] strings = new String[pos.length];
                //Log.d("Call", "checked items--->" + showMessageListView.getCheckedItemIds());
                //Log.d("Call", pos.length + "");
                for (int i = 0;i < pos.length;i++){
                    //Log.d("Call", "p:" + pos[i]);
                    strings[i] = String.valueOf(pos[i]);
                    Object[] params = {strings[i]};
                    topContacts.delete(params);//根据被选中的ID删除数据库中对应的条目
                }
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                Action.showDatabase(topContacts,ListViewForDelete.this,showMessageListView);//用数据库中的数据更新当前ListView
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                Log.d("Call", "Action.updatePosition(showMessageListView,topContacts):" +
                        Action.updatePosition(showMessageListView, topContacts));//用当前ListView中条目的ID更新数据库中对应的position值
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
