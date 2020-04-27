package com.example.attendance.ui.tabcontainer.lecture;

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
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
import com.example.attendance.ui.tabcontainer.TabViewModel;
import com.example.attendance.util.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LectureListFragment extends Fragment {

    private static final String TAG = "LectureListFragment";

    private TabViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView no_lectures_text_view;
    private FloatingActionButton add_lecture_button;

    final private LectureRecyclerViewAdapter adapter = new LectureRecyclerViewAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lecture_list_fragment, container, false);


        //Set the onclick adapter to each recycler view item
        adapter.setOnItemClickListener(lectureModel -> {
            Toast.makeText(getContext(), "Lecture ID: " +  lectureModel.getId(), Toast.LENGTH_SHORT).show();

            viewModel.setLecture(lectureModel.getId());
            Navigation.findNavController(v).navigate(R.id.action_tabFragment_to_lectureTabFragment);
        });

        swipeRefreshLayout = v.findViewById(R.id.pull_to_refresh_lectures);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getLecturesForToday();
            Log.d(TAG, "onCreateView: Refresh Listened!!");
        });

        recyclerView = v.findViewById(R.id.recycler_view_lecture_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        no_lectures_text_view = v.findViewById(R.id.no_lectures_text_view);

        recyclerView.setAdapter(adapter);

        if (SessionManager.isAuthenticated() && SessionManager.getUser().isLecturer()){
            add_lecture_button = v.findViewById(R.id.create_lecture_button);
            add_lecture_button.setVisibility(View.VISIBLE);
            add_lecture_button.setOnClickListener(view -> {
                Navigation.findNavController(view).navigate(R.id.action_tabFragment_to_createLectureFragment);
            });
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: called");

        viewModel = new ViewModelProvider(requireActivity()).get(TabViewModel.class);
        subscribeObserver();
    }

    public void subscribeObserver(){

        viewModel.observeLectures().observe(getViewLifecycleOwner(),  lectureModels -> {
            Log.d(TAG, "refreshLectures: observer notified");
            //Has the call been successful?
            if (lectureModels != null){
                Log.d(TAG, "refreshLectures: Lectures returned");
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
            //Stop the refresh animation
            Log.d(TAG, "refreshLectures: Stop refresh animation");
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        swipeRefreshLayout.setRefreshing(true);
        viewModel.getLecturesForToday();
    }
}
