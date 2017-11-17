package com.zhang.shaon.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks,
        CrimeFragment.Callbacks ,CrimeListFragment.OnDeleteCrimeListener{
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

    }

    @Override
    public void onCrimeIdSelected(UUID id) {

    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        CrimeListFragment fragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        fragment.updateUI();

    }

    @Override
    public void onCrimeDelete(Crime crime) {
        CrimeListFragment listFragment = (CrimeListFragment)
                getSupportFragmentManager()
                        .findFragmentById(R.id.fragment_container);
        listFragment.deleteCrime(crime);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
                .hide(newDetail)
                .commit();
    }
}
