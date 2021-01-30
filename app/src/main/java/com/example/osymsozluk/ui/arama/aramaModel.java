package com.example.osymsozluk.ui.arama;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class aramaModel extends ViewModel {
    private MutableLiveData<Boolean> hemenAra = new MutableLiveData<>(false);
    private MutableLiveData<String> kelime = new MutableLiveData<>("");
    private MutableLiveData<String> anlam = new MutableLiveData<>("");


    public MutableLiveData<String> getKelime(){

        return kelime;
    }

    public void setKelime(String x){

        kelime.setValue(x);
    }

    public MutableLiveData<String> getAnlam() {

        return anlam;
    }

    public void setAnlam(String anlam) {

        this.anlam.setValue(anlam);
    }

    public MutableLiveData<Boolean> getHemenAra() {

        return hemenAra;
    }
    public void setHemenAra(Boolean b) {

        this.hemenAra.setValue(b);
    }
}
