package com.ceres.quickcheck.Units;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

/**
 * 所有fragment的基礎，放置共用功能
 * Created by apple on 2016/8/18.
 */
public class MyFragment extends Fragment {

    protected void setLayoutHieghtByUnit(View view, int unit){


        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int)(getBasicHeight() * unit + 0.5f);
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.requestLayout();
    }

    protected float getBasicHeight(){
        return  getArguments().getFloat("basicHeight",0);
    }
    protected float getScreenWidth(){
        return getArguments().getFloat("screen_width", 0);
    }


    protected String fixDate(int date){
        String showText;
        if(date < 10){
            showText = "0"+ date;
        }else{
            showText = "" + date;
        }
        return showText;
    }

    protected String[] spiltStringArray(String target,String mark){
        return target.split(mark);
    }

}
