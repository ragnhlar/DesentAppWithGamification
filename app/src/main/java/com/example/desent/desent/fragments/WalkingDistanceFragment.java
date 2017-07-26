package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.desent.desent.R;
import com.example.desent.desent.activities.MainActivity;
import com.example.desent.desent.models.Indicator;

/**
 * Created by celine on 22/06/17.
 */

public class WalkingDistanceFragment extends Fragment {

    private SeekBar seekBar;
    private TextView walkingTextView;
    private TextView drivingTextView;

    protected int walkingDistance = 20; //TODO:test
    protected int drivingDistance = 100; //TODO:test

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_walking_distance, container, false);
    }

    public void setUp() {

        seekBar = getView().findViewById(R.id.seekbar_walking_distance);
        walkingTextView = getView().findViewById(R.id.text_view_walking_distance);
        drivingTextView = getView().findViewById(R.id.text_view_driving_distance);

        seekBar.setMax(walkingDistance +drivingDistance);
        seekBar.setProgress(walkingDistance);

        walkingTextView.setText(String.valueOf(walkingDistance) + " km");
        drivingTextView.setText(String.valueOf(drivingDistance) + " km");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                walkingDistance = progress;
                drivingDistance = seekBar.getMax() - progress;

                walkingTextView.setText(String.valueOf(walkingDistance) + " km");
                drivingTextView.setText(String.valueOf(drivingDistance) + " km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getActivity(), "Estimate your values with a different walking distance", Toast.LENGTH_SHORT).show(); //TODO: test
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                //TODO: don't think is a good way to do it
                MainActivity main = (MainActivity) getActivity();
                for(Indicator indicator : main.getIndicators()) {
                    indicator.setWalkingDistance((float) walkingDistance);
                    indicator.setDrivingDistance((float) drivingDistance);
                }
                main.refreshAll();

            }
        });
    }
}