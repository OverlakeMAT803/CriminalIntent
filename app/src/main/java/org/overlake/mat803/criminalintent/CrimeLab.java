package org.overlake.mat803.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.overlake.mat803.criminalintent.database.CrimeBaseHelper;
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
        // TODO
        return null;
    }

    public int size(){
        // TODO
        return 0;
    }


    public List<Crime> getCrimes(){
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id){
        return null;
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



}
