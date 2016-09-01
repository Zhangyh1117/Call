package com.yh.call;

import android.content.Intent;
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

        //topContacts = (TopContacts)getIntent().getParcelableExtra("db") ;
        listForListView = (ArrayList<Map<String,String>>) getIntent().getSerializableExtra("db") ;
        topContacts = MainActivity.getTopContacts();
        //Log.d("CallDelete", listForListView.toString());

        showMessageListView = (ListView)findViewById(R.id.list_delete) ;
        confirmButton = (Button)findViewById(R.id.confirm_button) ;
        cancelButton = (Button)findViewById(R.id.cancel_button) ;

        //Log.d("Call", "listView.getCount():" + showMessageListView.getCount());*/
        Action.choiceListView(this,listForListView,showMessageListView,"name","phone");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long[] pos = showMessageListView.getCheckItemIds();
                String[] strings = new String[pos.length];
                Object[] params = new Object[pos.length];
                //Log.d("Call", "checked items--->" + showMessageListView.getCheckedItemIds());
                //Log.d("Call", pos.length + "");
                for (int i = 0;i < pos.length;i++){
                    Log.d("Call", "p:" + pos[i]);
                    strings[i] = String.valueOf(pos[i]);
                    params[i] = strings[i];
                }
                //topContacts.listMaps(null);
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                topContacts.delete(params);
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                Action.showDatabase(topContacts,ListViewForDelete.this,showMessageListView);
                //Log.d("Call", "showMessageListView.getCount():" + showMessageListView.getCount());
                Log.d("Call", "Action.updatePosition(showMessageListView,topContacts):" +
                        Action.updatePosition(showMessageListView, topContacts));
                //topContacts.listMaps(null);
                Intent intent = new Intent();
                intent.setClass(ListViewForDelete.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ListViewForDelete.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
