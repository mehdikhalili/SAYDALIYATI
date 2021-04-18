package com.iao.saydaliyati.repository;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iao.saydaliyati.entity.Pharmacy;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class PharmacyRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "Pharmacy";

    public List<Pharmacy> findAll() {
        List<Pharmacy> list = new ArrayList<Pharmacy>();
        db.collection("pharmacies")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Pharmacy pharmacy = document.toObject(Pharmacy.class);

                                Log.d(TAG, document.getId() + " => " + document.getData());

                                list.add(pharmacy);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
        return list;
    }

    public Pharmacy find(String id) {
        final Pharmacy[] pharmacy = new Pharmacy[1];
        db.document("pharmacies/"+id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        pharmacy[0] = documentSnapshot.toObject(Pharmacy.class);
                    }
                });
        return pharmacy[0];
    }
}
