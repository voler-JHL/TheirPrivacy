package com.voler.theirprivacy.weather.model;

import com.voler.theirprivacy.api.HttpManager;
import com.voler.theirprivacy.weather.contract.WeatherContract;
import com.voler.theirprivacy.weather.model.entity.AddressEntity;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;

import rx.Observable;


/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/12.
 */

public class WeatherModel implements WeatherContract.Model {
    @Override
    public Observable<WeatherEntity> requestWeather(String city) {
        return HttpManager.getHttpService().getUsers(city);
    }

    @Override
    public Observable<AddressEntity> requestAddress() {
        return HttpManager.getHttpService().getAddress();
    }
}
