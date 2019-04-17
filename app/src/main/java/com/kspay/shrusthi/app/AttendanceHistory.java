package com.kspay.shrusthi.app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kspay.shrusthi.app.Adapters.AttendanceHistoryAdapter;
import com.kspay.shrusthi.app.Extras.GenFunction;
import com.kspay.shrusthi.app.Extras.Url;
import com.kspay.shrusthi.app.models.AttendanceHistoryModel;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttendanceHistory extends AppCompatActivity {

    private ProgressDialog progressDialog;
    String id;
    EditText et_start_date, et_end_date;
    Button button_ok;
    LinearLayout ll_list_head;

    /////Attendance History List//////
    private List<AttendanceHistoryModel> attendanceHistoryList = new ArrayList<>();
    private RecyclerView recycler_view_attendance_history;
    private AttendanceHistoryAdapter attendanceHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_history);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        id = prefs.getString("id", null);

        progressDialog = new ProgressDialog(AttendanceHistory.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        et_start_date = findViewById(R.id.et_start_date);
        et_end_date = findViewById(R.id.et_end_date);
        et_start_date.setOnClickListener(startClickListener);
        et_end_date.setOnClickListener(endClickListener);

        recycler_view_attendance_history = (RecyclerView) findViewById(R.id.recycler_view_attendance_history);
        button_ok = findViewById(R.id.button_ok);
        ll_list_head = findViewById(R.id.ll_list_head);
        ll_list_head.setVisibility(View.GONE);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isError = false;
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
                try {
                    Date startDate = format1.parse(et_start_date.getText().toString());
                    Date endDate = format1.parse(et_end_date.getText().toString());
                    if (startDate.compareTo(endDate) > 0) {
                        isError = true;
                    }
                } catch (Exception e) {
                    isError = true;
                }
                if (isError) {
                    Toast.makeText(AttendanceHistory.this, "Please fill correct date!", Toast.LENGTH_SHORT).show();
                } else {
                    if (GenFunction.isNetworkAvailable(AttendanceHistory.this)) {
                        new getcurrentattendancehistory().execute();
                    } else {
                        Toast.makeText(AttendanceHistory.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
        String currentDate = sdfDate.format(new Date());
        et_start_date.setText("01-"+GenFunction.MonthMMM[month]+"-"+year);
        et_end_date.setText(currentDate);
        if (GenFunction.isNetworkAvailable(AttendanceHistory.this)) {
            new getcurrentattendancehistory().execute();
        } else{
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }

    // from date listener
    View.OnClickListener startClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DateDialog(1);
        }
    };
    // to date listener
    View.OnClickListener endClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DateDialog(0);
        }
    };


    private void DateDialog(final int status) {
        int day, month, year;
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (status == 1) {
//                    if (month + 1 <= 9) {
                    if (dayOfMonth <= 9) {
                        et_start_date.setText("0" + String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    } else {
                        et_start_date.setText(String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    }
//                    } else {
//                        if (dayOfMonth <= 9) {
//                            et_start_date.setText("0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        } else {
//                            et_start_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        }
//                    }
                    et_end_date.setText("");
                } else {
//                    if (month + 1 <= 9) {
                    if (dayOfMonth <= 9) {
                        et_end_date.setText("0" + String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    } else {
                        et_end_date.setText(String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    }
//                    } else {
//                        if (dayOfMonth <= 9) {
//                            et_end_date.setText("0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        } else {
//                            et_end_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        }
//                    }
                }
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    class getcurrentattendancehistory extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONArray returnData;
        JSONObject returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        String startDate, endDate;
//        SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
//        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.getcurrentattendancehistory);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
//                Date date = format1.parse(et_start_date.getText().toString());
//                startDate = format2.format(date);
//                date = format1.parse(et_end_date.getText().toString());
//                endDate = format2.format(date);
                json.put("startdate", et_start_date.getText().toString());
                json.put("enddate", et_end_date.getText().toString());
                json.put("employeeId", Integer.parseInt(id));
                httppost.setEntity(new ByteArrayEntity(json.toString().replaceAll("\\\\", "").replaceAll("\"\"", "\"").getBytes("UTF8")));
                String responseBody = httpclient.execute(httppost, responseHandler);
                jsonobject = new JSONObject(responseBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } finally {
                progressDialog.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jsonobject != null) {
                    isExceptionOccured = jsonobject.getBoolean("isExceptionOccured");
                    status = jsonobject.getBoolean("status");
                    if (status && !isExceptionOccured) {
                        ll_list_head.setVisibility(View.VISIBLE);
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        attendanceHistoryList = new ArrayList<>();
                        for (int i = 0; i < returnData.length(); i++) {
                            returnValue = returnData.getJSONObject(i);
                            AttendanceHistoryModel attendanceHistoryModel = new AttendanceHistoryModel();
                            attendanceHistoryModel.id = returnValue.getString("id");
                            attendanceHistoryModel.employeeId = returnValue.getString("employeeId");
                            attendanceHistoryModel.month = returnValue.getString("month");
                            attendanceHistoryModel.year = returnValue.getString("year");
                            attendanceHistoryModel.punchDate = returnValue.getString("punchDate");
                            attendanceHistoryModel.punchTime = returnValue.getString("punchTime");
                            attendanceHistoryModel.punchImage = returnValue.getString("punchImage");
                            attendanceHistoryModel.gpsLocation = returnValue.getString("gpsLocation");
                            attendanceHistoryModel.punchStatus = returnValue.getString("punchStatus");
                            attendanceHistoryModel.entryBy = returnValue.getString("entryBy");
                            attendanceHistoryModel.mode = returnValue.getString("mode");
                            attendanceHistoryModel.inDate = returnValue.getString("inDate");
                            attendanceHistoryModel.inTime = returnValue.getString("inTime");
                            attendanceHistoryModel.outDate = returnValue.getString("outDate");
                            attendanceHistoryModel.outTime = returnValue.getString("outTime");
                            attendanceHistoryModel.startDate = returnValue.getString("startDate");
                            attendanceHistoryModel.endDate = returnValue.getString("endDate");
                            attendanceHistoryModel.inImage = returnValue.getString("inImage");
                            attendanceHistoryModel.outImage = returnValue.getString("outImage");
                            attendanceHistoryModel.inLocation = returnValue.getString("inLocation");
                            attendanceHistoryModel.outLocation = returnValue.getString("outLocation");
                            attendanceHistoryModel.totalCount = returnValue.getString("totalCount");
                            attendanceHistoryModel.inColor = returnValue.getString("inColor");
                            attendanceHistoryModel.outColor = returnValue.getString("outColor");
                            attendanceHistoryList.add(attendanceHistoryModel);
                        }
                        attendanceHistoryAdapter = new AttendanceHistoryAdapter(attendanceHistoryList, getApplicationContext());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view_attendance_history.setLayoutManager(mLayoutManager);
                        recycler_view_attendance_history.setItemAnimator(new DefaultItemAnimator());
                        recycler_view_attendance_history.setAdapter(attendanceHistoryAdapter);
                    } else {
                        ll_list_head.setVisibility(View.GONE);
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AttendanceHistory.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }
}
