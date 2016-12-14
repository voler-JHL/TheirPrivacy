package com.voler.theirprivacy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.voler.theirprivacy.R;
import com.voler.theirprivacy.weather.model.entity.WeatherEntity;

import java.util.List;

/**
 * 三尺春光驱我寒，一生戎马为长安
 * Created by Han on 16/12/13.
 */

public class WeatherAdapter extends BaseAdapter{
    protected List<WeatherEntity.HeWeatherSataServiceBean.DailyForecastBean> list ;
    protected Context mContext;




    public WeatherAdapter(List list,Context context) {
        this.list = list;
        this.mContext=context;
    }


    /**
     * 更新经验列表
     */
    public void notifyDataSetChanged(int skipCount, List array) {
        if(array == null) {
            return;
        }
        synchronized (WeatherAdapter.class) {
            if(skipCount <= 0) {
                this.list = array;
            } else {
                this.list.addAll(array);
            }
        }
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=ViewHolder.getViewHolder(mContext,convertView,parent, R.layout.item_feature_weather,position);
        TextView tv_date = viewHolder.getView(R.id.tv_date);
        TextView tv_cond_txt_d = viewHolder.getView(R.id.tv_cond_txt_d);
        TextView tv_tmp = viewHolder.getView(R.id.tv_tmp);

        WeatherEntity.HeWeatherSataServiceBean.DailyForecastBean dailyForecastBean = list.get(position);
        tv_date.setText(dailyForecastBean.getDate());
        tv_cond_txt_d.setText(dailyForecastBean.getCond().getTxt_d());
        tv_tmp.setText(dailyForecastBean.getTmp().getMin()+"℃ ~ "+dailyForecastBean.getTmp().getMax()+"℃");

        return viewHolder.getConverView();
    }

}
