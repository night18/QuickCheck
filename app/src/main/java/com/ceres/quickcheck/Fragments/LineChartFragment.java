package com.ceres.quickcheck.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.MyMarkerView;
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

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 線型圖
 * Created by apple on 2016/4/12.
 */
public class LineChartFragment extends MyFragment {
    private View view;
    private LineChart chart_view;
    private TextView todayDate;
    private ImageButton ib_prev,ib_next,close_char;
    private Calendar char_calendar;
    private RadioGroup rg_line;
    private int type = 2;

    public static LineChartFragment newInstance(float basicHeight){
        LineChartFragment mainFragment = new LineChartFragment();

        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        args.putFloat("basicHeight",basicHeight);

        return mainFragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        char_calendar = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        super.onCreateView(inflater,container,saveInstanceState);

        view = inflater.inflate(R.layout.line_chart, container, false);

        todayDate = (TextView) view.findViewById(R.id.today_date);
        ib_prev = (ImageButton)view.findViewById(R.id.prev);
        ib_next = (ImageButton)view.findViewById(R.id.next);
        close_char = (ImageButton)view.findViewById(R.id.close_char);
        setDate();

        rg_line = (RadioGroup)view.findViewById(R.id.rg_line);
        rg_line.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_outcome:
                        type = 2;
                        break;
                    case R.id.rb_income:
                        type = 1;
                        break;
                    case R.id.rb_all:
                        type = 3;
                        break;
                }
                setLinechartDataView();
            }
        });

        chart_view = (LineChart) view.findViewById(R.id.LineChartView);
        setLayoutHieghtByUnit(chart_view, 15);
        chart_view.setPadding(15, 15, 15, 15);


        ib_prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                char_calendar.roll(Calendar.MONTH, -1);
                setDate();
                setLinechartDataView();
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                char_calendar.roll(Calendar.MONTH, 1);
                setDate();
                setLinechartDataView();
            }
        });

        close_char.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(0);
                ((MainActivity) getActivity()).isLineChart = false;
            }
        });

        chart_view.setScaleEnabled(false);
        chart_view.setDragEnabled(true);
        chart_view.setDescription("");

        XAxis xAxis = chart_view.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = chart_view.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(true);

        chart_view.getAxisRight().setEnabled(false);

        setLinechartDataView();

        return view;
    }

    /**設定時間**/
    private void setDate(){
        int tyear = char_calendar.get(Calendar.YEAR);
        int tmonth = char_calendar.get(Calendar.MONTH)+1;


        todayDate.setText(tyear + "年" + tmonth + "月");
    }

    private void setLinechartDataView(){

        float upperLimit = 100;
        int color = 0,color2 = 0;//避免出意外
        String showBeside = "";
        if(type == 1){
            color = getActivity().getResources().getColor(R.color.color_income);
            showBeside = getActivity().getResources().getString(R.string.income);
        }else if(type == 2){
            color = getActivity().getResources().getColor(R.color.color_cost);
            showBeside = getActivity().getResources().getString(R.string.outcome);
        }else if(type == 3){
            color = getActivity().getResources().getColor(R.color.color_income);
            color2 = getActivity().getResources().getColor(R.color.color_cost);
        }


        ArrayList<String> XVals = new ArrayList<String>();
        ArrayList<Entry> YVals = new ArrayList<Entry>();
        ArrayList<Entry> YVals_2 = new ArrayList<Entry>();

        String Month = char_calendar.get(Calendar.MONTH) < 9?
                "0" + (char_calendar.get(Calendar.MONTH)+1):(char_calendar.get(Calendar.MONTH)+1) + "";
        String Year = char_calendar.get(Calendar.YEAR)+"";

        for(int i = 0; i < char_calendar.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
            String day = i < 9? "0" + (i + 1): "" + (i + 1);
            String today_date =  Year + "-" + Month + "-"+ day;

            if( i < 9){
                XVals.add( Year + "-"+ Month + "-0" +(i+1));//日子
            }else{
                XVals.add( Year + "-"+ Month + "-" +(i+1));//日子
            }

            if(type != 3) {
                YVals.add(new Entry((float) MainActivity.dbhelper.showTodayMoney(today_date, type), i));

                if (upperLimit < MainActivity.dbhelper.showTodayMoney(today_date, type)) {
                    upperLimit = MainActivity.dbhelper.showTodayMoney(today_date, type);
                }
            }else{
                YVals.add(new Entry((float) MainActivity.dbhelper.showTodayMoney(today_date, 1), i));

                if (upperLimit < MainActivity.dbhelper.showTodayMoney(today_date, 1)) {
                    upperLimit = MainActivity.dbhelper.showTodayMoney(today_date, 1);
                }

                YVals_2.add(new Entry((float) MainActivity.dbhelper.showTodayMoney(today_date, 2), i));

                if (upperLimit < MainActivity.dbhelper.showTodayMoney(today_date, 2)) {
                    upperLimit = MainActivity.dbhelper.showTodayMoney(today_date, 2);
                }
            }
        }

        chart_view.getAxisLeft().setAxisMaxValue(upperLimit);

        LineDataSet set1;
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();

        if(type != 3) {
            // create a dataset and give it a type
            set1 = new LineDataSet(YVals, showBeside);

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(color);
            set1.setCircleColor(color);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);

            //        if (Utils.getSDKInt() >= 18) {
            //            // fill drawable only supported on api level 18 and above
            //            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.fade_green);
            //            set1.setFillDrawable(drawable);
            //        }
            //        else {
            //            set1.setFillColor(Color.BLACK);
            //        }

            dataSets.add(set1); // add the datasets


        }else{//全部
            // create a dataset and give it a type
            set1 = new LineDataSet(YVals, getActivity().getResources().getString(R.string.income));

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(color);
            set1.setCircleColor(color);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(false);
            set1.setDrawValues(false);

            LineDataSet set2 = new LineDataSet(YVals_2, getActivity().getResources().getString(R.string.outcome));
            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
            set2.enableDashedLine(10f, 5f, 0f);
            set2.enableDashedHighlightLine(10f, 5f, 0f);
            set2.setColor(color2);
            set2.setCircleColor(color2);
            set2.setLineWidth(1f);
            set2.setCircleRadius(2f);
            set2.setDrawCircleHole(false);
            set2.setValueTextSize(9f);
            set2.setDrawFilled(false);
            set2.setDrawValues(false);

            dataSets.add(set1); // add the datasets
            dataSets.add(set2);
        }

        // create a data object with the datasets
        LineData data = new LineData(XVals, dataSets);

        // set data
        chart_view.setData(data);

        chart_view.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        Legend l = chart_view.getLegend();

        l.setForm(Legend.LegendForm.LINE);

        MyMarkerView mv = new MyMarkerView(getActivity(), R.layout.customer_marker_view,chart_view , color);
        chart_view.setMarkerView(mv);
    }

}
