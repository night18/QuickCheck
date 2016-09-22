package com.ceres.quickcheck.Fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;

/**
 * Created by apple on 2016/5/9.
 */
public class SettingsFragment extends MyFragment {
    private View view;
    private ImageButton ib_confirm;
    private ScrollView sv_setting;
    private RelativeLayout setting_header;
    private Button bt_routine, bt_sent_in;

    public static SettingsFragment newInstance(float basicHeight){
        SettingsFragment fragment = new SettingsFragment();

        Bundle args = new Bundle();
        args.putFloat("basicHeight",basicHeight);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        view = inflater.inflate(R.layout.settings, container, false);
        setLayoutHieghtByUnit(view, 28);
        ib_confirm = (ImageButton)view.findViewById(R.id.confirm);
        ib_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(0);
            }
        });


        sv_setting = (ScrollView)view.findViewById(R.id.sv_setting);
        setLayoutHieghtByUnit(sv_setting,24);

        setting_header = (RelativeLayout)view.findViewById(R.id.setting_header);
        setLayoutHieghtByUnit(setting_header,2);

        bt_routine = (Button) view.findViewById(R.id.routine_cost_earn);
        bt_routine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(61);
            }
        });

        bt_sent_in = (Button) view.findViewById(R.id.in_and_out);
        bt_sent_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(62);
            }
        });

        return view;
    }

}
