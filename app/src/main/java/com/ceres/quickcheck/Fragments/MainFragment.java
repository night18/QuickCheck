package com.ceres.quickcheck.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.CategoryItem;
import com.ceres.quickcheck.Units.Item;
import com.ceres.quickcheck.Units.MyFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by apple on 2016/4/11.
 */
public class MainFragment extends MyFragment {

    private View view;
    private TextView todayDate;
    private TextView bt_time;
    private ImageButton ib_next, ib_prev, ib_save, ib_statistics, ib_settings;
    private EditText et_category,et_subcategory,et_note, et_price;
    private String selected_category = "", selected_sub_category = "";
    private Handler mHandler;
    private Dialog quick_datePicker_dialog;
    private Button bt_today, bt_confirm;
    private DatePicker quick_datePicker;

    public static MainFragment newInstance(float basicHeight, long editItemID){
        MainFragment mainFragment = new MainFragment();

        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        args.putFloat("basicHeight", basicHeight);
        args.putLong("id",editItemID);

        return mainFragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){

        view = inflater.inflate(R.layout.save_fragment, container, false);

        ib_prev = (ImageButton)view.findViewById(R.id.prev);
        ib_next = (ImageButton)view.findViewById(R.id.next);
        ib_settings = (ImageButton)view.findViewById(R.id.settings);
        todayDate = (TextView) view.findViewById(R.id.today_date);
        bt_time = (TextView) view.findViewById(R.id.bt_time);
        ib_statistics = (ImageButton)view.findViewById(R.id.statistics);
        et_category = (EditText) view.findViewById(R.id.category);
        et_subcategory = (EditText) view.findViewById(R.id.sub_category);
        et_note = (EditText) view.findViewById((R.id.et_note));
        et_price = (EditText) view.findViewById(R.id.et_price);
        ib_save = (ImageButton) view.findViewById(R.id.ib_save);

        if(getEditId() == -1){
            ib_save.setBackgroundResource(R.drawable.add);
        }else{
            ib_save.setBackgroundResource(R.drawable.pencil);
        }

        setAllLayout();

        ib_prev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.calendar.roll(Calendar.DAY_OF_YEAR, -1);
                setDate();
            }
        });

        ib_next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MainActivity.calendar.roll(Calendar.DAY_OF_YEAR, 1);
                setDate();
            }
        });

        ib_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).handler.sendEmptyMessage(6);
            }
        });

        ib_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainActivity) getActivity()).click_right_top_button();
                    }
                }, 0);
            }
        });


        et_category.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((MainActivity) getActivity()).chooseCategory();
                } else {
                    ((MainActivity) getActivity()).closeAutoText();
                }
                ((MainActivity) getActivity()).isCategoryFocus = hasFocus;
            }
        });


        // 子類別
        et_subcategory.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !et_category.getText().toString().trim().equals("")) {
                    ((MainActivity) getActivity()).chooseSubCategory(et_category.getText().toString());
                } else {
                    ((MainActivity) getActivity()).closeAutoText();
                }
                ((MainActivity) getActivity()).isSubCategoryFocus = hasFocus;
            }
        });

        if(getEditId() != -1){
            Item editItem = MainActivity.dbhelper.queryRecord(MainActivity.dbhelper.KEY_ID, getEditId()+"" ).get(0);
            et_category.setText(editItem.category);
            et_subcategory.setText(editItem.sub_category);
            et_price.setText(editItem.money + "");
            et_note.setText(editItem.note);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = sdf.parse(editItem.datetime);
                MainActivity.calendar.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        setDate();

        //快速選取日期
        quick_datePicker_dialog = new Dialog(getActivity());
        quick_datePicker_dialog.setContentView(R.layout.quick_date_picker);
        bt_today = (Button) quick_datePicker_dialog.findViewById(R.id.bt_today);
        quick_datePicker = (DatePicker) quick_datePicker_dialog.findViewById(R.id.quick_datePicker);
        bt_confirm = (Button) quick_datePicker_dialog.findViewById(R.id.confirm);

        todayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q_day = MainActivity.calendar.get(Calendar.DAY_OF_MONTH);
                int q_month = MainActivity.calendar.get(Calendar.MONTH);
                int q_year = MainActivity.calendar.get(Calendar.YEAR);

                quick_datePicker.init(q_year,q_month,q_day,null);
                quick_datePicker_dialog.show();
            }
        });

        bt_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar tmp_c = Calendar.getInstance();

                int q_day = tmp_c.get(Calendar.DAY_OF_MONTH);
                int q_month = tmp_c.get(Calendar.MONTH);
                int q_year = tmp_c.get(Calendar.YEAR);

                quick_datePicker.updateDate(q_year,q_month,q_day);
            }
        });

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int q_day = quick_datePicker.getDayOfMonth();
                int q_month = quick_datePicker.getMonth();
                int q_year = quick_datePicker.getYear();

                MainActivity.calendar.set(q_year,q_month,q_day);
                setDate();

                quick_datePicker_dialog.dismiss();
            }
        });

        //時間按鈕
        int hour = MainActivity.calendar.get(Calendar.HOUR_OF_DAY);
        int minute = MainActivity.calendar.get(Calendar.MINUTE);
        if(minute < 10){
            bt_time.setText(hour + ":0" + minute);
        }else {
            bt_time.setText(hour + ":" + minute);
        }
        bt_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).showTimeFragment();
            }
        });

        //加號按鈕，儲存
        ib_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selected_category = et_category.getText().toString();
                selected_sub_category = et_subcategory.getText().toString();

                if (!selected_category.equals("")) {
                    Item record = new Item();
                    //避免金額空值
                    if (!et_price.getText().toString().trim().equals("") && Float.parseFloat(et_price.getText().toString().trim()) != 0.0) {
                        int isIncome = 0;

                        CategoryItem tmp_categoryItem = MainActivity.dbhelper.queryCategory(MainActivity.dbhelper.CATEGORY_COLUMN, selected_category);
                        if(null != tmp_categoryItem) {
                            isIncome = tmp_categoryItem.isIncome;
                        }

                        record.money = Float.parseFloat(et_price.getText().toString());

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        String timeFormat = bt_time.getText().toString().length() < 5?
                                "0"+bt_time.getText().toString() : bt_time.getText().toString();


                        record.datetime = dateFormat.format(MainActivity.calendar.getTime()) + " " + timeFormat + ":00";
                        record.category = selected_category;
                        record.sub_category = selected_sub_category;
                        record.note = et_note.getText().toString().trim();
                        record.user = "default"; //TODO 研究這邊的規則
                        record.place = ""; //TODO next version
                        record.isIncome = isIncome;

                        if(getEditId() == -1) {
                            MainActivity.dbhelper.insertRecord(record);
                        }else{
                            record.id = getEditId();
                            MainActivity.dbhelper.updateRecord(record);
                        }

                        if(null == tmp_categoryItem ){//新增類別
                            CategoryItem new_category = new CategoryItem();
                            new_category.parentItem = selected_category;
                            new_category.childItem = new ArrayList<String>();
                            new_category.childItem.add(selected_sub_category);
                            new_category.isIncome = 0;

                            MainActivity.dbhelper.insertCategory(new_category);
                        }else{
                            Boolean isInRecord = false;
                            for(int i = 0; i < tmp_categoryItem.childItem.size(); i++){
                                if(selected_sub_category.equals(tmp_categoryItem.childItem.get(i))){
                                    isInRecord = true;
                                    break;
                                }
                            }
                            if(!isInRecord){ //新增子類別
                                tmp_categoryItem.childItem.add(selected_sub_category);

                                MainActivity.dbhelper.updateCategory(tmp_categoryItem);
                            }
                        }



                        ((MainActivity)getActivity()).handler.sendEmptyMessage(70);
                    }else {
                        Toast.makeText(getContext(),getActivity().getString(R.string.money_warning), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getContext(),getActivity().getString(R.string.category_warning), Toast.LENGTH_SHORT).show();
                }

                et_category.setText("");
                et_subcategory.setText("");
                et_note.setText("");
                et_price.setText("");

            }
        });


        return view;
    }

    public void confirm_category(String name){

        et_category.setText(name);
        et_subcategory.requestFocus();
    }

    public void confirm_subcategory(String name){
        et_subcategory.setText(name);
        et_price.requestFocus();
    }

    /**設定時間**/
    private void setDate(){
        int tyear = MainActivity.calendar.get(Calendar.YEAR);
        int tmonth = MainActivity.calendar.get(Calendar.MONTH)+1;
        int tday = MainActivity.calendar.get(Calendar.DAY_OF_MONTH);
        int tweekday = MainActivity.calendar.get(Calendar.DAY_OF_WEEK);

        String weekday = "一";

        switch (tweekday){
            case 1:
                weekday = "日";
                break;
            case 2:
                weekday = "一";
                break;
            case 3:
                weekday = "二";
                break;
            case 4:
                weekday = "三";
                break;
            case 5:
                weekday = "四";
                break;
            case 6:
                weekday = "五";
                break;
            case 7:
                weekday = "六";
                break;
        }

        todayDate.setText(tmonth + "月" + tday + "日 星期" + weekday);
    }

    public void setTimeText(String time){
        bt_time.setText(time);
    }

    private long getEditId(){
        return  getArguments().getLong("id", -1);
    }

    /**變動所有Layout符合螢幕大小
     **/
    private void setAllLayout(){
        setLayoutHieghtByUnit(et_category, 2);
        setLayoutHieghtByUnit(bt_time,2);
        setLayoutHieghtByUnit(et_subcategory,2);
        setLayoutHieghtByUnit(et_note,2);
        setLayoutHieghtByUnit(et_price,2);
    }
}
