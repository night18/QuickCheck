package com.ceres.quickcheck.Fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.ceres.quickcheck.MainActivity;
import com.ceres.quickcheck.R;
import com.ceres.quickcheck.Units.MyFragment;

/**
 * Created by apple on 2016/8/18.
 */
public class ImAndEx extends MyFragment {
    private View view;
    private ImageButton back;
    private Button cvs2sql, sql2cvs;

    public static ImAndEx newInstance(float basicHeight){
        ImAndEx fragment = new ImAndEx();

        Bundle args = new Bundle();
        args.putFloat("basicHeight",basicHeight);
        fragment.setArguments(args);

        return  fragment;
    }

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.import_and_export, container, false);
        setLayoutHieghtByUnit(view, 28);


        back = (ImageButton)view.findViewById(R.id.backPage);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).handler.sendEmptyMessage(6);
            }
        });

        sql2cvs = (Button) view.findViewById(R.id.export_to_csv);
        sql2cvs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).dbhelper.exportToCSV();
            }
        });

        return view;
    }

}
