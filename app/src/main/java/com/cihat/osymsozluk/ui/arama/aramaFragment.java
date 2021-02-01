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
    public static Button arama_buton;
    public static Button rasgele_buton;
    public static EditText kelime_giris;
    public static com.cihat.osymsozluk.ui.arama.aramaModel arama_model;
    public static kelimelerModel kelimeler_model;
    public static String ayrac = "";
}

class TDK extends AsyncTask<Void, Void, String>
{
    public static String basardikMi = "hayir";
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
    @Override
    protected String doInBackground(Void... voids) { // arkaplanda siteden veri çekilir
        my.ayrac = "";
        while(my.sonuclar.getPaint().measureText(my.ayrac + "-") <= my.sonuclar.getMeasuredWidth() - dpToPx(26))
            my.ayrac += "-";

        try {
            String word = my.kelime_giris.getText().toString();
            String aranacak = "https://sozluk.gov.tr/gts?ara=" + word;
            URL u = new URL(aranacak);
            BufferedReader buf = new BufferedReader(new InputStreamReader(u.openStream()));

            String line;
            String source = "";
            while ((line = buf.readLine()) != null) {
                source += line;
            }

            if(source.charAt(0) == '{')
                return "";

            String sonuc = "";
            JSONArray govde = new JSONArray(source);

            String anlamlar = "";
            String atasozler = "";
            String bitisikler = "";
            for(int i = 0; i < govde.length(); i++) {

                try {
                    anlamlar = anlamlari_ayikla(govde.getJSONObject(i).getJSONArray("anlamlarListe"));
                } catch (JSONException e) {
                    anlamlar = "<b>Bu kelimeyle ilişkili anlam bulunamadı.</b>";
                }

                try {
                    atasozler = atasozleri_ayikla(govde.getJSONObject(i).getJSONArray("atasozu"));
                } catch (JSONException e) {
                    atasozler = "<b>Bu kelimeyle ilgili atasözü bulunamadı.</b>";
                }
                bitisikler = govde.getJSONObject(i).getString("birlesikler");
                if (bitisikler == "null")
                    bitisikler = "<b>Bu kelimeyle ilgili birleşik kelime bulunamadı.</b>";

                sonuc += "<p style = 'color:red'><b><u>Anlamlar</u></b></p><p>" +
                        anlamlar +
                        "</p><p style = 'color:blue'><b><u>Atasözleri</u></b></p><p>" +
                        atasozler +
                        "</p><p style = 'color:green'><b><u>Birleşik Kelimeler</u></b></p><p>" +
                        bitisikler +
                        "</p>";
                if(i < govde.length() - 1)sonuc += "<p>"+my.ayrac+"</p>";
            }
            basardikMi = "evet";
            return sonuc;

        }
        catch (UnknownHostException e)
        {
            basardikMi = "intyok";
            return "";
        }
        catch (Exception e)
        {
            return e.toString();
        }

    }

    @Override
    protected void onPostExecute(String s) {
        if(basardikMi.equals("evet")) { // sonuca ulaşıldı
            my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            my.sonuclar.setText(Html.fromHtml(s));
            basardikMi = "hayir";
        }
        else if(basardikMi.equals("hayir")) { // böyle bir kelime yok #FIXMEEE çünkü belki ileride şartlar değişebilir, doğru bir kontrol değil
            my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.unfound);
            my.sonuclar.setText(Html.fromHtml("<div style = 'text-align : center'><h2><font color = 'red'><b><u>Hay Aksi! Aradığın Kelimeye Ulaşamadık :(</u></b></font></h2></div>"));
        }
        else if(basardikMi.equals("intyok")) // intyok -> internet yok
        {
            my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.net_error);
            my.sonuclar.setText(Html.fromHtml("<div style = 'text-align : center'><h2><font color = 'red'><b><u>İnternet Bağlantısı Olmadığı İçin İsteğinizi Yerine Getiremedik :(</u></b></font></h2></div>"));
            basardikMi = "hayir";
        }

        my.kelime_giris.setEnabled(true);
        my.rasgele_buton.setEnabled(true);
        my.arama_buton.setEnabled(true);
        my.arama_buton.setText("Arama");

    }

    public String anlamlari_ayikla(JSONArray anlamlarListe) // siteden gelen veriden anlamlar alınır
    {
        try {

            String a = "";
            for (int i = 0; i < anlamlarListe.length(); i++) {
                a += "<p><b><i>" + (i + 1) + ") anlam </b></i></p>";
                a += "<p><i>" +anlamlarListe.getJSONObject(i).getString("anlam") + "</i></p>";
            }
            return a;
        }
        catch (Exception e)
        {
            return e.toString();
        }

    }

    public String atasozleri_ayikla(JSONArray atasozlerListe) // siteden gelen veriden atasözleri alınır
    {
        try {

            String a = "";
            for (int i = 0; i < atasozlerListe.length(); i++) {
                a += "<p><b><i>" + (i + 1) + ") atasöz</i></b></p>\n";
                a += "<p>" + atasozlerListe.getJSONObject(i).getString("madde") + "</p>";
            }
            return a;
        }
        catch (Exception e)
        {
            return e.toString();
        }

    }


}

