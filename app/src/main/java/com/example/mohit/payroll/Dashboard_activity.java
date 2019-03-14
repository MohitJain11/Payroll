package com.example.mohit.payroll;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mohit.payroll.Adapters.NotificationAdapter;
import com.example.mohit.payroll.Extras.Url;
import com.example.mohit.payroll.models.NotificationModel;

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
import java.util.ArrayList;
import java.util.List;

public class Dashboard_activity extends Activity {

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


}
