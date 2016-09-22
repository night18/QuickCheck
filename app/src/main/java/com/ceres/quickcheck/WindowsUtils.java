package com.ceres.quickcheck;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

/**
 * 浮動按鈕
 * Created by apple on 2016/4/8.
 */
public class WindowsUtils {
    private static View mView = null;
    private static Context mContext = null;
    public static boolean isShown = false;
    private static MainActivity mainActivity;
    private static Dialog dialog;

    public static void showPopupWindow(final MainActivity context){
        mainActivity = context;
        mContext = context.getApplicationContext();
//        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        dialog =  new Dialog(context, R.style.popupDialog);
        mView = setupView(context);
        dialog.setContentView(mView);

        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP|Gravity.RIGHT;
        dialog.show();
    }

    public static void hidePopupWindow(){
        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    private static View setupView(final Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.popup_window,null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WindowsUtils.hidePopupWindow();
            }
        });

        ImageButton ib_dates = (ImageButton)view.findViewById(R.id.pop_ib_dates);
        ib_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mainActivity.handler){
                    hidePopupWindow();
                    mainActivity.handler.sendEmptyMessage(1);
                }
            }
        });

        //
        ImageButton ib_statistics_2 = (ImageButton)view.findViewById(R.id.pop_ib_statistics_2);

        ib_statistics_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mainActivity.handler){
                    hidePopupWindow();
                    mainActivity.handler.sendEmptyMessage(2);
                }

            }
        });

        ImageButton ib_statistics_3 = (ImageButton)view.findViewById(R.id.pop_ib_statistics_3);

        ib_statistics_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mainActivity.handler){
                    hidePopupWindow();
                    mainActivity.handler.sendEmptyMessage(3);
                }

            }
        });

        ImageButton ib_statistics_4 = (ImageButton)view.findViewById(R.id.pop_ib_statistics_4);

        ib_statistics_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(null != mainActivity.handler){
                    hidePopupWindow();
                    mainActivity.handler.sendEmptyMessage(4);
                }
            }
        });




        return  view;
    }

}
