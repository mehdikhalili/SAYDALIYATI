package com.iao.saydaliyati.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    private void findGardPharmaciesByDay(PharmaciesCallback callback) {

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
                            List pharmacies = new ArrayList();
                            pharmacies.add("TEST");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                pharmacies = (List) document.getData().get("pharmacies");
                            }
                            callback.myResponseCallback(pharmacies);
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
                                setDataInCallback(callback, task, true);
                            }
                        });
            }
        });
    }

    public void findNormalPharmacies(ListPharmaciesCallback callback) {
        findGardPharmaciesByDay(new PharmaciesCallback() {
            @Override
            public void myResponseCallback(List pharmacies) {
                db.collection("pharmacies")
                        .whereNotIn(FieldPath.documentId(), pharmacies)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                setDataInCallback(callback, task, false);
                            }
                        });
            }
        });
    }

    private void setDataInCallback(ListPharmaciesCallback callback, Task<QuerySnapshot> task, boolean isGard) {
        if (task.isSuccessful()) {
            List<Pharmacy> list = new ArrayList<Pharmacy>();
            for (QueryDocumentSnapshot document : task.getResult()) {
                Pharmacy pharmacy = document.toObject(Pharmacy.class);
                pharmacy.setId(document.getId());
                pharmacy.setGard(isGard);
                list.add(pharmacy);
            }
            callback.myResponseCallback(list);
        }
    }
}
