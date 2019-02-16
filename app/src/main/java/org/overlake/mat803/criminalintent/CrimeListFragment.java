package org.overlake.mat803.criminalintent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {

    public static final String CRIME_LIST_FRAGMENT = "crime_list_fragment";
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Find our layout resource (fragment_crime_list.xml) and inflate it into a view object
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);

        // Locate the RecyclerView within the view object
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);

        // All recycler views need a layout manager to work. We'll use android's
        // provided LinearLayoutManager. LinearLayoutManager requires the context in which
        // it is working. Note that I can just as well pass in getActivity() as getContext()
        // getActivity() refers to the parent activity (CrimeListActivity) which is itself
        // a context.
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Create a new RecyclerView.Adapter. This adapter is what translates
        // the data into views the RecyclerView can use (and recycle).
        mAdapter = new CrimeAdapter();

        // Tell our RecyclerView to use this adapter.
        mCrimeRecyclerView.setAdapter(mAdapter);

        // Return the view that CONTAINS the RecyclerView.
        return view;
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;
        private LayoutInflater mInflater;

        public CrimeAdapter(){
            // Since the adapter is tho only object that needs to know about the data,
            // let's initialize the dataset here instead.
            initDataset();

            // Rather then create a new LayoutInflater every time OnCreateViewHolder() is called,
            // let's create it once and reuse it! Technically we could have saved the LayoutInflater
            // passed to onCreateView() to a member variable on CrimeListFragment (which would be
            // accessible here), but this keeps CrimeAdapter encapsulated.
            mInflater = LayoutInflater.from(getActivity());
        }

        // A private helper method to initialize the field that holds our dataset.
        private void initDataset(){
            // Retrieve data source (a singleton in this example)
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            // Set the dataset field (mCrimes)
            mCrimes = crimeLab.getCrimes();
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CrimeHolder(mInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = mCrimes.get(position);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime,parent,false));

            // itemView is a member variable belonging to RecyclerView.ViewHolder and is set when
            // super() is called above. It IS the inflated individual crime view.
            itemView.setOnClickListener(this);
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
        }

        public void bind(Crime crime){
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(),mCrime.getTitle() + " clicked!",Toast.LENGTH_SHORT).show();
        }
    }




}
