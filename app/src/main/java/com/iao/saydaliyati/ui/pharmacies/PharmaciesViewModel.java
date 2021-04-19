package com.iao.saydaliyati.ui.pharmacies;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PharmaciesViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PharmaciesViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Pharmacies fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}