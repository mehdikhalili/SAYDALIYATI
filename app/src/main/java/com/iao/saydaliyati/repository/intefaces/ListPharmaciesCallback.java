package com.iao.saydaliyati.repository.intefaces;

import com.iao.saydaliyati.entity.Pharmacy;

import java.util.List;

public interface ListPharmaciesCallback {
    public void myResponseCallback(List<Pharmacy> pharmacies);
}
