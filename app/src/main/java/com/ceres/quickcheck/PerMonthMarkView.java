package com.ceres.quickcheck;

/**
 * Created by apple on 2016/4/23.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.os.Message;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

import android.content.Context;
import android.os.Message;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by apple on 2016/4/19.
 */
public class PerMonthMarkView extends MarkerView {

    private TextView tvcontent;
    private LineChart lineChart;
    private MainActivity myActivity;
    private int color;
    private int uiScreenWidth;

    public PerMonthMarkView(Context context, int layoutResource, LineChart lineChart, int colorCode){
        super(context, layoutResource);

        tvcontent = (TextView) findViewById(R.id.tvContent);
        this.lineChart = lineChart;
        this.myActivity = (MainActivity)context;
        this.color = colorCode;
        uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
    }

    public PerMonthMarkView(Context context, int layoutResource) {
        this(context, layoutResource, null,0);
    }

    @Override
    public void draw(Canvas canvas, float posx, float posy)
    {
        // Check marker position and update offsets.
        int w = getWidth();
        if((uiScreenWidth-posx-w) < w) {
            posx -= w;
        }

        // translate to the correct position and draw
        canvas.translate(posx, posy);
        draw(canvas);
        canvas.translate(-posx, -posy);
    }

    @Override
    public void refreshContent(Entry entry, Highlight highlight) {
        if(entry instanceof CandleEntry){
            CandleEntry ce = (CandleEntry) entry;

            tvcontent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
        }else{
            findViewById(R.id.mark_background).setBackgroundColor(color);
            findViewById(R.id.mark_background).setAlpha(0.5f);
            int x = entry.getXIndex();
            String s_date = "$";
            if(lineChart != null){
                s_date = lineChart.getData().getXVals().get(x)+"  \n$ ";
            }

            tvcontent.setText(s_date  + Utils.formatNumber(entry.getVal(), 0, true));
        }
    }

    @Override
    public int getXOffset(float v) {
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float v) {
        return -getHeight();
    }
}
