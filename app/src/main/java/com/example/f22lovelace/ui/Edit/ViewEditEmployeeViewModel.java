package com.example.f22lovelace.ui.Edit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewEditEmployeeViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ViewEditEmployeeViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}