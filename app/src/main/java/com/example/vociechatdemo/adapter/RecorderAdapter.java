package com.example.vociechatdemo.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vociechatdemo.R;
import com.example.vociechatdemo.ui.MainActivity;

import java.util.List;

public class RecorderAdapter extends ArrayAdapter<MainActivity.Recorder> {
    private List<MainActivity.Recorder> mDatas;
    private Context mContext;
    private int mMinWidth;
    private int mMaxWidth;
    private LayoutInflater layoutInflater;

    public RecorderAdapter(Context context, List<MainActivity.Recorder> datas) {
        super(context, -1,datas);
        this.mDatas = datas;
        this.mContext = context;

        layoutInflater = LayoutInflater.from(context);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        mMaxWidth = (int) (outMetrics.widthPixels*0.7f);
        mMinWidth = (int) (outMetrics.widthPixels*0.15f);
    }



    @Override
    public View getView(int position,  View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            convertView  = layoutInflater.inflate(R.layout.item_recorder,parent,false);
            holder = new ViewHolder();
            holder.lengths = convertView.findViewById(R.id.id_recorder_length);
            holder.seconds = convertView.findViewById(R.id.id_recorder_time);
            convertView.setTag(holder);

        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.seconds.setText(Math.round(getItem(position).getTime())+"\"");

        ViewGroup.LayoutParams layoutParams = holder.lengths.getLayoutParams();
        layoutParams.width = (int) (mMinWidth+mMaxWidth/60f*getItem(position).getTime());
        return convertView;
    }

    class ViewHolder{
        TextView seconds;
        View lengths;
    }
}
