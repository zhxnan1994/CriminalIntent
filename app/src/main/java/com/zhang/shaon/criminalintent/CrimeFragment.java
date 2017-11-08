package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private Button mDateButton,mTimeButton;

    private static final String ARG_CRIME_ID = "crime_id";
    public static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;

    private boolean mIsLargeLayout;
    private Callbacks mCallbacks;
    public interface Callbacks{
        void onCrimeDate(Crime crime);
        void onCrimeIdSelected(UUID id);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        EditText titleField = v.findViewById(R.id.crime_title);
        titleField.setText(mCrime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton =  v.findViewById(R.id.crime_date);
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCallbacks.onCrimeDate(mCrime);
                /*Intent intent = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                startActivityForResult(intent,0);*/
                if (mIsLargeLayout) {
                    FragmentManager manager = getFragmentManager();
                    Fragment newDetail = DatePickerFragment.newInstance(mCrime.getDate());
                    manager.beginTransaction()
                            .replace(R.id.date_fragment_container,newDetail)
                            .commit();

                } else {
                    Intent intent = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }

            }
        });

        mTimeButton = v.findViewById(R.id.crime_time);
        mTimeButton.setText(mCrime.getTimeString());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                TimePickerFragment fragment = TimePickerFragment.newInstance(mCrime.getCalendar());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                fragment.show(manager,DIALOG_TIME);

            }
        });

        CheckBox solved = v.findViewById(R.id.crime_solved);
        solved.setChecked(mCrime.isSolved());
        solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        CheckBox callPolice = v.findViewById(R.id.crime_call_police);
        callPolice.setChecked(mCrime.isRequirePolice());
        callPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequirePolice(isChecked);
            }
        });

        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }
        if(requestCode==REQUEST_DATE){
            Date date=(Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCrime.setDateShow();
            updateDate();
        }
        if (requestCode == REQUEST_TIME) {
            Calendar time = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateTime();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateDate() {
        updateDate(mCrime.getDateShow());
    }

    private void updateDate(String text) {
        mDateButton.setText(text);
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK,null);
    }

    private void updateTime() {
        mTimeButton.setText(mCrime.getTimeString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                mCallbacks.onCrimeIdSelected(mCrime.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
