package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementsViewHolder> {

    private List<Announcement> announcements;

    private Context context;

    public AnnouncementsAdapter(List<Announcement> announcements, Context context) {
        this.announcements = announcements;
    }

    public class AnnouncementsViewHolder extends RecyclerView.ViewHolder {
        private TextView description, userName, date, expireDate;
        private ImageView image;
        private CardView cardView;
        private ConstraintLayout constraintLayout;

        public AnnouncementsViewHolder(View view) {
            super(view);
            description = (TextView) view.findViewById(R.id.descriptionOnCard);
            date = (TextView) view.findViewById(R.id.dateOnCard);
            userName = (TextView) view.findViewById(R.id.userNameOnCard);
            expireDate = (TextView) view.findViewById(R.id.expireDateOnCard);
            image = (ImageView) view.findViewById(R.id.imageViewOnCard);
            constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraintLayoutOnrecycler);
            cardView = (CardView) view.findViewById(R.id.card_viewOnRecycler);

        }

        public void setData(Announcement announcement){
            this.description.setText(announcement.getDescription());
            this.date.setText(announcement.getDateTime());
            this.userName.setText(announcement.getUserName());
            this.expireDate.setText(announcement.getExpireDate());
            //this.image.setBackgroundResource(announcement.getImage());
        }
    }




    @Override
    public AnnouncementsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_announcement_item, parent, false);

        return new AnnouncementsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnnouncementsViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.setData(announcement);
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }
}

