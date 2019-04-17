package com.kspay.shrusthi.app;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kspay.shrusthi.app.Adapters.LeaveBalanceAdapter;
import com.kspay.shrusthi.app.Extras.GenFunction;
import com.kspay.shrusthi.app.Extras.Url;
import com.kspay.shrusthi.app.models.LeaveBalanceModel;

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

public class LeaveBalanceActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    String id;

    /////Notification List//////
    private List<LeaveBalanceModel> leaveBalanceList = new ArrayList<>();
    private RecyclerView recycler_view_leave_balance;
    private LeaveBalanceAdapter leaveBalanceAdapter;
    public static LeaveBalanceModel selectedLeaveBalanceData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_balance);

        SharedPreferences prefs = getSharedPreferences("LoginData", MODE_PRIVATE);
        id = prefs.getString("id", null);

        progressDialog = new ProgressDialog(LeaveBalanceActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        recycler_view_leave_balance = (RecyclerView) findViewById(R.id.recycler_view_leave_balance);
        if (GenFunction.isNetworkAvailable(LeaveBalanceActivity.this)) {
            new GetApplyLeaveBalance().execute();
        } else {
            Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        }
    }


    class GetApplyLeaveBalance extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONArray returnData;
        JSONObject returnValue;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.GetApplyLeaveBalance);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("ERPID", Integer.parseInt(id));
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
                        returnData = new JSONArray(jsonobject.getString("returnValue"));
                        for (int i = 0; i < returnData.length(); i++) {
                            returnValue = returnData.getJSONObject(i);
                            String leaveId = returnValue.getString("leaveId");
                            String erpId = returnValue.getString("erpId");
                            String leaveType = returnValue.getString("leaveType");
                            String leaveName = returnValue.getString("leaveName");
                            String balance = returnValue.getString("balance");
                            String leaveFrom = returnValue.getString("leaveFrom");
                            String isHalfDay = returnValue.getString("isHalfDay");
                            String isFirstHalf = returnValue.getString("isFirstHalf");
                            String isSecondHalf = returnValue.getString("isSecondHalf");
                            String leaveTo = returnValue.getString("leaveTo");
                            String isHalfDayTo = returnValue.getString("isHalfDayTo");
                            String noofLeave = returnValue.getString("noofLeave");
                            String applyDate = returnValue.getString("applyDate");
                            String leaveStatus = returnValue.getString("leaveStatus");
                            String leaveRemark = returnValue.getString("leaveRemark");
                            String leaveActionBy = returnValue.getString("leaveActionBy");
                            String entryBy = returnValue.getString("entryBy");
                            String pageSize = returnValue.getString("pageSize");
                            String pageNumber = returnValue.getString("pageNumber");
                            String totalCount = returnValue.getString("totalCount");
                            String empRH = returnValue.getString("empRH");

                            LeaveBalanceModel leaveBalanceModel = new LeaveBalanceModel(leaveId, erpId, leaveType, leaveName, balance,
                                    leaveFrom, isHalfDay, isFirstHalf, isSecondHalf, leaveTo,
                                    isHalfDayTo, noofLeave, applyDate, leaveStatus, leaveRemark,
                                    leaveActionBy, entryBy, pageSize, pageNumber, totalCount, empRH);
                            leaveBalanceList.add(leaveBalanceModel);
                        }
                        leaveBalanceAdapter = new LeaveBalanceAdapter(leaveBalanceList, getApplicationContext());
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recycler_view_leave_balance.setLayoutManager(mLayoutManager);
                        recycler_view_leave_balance.setItemAnimator(new DefaultItemAnimator());
                        recycler_view_leave_balance.setAdapter(leaveBalanceAdapter);
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(LeaveBalanceActivity.this, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                progressDialog.dismiss();
            }
        }
    }
}
