package com.ceres.quickcheck.Fragments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.PerMonthMarkView;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 每月結餘圖
 * Created by apple on 2016/4/21.
 */
public class SumPerMonthFragment extends MyFragment {
    private View view;
    private LineChart chart_view;
    private ImageButton close;
    Calendar char_calendar;

    public static SumPerMonthFragment newInstance(float basicHeight){
        SumPerMonthFragment sumPerMonthFragment = new SumPerMonthFragment();

        Bundle args = new Bundle();
        sumPerMonthFragment.setArguments(args);
        args.putFloat("basicHeight",basicHeight);

        return sumPerMonthFragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        super.onCreateView(inflater, container, saveInstanceState);
        char_calendar = Calendar.getInstance();
        view = inflater.inflate(R.layout.sum_per_month, container, false);

        close = (ImageButton)view.findViewById(R.id.close_char);
        chart_view = (LineChart) view.findViewById(R.id.Sum_LineChartView);

        setLayoutHieghtByUnit(chart_view, 15);
        chart_view.setPadding(15, 15, 15, 15);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(0);
            }
        });

        chart_view.setScaleEnabled(false);
        chart_view.setDragEnabled(true);
        chart_view.setDescription("");

        PerMonthMarkView mv = new PerMonthMarkView(getActivity(), R.layout.customer_marker_view,chart_view, getActivity().getResources().getColor(R.color.color_income));
        chart_view.setMarkerView(mv);

        XAxis xAxis = chart_view.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart_view.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        chart_view.getAxisRight().setEnabled(false);

        setLinechartDataView();

        chart_view.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        Legend l = chart_view.getLegend();

        l.setForm(Legend.LegendForm.LINE);


        return view;
    }

    private void setLinechartDataView(){

        int upperLimit = 0;
        int lowerLimit = 0;
        int initial_sum = 0;

        ArrayList<String> XVals = new ArrayList<String>();
        ArrayList<Entry> YVals = new ArrayList<Entry>();

        String Month = char_calendar.get(Calendar.MONTH) < 9?
                "0" + (char_calendar.get(Calendar.MONTH)+1):(char_calendar.get(Calendar.MONTH)+1) + "";
        String Year = char_calendar.get(Calendar.YEAR)+"";

        int total_month = MainActivity.dbhelper.countMonths(Year + "-" + Month);
        char_calendar.roll(Calendar.MONTH, -total_month);

        for(int i = 0; i <= total_month; i++){
            String show_m = (char_calendar.get(Calendar.MONTH)+1)+ "";
            String m = char_calendar.get(Calendar.MONTH) < 9?
                    "0" + (char_calendar.get(Calendar.MONTH)+1):(char_calendar.get(Calendar.MONTH)+1) + "";
            String y = char_calendar.get(Calendar.YEAR)+"";

            XVals.add( Year + "/"+ show_m );

            initial_sum +=  MainActivity.dbhelper.showMonthMoney(y+"-"+m);

            YVals.add(new Entry( (float)initial_sum , i));

            if(upperLimit<initial_sum){
                upperLimit = initial_sum;
            }
            if(lowerLimit > initial_sum){
                lowerLimit = initial_sum;
            }
            char_calendar.roll(Calendar.MONTH,1);
        }

        chart_view.getAxisLeft().setAxisMaxValue(upperLimit);
        chart_view.getAxisLeft().setAxisMinValue(lowerLimit);

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(YVals, "支出");

        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(2f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);
        set1.setDrawValues(false);

        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_green);
            set1.setFillDrawable(drawable);
        }
        else {
            set1.setFillColor(Color.BLACK);
        }

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(XVals, dataSets);

        // set data
        chart_view.setData(data);

    }

}
