package com.eatgo.eatgo;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MyEatery {

    private Context contextNow;
    private Button minusBtn;
    private Button plusBtn;
    private EditText scoreEdt;
    private CheckBox exceptCB;

    public MyEatery(Context context, Button minus, Button plus, EditText score, CheckBox except) {
        contextNow = context;
        minusBtn = minus;
        plusBtn = plus;
        scoreEdt = score;
        exceptCB = except;

        minusBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int scoreOn = Integer.parseInt(scoreEdt.getText().toString());
                if(scoreOn > 0) scoreOn--;
                else Toast.makeText(contextNow, "점수의 최솟값은 0입니다!", Toast.LENGTH_SHORT).show();
                scoreEdt.setText(Integer.toString(scoreOn));
            }
        });

        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int scoreOn = Integer.parseInt(scoreEdt.getText().toString());
                if(scoreOn < 10) scoreOn++;
                else Toast.makeText(contextNow, "점수의 최댓값은 10입니다!", Toast.LENGTH_SHORT).show();
                scoreEdt.setText(Integer.toString(scoreOn));
            }
        });

        exceptCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    minusBtn.setEnabled(false);
                    plusBtn.setEnabled(false);
                } else {
                    minusBtn.setEnabled(true);
                    plusBtn.setEnabled(true);
                }
            }
        });

    }


}
