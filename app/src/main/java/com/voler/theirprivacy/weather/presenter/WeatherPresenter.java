package com.voler.theirprivacy.weather.presenter;

import android.util.Log;

import com.voler.theirprivacy.weather.contract.WeatherContract;
import com.voler.theirprivacy.weather.model.WeatherModel;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/12.
 */

public class WeatherPresenter implements WeatherContract.Presenter {
    private WeatherContract.View view;
    private WeatherContract.Model model;

    public WeatherPresenter(WeatherContract.View view) {
        this.view = view;
        this.model = new WeatherModel();
    }

    @Override
    public void requestWeather(String city) {
        model.requestWeather(city).subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        Log.i("-----", "showdiolog");
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0() {
                    @Override
                    public void call() {
                        Log.i("-----", "dissdiolog");
                    }
                }).subscribe(new Subscriber<WeatherEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.i("-----", e.getMessage());
            }

            @Override
            public void onNext(WeatherEntity weatherEntity) {
                view.showWeather(weatherEntity);
                Log.i("-----", weatherEntity.toString());
            }
        });

    }
}
