package com.iao.saydaliyati.ui.pharmacydetails;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iao.saydaliyati.R;

public class PharmacyDetailsFragment extends Fragment {

    public static PharmacyDetailsFragment newInstance() {
        return new PharmacyDetailsFragment();
    }

    private PharmacyDetailsViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pharmacy_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PharmacyDetailsViewModel.class);
        // TODO: Use the ViewModel
    }

}