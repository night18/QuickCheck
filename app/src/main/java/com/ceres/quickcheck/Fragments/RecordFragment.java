package com.ceres.quickcheck.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.CategoryItem;
import com.ceres.quickcheck.Units.Item;
import com.ceres.quickcheck.Units.MyFragment;
import com.ceres.quickcheck.databaseControls.DBController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 主畫面下方顯示所有過往紀錄
 * Created by apple on 2016/5/17.
 */
//TODO animation cahnge to animator
public class RecordFragment extends MyFragment {
    private View view;
    private ScrollView sv_table;
    private TableLayout tl_record;
    private String tmp_category = "";
    private Boolean[] isEditMode;
    private static HashSet<Date> events;
    private float screen_width,basicHeight;

    public static RecordFragment newInstance(float basicHeight, float screen_width){
        RecordFragment mainFragment = new RecordFragment();

        Bundle args = new Bundle();
        mainFragment.setArguments(args);
        args.putFloat("basicHeight", basicHeight);
        args.putFloat("screen_width", screen_width);

        return mainFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.record_fragment, container, false);

        screen_width = getArguments().getFloat("screen_width");
        basicHeight = getArguments().getFloat("basicHeight");

        sv_table = (ScrollView) view.findViewById(R.id.sv_table);
        tl_record = (TableLayout) view.findViewById(R.id.tl_show_record);

        showAllRecord();
        sv_table.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                for (int i = 0; i < tl_record.getChildCount(); i++) {
                    View row = tl_record.getChildAt(i);
                    if (row instanceof TableRow) {
                        TableRow rows = (TableRow) row;
                        View childView = rows.findViewById(R.id.btn_edit_Id);
                        View childView2 = rows.findViewById(R.id.category_icon);
                        if (childView instanceof ImageButton && childView2 instanceof ImageView) {
                            ImageButton imageButton = (ImageButton) childView;
                            ImageView imageView = (ImageView) childView2;

                            Boolean isHideIcon = (Boolean) rows.getTag(R.id.isHideIcon);

                            if (imageButton.getVisibility() != View.GONE) {
                                if (isHideIcon) {
                                    imageButton.startAnimation(editFadeOutAnimation(imageButton, imageView));
                                } else {
                                    imageButton.startAnimation(editFadeOutAnimation(imageButton, imageView));
                                    imageView.startAnimation(iconFadeInAnimation(imageButton, imageView));
                                }
                            }
                        }
                    }
                }

