package com.example.mobilodev;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView textView;
    //konum hizmetlerine erişen sınıf
    private LocationManager locationManager;
    //konum değişikliğini algılayan sınıf
    private LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            //konum değiştirme methodu, konum her güncellendiğinde çağrılır.
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String sehir = hereLocation(location.getLatitude(), location.getLongitude());
                textView.setText(sehir);
            }

            //GPS açık mı kapalı mı onu kontrol eder. Kullanıcıyı, uygulama için GPS ayarlarını
            //etkinleştirebileceği Ayarlar paneline götürecek intent buraya yazılır.
            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET
            }, 10);
            return;
        }else{
            configureButton();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 10:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
        }
    }

    private void configureButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Konum isteğinde bulunmak için:
                //parametreler:
                //provider: gps servis
                //mintime(3000): ne sıklıkla yenilenmesi gerektiğini temsil eder. milisaniye cinsinden yazılır
                //minDistance: 5 yazarsak Konum bir önceki konuma göre 5 metre değiştiğinde güncellenecektir.
                //(biz 0 yaptık, 5 saniyede bir konum olduğumuz yerde güncellenecek.)
                locationManager.requestLocationUpdates("gps", 3000, 0, locationListener);
            }
        });
    }

    //Şehir tespit etmek için Geocoder:
    private String hereLocation(double lat, double lon){

        String sehirAdi = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 10);
            if (addresses.size() > 0) {
                for (Address adr: addresses){
                    if(adr.getLocality() != null && adr.getLocality().length() > 0){
                        sehirAdi = adr.getLocality();
                        break;
                    }
                }
            }

        } catch (IOException e){
            e.printStackTrace();
        }
        return sehirAdi;
    }
}