package com.zhang.shaon.criminalintent;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,
        CrimeFragment.Callbacks {
    private static final String DIALOG_DATE = "DialogDate";
    public static final String REQUEST_DELETE="Delete Crime";
    private static final int REQUEST_DATE=0;
    private Fragment newDetail;
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    protected int getLayoutResId(){
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime,int position) {
        if(findViewById(R.id.detail_fragment_container)==null){
            Intent intent = CrimePageActivity.newIntent(this, crime.getId(),position);
            intent.putExtra(REQUEST_DELETE,3);
            startActivity(intent);
            /*Intent intent2 = CrimeActivity.newIntent(this, crime.getId());
            startActivity(intent2);*/

        }else{
            newDetail = CrimeFragment.newInstance(crime.getId());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container,newDetail)
                    .commit();
        }
    }

    @Override
    public void onCrimeDate(Crime crime){
        if(findViewById(R.id.fragment_container)!=null){
            android.support.v4.app.FragmentManager manager=getSupportFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
            dialog.setTargetFragment(newDetail, REQUEST_DATE);
            dialog.show(manager,DIALOG_DATE);
        }

    }

    @Override
    public void onCrimeIdSelected(UUID id) {

    }
}
