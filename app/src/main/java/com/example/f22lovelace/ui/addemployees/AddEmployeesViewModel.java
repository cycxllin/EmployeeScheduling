package com.example.f22lovelace.ui.addemployees;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddEmployeesViewModel extends ViewModel{
    private final MutableLiveData<String> mText;

    public AddEmployeesViewModel() {
        mText = new MutableLiveData<>();
        //Will add contents of the add employee page here
    }

    public LiveData<String> getText() {
       return mText;
    }
}
