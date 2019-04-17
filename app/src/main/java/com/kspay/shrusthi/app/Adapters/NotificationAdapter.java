package com.kspay.shrusthi.app.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kspay.shrusthi.app.R;
import com.kspay.shrusthi.app.models.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private List<NotificationModel> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_notice_name, tv_total_count;

        public MyViewHolder(View view) {
            super(view);
            tv_notice_name = (TextView) view.findViewById(R.id.tv_notice_name);
//            tv_total_count = (TextView) view.findViewById(R.id.tv_total_count);
        }
    }

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NotificationModel notificationModel = notificationList.get(position);
        holder.tv_notice_name.setText(notificationModel.getNoticeName());
//        holder.tv_total_count.setText(notificationModel.getTotalCount());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}