package com.kspay.shrusthi.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kspay.shrusthi.app.Adapters.LeaveAppliedAdapter;
import com.kspay.shrusthi.app.Extras.GenFunction;
import com.kspay.shrusthi.app.Extras.Url;
import com.kspay.shrusthi.app.models.LeaveAppliedModel;

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

public class LeaveApplicationActivity extends AppCompatActivity {

    private EditText from_date, to_date, remark;
    CheckBox checkbox_from_first_halfday, checkbox_to_first_halfday;
    RadioButton radioButton_first_half, radioButton_second_half;
    RadioGroup radioGroup_by_from_date;
    TextView tv_leave_name;
    TextView tv_approval_authority;
    Button button_submit;
    private ProgressDialog progressDialog;
    public static String id;
    LinearLayout ll_to_main;
    View view_previous_leave, view_apply_leave;
    TextView tv_previous_leave, tv_apply_leave;
    LinearLayout ll_main_apply_leave;
    LinearLayout ll_main_previous_leave;

    /////Notification List//////
    private List<LeaveAppliedModel> leaveAppliedList = new ArrayList<>();
    private RecyclerView recycler_view_leave_applied;
    public static LeaveAppliedAdapter leaveAppliedAdapter;

    ProgressBar leave_application_loader;
    public static TextView tv_error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_application);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        id = prefs.getString("id", null);

        progressDialog = new ProgressDialog(LeaveApplicationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        ll_main_previous_leave = findViewById(R.id.ll_main_previous_leave);
        ll_main_apply_leave = findViewById(R.id.ll_main_apply_leave);
        ll_main_previous_leave.setVisibility(View.GONE);
        view_apply_leave = findViewById(R.id.view_apply_leave);
        view_previous_leave = findViewById(R.id.view_previous_leave);
        view_previous_leave.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
        tv_apply_leave = findViewById(R.id.tv_apply_leave);
        tv_previous_leave = findViewById(R.id.tv_previous_leave);
        tv_apply_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_apply_leave.setBackgroundColor(getResources().getColor(R.color.white_color));
                view_previous_leave.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                ll_main_apply_leave.setVisibility(View.VISIBLE);
                ll_main_previous_leave.setVisibility(View.GONE);
            }
        });

        tv_previous_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view_previous_leave.setBackgroundColor(getResources().getColor(R.color.white_color));
                view_apply_leave.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                ll_main_apply_leave.setVisibility(View.GONE);
                ll_main_previous_leave.setVisibility(View.VISIBLE);
            }
        });



        tv_leave_name = findViewById(R.id.tv_leave_name);
        tv_approval_authority = findViewById(R.id.tv_approval_authority);
        tv_leave_name.setText(LeaveBalanceActivity.selectedLeaveBalanceData.getLeaveName());
        tv_approval_authority.setText(LeaveBalanceActivity.selectedLeaveBalanceData.getEmpRH());

        tv_error_message = findViewById(R.id.tv_error_message);
        leave_application_loader = findViewById(R.id.leave_application_loader);
        recycler_view_leave_applied = (RecyclerView) findViewById(R.id.recycler_view_leave_applied);
        tv_error_message.setVisibility(View.GONE);
        leave_application_loader.setVisibility(View.VISIBLE);
        recycler_view_leave_applied.setVisibility(View.GONE);
        new GetApplyLeaveData().execute();

        remark = findViewById(R.id.remark);
        from_date = findViewById(R.id.from_date);
        to_date = findViewById(R.id.to_date);
        from_date.setOnClickListener(fromClickListener);
        to_date.setOnClickListener(toClickListener);

        ll_to_main = findViewById(R.id.ll_to_main);
        checkbox_to_first_halfday = findViewById(R.id.checkbox_to_first_halfday);
        radioGroup_by_from_date = findViewById(R.id.radioGroup_by_from_date);
        radioButton_first_half = findViewById(R.id.radioButton_first_half);
        radioButton_second_half = findViewById(R.id.radioButton_second_half);
        checkbox_from_first_halfday = findViewById(R.id.checkbox_from_first_halfday);
        radioGroup_by_from_date.setVisibility(View.GONE);
        checkbox_from_first_halfday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkbox_from_first_halfday.isChecked()) {
                    radioGroup_by_from_date.setVisibility(View.VISIBLE);
                } else {
                    radioGroup_by_from_date.setVisibility(View.GONE);
                    radioGroup_by_from_date.clearCheck();
                    ll_to_main.setVisibility(View.VISIBLE);
                }
            }
        });

        radioButton_first_half.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButton_first_half.isChecked()) {
                    ll_to_main.setVisibility(View.GONE);
                    to_date.setText("");
                }
            }
        });

        radioButton_second_half.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButton_second_half.isChecked()) {
                    ll_to_main.setVisibility(View.VISIBLE);
                    to_date.setText("");
                }
            }
        });

        button_submit = findViewById(R.id.button_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(LeaveApplicationActivity.this);
                Boolean isError = false;
                if (from_date.getText().toString().equals(""))
                    isError = true;
//                if (to_date.getText().toString().equals(""))
//                    isError = true;
                if (remark.getText().toString().equals(""))
                    isError = true;
                if (checkbox_from_first_halfday.isChecked()) {
                    if (radioGroup_by_from_date.getCheckedRadioButtonId() != R.id.radioButton_first_half && radioGroup_by_from_date.getCheckedRadioButtonId() != R.id.radioButton_second_half) {
                        isError = true;
                    }
                }
                if (!(to_date.getText().toString().equals(""))) {
                    SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
                    try {
                        Date fromDate = format1.parse(from_date.getText().toString());
                        Date toDate = format1.parse(to_date.getText().toString());
                        if (fromDate.compareTo(toDate) > 0) {
                            isError = true;
                        }
                    } catch (Exception e) {
                        isError = true;
                    }
                }

                if (isError)
                    Toast.makeText(LeaveApplicationActivity.this, "Please fill all the details correctly!", Toast.LENGTH_SHORT).show();
                else {
                    if (GenFunction.isNetworkAvailable(LeaveApplicationActivity.this)) {
                        new SaveApplyLeaveData().execute();
                    } else {
                        Toast.makeText(LeaveApplicationActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


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
                        from_date.setText("0" + String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    } else {
                        from_date.setText(String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    }
//                    } else {
//                        if (dayOfMonth <= 9) {
//                            from_date.setText("0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        } else {
//                            from_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        }
//                    }
                    to_date.setText("");
                } else {
//                    if (month + 1 <= 9) {
                    if (dayOfMonth <= 9) {
                        to_date.setText("0" + String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    } else {
                        to_date.setText(String.valueOf(dayOfMonth) + "-" + GenFunction.MonthMMM[month] + "-" + String.valueOf(year));
                    }
//                    } else {
//                        if (dayOfMonth <= 9) {
//                            to_date.setText("0" + String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        } else {
//                            to_date.setText(String.valueOf(dayOfMonth) + "-" + String.valueOf(month + 1) + "-" + String.valueOf(year));
//                        }
//                    }
                }

            }
        }, year, month, day);
//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void resetForm(){
        from_date.setText("");
        to_date.setText("");
        remark.setText("");
        checkbox_from_first_halfday.setChecked(false);
        checkbox_to_first_halfday.setChecked(false);
        radioGroup_by_from_date.clearCheck();
    }

    class SaveApplyLeaveData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONObject returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;
        SimpleDateFormat format1 = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM-yyyy");
        String fromDate, toDate;
        Boolean isFromFirstHalfDay, isToHalfDay;
        Boolean fromFirstHalfDay = false, fromSecondHalfDay = false;
        long difference;
        float noofLeave;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.SaveApplyLeaveData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                Date dateFrom = format1.parse(from_date.getText().toString());
                fromDate = format2.format(dateFrom);
                if (!(to_date.getText().toString().equals(""))) {
                    Date dateTo = format1.parse(to_date.getText().toString());
                    toDate = format2.format(dateTo);
                    difference = dateTo.getTime() - dateFrom.getTime();
                    difference = (difference / 1000L / 60L / 60L / 24L) + 1;
                } else {
                    difference = 1;
                }
                isFromFirstHalfDay = checkbox_from_first_halfday.isChecked();
                isToHalfDay = checkbox_to_first_halfday.isChecked();
                if (radioGroup_by_from_date.getCheckedRadioButtonId() == R.id.radioButton_first_half) {
                    fromFirstHalfDay = true;
                    fromSecondHalfDay = false;
                }
                if (radioGroup_by_from_date.getCheckedRadioButtonId() == R.id.radioButton_second_half) {
                    fromSecondHalfDay = true;
                    fromFirstHalfDay = false;
                }
                if (to_date.getText().toString().equals("")) {
                    isToHalfDay = false;
                    checkbox_to_first_halfday.setChecked(false);
                }
                if (isFromFirstHalfDay && isToHalfDay) {
                    noofLeave = difference - 1;
                } else {
                    if (isFromFirstHalfDay || isToHalfDay) {
                        noofLeave = difference - 0.5f;
                    } else {
                        noofLeave = difference;
                    }
                }
                json.put("ERPID", Integer.parseInt(id));
                json.put("LEAVETYPE", LeaveBalanceActivity.selectedLeaveBalanceData.getLeaveType());
                json.put("LEAVEFROM", from_date.getText().toString());
                json.put("ISHALFDAY", isFromFirstHalfDay);
                json.put("ISFIRSTHALF", fromFirstHalfDay);
                json.put("ISSECONDHALF", fromSecondHalfDay);
                json.put("LEAVETO", ((to_date.getText().toString().equals("")) ? from_date.getText().toString() : to_date.getText().toString()));
                json.put("ISHALFDAYTO", isToHalfDay);
                json.put("NOOFLEAVE", noofLeave);
                json.put("LEAVEREMARK", remark.getText().toString());
                Log.i("req save", json.toString());
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
                        Log.i("response save", jsonobject.toString());
                        String message = jsonobject.getString("message");
                        Toast.makeText(LeaveApplicationActivity.this, message, Toast.LENGTH_SHORT).show();
                        new GetApplyLeaveData().execute();
                        resetForm();
                        view_previous_leave.setBackgroundColor(getResources().getColor(R.color.white_color));
                        view_apply_leave.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
                        ll_main_apply_leave.setVisibility(View.GONE);
                        ll_main_previous_leave.setVisibility(View.VISIBLE);
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LeaveApplicationActivity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

    class GetApplyLeaveData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONObject returnValue;
        JSONArray returnData;
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
            HttpPost httppost = new HttpPost(Url.GetApplyLeaveData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("ERPID", Integer.parseInt(id));
                Log.i("req save", json.toString());
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
                        Log.i("response save", jsonobject.toString());
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        leaveAppliedList = new ArrayList<>();
                        for (int i = 0; i < returnData.length(); i++) {
                            returnValue = returnData.getJSONObject(i);
                            LeaveAppliedModel appliedModel = new LeaveAppliedModel();
                            appliedModel.leaveId = returnValue.getString("leaveId");
                            appliedModel.erpId = returnValue.getString("erpId");
                            appliedModel.leaveType = returnValue.getString("leaveType");
                            appliedModel.leaveName = returnValue.getString("leaveName");
                            appliedModel.balance = returnValue.getString("balance");
                            appliedModel.leaveFrom = returnValue.getString("leaveFrom");
                            appliedModel.isHalfDay = returnValue.getString("isHalfDay");
                            appliedModel.isFirstHalf = returnValue.getString("isFirstHalf");
                            appliedModel.isSecondHalf = returnValue.getString("isSecondHalf");
                            appliedModel.leaveTo = returnValue.getString("leaveTo");
                            appliedModel.isHalfDayTo = returnValue.getString("isHalfDayTo");
                            appliedModel.noofLeave = returnValue.getString("noofLeave");
                            appliedModel.applyDate = returnValue.getString("applyDate");
                            appliedModel.leaveStatus = returnValue.getString("leaveStatus");
                            appliedModel.leaveRemark = returnValue.getString("leaveRemark");
                            appliedModel.leaveActionBy = returnValue.getString("leaveActionBy");
                            appliedModel.entryBy = returnValue.getString("entryBy");
                            appliedModel.pageSize = returnValue.getString("pageSize");
                            appliedModel.pageNumber = returnValue.getString("pageNumber");
                            appliedModel.totalCount = returnValue.getString("totalCount");
                            leaveAppliedList.add(appliedModel);
                        }
                        tv_error_message.setVisibility(View.GONE);
                        leave_application_loader.setVisibility(View.GONE);
                        recycler_view_leave_applied.setVisibility(View.VISIBLE);
                        leaveAppliedAdapter = new LeaveAppliedAdapter(leaveAppliedList, LeaveApplicationActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view_leave_applied.setLayoutManager(mLayoutManager);
                        recycler_view_leave_applied.setItemAnimator(new DefaultItemAnimator());
                        recycler_view_leave_applied.setAdapter(leaveAppliedAdapter);
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
//                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        tv_error_message.setVisibility(View.VISIBLE);
                        leave_application_loader.setVisibility(View.GONE);
                        recycler_view_leave_applied.setVisibility(View.GONE);
                        tv_error_message.setText(errorMessage);
                    }
                } else {
                    Toast.makeText(LeaveApplicationActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                    tv_error_message.setVisibility(View.VISIBLE);
                    leave_application_loader.setVisibility(View.GONE);
                    recycler_view_leave_applied.setVisibility(View.GONE);
                    tv_error_message.setText("No Leave Available");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                tv_error_message.setVisibility(View.VISIBLE);
                leave_application_loader.setVisibility(View.GONE);
                recycler_view_leave_applied.setVisibility(View.GONE);
                tv_error_message.setText("No Leave Available");
            } finally {
//                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LeaveApplicationActivity.this, Dashboard_activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
