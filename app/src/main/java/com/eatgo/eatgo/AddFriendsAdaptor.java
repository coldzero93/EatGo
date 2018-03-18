package com.eatgo.eatgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AddFriendsAdaptor extends BaseAdapter {

    private ArrayList<FriendData> addFriendsList = new ArrayList<FriendData>();

    @Override
    public int getCount() {
        return addFriendsList.size();
    }

    @Override
    public FriendData getItem(int position) {
        return addFriendsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.add_friends_list, parent, false);
        }

        TextView friendID = (TextView) convertView.findViewById(R.id.friendID);
        CheckBox friendCheck = (CheckBox) convertView.findViewById(R.id.friendCheck);

        FriendData friendData = addFriendsList.get(position);

        friendID.setText(friendData.getId());
        friendData.setFriendCheck(friendCheck);

        return convertView;
    }

    public void addFriend(FriendData friendData) {
        addFriendsList.add(friendData);
    }

    public void clearFriendsList() {
        addFriendsList.clear();
    }

}