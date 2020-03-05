package org.khj.khjbasiscamerasdk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.khj.khjbasiscamerasdk.R;

public class DpiAdapter extends BaseAdapter {
    private String[] dpis;
    private Context mContext;
    public DpiAdapter(Context context) {
        dpis = context.getResources().getStringArray(R.array.videoRecordQuality);
        mContext=context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return dpis[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dpi, parent, false);
        TextView textView = view.findViewById(R.id.tv_ssid);
        textView.setText(dpis[position]);
        return view;
    }
}
