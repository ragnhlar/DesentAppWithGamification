package com.example.desent.desent.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.desent.desent.R;
import com.example.desent.desent.models.Indicator;
import com.example.desent.desent.utils.Utility;
import com.example.desent.desent.views.CircularIndicator;

/**
 * Created by celine on 28/04/17.
 */
public class CircleFragment extends Fragment {

    protected Indicator indicator;
    protected CircularIndicator circularIndicator;
    protected TextView caption;
    protected ImageView earthImage;

    //Circle category
    protected int startAngle = 270;
    protected int sweepAngle = 360;
    protected String imgName = "earth";
    protected int numberOfStates = 5;
    protected int decimalsNumber;

    public int getDecimalsNumber() {
        return decimalsNumber;
    }

    public void setDecimalsNumber(int decimalsNumber) {
        this.decimalsNumber = decimalsNumber;
    }

    public void setIndicator(Indicator indicator) {
        this.indicator = indicator;
    }

    public Indicator getIndicator() {return this.indicator;}

    public void setCircularIndicator (CircularIndicator circularIndicator) {this.circularIndicator = circularIndicator;}

    public CircularIndicator getCircularIndicator() {return this.circularIndicator;}

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public int getNumberOfStates() {
        return numberOfStates;
    }

    public void setNumberOfStates(int numberOfStates) {
        this.numberOfStates = numberOfStates;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_circle, container, false);
    }



    public void updateImgState(){
        int state = (int) (((numberOfStates - 1) * indicator.getSumValues()) / indicator.getMaxValue()) + 1;
        state = (state < numberOfStates) ? state : numberOfStates;
        Resources res = getResources();
        earthImage.setImageBitmap(BitmapFactory.decodeResource(res, res.getIdentifier(imgName + String.valueOf(state), "drawable", getActivity().getPackageName())));

    }

    public void refresh(){

        circularIndicator.setValues(indicator.getAverageValues());
        circularIndicator.invalidate();

        caption.setText(Utility.floatToStringNDecimals(indicator.getSumValues(), indicator.getDecimalsNumber()) + " " + indicator.getUnit());

        updateImgState();
    }

    public void setUp(){

        circularIndicator = getView().findViewById(R.id.circular_indicator);
        circularIndicator.setMaxValue((int) indicator.getMaxValue()); //TODO: change
        circularIndicator.setColors(indicator.getColors());
        circularIndicator.setValues(indicator.getAverageValues());
        circularIndicator.setStartAngle(this.startAngle);
        circularIndicator.setSweepAngle(this.sweepAngle);
        circularIndicator.setDecimalsNumber(indicator.getDecimalsNumber());
        circularIndicator.setLimitColor(indicator.getLimitColor());

        caption = getView().findViewById(R.id.caption);
        caption.setText(Utility.floatToStringNDecimals(indicator.getSumValues(), indicator.getDecimalsNumber()) + " " + indicator.getUnit());

        earthImage = getView().findViewById(R.id.image_view_earth);
        updateImgState();
    }
}
