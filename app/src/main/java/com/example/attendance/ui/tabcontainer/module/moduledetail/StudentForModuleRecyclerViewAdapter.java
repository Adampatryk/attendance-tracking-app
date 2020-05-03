package com.example.attendance.ui.tabcontainer.module.moduledetail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.UserModel;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class StudentForModuleRecyclerViewAdapter  extends ListAdapter<UserModel, StudentForModuleRecyclerViewAdapter.StudentHolder> {

	private StudentForModuleRecyclerViewAdapter.OnItemClickListener listener;
	private static final String TAG = "StudentModuleAdapter";

	public StudentForModuleRecyclerViewAdapter() {
		super(DIFF_CALLBACK);
	}

	//Used to determine if the recycler view needs to refresh its data and in what way
	private static final DiffUtil.ItemCallback<UserModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<UserModel>() {
		@Override
		public boolean areItemsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {
			return oldItem.getId() == newItem.getId();
		}

		@Override
		public boolean areContentsTheSame(@NonNull UserModel oldItem, @NonNull UserModel newItem) {

			return oldItem.getUsername().equals(newItem.getUsername()) &&
					oldItem.getLastName().equals(newItem.getLastName()) &&
					oldItem.getFirstName().equals(newItem.getFirstName());
		}
	};

	@NonNull
	@Override
	public StudentForModuleRecyclerViewAdapter.StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.student_recycler_view_item, parent, false);
		return new StudentForModuleRecyclerViewAdapter.StudentHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull StudentForModuleRecyclerViewAdapter.StudentHolder holder, int position) {
		//Get the current lecture item
		UserModel currentStudent = getItem(position);


		String fullName = "";

		if (currentStudent.getId() != SessionManager.getUser().getId()){
			fullName = currentStudent.getFirstName() + " " + currentStudent.getLastName() +
					" (" + currentStudent.getUsername() + ")";
		} else {
			fullName = "You (" + currentStudent.getUsername() + ")";
		}

		//Only add attendance to each student if the user is authenticated as a lecturer
		if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){
			String attendancePercent = currentStudent.getAttendanceForModule() * 100 + "%";
			holder.txt_view_module_attendance.setText(attendancePercent);
		}


		//Set the text of the item to the lecture details
		holder.txt_view_student_name.setText(fullName);
	}

	class StudentHolder extends RecyclerView.ViewHolder {
		private MaterialTextView txt_view_student_name;
		private MaterialTextView txt_view_module_attendance;

		public StudentHolder(@NonNull View itemView) {
			super(itemView);
			txt_view_student_name = itemView.findViewById(R.id.item_lbl_student_name);
			txt_view_module_attendance = itemView.findViewById(R.id.item_student_info);

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
		void onItemClick(UserModel userModel);
	}

	public void setOnItemClickListener(StudentForModuleRecyclerViewAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
