package com.iao.saydaliyati.ui.pharmacies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.iao.saydaliyati.R;

public class PharmaciesFragment extends Fragment {

    private PharmaciesViewModel pharmaciesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        pharmaciesViewModel =
                new ViewModelProvider(this).get(PharmaciesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_pharmacies, container, false);
       // final TextView textView = root.findViewById(R.id.text_home);
        pharmaciesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

                //textView.setText(s);
            }
        });
        return root;
    }
}