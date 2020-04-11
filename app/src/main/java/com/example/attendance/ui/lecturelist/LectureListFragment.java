package com.example.attendance.ui.lecturelist;

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
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.LectureModel;
public class LectureListFragment extends Fragment {

    private static final String TAG = "LectureListFragment";

    private LectureViewModel viewModel;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Button btn_logout;

    final private LectureRecyclerViewAdapter adapter = new LectureRecyclerViewAdapter();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lecture_list_fragment, container, false);

        adapter.setOnItemClickListener(lectureModel -> {
            Toast.makeText(getContext(), "Lecture ID: " +  lectureModel.getId(), Toast.LENGTH_SHORT).show();

            Bundle bundle = new Bundle();
            bundle.putInt("lecture_id", lectureModel.getId());
            Navigation.findNavController(v).navigate(R.id.action_lectureListFragment_to_lectureDetailFragment, bundle);
        });

        swipeRefreshLayout = v.findViewById(R.id.pull_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            viewModel.getLectures();
            swipeRefreshLayout.setRefreshing(false);
        });

        btn_logout = v.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v1 -> {
            Navigation.findNavController(v1).navigate(R.id.action_lectureListFragment_to_loginFragment);
            SessionManager.logout();
        });

        recyclerView = v.findViewById(R.id.recycler_view_lecture_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Toast.makeText(getContext(), "Lecturer: " + SessionManager.getUser().isLecturer(), Toast.LENGTH_SHORT).show();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LectureViewModel.class);
        viewModel.getLectures();

        subscribeObservers();
    }

    public void subscribeObservers(){
        viewModel.observeLectures().observe(getViewLifecycleOwner(), lectureModels -> {
            if (lectureModels != null){
                Log.d(TAG, "onChanged: lectures changed...");
                adapter.submitList(lectureModels);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: LectureListFragment resumed");
    }
}
