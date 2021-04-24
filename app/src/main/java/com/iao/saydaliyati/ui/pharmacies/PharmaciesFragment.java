package com.iao.saydaliyati.ui.pharmacies;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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

public class PharmaciesFragment extends Fragment implements AdapterView.OnItemSelectedListener, ListPharmaciesCallback {

    RecyclerView recyclerView;
    Spinner spinner_city, spinner_gard;
    List<Pharmacy> allPharmacies = new ArrayList<Pharmacy>();
    String spinner_city_value = "";
    String spinner_gard_value = "";
    PharmacyRepository pharmacyRepository = new PharmacyRepository();
    boolean dataIsHere = false;

    private static int compare(Pharmacy o1, Pharmacy o2) {
        return o1.getName().compareTo(o2.getName());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pharmacies, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        spinner_city = view.findViewById(R.id.spinner_city);
        spinner_gard = view.findViewById(R.id.spinner_gard);

        ArrayAdapter<CharSequence> arrayAdapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.cities_spinner, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> arrayAdapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.gard_spinner, android.R.layout.simple_spinner_item);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(arrayAdapter1);
        spinner_gard.setAdapter(arrayAdapter2);

        spinner_city.setOnItemSelectedListener(this);
        spinner_gard.setOnItemSelectedListener(this);

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spinner_city) {
            spinner_city_value = parent.getItemAtPosition(position).toString();
        } else if (parent == spinner_gard){
            spinner_gard_value = parent.getItemAtPosition(position).toString();
        }

        if (!spinner_city_value.isEmpty() && !spinner_gard_value.isEmpty()) {

            if (spinner_city_value.equals(getString(R.string.toutes_les_villes))
                    && spinner_gard_value.equals(getString(R.string.toutes_les_pharmacies))) {
                // All pharmacies
                if (dataIsHere) {
                    implementRecycleView(allPharmacies);
                } else {
                    pharmacyRepository.findNormalPharmacies(PharmaciesFragment.this::myResponseCallback);
                    pharmacyRepository.findGardPharmacies(PharmaciesFragment.this::myResponseCallback);
                }
            }
            else if (spinner_city_value.equals(getString(R.string.toutes_les_villes))
                    && spinner_gard_value.equals(getString(R.string.en_garde))) {
                // All gard pharmacies
                List<Pharmacy> pharmacies = new ArrayList<>();
                for (Pharmacy pharmacy: allPharmacies) {
                    if (pharmacy.isGard()) {
                        pharmacies.add(pharmacy);
                    }
                }
                implementRecycleView(pharmacies);
            }
            else if (!spinner_city_value.equals(getString(R.string.toutes_les_villes))
                    && spinner_gard_value.equals(getString(R.string.toutes_les_pharmacies))) {
                // All pharmacies by city
                List<Pharmacy> pharmacies = new ArrayList<>();
                for (Pharmacy pharmacy: allPharmacies) {
                    if (pharmacy.getCity().equals(spinner_city_value)) {
                        pharmacies.add(pharmacy);
                    }
                }
                implementRecycleView(pharmacies);
            }
            else if (!spinner_city_value.equals(getString(R.string.toutes_les_villes))
                    && spinner_gard_value.equals(getString(R.string.en_garde))) {
                // All gard pharmacies by city
                List<Pharmacy> pharmacies = new ArrayList<>();
                for (Pharmacy pharmacy: allPharmacies) {
                    if (pharmacy.isGard() && pharmacy.getCity().equals(spinner_city_value)) {
                        pharmacies.add(pharmacy);
                    }
                }
                implementRecycleView(pharmacies);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void myResponseCallback(List<Pharmacy> pharmacies) {
        allPharmacies.addAll(pharmacies);
        implementRecycleView(allPharmacies);
        dataIsHere = true;
    }

    private void implementRecycleView(List<Pharmacy> pharmacies) {
        pharmacies.sort(Comparator.comparing(Pharmacy::getName));
        MyAdapter adapter = new MyAdapter(getContext(), pharmacies);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Toast.makeText(getContext(), "IS GARD", Toast.LENGTH_SHORT).show();
    }
}
