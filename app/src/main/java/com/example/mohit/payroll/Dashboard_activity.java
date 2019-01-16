package com.example.mohit.payroll;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class Dashboard_activity extends AppCompatActivity {

    LinearLayout ll_attendance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activity);

        ll_attendance = findViewById(R.id.ll_attendance);
        ll_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AttendanceActivity.class);
                startActivity(intent);
            }
        });
    }
}
