package com.example.usmile.user.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usmile.R;
import com.example.usmile.user.models.Tips;

import java.util.ArrayList;
import java.util.List;


public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder>{


    private List<Tips> tipsList;
    private Context context;

    String source;

    public TipsAdapter(List<Tips> newTips) {
        this.tipsList = newTips;
    }

    public TipsAdapter() {
        this.tipsList = new ArrayList<>();
    }

    public void setData(List<Tips> newTips) {
        this.tipsList = newTips;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tip_item_layout, parent, false);

        context = parent.getContext();

        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        Tips item = tipsList.get(position);

        if (item == null)
            return;

        holder.tipsAvatar.setImageResource(item.getResource());

        holder.titleTextView.setText(item.getTitle());
        holder.sourceTextView.setText(item.getSourceWebsWebsite());
        holder.timeTextView.setText("2 giờ trước");

        holder.contentTextView.setText(item.getShort_content());

        source = item.getUrl();
    }

    @Override
    public int getItemCount() {
        if (tipsList == null)
            return 0;
        return tipsList.size();
    }

    public class TipsViewHolder extends RecyclerView.ViewHolder {

        ImageView tipsAvatar;
        TextView titleTextView;
        TextView contentTextView;
        TextView sourceTextView, timeTextView;
        TextView checkDetailsButton;

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);

            tipsAvatar = (ImageView) itemView.findViewById(R.id.tipsAvatar);
            titleTextView = (TextView) itemView.findViewById(R.id.tipsTitle);
            contentTextView = (TextView) itemView.findViewById(R.id.contentTextView);

            sourceTextView = (TextView) itemView.findViewById(R.id.sourceTextView);
            timeTextView = (TextView) itemView.findViewById(R.id.timeTextView);
            checkDetailsButton = (TextView) itemView.findViewById(R.id.checkDetailButton);

            checkDetailsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = source;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                }
            });
        }


    }
}
