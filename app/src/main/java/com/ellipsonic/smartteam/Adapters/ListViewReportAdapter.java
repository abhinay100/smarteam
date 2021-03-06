package com.ellipsonic.smartteam.Adapters;

//import static com.techlovejump.multicolumnlistview.Constants.FIRST_COLUMN;
//import static com.techlovejump.multicolumnlistview.Constants.FOURTH_COLUMN;
//import static com.techlovejump.multicolumnlistview.Constants.SECOND_COLUMN;
//import static com.techlovejump.multicolumnlistview.Constants.THIRD_COLUMN;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ellipsonic.smartteam.R;

/**
 * Created by administrator on 11/4/16.
 */

public class ListViewReportAdapter extends BaseAdapter{

    public ArrayList<HashMap<String, String>> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    TextView txtFourth;

    public ListViewReportAdapter(Activity activity,ArrayList<HashMap<String, String>> list){
        super();
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater=activity.getLayoutInflater();

        if(convertView == null){

            convertView=inflater.inflate(R.layout.colmn_row, null);

            txtFirst=(TextView) convertView.findViewById(R.id.name);
            txtSecond=(TextView) convertView.findViewById(R.id.gender);
            txtThird=(TextView) convertView.findViewById(R.id.age);
            txtFourth=(TextView) convertView.findViewById(R.id.status);

        }

        HashMap<String, String> map=list.get(position);
//        txtFirst.setText(map.get(FIRST_COLUMN));
//        txtSecond.setText(map.get(SECOND_COLUMN));
//        txtThird.setText(map.get(THIRD_COLUMN));
//        txtFourth.setText(map.get(FOURTH_COLUMN));

        return convertView;
    }
}