                for (int j = 0; j < isEditMode.length; j++) {
                    isEditMode[j] = false;
                }
            }
        });

        return view;
    }

    /**顯示所有紀錄**/
    public void showAllRecord(){
        tl_record.removeAllViews();
        List<Item> showItems = MainActivity.dbhelper.getAllRecord();
        String save_date = "";
        events = new HashSet<>();

        isEditMode = new Boolean[showItems.size()];

        for(int i = 0; i < showItems.size(); i++){
            String date = spiltStringArray(showItems.get(i).datetime, " ")[0];
            if(!date.equals(save_date)){
                tmp_category = "";
                Calendar today = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String today_date = dateFormat.format(today.getTime());
                save_date = date;

                if(date.equals(today_date)){
                    showTodayMark(today_date);
                }else{
                    showRecordTime(save_date);
                }

                try {
                    //給calanderView 記錄用
                    Date eventDate = dateFormat.parse(save_date);
                    events.add(eventDate);

                }catch (ParseException e){
                    e.printStackTrace();
                }
            }

            isEditMode[i] = false;
            showRecord(showItems.get(i), i);
        }

        if(MainActivity.isLineChart){
            ((MainActivity)getActivity()).handler.sendEmptyMessage(2);
        }

    }

    /**顯示過往紀錄**/
    private void showRecord(final Item item, final int index){
        final  boolean isHideIcon;
        TableRow tableRow = new TableRow(getActivity());
        LinearLayout.LayoutParams lp;

        LinearLayout vertical_ll = new LinearLayout(getActivity());
        LinearLayout.LayoutParams ver_lp = new LinearLayout.LayoutParams((int)(screen_width * 0.8 ), ViewGroup.LayoutParams.WRAP_CONTENT);
        vertical_ll.setLayoutParams(ver_lp);
        vertical_ll.setOrientation(LinearLayout.VERTICAL);

        LinearLayout horizon_ll_up = new LinearLayout(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.8 ) , ViewGroup.LayoutParams.WRAP_CONTENT);
        horizon_ll_up.setLayoutParams(lp);
        horizon_ll_up.setOrientation(LinearLayout.HORIZONTAL);

        RelativeLayout horizon_ll_down = new RelativeLayout(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.8 ), ViewGroup.LayoutParams.WRAP_CONTENT);
        horizon_ll_down.setLayoutParams(lp);

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
        tv_subcategory.setId(R.id.textView_subCategory);
        tv_price.setText(item.money + "");
        tv_price.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

        TextView tv_note = new TextView(getActivity());
        if(!item.note.equals("")){
            tv_note.setText(","+item.note);
        }else{
            tv_note.setText("");
        }

        tv_note.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        tv_note.setTextColor(getActivity().getResources().getColor(R.color.colorAutoTextBG));

        RelativeLayout.LayoutParams rl_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl_lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        tv_subcategory.setLayoutParams(rl_lp);

        rl_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl_lp.addRule(RelativeLayout.RIGHT_OF, R.id.textView_subCategory);
        tv_note.setLayoutParams(rl_lp);

        rl_lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        tv_price.setLayoutParams(rl_lp);
        if(item.isIncome == 1){
            tv_price.setTextColor(getActivity().getResources().getColor(R.color.color_income));
        }else{
            tv_price.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
        }
        tv_price.setTypeface(null, Typeface.BOLD);

        HorizontalScrollView sv = new HorizontalScrollView(getActivity());
        final LinearLayout ll_outside = new LinearLayout(getActivity());
        TableRow.LayoutParams out_lp = new TableRow.LayoutParams((int)(screen_width) , ViewGroup.LayoutParams.WRAP_CONTENT);
        sv.setLayoutParams(out_lp);
        sv.setHorizontalScrollBarEnabled(false);

        final ImageButton bt_delete = new ImageButton(getActivity());
        bt_delete.setImageResource(R.drawable.trash);
        bt_delete.setBackgroundColor(getActivity().getResources().getColor(R.color.color_del));
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.2), ViewGroup.LayoutParams.MATCH_PARENT);
        bt_delete.setLayoutParams(lp);

        //刪除按鈕的實作
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.dbhelper.deleteRecord(item.id);
                showAllRecord();
            }
        });

        final ImageButton bt_edit = new ImageButton(getActivity());
        bt_edit.setId(R.id.btn_edit_Id);
        bt_edit.setImageResource(R.drawable.pencil);
        bt_edit.setBackgroundColor(Color.TRANSPARENT);
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.2), (int)(basicHeight * 1.5 + 0.5f));
        lp.gravity = Gravity.CENTER;
        bt_edit.setLayoutParams(lp);
        bt_edit.setVisibility(View.GONE);
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = new Message();
                msg.what = 8;
                msg.obj = item.id;
                ((MainActivity) getActivity()).handler.sendMessage(msg);
            }
        });


        CategoryItem result_category = MainActivity.dbhelper.queryCategory(DBController.CATEGORY_COLUMN,item.category);
        final ImageView category_icon = new ImageView(getActivity());
        category_icon.setId(R.id.category_icon);
        if(result_category.iconStyle >= 0  && result_category.iconColor != null && !result_category.iconColor.equals("")){
            category_icon.setImageDrawable(((MainActivity)getActivity()).getCategoryPic(result_category.iconStyle, result_category.iconColor));
        }
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.2), (int)(basicHeight * 1.5 + 0.5f));
        lp.gravity = Gravity.CENTER;
        category_icon.setLayoutParams(lp);



        final Animation bt_edit_fadeIn = editFadeInAnimation(bt_edit,category_icon);

        final Animation bt_edit_fadeOut = editFadeOutAnimation(bt_edit,category_icon);

        final Animation icon_fadeIn = new AlphaAnimation(0,1);
        icon_fadeIn.setInterpolator(new AccelerateInterpolator()); //add this
        icon_fadeIn.setDuration(500);
        icon_fadeIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                category_icon.setVisibility(View.VISIBLE);
            }
        });

        final Animation icon_fadeOut = new AlphaAnimation(1,0);
        icon_fadeOut.setInterpolator(new AccelerateInterpolator()); //add this
        icon_fadeOut.setDuration(500);
        icon_fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                category_icon.setVisibility(View.GONE);
            }
        });

        horizon_ll_up.addView(tv_category);
        horizon_ll_down.addView(tv_subcategory);
        horizon_ll_down.addView(tv_note);
        horizon_ll_down.addView(tv_price);
        vertical_ll.addView(horizon_ll_up);
        vertical_ll.addView(horizon_ll_down);
