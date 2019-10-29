package com.joseph.weather.viewmodel;

import androidx.lifecycle.ViewModel;

import com.joseph.weather.api.Api;
import com.joseph.weather.api.RetrofitClientInstance;
import com.joseph.weather.model.Response;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainViewModel extends ViewModel {
    private Api api;
    final private String API_ID = "ad74a277a5027b58e567ca21fcf36359";


    public MainViewModel() {
        api = RetrofitClientInstance.getRetrofitInstance().create(Api.class);
    }

    public Observable<Response> getCurrentWeatherByLocation(final double lat, final double lon) {
        return api.getWeatherByLocation(lat, lon, API_ID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
