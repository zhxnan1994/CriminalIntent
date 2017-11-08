package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by zhang on 2017-11-03.
 */

public class TimePickerFragment extends DialogFragment {
    private TimePicker mTimePicker;
    private Button mButton;
    private Calendar mCalendar;
    private static final String ARG_TIME="time";
    public static final String EXTRA_TIME = "com.bignerdranch.android.criminalintent.time";
    /*@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.time_picker,container, false);
        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);
        mButton = v.findViewById(R.id.time_button_ok);
        Date date = (Date)getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        if(BuildConfig.VERSION_CODE>=23){
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minutes);}
        else{
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minutes);
        }
        return v;
    }*/
    public Dialog onCreateDialog(Bundle savedInstanceState){
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.time_picker, null);
        mTimePicker = (TimePicker) v.findViewById(R.id.time_picker);

        mCalendar = (Calendar) getArguments().getSerializable(ARG_TIME);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minutes = mCalendar.get(Calendar.MINUTE);
        int seconds = mCalendar.get(Calendar.SECOND);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minutes);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.time_picker_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int hour, minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = mTimePicker.getHour();
                            minute = mTimePicker.getMinute();
                        } else {
                            hour = mTimePicker.getCurrentHour();
                            minute = mTimePicker.getCurrentMinute();
                        }
                        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
                        mCalendar.set(Calendar.MINUTE, minute);

                        sendResult(Activity.RESULT_OK, mCalendar);

                    }
                }).create();

    }
    private void sendResult(int resultCode,Calendar date){

        if(getTargetFragment()==null){
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(),resultCode,intent);
    }

    public static TimePickerFragment newInstance(Calendar time){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME,time);

        TimePickerFragment fragment=new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
