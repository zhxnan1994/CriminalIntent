package com.zhang.shaon.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import database.CrimeDbSchema.*;
/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private  Context mContext;
    private SQLiteDatabase mDatabase;
    public static CrimeLab get(Context context){
        if(sCrimeLab==null)
            sCrimeLab=new CrimeLab(context);
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext= context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext)
                .getWritableDatabase();


    }


    public List<Crime> getCrimeList(){
        List<Crime> crimeList = new ArrayList<>();
        CrimeCursorWraper cursor=queryCrimes(null,null);
        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                crimeList.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return crimeList;
    }


    public Crime getCrime(UUID id){
        CrimeCursorWraper cursor = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID +
                "=?" ,new String[]{id.toString()});
        try{
            if(cursor.getCount()==0)
                return  null;
            cursor.moveToFirst();
            return cursor.getCrime();
        }finally {
            cursor.close();
        }
    }


    private static ContentValues getContentValues(Crime crime) {
        ContentValues values=new ContentValues();
        values.put(CrimeDbSchema.CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved()?1:0);
        values.put(CrimeDbSchema.CrimeTable.Cols.POLICE, crime.isRequirePolice()?1:0);
        return values;
    }
    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME,null, values);
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, values,
                CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[]{uuidString});
    }

    private CrimeCursorWraper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                CrimeDbSchema.CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWraper(cursor);
    }
    public void delteCrime(Crime c){
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME,
                CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[]{c.getId().toString()});
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
