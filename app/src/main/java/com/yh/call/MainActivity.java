package com.yh.call;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yh.call.database.DbHelper;
import com.yh.call.database.TopContacts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YH on 2016/8/29.
 * version 0.1: 可以从电话本中获得信息。
 * version 0.2: 将电话本中获得的信息用ListView显示出来；当点击ListView中的内容时，可以向选中的对象拨打电话。
 * version 0.3: 添加数据库，每次改动ListView均变化数据库，实现添加、删除功能
 *              问题：同样的数据会同时添加；一次只能删除一个数据，不能同时删除多个；activity间切换太慢
 * version 0.4: 解决了上一版本中的问题，重复数据不再重复添加，可以同时删除多人，改进了activity间的切换
 */
public class MainActivity extends AppCompatActivity {

    private TextView showPhone;
    private ListView showMessageListView;

    private String name = "";
    private String phone = "";
    private List<Map<String, String>> listForListView = new ArrayList<Map<String, String>>();
    private DbHelper dbHelper;

    public static TopContacts getTopContacts() {
        return topContacts;
    }

    private static TopContacts topContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //TopContacts.DeleteDatabase(this);
        dbHelper = new DbHelper(MainActivity.this,DbHelper.getName(),null,DbHelper.getVersion());
        dbHelper.getWritableDatabase();
        topContacts = new TopContacts(MainActivity.this);

        showPhone = (TextView) findViewById(R.id.show_phone);

        showMessageListView = (ListView) findViewById(R.id.show_message_listview);
        listForListView = Action.showDatabase(topContacts,this,showMessageListView);
        showMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * 给所选的目标拨打电话
             * @param adapterView:listview适配器的一个指针
             * @param view:所点击的item的view的句柄
             * @param i:所点击item在适配器里的位置
             * @param l:所点击item在ListView中的第几行
             */
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> map = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                String nameList = map.get("name");
                String phoneList = map.get("phone");
                //Toast.makeText(MainActivity.this, nameList, Toast.LENGTH_SHORT).show();
                Log.d("Call", nameList + ": " + phoneList);
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneList));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] columns = {"name", "phone"};
        String selection = null;
        String[] selectionArgs = null;
        String orderBy = "position";
        List<Map<String, String>> initList = topContacts.listMaps(columns, selection, selectionArgs, orderBy);
        Action.initListView(this,initList,showMessageListView,"name","phone");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                listForListView = Action.openContacts(data,this,name,phone,showMessageListView,topContacts);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_insert:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                //startActivityForResult(intent,REQUEST_CONTACT);
                startActivityForResult(intent, 1);
                break;
            case R.id.action_delete:
                Intent intent_delete = new Intent(this,ListViewForDelete.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("db",topContacts);
                intent_delete.putExtra("db",(Serializable) listForListView);
                startActivity(intent_delete);
                break;
            case R.id.action_settings:
                break;
            default:
                Toast.makeText(this, "no this key", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
