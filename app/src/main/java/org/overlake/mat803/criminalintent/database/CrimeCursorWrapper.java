package org.overlake.mat803.criminalintent.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import org.overlake.mat803.criminalintent.Crime;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.CrimeTable;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.SuspectTable;


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
        String name = getString(getColumnIndex(SuspectTable.Cols.DISPLAY_NAME));
        String phone = getString(getColumnIndex(SuspectTable.Cols.PHONE));

        Crime crime = new Crime(UUID.fromString(uuid));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspectId(suspectId);
        crime.setSuspectName(name);
        crime.setSupsectPhone(phone);

        return crime;
    }

}
