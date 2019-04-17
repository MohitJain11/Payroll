package com.kspay.shrusthi.app.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kspay.shrusthi.app.Extras.GenFunction;
import com.kspay.shrusthi.app.Extras.Url;
import com.kspay.shrusthi.app.LeaveApplicationActivity;
import com.kspay.shrusthi.app.R;
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
import java.util.ArrayList;
import java.util.List;

public class LeaveAppliedAdapter extends RecyclerView.Adapter<LeaveAppliedAdapter.MyViewHolder> {

    private List<LeaveAppliedModel> leaveAppliedList;
    private Context context;
    private ProgressDialog progressDialog;
    private int selectedLeaveId = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_leave_from_to_date, tv_leave_apply_date, tv_leave_number, tv_leave_remark, tv_leave_name, tv_leave_status;
        public ImageView icon_delete_leave;

        public MyViewHolder(View view) {
            super(view);
            tv_leave_name = (TextView) view.findViewById(R.id.tv_leave_name);
            tv_leave_status = (TextView) view.findViewById(R.id.tv_leave_status);
            tv_leave_from_to_date = (TextView) view.findViewById(R.id.tv_leave_from_to_date);
            tv_leave_apply_date = (TextView) view.findViewById(R.id.tv_leave_apply_date);
            tv_leave_number = (TextView) view.findViewById(R.id.tv_leave_number);
            tv_leave_remark = (TextView) view.findViewById(R.id.tv_leave_remark);
            icon_delete_leave = (ImageView) view.findViewById(R.id.icon_delete_leave);
        }
    }

    public LeaveAppliedAdapter(List<LeaveAppliedModel> leaveAppliedList, Context context) {
        this.leaveAppliedList = leaveAppliedList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LeaveAppliedModel leaveAppliedModel = leaveAppliedList.get(position);
//        holder.tv_leave_type.setText(leaveAppliedModel.getLeaveType());
        holder.tv_leave_from_to_date.setText(leaveAppliedModel.getLeaveFrom()+"  to  "+leaveAppliedModel.getLeaveTo());
        holder.tv_leave_apply_date.setText(leaveAppliedModel.getApplyDate());
        holder.tv_leave_number.setText(leaveAppliedModel.getNoofLeave());
        holder.tv_leave_remark.setText(leaveAppliedModel.getLeaveRemark());
        holder.tv_leave_name.setText(leaveAppliedModel.getLeaveName());
        holder.tv_leave_status.setText(leaveAppliedModel.getLeaveStatus());
        if(leaveAppliedModel.getLeaveStatus().equals("APPROVED")){
            holder.tv_leave_status.setTextColor(Color.parseColor("#008577"));
            holder.icon_delete_leave.setVisibility(View.GONE);
        }
        holder.icon_delete_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Delete Applied Leave");
                alertDialog.setMessage("Are you sure, you want to delete applied leave?");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedLeaveId = Integer.parseInt(leaveAppliedModel.getLeaveId());
                        if (GenFunction.isNetworkAvailable(context)) {
                            new DeleteApplyLeaveData().execute();
                        } else {
                            Toast.makeText(context, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_leave_applied, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
         return leaveAppliedList.size();
    }

    class DeleteApplyLeaveData extends AsyncTask<String, Void, Void> {
        JSONObject jsonobject;
        JSONObject returnValue;
        JSONArray returnData;
        Boolean status;
        Boolean isExceptionOccured;
        String errorMessage, message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            HttpPost httppost = new HttpPost(Url.DeleteApplyLeaveData);
            httppost.setHeader("Content-Type", "application/json");
            try {
                JSONObject json = new JSONObject();
                json.put("LEAVEID", selectedLeaveId);
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
                        Toast.makeText(context, message , Toast.LENGTH_SHORT).show();
                        new GetApplyLeaveData().execute();
                    } else {
                        errorMessage = jsonobject.getString("errorMessage");
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Please try again later!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            } finally {
//                progressDialog.dismiss();
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
                json.put("ERPID", Integer.parseInt(LeaveApplicationActivity.id));
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
                        LeaveApplicationActivity.leaveAppliedAdapter.notifyDataSetChanged();
                    } else {
                        leaveAppliedList = new ArrayList<>();
                        LeaveApplicationActivity.leaveAppliedAdapter.notifyDataSetChanged();
                        errorMessage = jsonobject.getString("errorMessage");
                        LeaveApplicationActivity.tv_error_message.setText(errorMessage);
//                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    leaveAppliedList = new ArrayList<>();
                    LeaveApplicationActivity.leaveAppliedAdapter.notifyDataSetChanged();
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                leaveAppliedList = new ArrayList<>();
                LeaveApplicationActivity.leaveAppliedAdapter.notifyDataSetChanged();
            } finally {
                progressDialog.dismiss();
            }
        }
    }

}