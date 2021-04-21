package com.iao.saydaliyati.repository;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.iao.saydaliyati.entity.Pharmacy;
import com.iao.saydaliyati.repository.intefaces.ListPharmaciesCallback;
import com.iao.saydaliyati.repository.intefaces.PharmaciesCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;

public class PharmacyRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String TAG = "Pharmacy repository";

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

    public void findGardPharmaciesByDay(PharmaciesCallback callback) {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        db.collection("gardPharmacies")
                .whereEqualTo("date", formattedDate)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                List pharmacies = (List) document.getData().get("pharmacies");
                                //Log.d(TAG, "List inside listener => " + pharmacies[0]);
                                callback.myResponseCallback(pharmacies);
                            }
                        }
                    }
                });
    }

    public void findGardPharmacies(ListPharmaciesCallback callback) {
        findGardPharmaciesByDay(new PharmaciesCallback() {
            @Override
            public void myResponseCallback(List pharmacies) {
                db.collection("pharmacies")
                        .whereIn(FieldPath.documentId(), pharmacies)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    List<Pharmacy> list = new ArrayList<Pharmacy>();

                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Pharmacy pharmacy = document.toObject(Pharmacy.class);
                                        pharmacy.setId(document.getId());
                                        list.add(pharmacy);
                                    }
                                    callback.myResponseCallback(list);
                                }
                            }
                        });
            }
        });
    }
}
