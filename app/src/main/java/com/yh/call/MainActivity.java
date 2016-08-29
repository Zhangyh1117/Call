package com.yh.call;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YH on 2016/8/29.
 * version 0.1:可以从电话本中获得信息。
 * version 0.2:将电话本中获得的信息用ListView显示出来；当点击ListView中的内容时，可以向选中的对象拨打电话。
 */
public class MainActivity extends AppCompatActivity {

    private Button queryButton;
    private TextView showPhone;
    private ListView showMessageListView;

    String name = "";
    String phone = "";
    ArrayList<Map<String, String>> listForListView = new ArrayList<Map<String, String>>();

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


        queryButton = (Button) findViewById(R.id.query_button);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                //startActivityForResult(intent,REQUEST_CONTACT);
                startActivityForResult(intent, 1);
            }
        });

        showPhone = (TextView) findViewById(R.id.show_phone);

        showMessageListView = (ListView) findViewById(R.id.show_message_listview);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1:
                if (data == null){
                    return;
                }
                Uri result = data.getData();
                String contactId = result.getLastPathSegment();

                //得到名称
                String[] projection = new String[] {Contacts.People._ID, Contacts.People.NAME,
                        Contacts.People.NUMBER};
                Cursor cursor = null;          //order by
                try {
                    cursor = getContentResolver().query(Contacts.People.CONTENT_URI,
                            projection,                     //select sentence
                            Contacts.People._ID + " = ?",   //where sentence
                            new String[]{contactId},        //where values
                            Contacts.People.NAME);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "请授予权限", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (cursor.moveToFirst()){
                    name = cursor.getString(cursor.getColumnIndex(Contacts.People.NAME));
                }

                //得到电话
                projection = new String[] {Contacts.Phones.PERSON_ID,Contacts.Phones.NUMBER};
                cursor = getContentResolver().query(Contacts.Phones.CONTENT_URI,
                        projection,                         //select sentence
                        Contacts.Phones.PERSON_ID + " = ?", //where sentence
                        new String[] {contactId},           //where values
                        Contacts.Phones.NAME);              //order by

                if (cursor.moveToFirst()){
                    phone = cursor.getString(cursor.getColumnIndex(Contacts.Phones.NUMBER));
                }

                //显示
                showPhone.setText(name + ":" + phone);
                showMessageOnListView(showMessageListView,"name",name,"phone",phone);
                break;
        }
    }

    /**
     * 将特定内容加入到ListView中，并显示出来
     * @param listView:要显示的ListView
     * @param titleKey:标题的键，此处标题为姓名
     * @param titleValue:标题的值
     * @param contentKey:内容的键，此处内容为电话号码
     * @param contentValue:内容的值
     */
    public void showMessageOnListView(ListView listView,String titleKey,String titleValue,
                                      String contentKey,String contentValue){
        HashMap<String,String> map = new HashMap<String, String>();
        map.put(titleKey,titleValue);
        map.put(contentKey,contentValue);
        listForListView.add(map);
        SimpleAdapter adapter = new SimpleAdapter(this,listForListView,android.R.layout.simple_list_item_2,
                new String[]{titleKey,contentKey},new int[] {android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
