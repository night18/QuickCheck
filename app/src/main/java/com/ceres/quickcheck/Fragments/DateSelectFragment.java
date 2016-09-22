package com.ceres.quickcheck.Fragments;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ceres.quickcheck.CalendarView;
import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by apple on 2016/5/2.
 */
public class DateSelectFragment extends MyFragment {
    private View view;
    private CalendarView calendarView;

    public static DateSelectFragment newInstance(HashSet<Date> events){
        DateSelectFragment fragment = new DateSelectFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        args.putSerializable("events",events);


        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        super.onCreateView(inflater,container,saveInstanceState);

        view = inflater.inflate(R.layout.datefragment, container, false);

        HashSet<Date> events = getEvents();

        calendarView = (CalendarView)view.findViewById(R.id.calendar_view);

        calendarView.updateCalendar(events);
        calendarView.setEvents(events);
        calendarView.setEventHandler(new CalendarView.EventHandler() {

            @Override
            public void onDateClick(Date date) {
                Message message = new Message();
                message.what = 11;
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                message.obj = c.get(Calendar.YEAR) + "-" + fixDate(c.get(Calendar.MONTH)+1) + "-" + fixDate(c.get(Calendar.DAY_OF_MONTH));
//
                ((MainActivity) getActivity()).handler.sendMessage(message);
            }
        });



        return view;
    }

    private HashSet<Date> getEvents(){
        return (HashSet<Date>)getArguments().getSerializable("events");
    }
}
