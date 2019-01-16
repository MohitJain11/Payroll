package com.example.mohit.payroll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class AttendanceActivity extends AppCompatActivity {

    LinearLayout ll_out_button;
    LinearLayout ll_in_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        ll_out_button = findViewById(R.id.ll_out_button);
        ll_in_button = findViewById(R.id.ll_in_button);
        ll_in_button.setVisibility(View.VISIBLE);
        ll_out_button.setVisibility(View.GONE);
        ll_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_in_button.setVisibility(View.GONE);
                ll_out_button.setVisibility(View.VISIBLE);
            }
        });
    }
}
