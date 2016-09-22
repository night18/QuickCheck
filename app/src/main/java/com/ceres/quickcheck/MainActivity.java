package com.ceres.quickcheck;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ceres.quickcheck.Fragments.DateSelectFragment;
import com.ceres.quickcheck.Fragments.EditFragment;
import com.ceres.quickcheck.Fragments.LineChartFragment;
import com.ceres.quickcheck.Fragments.MainFragment;
import com.ceres.quickcheck.Fragments.PieChartFragment;
import com.ceres.quickcheck.Fragments.RecordFragment;
import com.ceres.quickcheck.Fragments.SumPerMonthFragment;
import com.ceres.quickcheck.Fragments.TimeSelectorFragment;
import com.ceres.quickcheck.Fragments.settings.ImAndEx;
import com.ceres.quickcheck.Fragments.settings.SettingsFragment;
import com.ceres.quickcheck.Units.CategoryItem;
import com.ceres.quickcheck.databaseControls.DBController;
import com.ceres.quickcheck.Fragments.settings.RoutineSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends FragmentActivity{

    private TableLayout tl_record;
    private LinearLayout ll_autotext;
    public static DBController dbhelper = null;
    private final int basicUnit = 28;
    private float basicHeight, screen_width;
    private Boolean isFirstIn = false;
    public static Calendar calendar;
    public static boolean isLineChart = false;
    private int tmp_height = 0;
    public static boolean isCategoryFocus = false;
    public static boolean isSubCategoryFocus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        openDatabase();
        checkFirstIn();
        if(isFirstIn){
            addCategory2db();
        }
        calendar = Calendar.getInstance();
        initialLayout();
        setAllLayout();
        change2saveFragment();
        change2recordFragment();

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();

        //監控鍵盤是否跳起
        final View activityRootView = findViewById(R.id.activity_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightdiff = tmp_height - activityRootView.getHeight();
                tmp_height = activityRootView.getHeight();

                if (heightdiff < -basicHeight * basicUnit * 2 / 5) {
                    ll_autotext.setVisibility(View.GONE);
                } else if (heightdiff > basicHeight * basicUnit * 2 / 5) {
                    if (isCategoryFocus || isSubCategoryFocus)
                        ll_autotext.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        closeDatabase();
    }

    private void openDatabase(){
        dbhelper = new DBController(this);
    }

    private void closeDatabase(){
        dbhelper.close();
    }


    private List<String> findValueBySelection(String selection){
        CategoryItem parent_category = dbhelper.queryCategory(dbhelper.CATEGORY_COLUMN, selection);

        if(null != parent_category) {
            return parent_category.childItem;
        }else{
            return new ArrayList<String>();
        }
    }

    /**取得基礎行高以及螢幕寬高**/
    private void getScreeenSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        basicHeight = dm.heightPixels/basicUnit;
        screen_width = dm.widthPixels;
    }

    private void setLayoutHieghtByUnit(View view, int unit){


        LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int)(basicHeight * unit + 0.5f);
        layoutParams.width = LayoutParams.MATCH_PARENT;
        view.requestLayout();
    }


    /**初始化畫面元件**/
    private void initialLayout(){
        tl_record = (TableLayout) findViewById(R.id.tl_show_record);
        ll_autotext = (LinearLayout) findViewById(R.id.smart_auto_text);
    }

    /**變動所有Layout符合螢幕大小
     **/
    private void setAllLayout(){
        getScreeenSize();
        setLayoutHieghtByUnit(ll_autotext, 2);
    }



    /**類別選擇**/
    public void chooseCategory(){
        ll_autotext.setVisibility(View.VISIBLE);
        ll_autotext.removeAllViews();
        List<CategoryItem> categoryItems = dbhelper.getAllCategory();

        for(int i = 0; i < categoryItems.size(); i++){
            TextView textView = new TextView(MainActivity.this);
            String color = categoryItems.get(i).iconColor;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)basicHeight * 2 );
            lp.setMargins(3, 0, 3, 0);
            textView.setText(categoryItems.get(i).parentItem);
            textView.setCompoundDrawablesWithIntrinsicBounds(getCategoryPic(categoryItems.get(i).iconStyle, color), null, null, null);
            textView.setCompoundDrawablePadding(20);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            textView.setBackgroundColor(getResources().getColor(R.color.colorAutoTextBG));
            textView.setTextColor(getResources().getColor(R.color.colorBackground));//白色
            textView.setPadding(40, 20, 40, 20);
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER_VERTICAL);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView)v;

                    FragmentManager fm = getSupportFragmentManager();
                    MainFragment fragment = (MainFragment)fm.findFragmentById(R.id.fragment_container);
                    fragment.confirm_category(tv.getText().toString());

                }
            });

            ll_autotext.addView(textView);
        }

    }

    /** 子類別智慧選擇 **/
    public void chooseSubCategory(String chosen_category){
        ll_autotext.setVisibility(View.VISIBLE);
        ll_autotext.removeAllViews();
        List<String> s_subC = findValueBySelection(chosen_category);

        for(int i = 0; i < s_subC.size(); i++){
            TextView textView = new TextView(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
            lp.setMargins(3, 0, 3, 0);
            textView.setText(s_subC.get(i));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            textView.setBackgroundColor(getResources().getColor(R.color.colorAutoTextBG));
            textView.setTextColor(getResources().getColor(R.color.colorBackground));//白色
            textView.setPadding(40, 20, 40, 20);
            textView.setLayoutParams(lp);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;

                    FragmentManager fm = getSupportFragmentManager();
                    MainFragment fragment = (MainFragment)fm.findFragmentById(R.id.fragment_container);
                    fragment.confirm_subcategory(tv.getText().toString());

                }
            });
            ll_autotext.addView(textView);
        }
    }


    /**隱藏智慧選字**/
    public void closeAutoText(){
        ll_autotext.setVisibility(View.GONE);
    }

    /**偵測是否第一次使用**/
    private void checkFirstIn(){
        SharedPreferences pref = getSharedPreferences("quickCheck", 0);
        isFirstIn = pref.getBoolean("isFirstIn",true);
    }

    /**第一次使用把初始類別加入資料庫**/
    private void addCategory2db(){
        CategoryItem CItem = new CategoryItem();
        List<String> cgString = Arrays.asList(getResources().getStringArray(R.array.category));
        for(int i = 0 ; i < cgString.size(); i++){
            CItem.parentItem = spiltStringArray(cgString.get(i),";")[0];

            String[] tmp_childItem = spiltStringArray(spiltStringArray(cgString.get(i),";")[1],",");
            ArrayList<String> tmp_child = new ArrayList<String>();
            for(int j = 0; j < tmp_childItem.length ; j++){
                tmp_child.add(tmp_childItem[j]);
            }
            CItem.childItem = tmp_child;

            CItem.iconStyle = Integer.parseInt(spiltStringArray(cgString.get(i),";")[2]);
            CItem.iconColor = spiltStringArray(cgString.get(i),";")[3];
            CItem.isIncome = 0;
            dbhelper.insertCategory(CItem);
        }

        List<String> income_cgString = Arrays.asList(getResources().getStringArray(R.array.income_category));
        for(int i = 0 ; i < income_cgString.size(); i++){
            CItem.parentItem = spiltStringArray(income_cgString.get(i),";")[0];

            String[] tmp_childItem = spiltStringArray(spiltStringArray(cgString.get(i),";")[1],",");
            ArrayList<String> tmp_child = new ArrayList<String>();
            for(int j = 0; j < tmp_childItem.length ; j++){
                tmp_child.add(tmp_childItem[j]);
            }
            CItem.childItem = tmp_child;

            CItem.iconStyle = Integer.parseInt(spiltStringArray(income_cgString.get(i),";")[2]);
            CItem.iconColor = spiltStringArray(income_cgString.get(i),";")[3];
            CItem.isIncome = 1;
            dbhelper.insertCategory(CItem);
        }

        SharedPreferences pref = getSharedPreferences("quickCheck", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstIn", false);
        editor.commit();
    }

    private String[] spiltStringArray(String target,String mark){
        return target.split(mark);
    }

    public Drawable getCategoryPic(int pic_no, String color){
        Drawable iv;
        Resources res = getResources();

        switch (pic_no){
            case 0:
                iv = res.getDrawable(R.drawable.food);
                break;
            case 1:
                iv =res.getDrawable(R.drawable.clothes);
                break;
            case 2:
                iv =res.getDrawable(R.drawable.buildings);
                break;
            case 3:
                iv =res.getDrawable(R.drawable.transport);
                break;
            case 4:
                iv =res.getDrawable(R.drawable.game);
                break;
            case 5:
                iv =res.getDrawable(R.drawable.music);
                break;
            default:
                iv = null;
                break;
        }

        //TODO 轉換顏色
        if(iv != null && color != null){
            int iColor = Color.parseColor(color);

            int red = (iColor & 0xFF0000) / 0xFFFF;
            int green = (iColor & 0xFF00) / 0xFF;
            int blue  = iColor & 0xFF;

            float[] matrix = { 0, 0, 0, 0, red,
                    0, 0, 0, 0, green,
                    0, 0, 0, 0, blue,
                    0, 0, 0, 1, 0 };

            ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
            iv.setColorFilter(colorFilter);
//            iv.mutate().setColorFilter(res.getColor(R.color.color_cost), PorterDuff.Mode.MULTIPLY);
        }

        return iv;
    }

    private void changeFragment(Fragment f){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, f);
        transaction.commitAllowingStateLoss();

    }

    private void changeDownFragment(Fragment f){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.down_fragment, f);
        transaction.commitAllowingStateLoss();
    }

    private void change2LineChart(){
        Fragment fragment = LineChartFragment.newInstance(basicHeight);
        changeFragment(fragment);
    }

    private void change2SumPerMonth(){
        Fragment fragment = SumPerMonthFragment.newInstance(basicHeight);
        changeFragment(fragment);
    }

    public void change2saveFragment(){
        changeFragment(MainFragment.newInstance(basicHeight,-1));
    }

    public void change2saveFragmentWithId(long id){
        changeFragment(MainFragment.newInstance(basicHeight,id));
    }

    public void change2PieChart(boolean isIncome, String startTime, String endTime){
        Fragment fragment = PieChartFragment.newInstance(basicHeight,screen_width,isIncome,startTime, endTime);
        changeFragment(fragment);
    }

    public void change2DateSelector(){
        if(null != RecordFragment.getEvent()){
            changeFragment(DateSelectFragment.newInstance( RecordFragment.getEvent()));
        }

    }

    public void change2recordFragment(){
        Fragment fragment = RecordFragment.newInstance(basicHeight,screen_width);
        changeDownFragment(fragment);
    }

    public void change2EditFragment(long id){
        Fragment fragment = EditFragment.newInstance(basicHeight,screen_width,id);
        changeDownFragment(fragment);
    }


    public void change2settings(){
        Fragment fragment = SettingsFragment.newInstance(basicHeight);
        changeFragment(fragment);
    }

    public void change2routine(){
        Fragment fragment = RoutineSetting.newInstance(basicHeight);
        changeFragment(fragment);
    }

    public void change2InAndOut(){
        Fragment fragment = ImAndEx.newInstance(basicHeight);
        changeFragment(fragment);
    }

    public void click_right_top_button(){
        WindowsUtils.showPopupWindow(MainActivity.this);
    }

    public void showTimeFragment(){
        DialogFragment timeFragmenet = TimeSelectorFragment.newInstance(calendar);
        timeFragmenet.show(getFragmentManager(), "timeDialog");
    }

    /**設置時間回來**/
    public void resetTimeText(int hour, int min){
        if( min < 10){
            FragmentManager fm = getSupportFragmentManager();
            MainFragment fragment = (MainFragment)fm.findFragmentById(R.id.fragment_container);
            fragment.setTimeText(hour + ":0" + min);

        }else{
            FragmentManager fm = getSupportFragmentManager();
            MainFragment fragment = (MainFragment)fm.findFragmentById(R.id.fragment_container);
            fragment.setTimeText(hour + ":" + min);
        }
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    change2saveFragment();
                    break;
                case 1:
                    change2DateSelector();
                    break;
                case 2: //切換至lineChart
                    change2LineChart();
                    isLineChart = true;
                    break;
                case 3: //月底盈餘圖
                    change2SumPerMonth();
                    break;
                case 4:
                    if(msg.obj == null){
                        Calendar c = Calendar.getInstance();
                        String month = c.get(Calendar.MONTH)< 9? "0"+(c.get(Calendar.MONTH)+1): ""+(c.get(Calendar.MONTH)+1);
                        String start = c.get(Calendar.YEAR)+"-"+month + "-01";
                        String end = c.get(Calendar.YEAR)+"-"+month + "-" + c.getActualMaximum(Calendar.DAY_OF_MONTH);

                        change2PieChart(false,start,end);
                    }else{
                        boolean isIncome = Integer.parseInt(spiltStringArray((String) msg.obj, ":")[0]) == 1;
                        change2PieChart(isIncome,spiltStringArray((String)msg.obj,":")[1],spiltStringArray((String)msg.obj,":")[2]);
                    }
                    break;
                case 6: //設定
                    change2settings();
                    break;
                case 7:
                    change2recordFragment();
                    break;
                case 8:
                    if(msg.obj != null){

                        change2saveFragmentWithId((long) msg.obj);
                        change2EditFragment((long)msg.obj);
                    }
                    break;
                case 61: //定期花費
                    change2routine();
                    break;
                case 62: //匯出資料庫
                    change2InAndOut();
                    break;
                case 70:
                    ll_autotext.removeAllViews();
                    change2saveFragment();
                    change2recordFragment();
                    break;
                case 11: //切換至指定日期
                    FragmentManager fm = getSupportFragmentManager();
                    RecordFragment fragment = (RecordFragment)fm.findFragmentById(R.id.down_fragment);
                    fragment.scrollToResult((String) msg.obj);
                    break;
            }
        }

    };

}
