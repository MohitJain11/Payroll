package com.kspay.shrusthi.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kspay.shrusthi.app.Adapters.NotificationAdapter;
import com.kspay.shrusthi.app.DB_Handler.AttendanceSaveHandler;
import com.kspay.shrusthi.app.Extras.GenFunction;
import com.kspay.shrusthi.app.Extras.Url;
import com.kspay.shrusthi.app.models.AttendanceSaveModel;
import com.kspay.shrusthi.app.models.MonthsModel;
import com.kspay.shrusthi.app.models.NotificationModel;

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

public class Dashboard_activity extends Activity implements AdapterView.OnItemSelectedListener {

    LinearLayout ll_attendance;
    LinearLayout ll_leave_application;
    LinearLayout ll_attendance_history;
    LinearLayout ll_profile;
    LinearLayout ll_log_out;
    AlertDialog.Builder builder;
    TextView tv_dashboard_name;
    TextView tv_error_message;
    private ProgressDialog progressDialog;
    ProgressBar progressBar;
    AttendanceSaveHandler attendanceSaveHandler;
    SQLiteDatabase sqLiteDatabase;
    ArrayList<AttendanceSaveModel> saveAttendanceList = null;
    AttendanceSaveModel tempSaveAttendanceModel = new AttendanceSaveModel();
    int temaIndex = 0;
    String id;

    LinearLayout ll_salary_slip_download;
    ArrayList<MonthsModel> monthList = null;
    String monthNamesList[];
    String selectedMonthName;
    String downloadPdfUrl = "";
    /////Notification List//////
    private List<NotificationModel> notificationList = new ArrayList<>();
    private RecyclerView recycler_view_notification;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_activity);

        progressDialog = new ProgressDialog(Dashboard_activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        attendanceSaveHandler = new AttendanceSaveHandler(getApplicationContext());

        tv_error_message = findViewById(R.id.tv_error_message);
        progressBar = findViewById(R.id.notification_loader);
        recycler_view_notification = (RecyclerView) findViewById(R.id.recycler_view_notification);
        tv_error_message.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        recycler_view_notification.setVisibility(View.GONE);
        new GetNoticeCircularData().execute();

        tv_dashboard_name = findViewById(R.id.tv_dashboard_name);
        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        String restoredText = prefs.getString("userName", null);
        id = prefs.getString("id", null);
        if (restoredText != null) {
            tv_dashboard_name.setText("Hello " + restoredText);
        }

        ll_attendance = findViewById(R.id.ll_attendance);
        ll_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AttendanceActivity.class);
                startActivity(intent);
            }
        });

        ll_leave_application = findViewById(R.id.ll_leave_application);
        ll_leave_application.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LeaveBalanceActivity.class);
                startActivity(intent);
            }
        });

        ll_attendance_history = findViewById(R.id.ll_attendance_history);
        ll_attendance_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AttendanceHistory.class);
                startActivity(intent);
            }
        });

        LayoutInflater li = LayoutInflater.from(Dashboard_activity.this);
//        View promptsView = li.inflate(R.layout.date_dialog, null);
        ll_salary_slip_download = findViewById(R.id.ll_salary_slip_download);
        new GetMonthName().execute();
        ll_salary_slip_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSalarySlipDownloadPopup();
            }
        });

        ll_profile = findViewById(R.id.ll_profile);
        ll_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        ll_log_out = findViewById(R.id.ll_log_out);
        ll_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Dashboard_activity.this);
                // Setting Dialog Title
                alertDialog.setTitle("Log Out");
                // Setting Dialog Message
                alertDialog.setMessage("Are you sure, you want to logout?");
                // Setting Icon to Dialog
//                alertDialog.setIcon(R.drawable.tick);
                alertDialog.setPositiveButton("Log Out", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences("LoginData", MODE_PRIVATE).edit();
                        editor.putString("employeeId", null);
                        editor.putString("isAdmin", null);
                        editor.putString("isRH", null);
                        editor.putString("isGH", null);
                        editor.putString("userName", null);
                        editor.putString("password", null);
                        editor.putString("mode", null);
                        editor.putString("isLogin", null);
                        editor.putString("empType", null);
                        editor.putString("id", null);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });

    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        selectedMonthName = monthNamesList[position];
