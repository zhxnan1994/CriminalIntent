package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

/**
 * Created by zhang on 2017-11-01.
 */

public class CrimePageActivity extends AppCompatActivity implements CrimeFragment.Callbacks {
    private ViewPager mViewPager;
    private List<Crime> mCrimes;
    private Button mJumpFirst;
    private Button mJumpLast;
    public static final String EXTRA_CRIME_ID="com.zhang.shaon.criminalintent_crime_id";
    private static final String EXTRA_POSITION = "com.zhang.shaon.criminalintent.position";
    private static final int REQUEST_DATE=0;

    public static Intent newIntent(Context packageContext, UUID crimeID,int position){
        Intent intent = new Intent(packageContext, CrimePageActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        intent.putExtra(EXTRA_POSITION, position);
        return intent;
    }

    /*public static Intent newIntent(Context packageContext, UUID crimeID){
        Intent intent = new Intent(packageContext, CrimePageActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeID);
        return intent;
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        mViewPager = findViewById(R.id.activity_crime_view_pager);
        mCrimes=CrimeLab.get(this).getCrimeList();


        UUID crimeId =(UUID)getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });
        int i = (Integer) getIntent().getSerializableExtra(EXTRA_POSITION);
        mViewPager.setCurrentItem(i);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener () {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (mViewPager.getCurrentItem() == 0) {
                    mJumpFirst.setVisibility(View.INVISIBLE);
                } else {
                    mJumpFirst.setVisibility(View.VISIBLE);
                }

                if(position == mViewPager.getAdapter().getCount()-1) {
                    mJumpLast.setVisibility(View.INVISIBLE);
                }

                if (mViewPager.getCurrentItem() == (mCrimes.size() - 1)) {
                    mJumpLast.setVisibility(View.INVISIBLE);
                } else {
                    mJumpLast.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        mJumpFirst = (Button) findViewById(R.id.jump_first);
        mJumpFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mJumpLast = (Button) findViewById(R.id.jump_last);
        mJumpLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mViewPager.getAdapter().getCount());
            }
        });

    }
        /*
        for(int i=0;i<mCrimes.size();i++){
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }*/

    @Override
    public void onCrimeDate(Crime crime) {
        Intent intent = DatePickerActivity.newIntent(this, crime.getDate());
        startActivity(intent);
    }

    @Override
    public void onCrimeIdSelected(UUID id) {
        CrimeLab crimeLab = CrimeLab.get(this);
        Crime crime = crimeLab.getCrime(id);
        crimeLab.delteCrime(crime);
        mCrimes=CrimeLab.get(this).getCrimeList();
        finish();
    }

    @Override
    public void onCrimeUpdated(Crime crime) {

    }


}

