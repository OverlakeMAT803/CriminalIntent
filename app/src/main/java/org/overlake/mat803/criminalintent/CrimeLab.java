package org.overlake.mat803.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;

import org.overlake.mat803.criminalintent.database.CrimeBaseHelper;
import org.overlake.mat803.criminalintent.database.CrimeCursorWrapper;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.CrimeTable;
import org.overlake.mat803.criminalintent.database.CrimeDbSchema.SuspectTable;

import java.io.File;
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
                CrimeTable.Cols.UUID + " =?",
                new String[] { id.toString() }
            );
        try {
            if (cursor.getCount()  == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

    public void delete(Crime c){
        mDatabase.delete(CrimeTable.NAME, CrimeTable.Cols.UUID + " = ?",
                new String[] { c.getID().toString() });
    }

    public void updateCrime(Crime c){

        ContentValues crimeValues = getCrimeContentValues(c);
        ContentValues suspectValues = getSuspectContentValues(c);

        mDatabase.update(CrimeTable.NAME, crimeValues,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { c.getID().toString() });


        int count = mDatabase.delete(
                SuspectTable.NAME,
                SuspectTable.Cols.SUSPECT_ID + " = " + c.getSuspectId(),
                null);

        mDatabase.insert(SuspectTable.NAME, null, suspectValues);
    }

    public void addCrime(Crime c){
        ContentValues crimeValues = getCrimeContentValues(c);
        mDatabase.insert(CrimeTable.NAME, null, crimeValues);
    }

    private static ContentValues getCrimeContentValues(Crime c){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, c.getID().toString());
        values.put(CrimeTable.Cols.TITLE, c.getTitle());
        values.put(CrimeTable.Cols.DATE, c.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, c.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT_ID, c.getSuspectId());
        return values;
    }

    private static ContentValues getSuspectContentValues(Crime c){
        ContentValues values = new ContentValues();
        values.put(SuspectTable.Cols.SUSPECT_ID, c.getSuspectId());
        values.put(SuspectTable.Cols.DISPLAY_NAME, c.getSuspectName());
        values.put(SuspectTable.Cols.PHONE, c.getSuspectPhone());
        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){

        String sql = "SELECT * FROM " + CrimeTable.NAME + " c " +
                "LEFT JOIN " + SuspectTable.NAME + " s " +
                "ON c." + CrimeTable.Cols.SUSPECT_ID + " = s." + SuspectTable.Cols.SUSPECT_ID;


        if(whereClause != null){
            sql += " WHERE " + whereClause;
        }

        Cursor cursor = mDatabase.rawQuery(sql, whereArgs);


        return new CrimeCursorWrapper(cursor);
    }

}