//        Toast.makeText(this, selectedMonthName, Toast.LENGTH_SHORT).show();
        downloadPdfUrl = "";
        new GetSalarySlipData().execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GenFunction.isNetworkAvailable(Dashboard_activity.this)) {
            SaveAttendanceData();
        }
    }

    private void SaveAttendanceData() {
        sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
        Cursor cursor = attendanceSaveHandler.getNoSyncAttendance(sqLiteDatabase, id);
        saveAttendanceList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                if ((cursor.getString(cursor.getColumnIndex("isSync"))).equals("N") && (cursor.getString(cursor.getColumnIndex("employeeId"))).equals(id)) {
                    AttendanceSaveModel attendanceSaveModel = new AttendanceSaveModel();
                    attendanceSaveModel.employeeId = Integer.parseInt(cursor.getString(cursor.getColumnIndex("employeeId")));
                    attendanceSaveModel.month = Integer.parseInt(cursor.getString(cursor.getColumnIndex("month")));
                    attendanceSaveModel.year = Integer.parseInt(cursor.getString(cursor.getColumnIndex("year")));
                    attendanceSaveModel.punchDate = cursor.getString(cursor.getColumnIndex("punchDate"));
                    attendanceSaveModel.punchTime = cursor.getString(cursor.getColumnIndex("punchTime"));
                    attendanceSaveModel.gpslocation = cursor.getString(cursor.getColumnIndex("gpslocation"));
                    attendanceSaveModel.punchStatus = cursor.getString(cursor.getColumnIndex("punchStatus"));
                    attendanceSaveModel.punchImage = cursor.getString(cursor.getColumnIndex("punchImage"));
                    attendanceSaveModel.isSync = cursor.getString(cursor.getColumnIndex("isSync"));
                    saveAttendanceList.add(attendanceSaveModel);
                }
            } while (cursor.moveToNext());
        }

        if (saveAttendanceList.size() > 0) {
            tempSaveAttendanceModel = saveAttendanceList.get(0);
            temaIndex++;
            new SaveAttendanceData().execute();
        }
    }


    class GetNoticeCircularData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONArray returnData;
        JSONObject returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetNoticeCircularData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                httppost.setEntity(new ByteArrayEntity(json.toString().replaceAll("\\\\", "").replaceAll("\"\"", "\"").getBytes("UTF8")));
                String responseBody = httpclient.execute(httppost, responseHandler);
                jsonobject = new JSONObject(responseBody);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(LoginActivity.this, "Something went worng!", Toast.LENGTH_SHORT).show();
            } finally {
//                progressDialog.dismiss();
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
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        for (int i = 0; i < returnData.length(); i++) {
                            returnValue = returnData.getJSONObject(i);
                            String noticeId = returnValue.getString("noticeId");
                            String noticeName = returnValue.getString("noticeName");
                            String entryBy = returnValue.getString("entryBy");
                            String pageSize = returnValue.getString("pageSize");
                            String pageNumber = returnValue.getString("pageNumber");
                            String totalCount = returnValue.getString("totalCount");
                            NotificationModel notificationModel = new NotificationModel(noticeId, noticeName, entryBy, pageSize, pageNumber, totalCount);
                            notificationList.add(notificationModel);
                        }
                        tv_error_message.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        recycler_view_notification.setVisibility(View.VISIBLE);
                        notificationAdapter = new NotificationAdapter(notificationList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view_notification.setLayoutManager(mLayoutManager);
                        recycler_view_notification.setItemAnimator(new DefaultItemAnimator());
                        recycler_view_notification.setAdapter(notificationAdapter);

                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
//                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        tv_error_message.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        recycler_view_notification.setVisibility(View.GONE);
                        tv_error_message.setText(errorMessage);
                    }
                } else {
//                    Toast.makeText(Dashboard_activity.this, "No Internet Connection!", Toast.LENGTH_LONG).show();
                    tv_error_message.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recycler_view_notification.setVisibility(View.GONE);
                    tv_error_message.setText("No Record Found!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
//                progressDialog.dismiss();
            }
        }
    }


    class SaveAttendanceData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject, returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
        String currentDate = sdfDate.format(new Date());
        String currentTime = sdfTime.format(new Date());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.SaveAttendanceData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("employeeId", tempSaveAttendanceModel.employeeId);
                json.put("month", tempSaveAttendanceModel.month);
                json.put("year", tempSaveAttendanceModel.year);
                json.put("punchDate", tempSaveAttendanceModel.punchDate);
                json.put("punchTime", tempSaveAttendanceModel.punchTime);
                json.put("gpslocation", tempSaveAttendanceModel.gpslocation);
                json.put("punchStatus", tempSaveAttendanceModel.punchStatus);
                json.put("punchImage", tempSaveAttendanceModel.punchImage);
                Log.i("json req saveattendance", json.toString());
                httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
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
                        progressDialog.dismiss();
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
//                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    if (temaIndex < saveAttendanceList.size()) {
                        sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
                        attendanceSaveHandler.updateNoSyncAttendance(sqLiteDatabase, tempSaveAttendanceModel.punchDate, tempSaveAttendanceModel.punchTime, id);
                        temaIndex++;
                        tempSaveAttendanceModel = saveAttendanceList.get(temaIndex - 1);
                        new SaveAttendanceData().execute();
                    } else {
                        if (temaIndex == saveAttendanceList.size()) {
                            sqLiteDatabase = attendanceSaveHandler.getWritableDatabase();
                            attendanceSaveHandler.updateNoSyncAttendance(sqLiteDatabase, tempSaveAttendanceModel.punchDate, tempSaveAttendanceModel.punchTime, id);
                            temaIndex = 0;
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Dashboard_activity.this, "Attendance not Synced, Please close app and come back again!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    public void openSalarySlipDownloadPopup() {
        LayoutInflater li = LayoutInflater.from(Dashboard_activity.this);
        View promptsView = li.inflate(R.layout.date_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Dashboard_activity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        final Button button_salary_download = (Button) promptsView.findViewById(R.id.button_salary_download);
        final Spinner date_selection_spinner = (Spinner) promptsView.findViewById(R.id.date_selection_spinner);

        date_selection_spinner.setOnItemSelectedListener(Dashboard_activity.this);
        ArrayAdapter aa = new ArrayAdapter(Dashboard_activity.this, android.R.layout.simple_spinner_item, monthNamesList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        date_selection_spinner.setAdapter(aa);

        button_salary_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoadPdf();
                alertDialog.dismiss();
            }
        });

        // show it
        alertDialog.show();
    }

    /////List of Date to fill Spinner/////
    class GetMonthName extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject, returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        JSONArray returnData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetMonthName);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("MODE", "getmonthname");
                Log.i("json req getMonths", json.toString());
                httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
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
                        progressDialog.dismiss();
                        monthList = new ArrayList<>();
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        monthNamesList = new String[returnData.length()];
                        for (int i = 0; i < returnData.length(); i++) {
                            returnValue = returnData.getJSONObject(i);
                            monthNamesList[i] = returnValue.getString("value");
                            int id = returnValue.getInt("id");
                            String value = returnValue.getString("value");
                            String mode = returnValue.getString("mode");
                            MonthsModel monthsModel = new MonthsModel(id, value, mode);
                            monthList.add(monthsModel);
                        }
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        progressDialog.dismiss();
                        Toast.makeText(Dashboard_activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Dashboard_activity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    /////Get Pdf link/////
    class GetSalarySlipData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject, returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        JSONArray returnData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetSalarySlipData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("monthId", getMonthId());
//                json.put("reportname", "salaryslip");
//                json.put("userId", id);
                json.put("ERPID", id);
                Log.i("json req getMonths", json.toString());
                httppost.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
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
                        progressDialog.dismiss();
//                        monthList = new ArrayList<>();
                        returnValue = jsonobject.getJSONObject("returnValue");
                        downloadPdfUrl = returnValue.getString("downloadURL");
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        progressDialog.dismiss();
//                        Toast.makeText(Dashboard_activity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Dashboard_activity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        }
    }

    private int getMonthId() {
        for (int i = 0; i < monthList.size(); i++) {
            MonthsModel monthsModel = monthList.get(i);
            if(selectedMonthName.equals(monthsModel.getValue())){
                return monthsModel.getId();
            }
        }
        return 0;
    }

    private void downLoadPdf(){
        if(!downloadPdfUrl.equals("")){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(downloadPdfUrl)));
        } else {
            Toast.makeText(this, "No PDF Available", Toast.LENGTH_SHORT).show();
        }
    }


}
