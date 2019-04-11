package com.example.mohit.payroll.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mohit.payroll.LeaveApplicationActivity;
import com.example.mohit.payroll.LeaveBalanceActivity;
import com.example.mohit.payroll.R;
import com.example.mohit.payroll.models.LeaveBalanceModel;

import java.util.List;

public class LeaveBalanceAdapter extends RecyclerView.Adapter<LeaveBalanceAdapter.MyViewHolder> {

    private List<LeaveBalanceModel> leaveBalanceList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_leave_balance, tv_leave_name;
//        ImageView button_leave_apply;
            LinearLayout ll_row_balance;
        public MyViewHolder(View view) {
            super(view);
            tv_leave_name = (TextView) view.findViewById(R.id.tv_leave_name);
            tv_leave_balance = (TextView) view.findViewById(R.id.tv_leave_balance);
            ll_row_balance = (LinearLayout) view.findViewById(R.id.ll_row_balance);
        }
    }

    public LeaveBalanceAdapter(List<LeaveBalanceModel> leaveBalanceModel, Context context) {
        this.leaveBalanceList = leaveBalanceModel;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final LeaveBalanceModel leaveBalanceModel = leaveBalanceList.get(position);
        holder.tv_leave_name.setText(leaveBalanceModel.getLeaveName());
        holder.tv_leave_balance.setText(leaveBalanceModel.getBalance());
        holder.ll_row_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeaveBalanceActivity.selectedLeaveBalanceData = leaveBalanceModel;
                Intent intent = new Intent(context, LeaveApplicationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_leave_balance, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return leaveBalanceList.size();
    }
}