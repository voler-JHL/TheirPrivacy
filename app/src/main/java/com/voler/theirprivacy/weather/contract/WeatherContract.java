package com.voler.theirprivacy.weather.contract;

import com.voler.theirprivacy.weather.model.entity.AddressEntity;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;

import rx.Observable;


/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/12.
 */

public interface WeatherContract {
    interface View extends BaseContract.View {
        void showWeather(WeatherEntity weatherEntity);
    }

    interface Model extends BaseContract.Model {
        Observable<WeatherEntity> requestWeather(String city);

        Observable<AddressEntity> requestAddress();
    }

    interface Presenter extends BaseContract.Presenter {
        void requestWeather(String city);

        void requestAddress();
    }
}
