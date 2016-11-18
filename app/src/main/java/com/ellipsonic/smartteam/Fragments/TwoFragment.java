package com.ellipsonic.smartteam.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ellipsonic.smartteam.Activity.MarkAttendence;
import com.ellipsonic.smartteam.Activity.MarkLeave;
import com.ellipsonic.smartteam.R;


/**
 * Created by Ellip sonic on 19-10-2016.
 */
public class TwoFragment extends Fragment implements View.OnClickListener {

    android.support.v7.widget.CardView applyLeave_card;
    android.support.v7.widget.CardView attendence_card;

    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_t, container, false);
        applyLeave_card = (android.support.v7.widget.CardView) view.findViewById(R.id.apply_leave_card);
        attendence_card = (android.support.v7.widget.CardView) view.findViewById(R.id.attendence_card);

        applyLeave_card.setOnClickListener(this);
        attendence_card.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.apply_leave_card:
                Intent intent = new Intent(getActivity(), MarkLeave.class);
                this.startActivity(intent);
                break;
            case R.id.attendence_card:
                Intent intent2 = new Intent(getActivity(), MarkAttendence.class);
                this.startActivity(intent2);
                break;
        }
    }
}
