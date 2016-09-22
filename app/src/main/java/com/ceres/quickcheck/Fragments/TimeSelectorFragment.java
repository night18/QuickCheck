package com.ceres.quickcheck.Fragments;

import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

/**
 * 時間選擇器
 * Created by apple on 2016/3/14.
 */
public class TimeSelectorFragment extends DialogFragment{
    Calendar calendar;
    TimePicker timePicker;


    public static TimeSelectorFragment newInstance(Calendar calendar){
        TimeSelectorFragment timeSelectorFragment = new TimeSelectorFragment();


        Bundle args = new Bundle();
        try {
            args.putByteArray("calendar", object2Bytes(calendar));
        } catch(IOException e){
            e.printStackTrace();
        }
        timeSelectorFragment.setArguments(args);

        return timeSelectorFragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        try {
            calendar = (Calendar) bytes2Object(getArguments().getByteArray("calendar"));
        }catch(IOException e){
            e.printStackTrace();
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }


    //處理畫面
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        View v = inflater.inflate(R.layout.time_selector, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        timePicker = (TimePicker) v.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        //設置時間
        showTime();

        Button bt_currtime = (Button) v.findViewById(R.id.ts_curr_time);
        bt_currtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currTime();
            }
        });

        Button bt_changetozero = (Button)v.findViewById(R.id.changeto0);
        bt_changetozero.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeMin(0);
            }
        });

        Button bt_changetofifteen = (Button)v.findViewById(R.id.changeto15);
        bt_changetofifteen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeMin(15);
            }
        });

        Button bt_changetothirty = (Button)v.findViewById(R.id.changeto30);
        bt_changetothirty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeMin(30);
            }
        });

        Button bt_changetofourtyfive = (Button)v.findViewById(R.id.changeto45);
        bt_changetofourtyfive.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeMin(45);
            }
        });

        //取消按鈕
        Button bt_tp_cancel = (Button)v.findViewById(R.id.timePicker_cancel);
        bt_tp_cancel.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        //確認按鈕
        Button bt_tp_confirm = (Button) v.findViewById(R.id.timePicker_confirm);
        bt_tp_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity callMain = (MainActivity) getActivity();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    callMain.resetTimeText(timePicker.getHour(),timePicker.getMinute());
                }else{
                    callMain.resetTimeText(timePicker.getCurrentHour(),timePicker.getCurrentMinute());
                }
                getDialog().dismiss();
            }
        });

        return v;
    }

    private static byte[] object2Bytes(Object o) throws IOException{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );

        oos.writeObject(o);
        return baos.toByteArray();
    }

    private static Object bytes2Object( byte raw[]) throws IOException, ClassNotFoundException{
        ByteArrayInputStream bais = new ByteArrayInputStream(raw);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();

        return o;

    }

    private void showTime(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(calendar.get(Calendar.MINUTE));
        }else{
            //TODO 暫時用暴力法解掉
            timePicker.setCurrentHour(0);
            timePicker.setCurrentHour(Integer.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
            timePicker.getCurrentHour();

            timePicker.setCurrentMinute(0);
            timePicker.setCurrentMinute(Integer.valueOf(calendar.get(Calendar.MINUTE)));
            timePicker.getCurrentMinute();

        }

    }

    private void currTime(){
        calendar = Calendar.getInstance();
        showTime();

    }

    private void changeMin(int min){
        int hour = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M? timePicker.getHour(): timePicker.getCurrentHour();
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, min);
        showTime();
    }


}
