package com.iao.saydaliyati.ui.pharmacies;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iao.saydaliyati.R;
import com.iao.saydaliyati.entity.Pharmacy;
import com.iao.saydaliyati.repository.PharmacyRepository;
import com.iao.saydaliyati.repository.intefaces.ListPharmaciesCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PharmaciesFragment extends Fragment {

    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pharmacies, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        PharmacyRepository pharmacyRepository = new PharmacyRepository();
        pharmacyRepository.findNormalPharmacies(new ListPharmaciesCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void myResponseCallback(List<Pharmacy> pharmacies) {
                pharmacies.sort(Comparator.comparing(Pharmacy::getName));
                MyAdapter adapter = new MyAdapter(getContext(), pharmacies);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });



        return view;
    }
}
