package com.yh.call;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button queryButton;
    private TextView showPhone;

    String name = "";
    String phone = "";

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


        queryButton = (Button)findViewById(R.id.query_button);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(ContactsContract.Contacts.CONTENT_URI);
                //startActivityForResult(intent,REQUEST_CONTACT);
                startActivityForResult(intent,1);
            }
        });

        showPhone = (TextView)findViewById(R.id.show_phone);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
