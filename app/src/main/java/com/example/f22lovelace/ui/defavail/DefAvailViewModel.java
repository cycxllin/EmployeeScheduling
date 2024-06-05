package com.example.f22lovelace.ui.defavail;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DefAvailViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    public DefAvailViewModel() {
        mText = new MutableLiveData<>();
        //Will add contents of the add employee page here
    }

    public LiveData<String> getText() {
        return mText;
    }
}
