package org.overlake.mat803.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspect;
    private String mSuspectName;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Callbacks mCallbacks;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_READ_CONTACTS = 1000;
    public static final int REQUEST_PHOTO = 2;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
        void onDeleteCrime();
    }

    public void onAttach(Context context){
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public void onDetach(){
        super.onDetach();
        mCallbacks = null;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
        setHasOptionsMenu(true);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        View v = inflater.inflate(R.layout.fragment_crime,container,false);
        mTitleField = v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TextWatcher is an interface, so we are required to implement this method.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                mCallbacks.onCrimeUpdated(mCrime);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TextWatcher is an interface, so we are required to implement this method.
            }
        });

        mDateButton = v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                FragmentManager fm = getFragmentManager();
                dialog.show(fm,DIALOG_DATE);
            }
        });

        mSolvedCheckBox = v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                mCallbacks.onCrimeUpdated(mCrime);
            }
        });

        mReportButton = v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = ShareCompat.IntentBuilder.from(getActivity())
                    .setType("text/plain")
                    .setChooserTitle(R.string.send_report)
                    .setSubject(getString(R.string.crime_report_subject))
                    .setText(getCrimeReport())
                    .createChooserIntent();

                startActivity(i);
            }
        });

        mSuspectButton = v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissions();
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspectId() != 0){
            mSuspectButton.setText(getSuspectName());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){
            mSuspectButton.setEnabled(false);
        }

        mCallSuspect = v.findViewById(R.id.crime_call);
        mCallSuspect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "tel:" + getSuspectPhone();
                Uri number = Uri.parse(phone);
                Intent intent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(intent);
            }
        });

        mPhotoButton = v.findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null
                && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "org.overlake.mat803.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = v.findViewById(R.id.crime_photo);
        updatePhotoView();
        return v;
    }

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    100);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode != Activity.RESULT_OK){
            return;
        }

        switch(requestCode){
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                mCallbacks.onCrimeUpdated(mCrime);
                break;
            case REQUEST_CONTACT:
                if(data != null){
                    String[] queryFields = new String[] {
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts._ID
                    };
                    Cursor c = getActivity().getContentResolver().query(data.getData(), queryFields, null, null, null);


                    try{
                        if(c.getCount() == 0){
                            return;
                        }
                        c.moveToFirst();
                        mCrime.setSuspectId(c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID)));

                    } finally {
                        c.close();
                    }

                    mSuspectButton.setText(getSuspectName());
                }
                break;
            case REQUEST_PHOTO:
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "org.overlake.mat803.criminalintent.fileprovider",
                        mPhotoFile);

                getActivity().revokeUriPermission(uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                updatePhotoView();
        }

    }


    public String getSuspectName(){
        if(mCrime.getSuspectId() == 0){
            return null;
        }


        String[] queryFields = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
        };
        String[] selectionArgs = new String[] {
                mCrime.getSuspectId() + ""
        };
        Cursor c = getActivity().getContentResolver()
                .query(
                        ContactsContract.Contacts.CONTENT_URI,
                        queryFields,
                        ContactsContract.Contacts._ID + " = ?",
                        selectionArgs, null
                );



        try{
            if(c.getCount() == 0){
                return null;
            }
            c.moveToFirst();
            mSuspectName =c.getString(0);

        } finally {
            c.close();
        }
        return mSuspectName;
    }

    public  String getSuspectPhone(){
        if(mCrime.getSuspectId() == 0){
            return null;
        }

        Cursor c = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mCrime.getSuspectId(),
                null,
                null);

        try{
            if(c.getCount() == 0){
                return null;
            }
            c.moveToFirst();
            return c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

        } finally {
            c.close();
        }
    }
    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_delete, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).delete(mCrime);
                mCallbacks.onCrimeUpdated(mCrime);
                mCallbacks.onDeleteCrime();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private String getCrimeReport(){
        int stringId = mCrime.isSolved() ?
                R.string.crime_report_solved : R.string.crime_report_unsolved;
        String solvedString = getString(stringId);

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspectName = getSuspectName();
        String crimeString = suspectName == null ?
                getString(R.string.crime_report_no_suspect) : getString(R.string.crime_report_suspect, suspectName);
        return getString(R.string.crime_report,mCrime.getTitle(), dateString, solvedString, crimeString);

    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
