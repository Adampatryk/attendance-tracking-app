package com.example.attendance.ui.tabcontainer.lecture;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class LectureRecyclerViewAdapter extends ListAdapter<LectureModel, LectureRecyclerViewAdapter.LectureListHolder> {

    private OnItemClickListener listener;
    private static final String TAG = "LectureRecyclerViewAdap";

    public LectureRecyclerViewAdapter() {
        super(DIFF_CALLBACK);
    }

    //Used to determine if the recycler view needs to refresh its data and in what way
    private static final DiffUtil.ItemCallback<LectureModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<LectureModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull LectureModel oldItem, @NonNull LectureModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LectureModel oldItem, @NonNull LectureModel newItem) {

            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDate().equals(newItem.getDate()) &&
                    oldItem.isPresent() == newItem.isPresent();
        }
    };

    @NonNull
    @Override
    public LectureListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lecture_list_recycler_view_item, parent, false);
        return new LectureListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LectureListHolder holder, int position) {
        //Get the current lecture item
        LectureModel currentLecture = getItem(position);

        //If the user is an authenticated student
        if (SessionManager.isAuthenticated() && !SessionManager.getUser().isLecturer()){
            if (currentLecture.isPresent() == 1){
                holder.itemView.setBackgroundColor(Color.rgb(1, 50, 32));
            }
            else if (currentLecture.isPresent() == 0) {
                holder.itemView.setBackgroundColor(Color.rgb(50, 8, 1));
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String time = formatter.format(currentLecture.getDate());

        //Set the text of the item to the lecture details
        holder.txt_view_title.setText(currentLecture.getTitle());
        holder.txt_view_module.setText(currentLecture.getModule().getTitle());
        holder.txt_view_time.setText(time);
    }

    class LectureListHolder extends RecyclerView.ViewHolder {
        private MaterialTextView txt_view_title;
        private MaterialTextView txt_view_module;
        private MaterialTextView txt_view_time;

        public LectureListHolder(@NonNull View itemView){
            super(itemView);
            txt_view_title = itemView.findViewById(R.id.item_lbl_lecture_title);
            txt_view_module = itemView.findViewById(R.id.item_lbl_lecture_module);
            txt_view_time = itemView.findViewById(R.id.item_lbl_time);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                //Make sure the listener is implemented, and that the click is on a valid position
                if (listener != null && position!= RecyclerView.NO_POSITION){
                    listener.onItemClick(getItem(position));
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(LectureModel lectureModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
            this.listener = listener;
    }
}
