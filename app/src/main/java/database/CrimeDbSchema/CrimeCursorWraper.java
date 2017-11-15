package database.CrimeDbSchema;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.zhang.shaon.criminalintent.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Created by zhang on 2017-11-09.
 */

public class CrimeCursorWraper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWraper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime(){
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title=getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date=getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        int police = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.POLICE));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved!=0);
        crime.setRequirePolice(police!=0);
        crime.setSuspect(suspect);

        return crime;
    }
}
