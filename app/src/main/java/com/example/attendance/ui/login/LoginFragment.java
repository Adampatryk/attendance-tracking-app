package com.example.attendance.ui.login;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.exceptions.Exceptions;
import io.reactivex.rxjava3.internal.util.ExceptionHelper;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendance.R;
import com.example.attendance.auth.SessionManager;
import com.example.attendance.models.UserModel;
import com.example.attendance.network.WebServiceProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.net.UnknownHostException;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";

    private LoginViewModel viewModel;
    private MaterialButton btn_login;
    private TextInputLayout username_field, password_field;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //SessionManager.logout();
        View v = inflater.inflate(R.layout.login_fragment, container, false);

        username_field = v.findViewById(R.id.username_field);
        password_field = v.findViewById(R.id.password_field);
        progressBar = v.findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.GONE);

        btn_login = v.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v1 -> attemptLogin());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Tell session manager about shared preferences
        Log.d(TAG, "onActivityCreated: Setting shared preferences in session manager");
        SessionManager.setSharedPreferences(getActivity().getSharedPreferences("login", FragmentActivity.MODE_PRIVATE));

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        //If logged in, navigate to list of lectures
        if (SessionManager.isAuthenticated()) {
            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_lectureListFragment);
        }

        // TODO: Use the ViewModel
    }

    private boolean attemptLogin(){
        String username, password;
        username = username_field.getEditText().getText().toString();
        password = password_field.getEditText().getText().toString();
        password_field.getEditText().onEditorAction(EditorInfo.IME_ACTION_DONE);

        if (!username.isEmpty() || !password.isEmpty()) {
            Log.d(TAG, "attemptLogin: Attempting login");
            UserModel user = new UserModel(username, password, null, null);
            makeLoginRequest(user);
        }
        else {
            Toast.makeText(getContext(), "Must provide credentials", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void makeLoginRequest(UserModel user){
        WebServiceProvider.getAuthApi().authenticateUser(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserModel>() {

                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d(TAG, "onSubscribe: ");
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserModel userModel) {
                        Log.d(TAG, "onNext: " + userModel.toString());
                        user.setToken(userModel.getToken());
                        if (SessionManager.login(user)){
                            Navigation.findNavController(getView()).navigate(R.id.action_loginFragment_to_lectureListFragment);
                        }
                        else {
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e.toString());
                        if (e instanceof HttpException){
                            Toast.makeText(getContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                        }
                        else if (e instanceof UnknownHostException) {
                            Toast.makeText(getContext(), "Check your network connection", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete: ");
                        progressBar.setVisibility(View.GONE);
                    }
            });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: LoginFragment Resumed, Authenticated: " + SessionManager.isAuthenticated());
        Log.d(TAG, "onResume: SessionManager shared preferences + " + SessionManager.hasSharedPreferences() );
    }
}
