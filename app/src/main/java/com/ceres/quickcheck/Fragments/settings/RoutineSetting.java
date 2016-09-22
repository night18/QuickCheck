package com.ceres.quickcheck.Fragments.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;

import java.util.Calendar;

/**
 * Created by apple on 2016/5/26.
 */
public class RoutineSetting extends MyFragment {
    private View view;
    private TextView tv_time,tv_startTime,tv_endTime;
    private ImageButton bt_back,ib_save;
    private RelativeLayout setting_header,routine_rule;
    private EditText et_category,et_subcategory,et_note, et_price;
    private Calendar start_calendar, end_calendar;

    public static RoutineSetting newInstance(float basicHeight){
        RoutineSetting fragment = new RoutineSetting();

        Bundle args = new Bundle();
        args.putFloat("basicHeight",basicHeight);
        fragment.setArguments(args);

        return  fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.routine, container, false);
        setLayoutHieghtByUnit(view, 13);

        bt_back = (ImageButton)view.findViewById(R.id.back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(6);
            }
        });

        setting_header = (RelativeLayout)view.findViewById(R.id.setting_header);
        routine_rule = (RelativeLayout)view.findViewById(R.id.routine_rule);
        tv_startTime = (TextView)view.findViewById(R.id.tv_startTime);
        tv_endTime = (TextView)view.findViewById(R.id.tv_endTime);
        et_category = (EditText) view.findViewById(R.id.category);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        et_subcategory = (EditText) view.findViewById(R.id.sub_category);
        et_note = (EditText) view.findViewById((R.id.et_note));
        et_price = (EditText) view.findViewById(R.id.et_price);
        ib_save = (ImageButton) view.findViewById(R.id.ib_save);

        start_calendar = Calendar.getInstance();
        int year = start_calendar.get(Calendar.YEAR);
        int month = start_calendar.get(Calendar.MONTH)+1;
        int first_day = start_calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
        int last_day = start_calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int hour = start_calendar.get(Calendar.HOUR_OF_DAY);
        int minute = start_calendar.get(Calendar.MINUTE);

        tv_startTime.setText( year + "-" + month + "-" + first_day);
        tv_endTime.setText(year + "-" + month + "-" + last_day);

        tv_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog startpicker = new Dialog(getActivity());
                startpicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
                startpicker.setContentView(R.layout.customdatapicker);

                DatePicker datePicker = (DatePicker) startpicker.findViewById(R.id.datePicker);
                datePicker.init(start_calendar.get(Calendar.YEAR), start_calendar.get(Calendar.MONTH), start_calendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                start_calendar.set(year, monthOfYear, dayOfMonth);
                                tv_startTime.setText(start_calendar.get(Calendar.YEAR) + "-" + fixDate(start_calendar.get(Calendar.MONTH) + 1) + "-" + fixDate(start_calendar.get(Calendar.DAY_OF_MONTH)));
                                startpicker.dismiss();
                            }
                        });

                startpicker.show();
            }
        });

        tv_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog endPicker = new Dialog(getActivity());
                endPicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
                endPicker.setContentView(R.layout.customdatapicker);

                DatePicker datePicker = (DatePicker) endPicker.findViewById(R.id.datePicker);
                datePicker.init(end_calendar.get(Calendar.YEAR), end_calendar.get(Calendar.MONTH), end_calendar.get(Calendar.DAY_OF_MONTH),
                        new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                //TODO 定期定額
//                                end_c.set(year, monthOfYear, dayOfMonth);
//                                ((Button) v1).setText(end_c.get(Calendar.YEAR) + "-" + fixDate(end_c.get(Calendar.MONTH) + 1) + "-" + fixDate(end_c.get(Calendar.DAY_OF_MONTH)));
//                                endPicker.dismiss();
//                                sp_quickTime.setSelection(4);
//                                isNeverChangeTime = false;
                            }
                        });

                endPicker.show();
            }
        });

        //時間按鈕
        if(minute < 10){
            tv_time.setText(hour + ":0" + minute);
        }else {
            tv_time.setText(hour + ":" + minute);
        }

        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showTimeFragment();
            }
        });



        setAllLayout();

        return view;
    }

    private void setAllLayout(){
        setLayoutHieghtByUnit(setting_header, 2);
        setLayoutHieghtByUnit(routine_rule,4);
        setLayoutHieghtByUnit(et_category, 2);
        setLayoutHieghtByUnit(tv_time,2);
        setLayoutHieghtByUnit(et_subcategory,2);
        setLayoutHieghtByUnit(et_note,2);
        setLayoutHieghtByUnit(et_price,2);

    }

}
