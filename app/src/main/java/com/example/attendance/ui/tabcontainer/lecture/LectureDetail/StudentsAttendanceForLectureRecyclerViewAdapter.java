package com.example.attendance.ui.tabcontainer.lecture.LectureDetail;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.LectureModel;
import com.example.attendance.models.UserModel;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class StudentsAttendanceForLectureRecyclerViewAdapter extends ListAdapter<AttendanceModel, StudentsAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder> {

	private StudentsAttendanceForLectureRecyclerViewAdapter.OnItemClickListener listener;
	private static final String TAG = "StudentAttAdapter";

	public StudentsAttendanceForLectureRecyclerViewAdapter() {
		super(DIFF_CALLBACK);
	}

	//Used to determine if the recycler view needs to refresh its data and in what way
	private static final DiffUtil.ItemCallback<AttendanceModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<AttendanceModel>() {
		@Override
		public boolean areItemsTheSame(@NonNull AttendanceModel oldItem, @NonNull AttendanceModel newItem) {
			return oldItem.getStudent() == newItem.getStudent();
		}

		@Override
		public boolean areContentsTheSame(@NonNull AttendanceModel oldItem, @NonNull AttendanceModel newItem) {

			return (oldItem.getLectureId() == newItem.getLectureId()) &&
					oldItem.getDate().equals(newItem.getDate()) &&
					oldItem.isPresent() == newItem.isPresent();
		}
	};

	@NonNull
	@Override
	public StudentsAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.student_attended_recycler_view_item, parent, false);
		return new StudentsAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull StudentsAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder holder, int position) {
		//Get the current lecture item
		AttendanceModel currentAttendanceModel = getItem(position);

		//If the attendance model states that the user is present, colour in green, otherwise red
		if (currentAttendanceModel.isPresent()) {
			holder.itemView.setBackgroundColor(Color.rgb(1, 50, 32));
		} else {
			holder.itemView.setBackgroundColor(Color.rgb(50, 8, 1));
		}

		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

		Date date = currentAttendanceModel.getDate();

		String time = "";
		//Check if date is null for records that are not yet scanned present
		if (date != null){
			time = formatter.format(date);
		} else {
			time = "N/A";
		}

		String fullName = "";

		UserModel student = currentAttendanceModel.getStudent();

		if (student.getId() != SessionManager.getUser().getId()){
			fullName = student.getFirstName() + " " + student.getLastName() +
					" (" + student.getUsername() + ")";
		} else {
			fullName = "You (" + student.getUsername() + ")";
		}

		//Set the text of the item to the lecture details
		holder.txt_view_student_name.setText(fullName);
		holder.txt_view_time.setText(time);
	}

	class StudentAttendanceHolder extends RecyclerView.ViewHolder {
		private MaterialTextView txt_view_student_name;
		private MaterialTextView txt_view_time;

		public StudentAttendanceHolder(@NonNull View itemView) {
			super(itemView);
			txt_view_student_name = itemView.findViewById(R.id.item_lbl_student_name);
			txt_view_time = itemView.findViewById(R.id.item_student_time);
		}
	}

	public interface OnItemClickListener {
		void onItemClick(AttendanceModel attendanceModel);
	}

	public void setOnItemClickListener(StudentsAttendanceForLectureRecyclerViewAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}