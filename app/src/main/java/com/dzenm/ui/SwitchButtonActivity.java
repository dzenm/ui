package com.dzenm.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.SwitchButton;

public class SwitchButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_button);

        SwitchButton switchButton1 = findViewById(R.id.switch_button1);
        switchButton1.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        SwitchButton switchButton2 = findViewById(R.id.switch_button2);
        switchButton2.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        SwitchButton switchButton3 = findViewById(R.id.switch_button3);
        switchButton3.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        SwitchButton switchButton4 = findViewById(R.id.switch_button4);
        switchButton4.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        SwitchButton switchButton5 = findViewById(R.id.switch_button5);
        switchButton5.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });

        SwitchButton switchButton6 = findViewById(R.id.switch_button6);
        switchButton6.setEnabled(false);
        switchButton6.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton switchButton, boolean isChecked) {
                Toast.makeText(SwitchButtonActivity.this, "选中状态" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
    }
}