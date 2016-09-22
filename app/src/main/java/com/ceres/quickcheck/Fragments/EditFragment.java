package com.ceres.quickcheck.Fragments;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.CategoryItem;
import com.ceres.quickcheck.Units.Item;
import com.ceres.quickcheck.Units.MyFragment;
import com.ceres.quickcheck.databaseControls.DBController;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by apple on 2016/5/18.
 */
public class EditFragment extends MyFragment {
    private View view;
    private Button bt_confirm, bt_cancel;
    private float basicHeight,screen_width;
    private long item_id;
    private Item item;

    public static EditFragment newInstance(float basicHeight,float screen_width, long id){
        EditFragment fragment = new EditFragment();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putFloat("basicHeight", basicHeight);
        args.putFloat("screen_width", screen_width);
        args.putLong("id", id);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.edit_fragment, container, false);
        bt_confirm = (Button)view.findViewById(R.id.delete);
        bt_cancel = (Button)view.findViewById(R.id.cancel);

        basicHeight = getArguments().getFloat("basicHeight");
        screen_width = getArguments().getFloat("screen_width");
        item_id = getArguments().getLong("id");

        item =  MainActivity.dbhelper.queryRecord(MainActivity.dbhelper.KEY_ID, item_id + "").get(0);

        showDate(spiltStringArray(item.datetime, " ")[0]);
        showRecord();

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
                exitEditMode();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitEditMode();
            }
        });

        return view;
    }

    private void showDate(String date){
        LinearLayout horizon_ll_up = (LinearLayout)view.findViewById(R.id.date_row);

        LinearLayout.LayoutParams lp;

        TextView tv_date = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.3),(int)(basicHeight * 1 + 0.5f));
        tv_date.setLayoutParams(lp);

        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today_date = dateFormat.format(today.getTime());

        if(date.equals(today_date)) {
            tv_date.setText("今日");
            tv_date.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            tv_date.setTypeface(null, Typeface.BOLD);
            tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            tv_date.setGravity(Gravity.CENTER);
            Drawable today_icon = getActivity().getResources().getDrawable(R.drawable.today);
            today_icon.setBounds(0, 0, (int) (basicHeight * 0.9 + 0.5f), (int) (basicHeight * 0.9 + 0.5f));
            tv_date.setCompoundDrawables(today_icon, null, null, null);
            tv_date.setPadding(140, 0, 0, 0);
        }else{
            tv_date.setText(date);
            tv_date.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            tv_date.setLayoutParams(lp);
            tv_date.setGravity(Gravity.CENTER);
            tv_date.setTypeface(null, Typeface.BOLD);
            tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        }
        TextView tv_date_money = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.7),(int)(basicHeight * 1 + 0.5f));

        float today_money = MainActivity.dbhelper.showTodayMoney(date, 0);
        if( today_money > 0){
            tv_date_money.setText("NT$" + today_money);
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_income));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_income)));
        } else{
            tv_date_money.setText("NT$" + (-today_money) );
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_cost)));
        }


        tv_date_money.setLayoutParams(lp);
        tv_date_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv_date_money.setGravity(Gravity.END);

        horizon_ll_up.addView(tv_date);
        horizon_ll_up.addView(tv_date_money);
    }

    private void showRecord(){

        LinearLayout.LayoutParams lp;

        LinearLayout ll_outside = (LinearLayout)view.findViewById(R.id.ll_record);

        LinearLayout vertical_ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams ver_lp = new LinearLayout.LayoutParams((int)(screen_width * 0.8 ), ViewGroup.LayoutParams.WRAP_CONTENT);
        vertical_ll.setLayoutParams(ver_lp);
        vertical_ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout horizon_ll_up = new LinearLayout(getActivity());
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        horizon_ll_up.setLayoutParams(lp);
        horizon_ll_up.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout horizon_ll_down = new LinearLayout(getActivity());
        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        horizon_ll_down.setLayoutParams(lp);
        horizon_ll_down.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv_category = new TextView(getActivity());
        TextView tv_subcategory = new TextView(getActivity());
        TextView tv_price = new TextView(getActivity());

        lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 15, 0, 15);
        lp.gravity = Gravity.CENTER_VERTICAL;
        tv_category.setLayoutParams(lp);
        tv_category.setText(item.category);
        tv_category.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv_category.setTypeface(null, Typeface.BOLD);
        tv_category.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));


        tv_subcategory.setLayoutParams(lp);
        tv_subcategory.setText("↳" + item.sub_category);
        tv_subcategory.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv_subcategory.setTextColor(getActivity().getResources().getColor(R.color.colorBlack));
        tv_price.setText(item.money + "");
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.4), ViewGroup.LayoutParams.WRAP_CONTENT);
        tv_subcategory.setLayoutParams(lp);
        tv_price.setLayoutParams(lp);
        tv_price.setGravity(Gravity.END);
        if(item.isIncome == 1){
            tv_price.setTextColor(getActivity().getResources().getColor(R.color.color_income));
        }else{
            tv_price.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
        }
        tv_price.setTypeface(null, Typeface.BOLD);

        CategoryItem result_category = MainActivity.dbhelper.queryCategory(DBController.CATEGORY_COLUMN,item.category);
        final ImageView category_icon = new ImageView(getActivity());
        category_icon.setId(R.id.category_icon);
        if(result_category.iconStyle >= 0  && result_category.iconColor != null && !result_category.iconColor.equals("")){
            category_icon.setImageDrawable(((MainActivity)getActivity()).getCategoryPic(result_category.iconStyle, result_category.iconColor));
        }
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.2), (int)(basicHeight * 1.5 + 0.5f));
        lp.gravity = Gravity.CENTER;
        category_icon.setLayoutParams(lp);

        horizon_ll_up.addView(tv_category);
        horizon_ll_down.addView(tv_subcategory);
        horizon_ll_down.addView(tv_price);
        vertical_ll.addView(horizon_ll_up);
        vertical_ll.addView(horizon_ll_down);

        ll_outside.addView(category_icon);
        ll_outside.addView(vertical_ll);

    }

    private void deleteItem(){
        MainActivity.dbhelper.deleteRecord(item.id);
    }

    private void exitEditMode(){
        ((MainActivity)getActivity()).handler.sendEmptyMessage(0);
        ((MainActivity)getActivity()).handler.sendEmptyMessage(7);
    }


}
