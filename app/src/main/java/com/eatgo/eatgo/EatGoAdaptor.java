package com.eatgo.eatgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;


public class EatGoAdaptor extends BaseAdapter {

    private ArrayList<Eatery> eateryList = new ArrayList<Eatery>();
    private int rank;

    public void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public int getCount() {
        return eateryList.size();
    }

    @Override
    public Eatery getItem(int position) {
        return eateryList.get(position);
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
            convertView = inflater.inflate(R.layout.eatgo_list, parent, false);
        }

        TextView rank = (TextView) convertView.findViewById(R.id.rank);
        TextView eateryName = (TextView) convertView.findViewById(R.id.eateryName);
        TextView avg = (TextView) convertView.findViewById(R.id.avg);
        TextView whoExp = (TextView) convertView.findViewById(R.id.whoExp);

        Eatery eatery = eateryList.get(position);

        rank.setText(Integer.toString(++this.rank)+"위");
        eateryName.setText(eatery.getName());
        avg.setText(Double.toString(Math.round(eatery.getAvg()*10)/10.0)); // 평균값의 소수점 자릿수를 조정하는 문장

        String whoExpList="";

        for(int i=0; i<eatery.getNumExp(); i++) {
            whoExpList += eatery.getWhoExp().get(i);
            if(i < eatery.getNumExp()-1) whoExpList += ", ";
        } // whoExpList에 제외한 사람 이름 찍어주는 반복문

        whoExpList += " (" + eatery.getNumExp() + "명)";
        whoExp.setText(whoExpList);

        return convertView; // convertView 돌려주기
    }

    public void addEatery(Eatery eatery) {
        eateryList.add(eatery);
    }

    public void clearEatery() {
        eateryList.clear();
    }

}
