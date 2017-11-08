package com.zhang.shaon.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private List<Crime> mCrimesList;
    private Map<UUID,Crime> mCrimes;
    public static CrimeLab get(Context context){
        if(sCrimeLab==null)
            sCrimeLab=new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mCrimes = new HashMap<UUID,Crime>();
        mCrimesList = new ArrayList<Crime>();

    }

    public List<Crime> getCrimeList(){
        return mCrimesList;
    }

    public Crime getCrime(UUID id){
        return mCrimes.get(id);
    }

    public int getIndex(UUID id){
        int index=-1;
        mCrimesList = new ArrayList<>(mCrimes.values());
        for(int i=0;i<mCrimesList.size();i++){
            if(mCrimesList.get(i).getId().equals(id)){
               index=i;
            }
        }
        return index;
    }

    public void addCrime(Crime c){
        mCrimes.put(c.getId(),c);
        mCrimesList.add(c);
    }

    public void delteCrime(Crime c){
        if(!mCrimesList.isEmpty()) {
            mCrimes.remove(c.getId());
            for(int i=0;i<mCrimesList.size();i++){
                if(mCrimesList.get(i).equals(c)){
                    mCrimesList.remove(c);
                    i--;
                }
            }
        }
    }
    /*public Crime getCrime(UUID id){
        for(Crime crime:mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }*/

}
