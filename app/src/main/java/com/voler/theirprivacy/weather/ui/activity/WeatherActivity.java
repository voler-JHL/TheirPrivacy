package com.voler.theirprivacy.weather.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.voler.theirprivacy.R;
import com.voler.theirprivacy.adapter.WeatherAdapter;
import com.voler.theirprivacy.weather.contract.WeatherContract;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;
import com.voler.theirprivacy.weather.presenter.WeatherPresenter;
import com.voler.theirprivacy.widget.MyListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/11.
 */

public class WeatherActivity extends AppCompatActivity implements WeatherContract.View {

    @BindView(R.id.iv_cond)
    ImageView ivCond;
    @BindView(R.id.tv_cond)
    TextView tvCond;
    @BindView(R.id.tv_tmp)
    TextView tvTmp;
    @BindView(R.id.tv_fl)
    TextView tvFl;
    @BindView(R.id.tv_hum)
    TextView tvHum;
    @BindView(R.id.tv_pcpn)
    TextView tvPcpn;
    @BindView(R.id.tv_pres)
    TextView tvPres;
    @BindView(R.id.tv_vis)
    TextView tvVis;
    @BindView(R.id.tv_dir)
    TextView tvDir;
    @BindView(R.id.tv_sc)
    TextView tvSc;
    @BindView(R.id.tv_spd)
    TextView tvSpd;
    @BindView(R.id.tv_comf_brf)
    TextView tvComfBrf;
    @BindView(R.id.iv_comf_brf)
    ImageView ivComfBrf;
    @BindView(R.id.tv_comf_text)
    TextView tvComfText;
    @BindView(R.id.tv_cw_brf)
    TextView tvCwBrf;
    @BindView(R.id.iv_cw_brf)
    ImageView ivCwBrf;
    @BindView(R.id.tv_cw_text)
    TextView tvCwText;
    @BindView(R.id.tv_drsg_brf)
    TextView tvDrsgBrf;
    @BindView(R.id.iv_drsg_brf)
    ImageView ivDrsgBrf;
    @BindView(R.id.tv_drsg_text)
    TextView tvDrsgText;
    @BindView(R.id.tv_flu_brf)
    TextView tvFluBrf;
    @BindView(R.id.iv_flu_brf)
    ImageView ivFluBrf;
    @BindView(R.id.tv_flu_text)
    TextView tvFluText;
    @BindView(R.id.tv_sport_brf)
    TextView tvSportBrf;
    @BindView(R.id.iv_sport_brf)
    ImageView ivSportBrf;
    @BindView(R.id.tv_sport_text)
    TextView tvSportText;
    @BindView(R.id.tv_trav_brf)
    TextView tvTravBrf;
    @BindView(R.id.iv_trav_brf)
    ImageView ivTravBrf;
    @BindView(R.id.tv_trav_text)
    TextView tvTravText;
    @BindView(R.id.lv_daily_forecast)
    MyListView lvDailyForecast;

    private WeatherPresenter mWeatherPresenter = new WeatherPresenter(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        mWeatherPresenter.requestAddress();
    }


    @Override
    public void showWeather(WeatherEntity weatherEntity) {

        WeatherEntity.HeWeatherSataServiceBean heWeatherSataServiceBean = weatherEntity.getHeWeatherDataService().get(0);
        WeatherEntity.HeWeatherSataServiceBean.NowBean nowBean = heWeatherSataServiceBean.getNow();
        WeatherEntity.HeWeatherSataServiceBean.SuggestionBean suggestion = heWeatherSataServiceBean.getSuggestion();


        Glide.with(this)
                .load("http://files.heweather.com/cond_icon/" + nowBean.getCond().getCode() + ".png")
                .into(ivCond);
        tvTmp.setText(nowBean.getTmp() + "℃");
        tvCond.setText(nowBean.getCond().getTxt());


        tvFl.setText("体感温度：" + nowBean.getFl() + "℃");
        tvHum.setText("相对湿度：" + nowBean.getHum() + "%");
        tvPcpn.setText("降水量：" + nowBean.getPcpn() + "mm");
        tvPres.setText("气压：" + nowBean.getPres());
        tvVis.setText("能见度：" + nowBean.getVis() + "km");
        tvDir.setText("风向：" + nowBean.getWind().getDir());
        tvSc.setText("风力：" + nowBean.getWind().getSc() + "级");
        tvSpd.setText("风速：" + nowBean.getWind().getSpd() + "kmph");
        tvComfBrf.setText(suggestion.getComf().getBrf());
        tvComfText.setText(suggestion.getComf().getTxt());
        tvCwBrf.setText(suggestion.getCw().getBrf());
        tvCwText.setText(suggestion.getCw().getTxt());
        tvDrsgBrf.setText(suggestion.getDrsg().getBrf());
        tvDrsgText.setText(suggestion.getDrsg().getTxt());
        tvFluBrf.setText(suggestion.getFlu().getBrf());
        tvFluText.setText(suggestion.getFlu().getTxt());
        tvSportBrf.setText(suggestion.getSport().getBrf());
        tvSportText.setText(suggestion.getSport().getTxt());
        tvTravBrf.setText(suggestion.getTrav().getBrf());
        tvTravText.setText(suggestion.getTrav().getTxt());

        lvDailyForecast.setAdapter(new WeatherAdapter(heWeatherSataServiceBean.getDaily_forecast(), this));
    }

    @OnClick({R.id.iv_comf_brf, R.id.iv_cw_brf, R.id.iv_drsg_brf, R.id.iv_flu_brf, R.id.iv_sport_brf, R.id.iv_trav_brf})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_comf_brf:
                setDescVisible(tvComfText, view);
                break;
            case R.id.iv_cw_brf:
                setDescVisible(tvCwText, view);
                break;
            case R.id.iv_drsg_brf:
                setDescVisible(tvDrsgText, view);
                break;
            case R.id.iv_flu_brf:
                setDescVisible(tvFluText, view);
                break;
            case R.id.iv_sport_brf:
                setDescVisible(tvSportText, view);
                break;
            case R.id.iv_trav_brf:
                setDescVisible(tvTravText, view);
                break;
        }
    }

    private void setDescVisible(TextView descVisible, View view) {
        if (descVisible.getVisibility() == View.VISIBLE) {
            descVisible.setVisibility(View.GONE);
            ((ImageView) view).setImageResource(R.mipmap.arrow_up);
        } else {
            descVisible.setVisibility(View.VISIBLE);
            ((ImageView) view).setImageResource(R.mipmap.arrow_down);
        }
    }
}
