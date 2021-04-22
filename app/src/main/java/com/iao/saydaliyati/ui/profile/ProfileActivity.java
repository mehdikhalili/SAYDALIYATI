package com.iao.saydaliyati.ui.profile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iao.saydaliyati.R;
import com.iao.saydaliyati.entity.Pharmacy;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_name, tv_address, tv_owner, tv_email, tv_phone, tv_arrondissement;
    ImageButton ib_map, ib_call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Pharmacy pharmacy = (Pharmacy) getIntent().getSerializableExtra("pharmacy");

        tv_name = findViewById(R.id.tv_name);
        tv_address = findViewById(R.id.tv_address);
        tv_owner = findViewById(R.id.tv_owner);
        tv_email = findViewById(R.id.tv_email);
        tv_phone = findViewById(R.id.tv_phone);
        tv_arrondissement = findViewById(R.id.tv_arrondissement);

        ib_map = findViewById(R.id.ib_map);
        ib_call = findViewById(R.id.ib_call);

        tv_name.setText("Pharmacie " + pharmacy.getName());
        tv_address.setText(pharmacy.getAddress());
        tv_owner.setText(pharmacy.getOwner());
        tv_email.setText(pharmacy.getEmail());
        tv_phone.setText(pharmacy.getPhone());
        tv_arrondissement.setText(pharmacy.getCity() + ", " + pharmacy.getArrondissement());


    }
}