//        tableRow.addView(vertical_ll);

        ll_outside.addView(category_icon);
        ll_outside.addView(bt_edit);
        ll_outside.addView(vertical_ll);
        ll_outside.addView(bt_delete);
        sv.addView(ll_outside, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        if(tmp_category.equals(item.category)){
            isHideIcon = true;
            horizon_ll_up.setVisibility(View.GONE);
            category_icon.setVisibility(View.INVISIBLE);
        }else{
            isHideIcon = false;

            horizon_ll_up.setVisibility(View.VISIBLE);
            category_icon.setVisibility(View.VISIBLE);
        }
        tableRow.setTag(R.id.isHideIcon, isHideIcon);
        tableRow.setTag(R.id.row_id, index);
        tmp_category = item.category;

        sv.setOnTouchListener(new View.OnTouchListener() {
            float init_x = 0;
            boolean isShowDel = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        init_x = event.getX();

                        for (int i = 0; i < tl_record.getChildCount(); i++) {
                            View row = tl_record.getChildAt(i);
                            if (row instanceof TableRow) {
                                TableRow rows = (TableRow) row;
                                View childView = rows.findViewById(R.id.btn_edit_Id);
                                View childView2 = rows.findViewById(R.id.category_icon);
                                if(rows.getTag(R.id.row_id) != null){
                                    int child_index = (int)rows.getTag(R.id.row_id);
                                    if(child_index != index && isEditMode[child_index].equals(true)){
                                        if (childView instanceof ImageButton && childView2 instanceof ImageView) {
                                            ImageButton imageButton = (ImageButton) childView;
                                            ImageView imageView = (ImageView) childView2;

                                            Boolean isHideIcon = (Boolean) rows.getTag(R.id.isHideIcon);

                                            if (imageButton.getVisibility() != View.GONE) {
                                                if (isHideIcon) {
                                                    imageButton.startAnimation(editFadeOutAnimation(imageButton, imageView));
                                                } else {
                                                    imageButton.startAnimation(editFadeOutAnimation(imageButton, imageView));
                                                    imageView.startAnimation(iconFadeInAnimation(imageButton, imageView));
                                                }
                                            }
                                        isEditMode[child_index] = false;
                                        }
                                    }
                                }
                            }
                        }

                        if(isHideIcon){
                            if (!isEditMode[index]) {
                                bt_edit.setVisibility(View.INVISIBLE);
                                category_icon.setVisibility(View.GONE);
                                bt_edit.startAnimation(bt_edit_fadeIn);

                            } else {
                                bt_edit.startAnimation(bt_edit_fadeOut);
                            }
                        }else{
                            if (!isEditMode[index]) {
                                bt_edit.startAnimation(bt_edit_fadeIn);
                                category_icon.startAnimation(icon_fadeOut);
                            } else {
                                bt_edit.startAnimation(bt_edit_fadeOut);
                                category_icon.startAnimation(icon_fadeIn);
                            }
                        }

                        isEditMode[index] = !isEditMode[index];

                        return true;
                    case MotionEvent.ACTION_UP:
                        float move_x = event.getX();
                        if (!isShowDel && init_x - move_x > screen_width / 10) { //移動超出一半刪除按鈕範圍
                            v.scrollTo((int) (screen_width * 0.2), 0);
                            isShowDel = true;
                            return true;
                        } else if (isShowDel && init_x - move_x < -screen_width / 10) {
                            v.scrollTo(0, 0);
                            isShowDel = false;
                            return true;
                        } else if (!isShowDel && init_x - move_x < screen_width && init_x - move_x > 0) {//未動超出ㄧ半刪除按鈕範圍
                            v.scrollTo(0, 0);
                            isShowDel = false;
                            return true;
                        } else if (isShowDel && init_x - move_x > -screen_width / 10 && init_x - move_x < 0) { //回收刪除按鈕
                            v.scrollTo((int) (screen_width * 0.2), 0);
                            isShowDel = true;
                        } else {
                            return false;
                        }

                    default:
                        break;
                }
                return false;
            }
        });


        tableRow.addView(sv);

        tl_record.addView(tableRow , new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void showTodayMark(String date){
        TableRow tableRow = new TableRow(getActivity());
        LinearLayout.LayoutParams lp;

        LinearLayout horizon_ll_up = new LinearLayout(getActivity());
        TableRow.LayoutParams tl_lp = new TableRow.LayoutParams((int)screen_width,(int)(basicHeight * 1 + 0.5f));
        horizon_ll_up.setLayoutParams(tl_lp);
        horizon_ll_up.setOrientation(LinearLayout.HORIZONTAL);

        TextView tv_date = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.3),(int)(basicHeight * 1 + 0.5f));
        tv_date.setLayoutParams(lp);
        tv_date.setText("今日");
        tv_date.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        tv_date.setTypeface(null, Typeface.BOLD);
        tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv_date.setGravity(Gravity.CENTER);
        Drawable today_icon = getActivity().getResources().getDrawable(R.drawable.today);
        today_icon.setBounds(0, 0, (int) (basicHeight * 0.9 + 0.5f), (int) (basicHeight * 0.9 + 0.5f));
        tv_date.setCompoundDrawables(today_icon, null, null, null);
        tv_date.setPadding(140, 0, 0, 0);

        TextView tv_date_money = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.7),(int)(basicHeight * 1 + 0.5f));

        float today_money = MainActivity.dbhelper.showTodayMoney(date, 0);
        if( today_money > 0){
            tv_date_money.setText( getActivity().getString(R.string.money_unit) + today_money);
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_income));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_income)));
        } else{
            tv_date_money.setText(getActivity().getString(R.string.money_unit) + (-today_money) );
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_cost)));
        }


        tv_date_money.setLayoutParams(lp);
        tv_date_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv_date_money.setGravity(Gravity.END);

        horizon_ll_up.addView(tv_date);
        horizon_ll_up.addView(tv_date_money);
        tableRow.addView(horizon_ll_up);

        tableRow.setTag(date);

        tl_record.addView(tableRow , new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**顯示日期**/
    private void showRecordTime(String date){
        TableRow tableRow = new TableRow(getActivity());
        LinearLayout.LayoutParams lp;

        LinearLayout horizon_ll_up = new LinearLayout(getActivity());
        TableRow.LayoutParams tl_lp = new TableRow.LayoutParams((int)screen_width,(int)(basicHeight * 1 + 0.5f));
        horizon_ll_up.setLayoutParams(tl_lp);
        horizon_ll_up.setOrientation(LinearLayout.HORIZONTAL);

        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.4), (int)(basicHeight * 1 + 0.5f));
        TextView tv_date = new TextView(getActivity());
        tv_date.setText(date);
        tv_date.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
        tv_date.setLayoutParams(lp);
        tv_date.setGravity(Gravity.CENTER);
        tv_date.setTypeface(null, Typeface.BOLD);
        tv_date.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);

        TextView tv_date_money = new TextView(getActivity());
        lp = new LinearLayout.LayoutParams((int)(screen_width * 0.6), (int)(basicHeight * 1 + 0.5f));

        float today_money = MainActivity.dbhelper.showTodayMoney(date, 0);
        if( today_money > 0){
            tv_date_money.setText(getActivity().getString(R.string.money_unit) + today_money);
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_income));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_income)));
        } else{
            tv_date_money.setText(getActivity().getString(R.string.money_unit) + (-today_money) );
            tv_date_money.setTextColor(getActivity().getResources().getColor(R.color.color_cost));
            horizon_ll_up.setBackgroundColor(getActivity().getResources().getColor((R.color.light_color_cost)));
        }

        tv_date_money.setLayoutParams(lp);
        tv_date_money.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        tv_date_money.setGravity(Gravity.END);


        horizon_ll_up.addView(tv_date);
        horizon_ll_up.addView(tv_date_money);

        tableRow.addView(horizon_ll_up);
        tableRow.setTag(date);


        tl_record.addView(tableRow , new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public Animation editFadeInAnimation(final ImageButton bt_edit,final ImageView category_icon){
        Animation animation;

        animation = new AlphaAnimation(1,0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                bt_edit.setVisibility(View.VISIBLE);
                category_icon.setVisibility(View.GONE);
            }
        });

        return animation;
    }

    public Animation editFadeOutAnimation(final ImageButton bt_edit, final ImageView category_icon){
        Animation animation;

        animation = new AlphaAnimation(1,0);

        animation.setInterpolator(new AccelerateInterpolator()); //add this
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                bt_edit.setVisibility(View.GONE);
                category_icon.setVisibility(View.INVISIBLE);
            }
        });
        return animation;
    }

    public Animation iconFadeInAnimation(final ImageButton bt_edit, final ImageView category_icon){

        Animation animation;

        animation = new AlphaAnimation(0,1);

        animation.setInterpolator(new AccelerateInterpolator()); //add this
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation arg0) {
            }

            public void onAnimationRepeat(Animation arg0) {
            }

            public void onAnimationEnd(Animation arg0) {
                category_icon.setVisibility(View.VISIBLE);
            }
        });
        return animation;
    }

    public static HashSet<Date> getEvent(){
        return events;
    }

    public void scrollToResult(String Date){
        for(int i = 0 ; i < tl_record.getChildCount(); i++){
            View v = tl_record.getChildAt(i);
            if(v instanceof TableRow){
                TableRow row = (TableRow) v;
                if(row.getTag()!= null && row.getTag().equals(Date)){
                    sv_table.smoothScrollTo(0,v.getTop());
                }
            }
        }
    }

}
