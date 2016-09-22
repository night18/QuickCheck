package com.ceres.quickcheck.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 圓餅圖
 * Created by apple on 2016/4/24.
 */
public class PieChartFragment extends MyFragment {
    private View view;
    private PieChart pieChart;
    private ImageButton close, condition;
    private RadioGroup rg_pie;
    private TextView tv_pie_period,tv_pie_sum_money;
    private LinearLayout ll_pie_line;
    private Calendar start_c, end_c;
    private boolean isNeverChangeTime = true, isIncome = false;

    public static PieChartFragment newInstance(float basicHeight, float screen_width,boolean isIncome, String startTime, String endTime){
        PieChartFragment pieChartFragment = new PieChartFragment();

        Bundle args = new Bundle();
        pieChartFragment.setArguments(args);
        args.putFloat("basicHeight", basicHeight);
        args.putFloat("screen_width",screen_width);
        args.putBoolean("isIncome", isIncome);
        args.putString("startTime",startTime);
        args.putString("endTime", endTime);


        return pieChartFragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        super.onCreateView(inflater, container, saveInstanceState);

        view = inflater.inflate(R.layout.pie_chart, container, false);
        setLayoutHieghtByUnit(view, 28);
        RelativeLayout rl_top = (RelativeLayout) view.findViewById(R.id.rl_pie_top);
        setLayoutHieghtByUnit(rl_top, 2);
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.sv_pie);
        setLayoutHieghtByUnit(scrollView, 9);

        start_c = Calendar.getInstance();
        end_c = Calendar.getInstance();

        close = (ImageButton) view.findViewById(R.id.close_char);
        condition = (ImageButton)view.findViewById(R.id.pie_condition);

        pieChart = (PieChart) view.findViewById(R.id.pieChartView);
        setLayoutHieghtByUnit(pieChart, 14);

