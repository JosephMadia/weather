package com.joseph.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.joseph.weather.model.Response;
import com.joseph.weather.viewmodel.MainViewModel;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.BehaviorSubject;

@SuppressLint("MissingPermission")
public class MainActivity extends AppCompatActivity {
    MainViewModel mainViewModel = new MainViewModel();
    BehaviorSubject<Location> behaviorSubject = BehaviorSubject.create();
    CompositeDisposable disposable = new CompositeDisposable();
    Button button;
    TextView tvCurrentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        tvCurrentWeather = findViewById(R.id.textView);

        isPermissionGranted();

        disposable.add(behaviorSubject.map(new Function<Location, Observable<Response>>() {
            @Override
            public Observable<Response> apply(Location location) {
                tvCurrentWeather.setText("Loading");
                return mainViewModel.getCurrentWeatherByLocation(location.getLatitude(), location.getLongitude());
            }
        }).subscribe(new Consumer<Observable<Response>>() {
                 @Override
                 public void accept(Observable<Response> responseObservable) {
                     responseObservable.subscribe(new Consumer<Response>() {
                         @Override
                         public void accept(Response response) {
                            tvCurrentWeather.setText(String.format("Current Weather : %s Description:  %s ", response.getWeather().get(0).getMain(), response.getWeather().get(0).getDescription()));

                         }
                     });
                 }
             }
        ));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        behaviorSubject.onNext(location);
                    } else {
                        Toast.makeText(MainActivity.this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(MainActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
