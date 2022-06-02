package com.example.usmile.user.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usmile.R;
import com.example.usmile.user.models.HealthRecord;

import java.util.ArrayList;
import java.util.List;

public class MultiHealthRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static int WAITING_HEALTH_RECORD = 0;
    private static int ACCEPTED_HEALTH_RECORD = 1;

    private List<HealthRecord> healthRecords;
    private Context context;

    public MultiHealthRecordAdapter(List<HealthRecord> healthRecords) {
        this.healthRecords = healthRecords;
    }

    public MultiHealthRecordAdapter() {
        this.healthRecords = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (ACCEPTED_HEALTH_RECORD == viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_record_accepted_item_layout, parent, false);

            context = parent.getContext();

            return new AcceptedHealthRecordViewHolder(view);

        } else if (WAITING_HEALTH_RECORD == viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.health_record_waiting_item_layout, parent, false);

            context = parent.getContext();

            return new WaitingHealthRecordViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        HealthRecord item = healthRecords.get(position);

        if (item == null)
            return;

        if (ACCEPTED_HEALTH_RECORD == holder.getItemViewType()) {
            AcceptedHealthRecordViewHolder acceptedHealthRecordViewHolder = (AcceptedHealthRecordViewHolder) holder;

            acceptedHealthRecordViewHolder.dateTimeTextView.setText(item.getSentDate());

            // should add this property to Health Record ?
            acceptedHealthRecordViewHolder.statusDetailTextView.setText("BS Nguyễn Tấn Hưng đã tiếp nhận");
        } else if (WAITING_HEALTH_RECORD == holder.getItemViewType()) {

            WaitingHealthRecordViewHolder waitingHealthRecordViewHolder = (WaitingHealthRecordViewHolder) holder;

            waitingHealthRecordViewHolder.sentDateTextView.setText(item.getSentDate());
        }

    }

    @Override
    public int getItemCount() {
        if (healthRecords == null)
            return 0;
        return healthRecords.size();
    }

    @Override
    public int getItemViewType(int position) {
        HealthRecord item = healthRecords.get(position);

        if (true == item.isAccepted())
            return ACCEPTED_HEALTH_RECORD;
        else
            return WAITING_HEALTH_RECORD;
    }

    public class AcceptedHealthRecordViewHolder extends RecyclerView.ViewHolder {

        TextView dateTimeTextView;
        TextView statusDetailTextView;
        TextView checkPictureButton;
        TextView checkAdviceButton;

        public AcceptedHealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTimeTextView = (TextView) itemView.findViewById(R.id.dateTimeTextView);
            statusDetailTextView = (TextView) itemView.findViewById(R.id.statusDetailTextView);
            checkPictureButton = (TextView) itemView.findViewById(R.id.leftButton);
            checkAdviceButton = (TextView) itemView.findViewById(R.id.rightButton);
        }
    }

    public class WaitingHealthRecordViewHolder extends RecyclerView.ViewHolder {

        TextView sentDateTextView;
        TextView editButton;
        TextView cancelButton;

        public WaitingHealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            sentDateTextView = (TextView) itemView.findViewById(R.id.sentDateTextView);
            editButton = (TextView) itemView.findViewById(R.id.editButton);
            cancelButton = (TextView) itemView.findViewById(R.id.cancelButton);
        }
    }
}
