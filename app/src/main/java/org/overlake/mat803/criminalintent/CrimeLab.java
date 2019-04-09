package org.overlake.mat803.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.overlake.mat803.criminalintent.database.CrimeBaseHelper;
import org.overlake.mat803.criminalintent.database.CrimeCursorWrapper;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.CrimeTable;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }


    public static CrimeLab get(Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public Crime get(int i){
        CrimeCursorWrapper cursor = getCursor();
        cursor.moveToPosition(i);
        return cursor.getCrime();
    }

    public int size(){
        // TODO
        CrimeCursorWrapper cursor = getCursor();
        int count = 0;
        try {
            count = cursor.getCount();
        } finally {
            cursor.close();
        }
        return count;
    }

    private CrimeCursorWrapper getCursor(){
        return queryCrimes(null,null);
    }

    private CrimeCursorWrapper getCursor(String whereClause, String[] whereArgs){
        return queryCrimes(whereClause, whereArgs);
    }

    public List<Crime> getCrimes(){
        List<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursor = getCursor();

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return crimes;
    }

    public Crime getCrime(UUID id){
        CrimeCursorWrapper cursor = queryCrimes(
                CrimeTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public void updateCrime(Crime c){
        String uuid = c.getID().toString();
        ContentValues values = getContentValues(c);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = " + uuid,
                new String[] { uuid });
    }

    public void addCrime(Crime c){
        ContentValues values = getContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    private static ContentValues getContentValues(Crime c){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, c.getID().toString());
        values.put(CrimeTable.Cols.TITLE, c.getTitle());
        values.put(CrimeTable.Cols.DATE, c.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, c.isSolved() ? 1 : 0);
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new CrimeCursorWrapper(cursor);
    }

}
