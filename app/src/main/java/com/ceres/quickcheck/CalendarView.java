package com.ceres.quickcheck;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by apple on 2016/5/4.
 */
public class CalendarView extends LinearLayout {

    private LinearLayout header;
    private ImageView btnPrev;
    private ImageView btnNext;
    private ImageButton close_char;
    private TextView txtDate;
    private GridView grid;
    private Calendar currentDate;
    private EventHandler eventHandler = null;
    private static final int DAYS_COUNT = 42;
    private  HashSet<Date> events;
    private Context c_context;

    public CalendarView(Context context) {
        super(context);
        c_context = context;
        initControl(context);
    }

    public CalendarView(Context context,AttributeSet attrs) {
        super(context, attrs);
        c_context = context;
        initControl(context);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);
        c_context = context;
        initControl(context);
    }

    public void setEvents( HashSet<Date> event){
        events = event;
    }

    /**讀取XNL**/
    private void initControl(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.calander_fragment,this);

        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_prev_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
        close_char = (ImageButton)findViewById(R.id.close_char);

        currentDate = Calendar.getInstance();
        assignClickHandlers();
        updateCalendar();

    }

    private void assignClickHandlers()
    {
        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        close_char.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) c_context).handler.sendEmptyMessage(0);
            }
        });

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View cell, int position, long id) {
                if (eventHandler != null){
                    eventHandler.onDateClick((Date) view.getItemAtPosition(position));
                }
            }
        });
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar() {
        updateCalendar(events);
    }

    /**
     * Display dates correctly in grid
     */
    public void updateCalendar(HashSet<Date> events){

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells, events));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
        txtDate.setText(sdf.format(currentDate.getTime()));


    }

    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // days with events
        private HashSet<Date> eventDays;

        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days, HashSet<Date> eventDays)
        {
            super(context, R.layout.control_calendar_day, days);
            this.eventDays = eventDays;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            Calendar c = Calendar.getInstance();

            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            if (eventDays != null)
            {
                for (Date eventDate : eventDays)
                {
                    if (eventDate.getDate() == day &&
                            eventDate.getMonth() == month &&
                            eventDate.getYear() == year)
                    {
                        // mark this day for event
                        view.setBackgroundResource(R.drawable.reminder);
                        break;
                    }
                }
            }

            // clear styling
            ((TextView)view).setTypeface(null, Typeface.NORMAL);
            // if this day is outside current month, grey it out
            ((TextView)view).setTextColor(getResources().getColor(R.color.greyed_out));

            if (month == currentDate.get(Calendar.MONTH) && (year+1900) == currentDate.get(Calendar.YEAR))
            {

                ((TextView)view).setTextColor(Color.BLACK);

            }

            // set text
            ((TextView)view).setText(String.valueOf(date.getDate()));

            return view;
        }
    }


    /**
     * Assign event handler to be passed needed events
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }


    public interface EventHandler
    {
        void onDateClick(Date date);
    }

}
