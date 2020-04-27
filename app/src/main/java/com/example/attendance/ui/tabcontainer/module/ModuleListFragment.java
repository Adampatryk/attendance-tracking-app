package com.example.attendance.ui.tabcontainer.module;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.example.attendance.util.Constants;

public class ModuleListFragment extends Fragment {

	private TabViewModel viewModel;
	private RecyclerView recyclerView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private TextView no_modules_text_view;

	private static final String TAG = "ModuleListFragment";

	final private ModuleRecyclerViewAdapter adapter = new ModuleRecyclerViewAdapter();

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
							 @Nullable Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.module_list_fragment, container, false);

		//Set the onclick adapter to each recycler view item
		adapter.setOnItemClickListener(moduleModel -> {
			Toast.makeText(getContext(), "Module ID: " +  moduleModel.getId(), Toast.LENGTH_SHORT).show();

			//Select the correct module in the viewmodel
			viewModel.setModule(moduleModel.getId());

			Navigation.findNavController(v).navigate(R.id.action_tabFragment_to_moduleDetailFragment);
		});

		swipeRefreshLayout = v.findViewById(R.id.pull_to_refresh_modules);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			viewModel.getModules();
		});

		recyclerView = v.findViewById(R.id.recycler_view_module_list);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

		no_modules_text_view = v.findViewById(R.id.no_modules_text_view);

		recyclerView.setAdapter(adapter);

		return v;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);
		subscribeObservers();
		viewModel.getModules();
		swipeRefreshLayout.setRefreshing(true);
	}

	public void subscribeObservers(){
		viewModel.observeModules().observe(getViewLifecycleOwner(),  moduleModels -> {

			//Has the call been successful?
			if (moduleModels != null){
				Log.d(TAG, "onChanged: modules changed...");
				adapter.submitList(moduleModels);
				if(moduleModels.size() == 0) {
					Log.d(TAG, "subscribeObservers: no modules for this user");
					no_modules_text_view.setVisibility(View.VISIBLE);
				} else {
					no_modules_text_view.setVisibility(View.GONE);
				}
			} else {
				Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
			}
			//Stop the refresh animation
			swipeRefreshLayout.setRefreshing(false);
		});
	}
	
}
