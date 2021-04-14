package com.example.osymsozluk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;

import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.osymsozluk.ui.kelimeler.kelimelerModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

class my
{
    // bu class, cpp'deki #define işlevi görür
    // böylece bu değişkenler AsyncTask'ta bize baya bir kolaylık sağlar
    // kısaca bu değişkenlere her yerden kolay bir şekilde erişilebilir
    public static String[] klm; // kelime listesi
}


public class MainActivity extends AppCompatActivity {

    public String veri_elde_et() throws IOException{ // dosya okuma
        String source = "";
        AssetManager as = this.getAssets();
        InputStream is = as.open("osym.json");
        BufferedReader buf = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        int i;
        while ((i = buf.read()) != -1) {
            source += (char) i;
        }
        return source;
    }

    public Map kelimeleri_elde_et() // kelimelerin listesi oluşturulur
    {                               // ve her kelimeye karşılıp map yardımıyla bir anlam atanır

        String source = "";
        try {

            source = veri_elde_et();

            JSONObject obj = new JSONObject(source);
            JSONArray kelime_json = obj.names();
            my.klm = new String[kelime_json.length()];

            Map kelimeler = new HashMap<String, String>();
            for (int j = 0; j < kelime_json.length(); j++) {
                String temp = kelime_json.getString(j);
                my.klm[j] = temp + "\n"; // ##DİKKAT: burada listview çok sıkışık gözükmesin diye
                                         // her kelimenin sonuna '\n' koyulur.
                                         // ama ileride bize kelime kontrolünde(bknz. aramaFragment.java) sıkıntı yapacak, dikkatli olun
                                         // bu yüzden aramaFragment.java daki aranacak_kelimeyi_ayarla fonksiyonunda kelimeyi önce bir arındırıyoruz
                                         // sonra kelime_kontrol fonksiyonunda kelimeyi kontrol ederken word değil de word + '\n' şeklinde
                                         // kontrol ediyoruz. Çünkü malum, temp + '\n' yapıyoruz burada
                kelimeler.put(temp + "\n", obj.getJSONObject(temp).getString("anlam"));
            }

            return kelimeler;
        }
        catch (Exception e)
        {
            Map<String, String> a = new HashMap<String, String>();
            a.put("hata", e.toString());
            return a;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String, String> kelimeler = kelimeleri_elde_et();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getApplicationContext(), R.layout.activity_osym_kelimeleri, R.id.kutu, my.klm);
        kelimelerModel kelime_model =
                ViewModelProviders.of(this).get(kelimelerModel.class);
        kelime_model.setMyadapter(adapter);
        kelime_model.setKelimeler(my.klm);
        kelime_model.setAnlamlar(kelimeler);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.



        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //navController.navigate(R.id.navigation_dashboard); jarbay
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            LayoutInflater li = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

            View viv = li.inflate(R.layout.hakkinda_popup, null);

            TextView tw = (TextView) viv.findViewById(R.id.about);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(viv);
            builder.setTitle(R.string.hakkinda);

            tw.setMovementMethod(LinkMovementMethod.getInstance());
            builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            return (true);
        }
        return(super.onOptionsItemSelected(item));
    }
}