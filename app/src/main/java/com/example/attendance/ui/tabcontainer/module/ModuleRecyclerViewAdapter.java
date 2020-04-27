package com.example.attendance.ui.tabcontainer.module;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.models.ModuleModel;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ModuleRecyclerViewAdapter extends ListAdapter<ModuleModel, ModuleRecyclerViewAdapter.ModuleListHolder> {

	private ModuleRecyclerViewAdapter.OnItemClickListener listener;

	public ModuleRecyclerViewAdapter() {
		super(DIFF_CALLBACK);
	}

	//Used to determine if the recycler view needs to refresh its data and in what way
	private static final DiffUtil.ItemCallback<ModuleModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ModuleModel>() {
		@Override
		public boolean areItemsTheSame(@NonNull ModuleModel oldItem, @NonNull ModuleModel newItem) {
			return oldItem.getId() == newItem.getId();
		}

		@Override
		public boolean areContentsTheSame(@NonNull ModuleModel oldItem, @NonNull ModuleModel newItem) {

			return oldItem.getTitle().equals(newItem.getTitle());// &&
			//oldItem.getDate().equals(newItem.getDate()) &&
			//oldItem.getModule().equals(newItem.getModule());
		}
	};

	@NonNull
	@Override
	public ModuleRecyclerViewAdapter.ModuleListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.lecture_list_recycler_view_item, parent, false);
		return new ModuleRecyclerViewAdapter.ModuleListHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull ModuleRecyclerViewAdapter.ModuleListHolder holder, int position) {
		//Get the current module item
		ModuleModel currentModule = getItem(position);

		//Set the text of the item to the module details
		holder.txt_view_title.setText(currentModule.getTitle());
		holder.txt_view_module.setText(currentModule.getModuleCode());
//
//		String teachers = "";
//
//		if (currentModule.getProfessors().length > 0){
//			teachers += currentModule.getProfessors()[0].getUsername();
//			int i = 1;
//
//			while (i < currentModule.getProfessors().length - 1){
//				teachers += currentModule.getProfessors()[i].getUsername();
//			}
//		}

		//holder.txt_view_prof.setText(teachers);
	}

	class ModuleListHolder extends RecyclerView.ViewHolder {
		private MaterialTextView txt_view_title;
		private MaterialTextView txt_view_module;
		//private MaterialTextView txt_view_prof;

		public ModuleListHolder(@NonNull View itemView){
			super(itemView);
			txt_view_title = itemView.findViewById(R.id.item_lbl_lecture_title);
			txt_view_module = itemView.findViewById(R.id.item_lbl_lecture_module);
			//txt_view_prof = itemView.findViewById(R.id.item_lbl_lecture_prof);

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
		void onItemClick(ModuleModel moduleModel);
	}

	public void setOnItemClickListener(ModuleRecyclerViewAdapter.OnItemClickListener listener) {
		this.listener = listener;
	}
}
