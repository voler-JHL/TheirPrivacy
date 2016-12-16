package com.voler.theirprivacy.api;

import com.voler.theirprivacy.weather.model.entity.AddressEntity;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/12.
 */

public interface ApiManager {

    String HEADER_API_VERSION = "b8b6f9e3763c0346a670d63de9b6b5f0";

    @Headers("apikey: b8b6f9e3763c0346a670d63de9b6b5f0")
    @GET("free")
    Observable<WeatherEntity> getUsers(@Query("city") String city);

    @GET("http://api.map.baidu.com/location/ip?ak=B4o1NuxVz84XDwwoL7eglg7lkk7jMf3K")
    Observable<AddressEntity> getAddress();


}
