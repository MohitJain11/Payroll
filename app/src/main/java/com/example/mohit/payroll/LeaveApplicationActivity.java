package com.example.mohit.payroll;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

public class LeaveApplicationActivity extends AppCompatActivity {

    private EditText fromDate,toDate,remark;
    private Spinner leaveType;
    private Calendar calendar;
    private int day,month,year;
    private String[] leaveItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);
        initialize();
        setCalender();
        setSpinnerData();
    }

    private void setSpinnerData() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, leaveItems);
        leaveType.setAdapter(adapter);
    }

    private void initialize(){
        fromDate = findViewById(R.id.from_date);
        toDate = findViewById(R.id.to_date);
        remark = findViewById(R.id.remark);
        leaveType = findViewById(R.id.leave_type);
        leaveItems = new String[]{"Bereavement", "Public holidays", "Vacation days","Child care","Personal leave","Adverse weather"};
    }

    private void setCalender() {
        fromDate.setOnClickListener(fromClickListener);
        toDate.setOnClickListener(toClickListener);
    }

    // from date listener
    View.OnClickListener fromClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DateDialog(1);
        }
    };
    // to date listener
    View.OnClickListener toClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DateDialog(0);
        }
    };

    private void DateDialog(  final int status) {
        DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                if(status == 1) {
                    fromDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                }
                else{
                    toDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
                }
            }};
        DatePickerDialog dpDialog=new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();
    }

    public void leaveSubmit(View view) {
        //submit function
    }
}
