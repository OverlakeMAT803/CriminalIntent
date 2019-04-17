package org.overlake.mat803.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import org.overlake.mat803.criminalintent.Crime;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {
    public CrimeCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Crime getCrime(){
        String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int suspectId = getInt(getColumnIndex(CrimeTable.Cols.SUSPECT_ID));



        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspectId(suspectId);

        return crime;
    }
}
