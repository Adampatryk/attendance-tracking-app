package com.example.attendance.ui.tabcontainer.lecture.lecturedetail;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.AttendanceModel;
import com.example.attendance.models.UserModel;
import com.example.attendance.util.DateTimeConversion;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class StudentAttendanceForLectureRecyclerViewAdapter extends ListAdapter<AttendanceModel, StudentAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder> {

	private StudentAttendanceForLectureRecyclerViewAdapter.OnItemClickListener listener;
	private static final String TAG = "StudentAttAdapter";
	private int colorPresent;
	private int colorAbsent;

	public StudentAttendanceForLectureRecyclerViewAdapter() {
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
	public StudentAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.student_recycler_view_item, parent, false);

		colorPresent = itemView.getContext().getResources().getColor(R.color.colorPresent);
		colorAbsent = itemView.getContext().getResources().getColor(R.color.colorAbsent);

		return new StudentAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull StudentAttendanceForLectureRecyclerViewAdapter.StudentAttendanceHolder holder, int position) {
		//Get the current lecture item
		AttendanceModel currentAttendanceModel = getItem(position);

		//If the attendance model states that the user is present, colour in green, otherwise red
		if (currentAttendanceModel.isPresent()) {
			holder.itemView.setBackgroundColor(colorPresent);
		} else {
			holder.itemView.setBackgroundColor(colorAbsent);
		}


		Date date = currentAttendanceModel.getDate();

		String time = "";
		//Check if date is null for records that are not yet scanned present
		if (date != null){
			time = DateTimeConversion.getTimeFromDate(date);
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
			txt_view_time = itemView.findViewById(R.id.item_student_info);

			//Only let lecturers find attendance for other students
			if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()) {
				itemView.setOnClickListener(v -> {
					int position = getAdapterPosition();
					//Make sure the listener is implemented, and that the click is on a valid position
					if (listener != null && position != RecyclerView.NO_POSITION) {
						listener.onItemClick(getItem(position));
					}
				});
			}
		}
	}

	public interface OnItemClickListener {
		void onItemClick(AttendanceModel attendanceModel);
	}

	public void setOnItemClickListener(StudentAttendanceForLectureRecyclerViewAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}