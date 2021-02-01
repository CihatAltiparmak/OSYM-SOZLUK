package com.cihat.osymsozluk.ui.kelimeler;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.cihat.osymsozluk.R;

import com.cihat.osymsozluk.ui.arama.aramaModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

class my
{
    // bu class, cpp'deki #define işlevi görür
    // böylece bu değişkenler AsyncTask'ta bize baya bir kolaylık sağlar
    // kısaca bu değişkenlere her yerden kolay bir şekilde erişilebilir
    public static View root;
    public static ListView osym_kelimeleri;
}


public class kelimelerFragment extends Fragment {


    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, final Bundle savedInstanceState) {

        my.root = inflater.inflate(R.layout.kelimeler_ekrani, container, false);
        my.osym_kelimeleri = my.root.findViewById(R.id.osym_kelimeleri);

        TextView baslik = (TextView) my.root.findViewById(R.id.baslikKaydedilenler);
        baslik.setText(Html.fromHtml("<div style = 'text-align : center'><h2><font color = '#ff6659'><b><u>ÖSYM'de Sıkça Kullanılan Kelimeler</u></b></font></h2></div>"));

        final kelimelerModel kaydet_model =
                ViewModelProviders.of(getActivity()).get(kelimelerModel.class);

        my.osym_kelimeleri.setAdapter(kaydet_model.getMyadapter().getValue());

        my.osym_kelimeleri.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String tiklanan = my.osym_kelimeleri.getItemAtPosition(position).toString();

                        BottomNavigationView bnw = (BottomNavigationView) inflater.inflate(R.layout.activity_main, container, false).findViewById(R.id.nav_view);

                        aramaModel arama_model =
                                    ViewModelProviders.of(getActivity()).get(com.cihat.osymsozluk.ui.arama.aramaModel.class);
                        arama_model.setKelime(tiklanan);
                        arama_model.setAnlam(kaydet_model.getAnlamlar().getValue().get(tiklanan));
                        arama_model.setHemenAra(true);
                        NavController nc = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                        nc.navigate(R.id.navigation_home);
                    }
                }
        );
        return my.root;
    }
}