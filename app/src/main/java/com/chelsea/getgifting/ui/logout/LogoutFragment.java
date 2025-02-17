package com.chelsea.getgifting.ui.logout;

import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.chelsea.getgifting.R;

public class LogoutFragment extends Fragment{

        private LogoutViewModel logoutViewModel;

        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            logoutViewModel =
                    ViewModelProviders.of(this).get(LogoutViewModel.class);
            View root = inflater.inflate(R.layout.fragment_home, container, false);
            final TextView textView = root.findViewById(R.id.text_home);
            logoutViewModel.getText().observe(this, new Observer<String>() {
                @Override
                public void onChanged(@Nullable String s) {
                    textView.setText(s);
                }
            });
            return root;
        }
    }


