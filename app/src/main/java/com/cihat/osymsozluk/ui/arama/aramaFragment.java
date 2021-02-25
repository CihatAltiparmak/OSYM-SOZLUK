package com.cihat.osymsozluk.ui.arama;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import com.cihat.osymsozluk.R;
import com.cihat.osymsozluk.ui.kelimeler.kelimelerModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Random;

class my
{
    // bu class, cpp'deki #define işlevi görür
    // böylece bu değişkenler AsyncTask'ta bize baya bir kolaylık sağlar
    // kısaca bu değişkenlere her yerden kolay bir şekilde erişilebilir
    public static View root;
    public static TextView sonuclar;
    public static Button rasgele_buton;
    public static TextView kelime_giris;
    public static com.cihat.osymsozluk.ui.arama.aramaModel arama_model;
    public static kelimelerModel kelimeler_model;
}


public class aramaFragment extends Fragment {

    public String rastgele_kelime_sec() // listeden rastgele kelime seçer
    {
        Random x = new Random();
        String [] kelime_hazinesi = (String[]) my.kelimeler_model.getKelimeler().getValue();
        return kelime_hazinesi[x.nextInt(kelime_hazinesi.length)];
    }

    public String kelimeden_anlam_elde_et(String word) // verilen sözcüğün anlamını bulur
    {
        return my.kelimeler_model.getAnlamlar().getValue().get(word);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* global değerleri ayarlama başlangıcı*/
        //global değerler için lütfen my class'ına bakın
        my.root = inflater.inflate(R.layout.arama_ekrani, container, false);
        my.sonuclar = (TextView) my.root.findViewById(R.id.sonuclar);
        my.rasgele_buton = (Button) my.root.findViewById(R.id.rasgele);
        my.kelime_giris = (TextView) my.root.findViewById(R.id.kelime_giris);

        my.sonuclar.setVisibility(View.INVISIBLE);
        my.arama_model =
                ViewModelProviders.of(getActivity()).get(aramaModel.class);

        my.kelimeler_model =
                ViewModelProviders.of(getActivity()).get(kelimelerModel.class);
        /* global değerleri ayarlama sonu */

        my.rasgele_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my.sonuclar.setText("");
                my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                // rastgele kelime seçme butonu
                // bu butonla elimizde bulunan listeden rastgele bir kelime seçilir
                // ve bu kelime anlamı ile beraber gösterilir
                String kelime_rastgele = rastgele_kelime_sec();
                String anlam_rastgele = kelimeden_anlam_elde_et(kelime_rastgele);
                anlam_rastgele = "<u><p style='color:#A26900'><i>Anlamı</i></p></u><p><i>" + anlam_rastgele + "</i></p>";
                my.kelime_giris.setText(Html.fromHtml("<div style = 'text-align : center'><h2><b><u>" + kelime_rastgele +"</u></b></h2></div>"));
                my.sonuclar.setText(Html.fromHtml(anlam_rastgele));
                my.sonuclar.setVisibility(View.VISIBLE);
            }
        });


        if(my.arama_model.getHemenAra().getValue()) // kelimeler bölümünden gelip gelmediğimizi kontrol ediyoruz
        {
            my.sonuclar.setText("");
            String word = my.arama_model.getKelime().getValue();
            my.kelime_giris.setText(Html.fromHtml("<div style = 'text-align : center'><h2><b><u>" + word +"</u></b></h2></div>"));
            String bastirilacak_anlam = my.arama_model.getAnlam().getValue();
            bastirilacak_anlam = "<u><i><p style = 'color:#A26900'>Anlamı</p></i></u>" +
                    "<p><i>" + bastirilacak_anlam + "</i></p>";
            my.sonuclar.setText(Html.fromHtml(bastirilacak_anlam));
            my.arama_model.setHemenAra(false);
            my.sonuclar.setVisibility(View.VISIBLE);
        }

        return my.root;
    }
}