        tv_pie_period = (TextView)view.findViewById(R.id.pie_period);
        tv_pie_period.setText(getStartTime() + " ~ " + getEndTime());
        tv_pie_period.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        tv_pie_sum_money = (TextView)view.findViewById(R.id.pie_sum_money);
        tv_pie_sum_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        ll_pie_line = (LinearLayout)view.findViewById(R.id.ll_pie_line);

        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(false);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        setData(getStartTime(), getEndTime(), getIsIncome());
        isIncome = getIsIncome();
        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(0);
            }
        });

        condition.setOnClickListener(condition_lostener);
        tv_pie_period.setOnClickListener(condition_lostener);

        rg_pie = (RadioGroup) view.findViewById(R.id.rg_pie);
        rg_pie.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_income:
                        setData(getStartTime(), getEndTime(), true);
                        isIncome = true;
                        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                        break;
                    case R.id.rb_outcome:
                        setData(getStartTime(), getEndTime(), false);
                        isIncome = false;
                        pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                        break;
                }

            }
        });





        return view;
    }

    private void setData(String startTime, String endTime, boolean isIncome){
        ArrayList<Entry> yVals = new ArrayList<>();
        ArrayList<String> xVals = MainActivity.dbhelper.getCategoryInPeriod(startTime, endTime, isIncome);
        PieDataSet dataSet;
        int booleanToInt;


        if(((LinearLayout) ll_pie_line).getChildCount() > 0){
            ll_pie_line.removeAllViews();
        }

        if(isIncome){
            dataSet = new PieDataSet(yVals, getString(R.string.income));
            booleanToInt = 1;
            tv_pie_sum_money.setTextColor(getActivity().getResources().getColor(R.color.color_income));
        }else {
            dataSet = new PieDataSet(yVals, getString(R.string.outcome));
            booleanToInt = 2;
            tv_pie_sum_money.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
        }

        Calendar c1 = Calendar.getInstance();
        c1.set(Integer.parseInt(spiltStringArray(startTime, "-")[0]), Integer.parseInt(spiltStringArray(startTime, "-")[1]) - 1, Integer.parseInt(spiltStringArray(startTime, "-")[2]));

        Calendar c2 = Calendar.getInstance();
        c2.set(Integer.parseInt(spiltStringArray(endTime, "-")[0]), Integer.parseInt(spiltStringArray(endTime, "-")[1]) - 1, Integer.parseInt(spiltStringArray(endTime, "-")[2]));

        long aDayInMilliSecond = 60 * 60 * 24 * 1000;     //一天的毫秒數
        long dayDiff = (c2.getTimeInMillis() - c1.getTimeInMillis()) / aDayInMilliSecond;
        int periodSum = 0;

        for(int i = 0 ; i <= dayDiff; i++){
            String Month = c1.get(Calendar.MONTH) < 9?
                    "0" + (c1.get(Calendar.MONTH)+1):(c1.get(Calendar.MONTH)+1) + "";
            String Year = c1.get(Calendar.YEAR)+"";
            String Day =c1.get(Calendar.DAY_OF_MONTH) < 9?
                    "0" + (c1.get(Calendar.DAY_OF_MONTH)):(c1.get(Calendar.DAY_OF_MONTH)) + "";

            periodSum += MainActivity.dbhelper.showTodayMoney(Year+"-"+Month+"-"+Day,booleanToInt);
            c1.add(Calendar.DAY_OF_MONTH,1);
        }

        tv_pie_sum_money.setText("NT$ " + periodSum);

        for(int i = 0; i < xVals.size(); i++ ){
            int sum_period = MainActivity.dbhelper.getSumofCategoryInPeriod(xVals.get(i), startTime, endTime);
            yVals.add(new Entry(sum_period, i));

            LinearLayout ll_v = new LinearLayout(getActivity());
            ViewGroup.LayoutParams ll_v_lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(getBasicHeight() * 2 + 0.5f));
            ll_v.setLayoutParams(ll_v_lp);
            ll_v.setOrientation(LinearLayout.VERTICAL);

            RelativeLayout rl = new RelativeLayout(getActivity());
            ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)(getBasicHeight() * 1 + 0.5f));
            rl.setLayoutParams(layoutParams1);
            TextView tv_category = new TextView(getActivity());

            tv_category.setText(xVals.get(i));
            tv_category.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            tv_category.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            tv_category.setLayoutParams(rlp);

            TextView tv_sum = new TextView(getActivity());

            tv_sum.setText(sum_period + "");
            tv_sum.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            RelativeLayout.LayoutParams rlp2 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tv_sum.setLayoutParams(rlp2);
            if(isIncome){
                tv_sum.setTextColor(getActivity().getResources().getColor(R.color.color_income));
            }else {
                tv_sum.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
            }

            rl.addView(tv_category);
            rl.addView(tv_sum);

            RelativeLayout rl2 = new RelativeLayout(getActivity());
            ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams((int) getScreenWidth()* sum_period / periodSum,(int)(getBasicHeight() * 1 + 0.5f));
            Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.horizon_fade_orange);
            rl2.setLayoutParams(layoutParams2);
            rl2.setBackground(drawable);

            TextView tv_percent = new TextView(getActivity());
            tv_percent.setText(String.format("%.1f", sum_period * 100 / (float) periodSum) + "%");
            RelativeLayout.LayoutParams rlp3 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            rlp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            tv_percent.setLayoutParams(rlp3);
            rl2.addView(tv_percent);

            ll_v.addView(rl);
            ll_v.addView(rl2);

            ll_pie_line.addView(ll_v);
        }

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        //TODO 顏色跟隨使用者選擇
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(16f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);

        pieChart.highlightValue(null);
        pieChart.invalidate();

    }


    private boolean getIsIncome(){
        return getArguments().getBoolean("isIncome", false);
    }
    private String getStartTime(){
        return getArguments().getString("startTime");
    }
    private String getEndTime(){
        return getArguments().getString("endTime");
    }


    private View.OnClickListener condition_lostener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.pie_timepicker);

            Window dialogWindow = dialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.width = (int)getScreenWidth();
            layoutParams.height = (int) getBasicHeight() * 12;
            dialog.onWindowAttributesChanged(layoutParams);

            final Button bt_startTime,bt_endTime,bt_confirm;
            final Spinner sp_quickTime;

            bt_startTime = (Button)dialog.findViewById(R.id.bt_startTime);
            bt_endTime = (Button)dialog.findViewById(R.id.bt_endTime);
            sp_quickTime = (Spinner)dialog.findViewById(R.id.bt_quickTime);
            bt_confirm = (Button)dialog.findViewById(R.id.bt_confirm);

            sp_quickTime.setMinimumHeight((int)(getBasicHeight()*2));

            if(isNeverChangeTime) {
                bt_startTime.setText(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.getActualMinimum(Calendar.DAY_OF_MONTH)));
                start_c.set(start_c.get(Calendar.YEAR), start_c.get(Calendar.MONTH), start_c.getActualMinimum(Calendar.DAY_OF_MONTH));
                bt_endTime.setText(end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.getActualMaximum(Calendar.DAY_OF_MONTH)));
                end_c.set(end_c.get(Calendar.YEAR),end_c.get(Calendar.MONTH),end_c.getActualMaximum(Calendar.DAY_OF_MONTH));
            }else{
                bt_startTime.setText(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.get(Calendar.DAY_OF_MONTH)));
                bt_endTime.setText(end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH)));
            }

            bt_startTime.setBackgroundColor(getActivity().getResources().getColor(R.color.colorBackground));
            bt_endTime.setBackgroundColor(getActivity().getResources().getColor(R.color.colorBackground));

            bt_startTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v1) {
                    final Dialog startpicker = new Dialog(getActivity());
                    startpicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    startpicker.setContentView(R.layout.customdatapicker);

                    DatePicker datePicker = (DatePicker) startpicker.findViewById(R.id.datePicker);
                    datePicker.init(start_c.get(Calendar.YEAR), start_c.get(Calendar.MONTH), start_c.get(Calendar.DAY_OF_MONTH),
                            new DatePicker.OnDateChangedListener() {
                                @Override
                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    start_c.set(year, monthOfYear, dayOfMonth);
                                    ((Button) v1).setText(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.get(Calendar.DAY_OF_MONTH)));
                                    startpicker.dismiss();
                                    sp_quickTime.setSelection(4);
                                    isNeverChangeTime = false;
                                }
                            });

                    startpicker.show();

                }
            });

            bt_endTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v1) {
                    final Dialog endPicker = new Dialog(getActivity());
                    endPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    endPicker.setContentView(R.layout.customdatapicker);

                    DatePicker datePicker = (DatePicker) endPicker.findViewById(R.id.datePicker);
                    datePicker.init(end_c.get(Calendar.YEAR), end_c.get(Calendar.MONTH), end_c.get(Calendar.DAY_OF_MONTH),
                            new DatePicker.OnDateChangedListener() {
                                @Override
                                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    end_c.set(year, monthOfYear, dayOfMonth);
                                    ((Button) v1).setText(end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH)));
                                    endPicker.dismiss();
                                    sp_quickTime.setSelection(4);
                                    isNeverChangeTime = false;
                                }
                            });

                    endPicker.show();

                }
            });

            ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),R.array.quick_time_choose,R.layout.spinner_center_item);
            adapter.setDropDownViewResource(R.layout.spinner_center_item);
            sp_quickTime.setAdapter(adapter);
            sp_quickTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Calendar tmp_calendar = Calendar.getInstance();
                    int tmp_y;
                    int tmp_m;
                    switch (position){
                        case 0:
                            tmp_y = tmp_calendar.get(Calendar.YEAR);
                            tmp_m = tmp_calendar.get(Calendar.MONTH);
                            start_c.set(tmp_y,tmp_m,tmp_calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            end_c.set(tmp_y,tmp_m,tmp_calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            break;
                        case 1:
                            tmp_calendar.add(Calendar.MONTH,-1);
                            tmp_y = tmp_calendar.get(Calendar.YEAR);
                            tmp_m = tmp_calendar.get(Calendar.MONTH);
                            start_c.set(tmp_y,tmp_m,tmp_calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            end_c.set(tmp_y,tmp_m,tmp_calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            break;
                        case 2:
                            tmp_y = tmp_calendar.get(Calendar.YEAR);
                            start_c.set(tmp_y,tmp_calendar.getActualMinimum(Calendar.MONTH),tmp_calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            end_c.set(tmp_y,tmp_calendar.getActualMaximum(Calendar.MONTH),31);
                            break;
                        case 3:
                            tmp_calendar.add(Calendar.YEAR,-1);
                            tmp_y = tmp_calendar.get(Calendar.YEAR);
                            start_c.set(tmp_y,tmp_calendar.getActualMinimum(Calendar.MONTH),tmp_calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                            end_c.set(tmp_y,tmp_calendar.getActualMaximum(Calendar.MONTH),31);
                            break;
                        default:
                            break;
                    }
                    bt_startTime.setText(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.get(Calendar.DAY_OF_MONTH)));
                    bt_endTime.setText(end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH)));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });



            bt_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isNeverChangeTime = false;

                    setData(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.get(Calendar.DAY_OF_MONTH))
                            ,end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH))
                            ,isIncome);
                    tv_pie_period.setText(start_c.get(Calendar.YEAR) + "-" + fixDate(start_c.get(Calendar.MONTH) + 1) + "-" + fixDate(start_c.get(Calendar.DAY_OF_MONTH))
                            + " ~ " + end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH)));

                    dialog.dismiss();

                }
            });




            dialog.show();
        }
    };
}
