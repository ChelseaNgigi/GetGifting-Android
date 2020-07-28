package com.chelsea.getgifting.ui.myDonations;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyDonationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyDonationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}