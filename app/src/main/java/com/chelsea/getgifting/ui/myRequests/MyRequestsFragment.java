package com.chelsea.getgifting.ui.myRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.chelsea.getgifting.R;

public class MyRequestsFragment extends Fragment {

    private MyRequestsViewModel myRequestsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myRequestsViewModel =
                ViewModelProviders.of(this).get(MyRequestsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myrequests, container, false);
        final TextView textView = root.findViewById(R.id.text_slideshow);
        myRequestsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}