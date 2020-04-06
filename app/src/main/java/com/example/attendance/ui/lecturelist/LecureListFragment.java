package com.example.attendance.ui.lecturelist;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.attendance.R;
import com.example.attendance.viewmodels.ViewModelProviderFactory;

import java.util.List;

import javax.inject.Inject;

public class LecureListFragment extends Fragment {

    private static final String TAG = "LecureListFragment";

    @Inject
    ViewModelProviderFactory viewModelProviderFactory;

    private LectureViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    final LectureRecyclerViewAdapter adapter = new LectureRecyclerViewAdapter();

    public static LecureListFragment newInstance() {
        return new LecureListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lecure_list_fragment, container, false);

        swipeRefreshLayout = v.findViewById(R.id.pull_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.getLectures();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        recyclerView = v.findViewById(R.id.recycler_view_lecture_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setAdapter(adapter);


        //DEPRECATED - TOCHANGE
        viewModel = ViewModelProviders.of(this, viewModelProviderFactory).get(LectureViewModel.class);
        viewModel.getLectures();

        subscribeObservers();
    }

    public void subscribeObservers(){
        viewModel.observeLectures().observe(getViewLifecycleOwner(), new Observer<List<LectureModel>>() {
            @Override
            public void onChanged(List<LectureModel> lectureModels) {
                if (lectureModels != null){
                    Log.d(TAG, "onChanged: lectures changed...");
                    adapter.submitList(lectureModels);
                }
            }
        });
    }
}
