package com.iao.saydaliyati.ui.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.iao.saydaliyati.MainActivity;
import com.iao.saydaliyati.R;
import com.iao.saydaliyati.entity.Pharmacy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class ProfileActivity extends AppCompatActivity {

    private static final int PHONE_CALL_PERMISSION_REQUEST_CODE = 500;

    TextView tv_name, tv_address, tv_owner, tv_email, tv_phone, tv_arrondissement;
    ImageButton ib_map, ib_call;

    Pharmacy pharmacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pharmacy = (Pharmacy) getIntent().getSerializableExtra("pharmacy");

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

        Activity activity = this;

        ib_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MainActivity.class);
                intent.putExtra("pharmacy", pharmacy);
                startActivity(intent);
            }
        });

        ib_call.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    makeCall();
                } else {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PHONE_CALL_PERMISSION_REQUEST_CODE);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PHONE_CALL_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall();
        } else {
            Toast.makeText(this, "Call phone permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + pharmacy.getPhone()));
        startActivity(intent);
    }
}