package com.example.smartdisplay.ui.alltasks;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AllTasksViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AllTasksViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is alltask fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}