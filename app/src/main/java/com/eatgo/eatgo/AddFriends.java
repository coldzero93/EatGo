package com.eatgo.eatgo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddFriends extends AppCompatActivity {

    // DB 제어 관련
    private DBManager dbmgr;
    private SQLiteDatabase sdb;
    private Cursor cursor;
    private String id;

    private Button backBtn;
    private Button addBtn;

    private ListView addFriendsListView;
    private AddFriendsAdaptor addFriendsAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        Intent receivedIntent = getIntent();
        id = receivedIntent.getStringExtra("id");

        addFriendsAdaptor = new AddFriendsAdaptor();
        addFriendsListView = (ListView) findViewById(R.id.addFriendsList);
        addFriendsListView.setAdapter(addFriendsAdaptor);

        try {
            dbmgr = new DBManager(getApplicationContext());
            sdb = dbmgr.getReadableDatabase();
            cursor = sdb.rawQuery("SELECT id FROM eatgo WHERE id NOT IN ('" + id + "');", null);

            FriendData friendData;
            String friendID;
            addFriendsAdaptor.clearFriendsList();

            while(cursor.moveToNext()) {
                friendData = new FriendData();
                friendID = cursor.getString(0);
                friendData.setId(friendID);
                addFriendsAdaptor.addFriend(friendData);
            }

        } catch (SQLiteException e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
            dbmgr.close();
        }

        backBtn = (Button) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addBtn = (Button) findViewById(R.id.addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                ArrayList<String> addedFriendsList = new ArrayList<String>();
                FriendData friendData;
                for(int i=0; i<addFriendsAdaptor.getCount(); i++) {
                    friendData = addFriendsAdaptor.getItem(i);
                    if(friendData.getFriendCheck().isChecked()) addedFriendsList.add(friendData.getId());
                }
                resultIntent.putStringArrayListExtra("addedFriendsList", addedFriendsList);
                setResult(RESULT_OK, resultIntent);
                Toast.makeText(getApplicationContext(), "먹자GO할 친구가 추가되었습니다!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