public class aramaFragment extends Fragment {

    public String aranacak_kelimeyi_ayarla(String word)
    {
        // gereksiz '\n' ve ' ' karakterlerden arındırılır
        // yoksa kelimenin listede olup olmadığını
        // kontrol edemeyiz
        // edittext'ten çekilen kelime sonundaki
        int a = word.length();
        while(a > 0 && (word.charAt(a - 1) == '\n' || word.charAt(a - 1) == ' ')) {a--;}
        return word.substring(0, a);
    }

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

    public Boolean kelime_kontrol(String word) // kelimenin listede olup olmadığı kontrol edilir
    {
        return  my.kelimeler_model.getAnlamlar()
                                  .getValue()
                                  .containsKey(word);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        /* global değerleri ayarlama başlangıcı*/
        //global değerler için lütfen my class'ına bakın
        my.root = inflater.inflate(R.layout.arama_ekrani, container, false);
        my.sonuclar = (TextView) my.root.findViewById(R.id.sonuclar);
        my.arama_buton = (Button) my.root.findViewById(R.id.arama);
        my.rasgele_buton = (Button) my.root.findViewById(R.id.rasgele);
        my.kelime_giris = (EditText) my.root.findViewById(R.id.kelime_giris);

        my.arama_model =
                ViewModelProviders.of(getActivity()).get(aramaModel.class);

        my.kelimeler_model =
                ViewModelProviders.of(getActivity()).get(kelimelerModel.class);
        /* global değerleri ayarlama sonu */
        my.sonuclar.setVisibility(View.INVISIBLE);

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
                my.kelime_giris.setText(kelime_rastgele);
                my.sonuclar.setText(Html.fromHtml(anlam_rastgele));
                my.sonuclar.setBackgroundResource(R.drawable.osym_sonuc_border);
                my.sonuclar.setVisibility(View.VISIBLE);
            }
        });

        my.arama_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // herhangi bir kelimenin anlamına ulaşma butonu
                // ilk önce kontrol mekanizması ile kelimenin listemizde yer alıp almadığı
                // kontrol edilir
                my.kelime_giris.setEnabled(false);
                my.arama_buton.setEnabled(false);
                my.rasgele_buton.setEnabled(false);
                my.arama_buton.setText("Aranıyor...");

                my.sonuclar.setText(Html.fromHtml("<div style = 'text-align : center'><h2><font color = '#EB984E'><b><i>KELİME ARANIYOR...</i></b></font></h2></div>"));
                my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                String word = my.kelime_giris.getText().toString();
                word = aranacak_kelimeyi_ayarla(word);

                // arama işlemi başladı

                if(kelime_kontrol(word)){
                    // eger kelime ÖSYM'de sıkça kullanılan sözcük listesi içindeyse
                    // direkt kelimenin sadece ÖSYM'de çokça kullanılan anlamı verilir,
                    // diğer anlamları verilmez

                    my.kelime_giris.setText(word);
                    String bastirilacak_anlam = kelimeden_anlam_elde_et(word);
                    bastirilacak_anlam = "<u><i><p style = 'color:#A26900'>Anlamı</p></i></u>" +
                            "<p><i>" + bastirilacak_anlam + "</i></p>";

                    my.kelime_giris.setEnabled(true);
                    my.rasgele_buton.setEnabled(true);
                    my.arama_buton.setEnabled(true);
                    my.arama_buton.setText("Arama");

                    my.sonuclar.setText(Html.fromHtml(bastirilacak_anlam));
                    my.sonuclar.setBackgroundResource(R.drawable.osym_sonuc_border);
                }
                else // eger kelime ÖSYM'de sıkça kullanılan sözcük listesi içinde değilse
                {    // kelime internetten araştırılır
                    TDK tdk = new TDK();
                    tdk.execute();
                    my.sonuclar.setBackgroundResource(R.drawable.normal_sonuc_border);
                    my.sonuclar.setVisibility(View.VISIBLE);
                }

                // arama islemi bittiği için widgetler artık kullanılabilir
            }


        });


        if(my.arama_model.getHemenAra().getValue()) // kelimeler bölümünden gelip gelmediğimizi kontrol ediyoruz
        {
            my.sonuclar.setText("");
            my.sonuclar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            String word = my.arama_model.getKelime().getValue();
            my.kelime_giris.setText(word);
            String bastirilacak_anlam = my.arama_model.getAnlam().getValue();
            bastirilacak_anlam = "<u><i><p style = 'color:#A26900'>Anlamı</p></i></u>" +
                    "<p><i>" + bastirilacak_anlam + "</i></p>";
            my.sonuclar.setText(Html.fromHtml(bastirilacak_anlam));
            my.sonuclar.setBackgroundResource(R.drawable.osym_sonuc_border);
            my.arama_model.setHemenAra(false);
            my.sonuclar.setVisibility(View.VISIBLE);
        }
        my.kelime_giris.setOnKeyListener(new EditText.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    my.arama_buton.performClick();
                    return true;
                }
                return false;
            }
        });

        return my.root;
    }
}