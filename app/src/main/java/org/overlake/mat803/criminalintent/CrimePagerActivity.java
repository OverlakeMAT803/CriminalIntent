package org.overlake.mat803.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private CrimeLab mCrimeLab;

    private static final String EXTRA_CRIME_ID = "crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId){
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimeLab = CrimeLab.get(this);

        mViewPager = findViewById(R.id.crime_view_pager);
        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int i) {
                UUID crimeId = mCrimeLab.get(i).getID();
                return CrimeFragment.newInstance(crimeId);
            }

            @Override
            public int getCount() {
                return mCrimeLab.size();
            }
        });
    }
}
