package com.example.osymsozluk.ui.kelimeler;

import android.widget.ArrayAdapter;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class kelimelerModel extends ViewModel {
    private MutableLiveData< ArrayAdapter<String> > myadapter = new MutableLiveData<>();
    private MutableLiveData<String[]> kelimeler = new MutableLiveData<>();
    private MutableLiveData<Map<String, String>> anlamlar = new MutableLiveData<>();

    public MutableLiveData<ArrayAdapter<String>> getMyadapter() {
        return myadapter;
    }

    public void setMyadapter(ArrayAdapter<String> a) {
        this.myadapter.setValue(a);
    }

    public MutableLiveData<String []> getKelimeler() {
        return kelimeler;
    }
    public void setKelimeler(String[] x) {
        this.kelimeler.setValue(x);
    }

    public MutableLiveData<Map<String, String>> getAnlamlar() {
        return anlamlar;
    }

    public void setAnlamlar(Map<String, String> anlamlar) {
        this.anlamlar.setValue(anlamlar);
    }
}
