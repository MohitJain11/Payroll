package com.example.mohit.payroll.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohit.payroll.Extras.GenFunction;
import com.example.mohit.payroll.R;
import com.example.mohit.payroll.models.AttendanceHistoryModel;

import java.util.List;

public class AttendanceHistoryAdapter extends RecyclerView.Adapter<AttendanceHistoryAdapter.MyViewHolder> {

    private List<AttendanceHistoryModel> attendanceHistoryList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_attendance_date, tv_in_time, tv_out_time;

        public MyViewHolder(View view) {
            super(view);
            tv_attendance_date = (TextView) view.findViewById(R.id.tv_attendance_date);
            tv_in_time = (TextView) view.findViewById(R.id.tv_in_time);
            tv_out_time = (TextView) view.findViewById(R.id.tv_out_time);
        }
    }

    public AttendanceHistoryAdapter(List<AttendanceHistoryModel> attendanceHistoryList, Context context) {
        this.attendanceHistoryList = attendanceHistoryList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        AttendanceHistoryModel attendanceHistoryModel = attendanceHistoryList.get(position);
        String[] punchDate, inTime, outTime;
        if (attendanceHistoryModel.getPunchDate() != "null") {
            punchDate = attendanceHistoryModel.getPunchDate().split("T");
        } else
            punchDate = new String[]{"-", "-"};
        if (attendanceHistoryModel.getInTime() != "null") {
            inTime = attendanceHistoryModel.getInTime().split("T");
        } else
            inTime = new String[]{"-", "-"};
        if (attendanceHistoryModel.getOutTime() != "null") {
            outTime = attendanceHistoryModel.getOutTime().split("T");
        } else
            outTime = new String[]{"-", "-"};
        try {
            holder.tv_attendance_date.setText(GenFunction.convertDDMMYYYYTODDMMMYYYY(punchDate[0]));
            holder.tv_in_time.setText(inTime[1]);
            holder.tv_out_time.setText(outTime[1]);
            holder.tv_in_time.setTextColor(Color.parseColor(attendanceHistoryModel.getInColor()));
            holder.tv_out_time.setTextColor(Color.parseColor(attendanceHistoryModel.getOutColor()));
        } catch (Exception e) {
            holder.tv_attendance_date.setText(attendanceHistoryModel.getPunchDate());
            holder.tv_in_time.setText(attendanceHistoryModel.getInTime());
            holder.tv_out_time.setText(attendanceHistoryModel.getOutTime());
        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_attendance_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return attendanceHistoryList.size();
    }
}