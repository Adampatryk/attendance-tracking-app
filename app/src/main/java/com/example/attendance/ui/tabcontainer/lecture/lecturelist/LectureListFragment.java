package com.example.attendance.ui.tabcontainer.lecture.lecturelist;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.example.attendance.util.Constants;

public class LectureListFragment extends Fragment {

    private static final String TAG = "LectureListFragment";

    private TabViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView no_lectures_text_view;

    final private LectureRecyclerViewAdapter adapter = new LectureRecyclerViewAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lecture_list_fragment, container, false);

        viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);

        //Set the onclick adapter to each recycler view item
        adapter.setOnItemClickListener(lectureModel -> {
            Toast.makeText(getContext(), "Lecture ID: " +  lectureModel.getId(), Toast.LENGTH_SHORT).show();

            viewModel.setLecture(lectureModel.getId());
            Navigation.findNavController(v).navigate(R.id.action_tabFragment_to_lectureDetailFragment);
        });

        swipeRefreshLayout = v.findViewById(R.id.pull_to_refresh_lectures);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getLectures();
        });

        subscribeObservers();
        viewModel.getLectures();
        Log.d(TAG, "onCreateView: called - refresh true");
        swipeRefreshLayout.setRefreshing(true);

        recyclerView = v.findViewById(R.id.recycler_view_lecture_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        no_lectures_text_view = v.findViewById(R.id.no_lectures_text_view);

        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: called");
    }

    public void subscribeObservers(){
        Log.d(TAG, "subscribeObservers: called");
        viewModel.observeLectures().observe(getViewLifecycleOwner(),  lectureModels -> {
            Log.d(TAG, "subscribeObservers: observer notified");
            
            //Has the call been successful?
            if (lectureModels != null){
                Log.d(TAG, "onChanged: lectures changed...");
                adapter.submitList(lectureModels);
                if(lectureModels.size() == 0) {
                    Log.d(TAG, "subscribeObservers: no lectures for this user");
                    no_lectures_text_view.setVisibility(View.VISIBLE);
                } else {
                    no_lectures_text_view.setVisibility(View.GONE);
                }
            } else {
                Log.d(TAG, "subscribeObservers: lectureModels returned null");
                Toast.makeText(getContext(), Constants.NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
            }
            Log.d(TAG, "subscribeObservers: refresh false");
            //Stop the refresh animation
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: LectureListFragment resumed");
    }
}
