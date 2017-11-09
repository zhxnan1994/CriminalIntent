package com.zhang.shaon.criminalintent;


import android.text.format.DateFormat;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Calendar mCalendar;
    private boolean mSolved;
    private boolean mRequirePolice;
    private String mDateShow;
    private CharSequence inFormat;
    private String mTimeShow;
    public Crime(){
        this(UUID.randomUUID());


    }

    public Crime(UUID id){
        mId=id;
        mDate = new Date();
        mCalendar=Calendar.getInstance();
        setInFormat("yyyy ,MMMM dd, EEEE ");
        setDateShow();
    }


    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isRequirePolice() {
        return mRequirePolice;
    }

    public String getDateShow() {
        return mDateShow;
    }

    public void setDateShow() {
        mDateShow= DateFormat.format(inFormat,mDate).toString();
    }

    public void setInFormat(CharSequence inFormat) {
        this.inFormat = inFormat;
    }

    public void setRequirePolice(boolean requirePolice) {
        mRequirePolice = requirePolice;
    }


    public String getTimeString() {
        return android.text.format.DateFormat.format(" a hh:mm", mCalendar).toString();
    }

    public void setTime(Calendar time) {

        mCalendar.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY));
        mCalendar.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
        mCalendar.set(Calendar.SECOND,time.get(Calendar.SECOND));
    }

    public Calendar getCalendar(){
        return mCalendar;
    }

}
