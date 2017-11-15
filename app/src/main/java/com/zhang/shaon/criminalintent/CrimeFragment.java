package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.print.PrintAttributes;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.format.DateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telecom.Call;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private Button mDateButton,mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageView mPhoto;
    private ImageButton mCamera;
    private File mPhotoFile;

    private static final String ARG_CRIME_ID = "crime_id";
    public static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE=0;
    private static final int REQUEST_TIME=1;
    private static final int REQUEST_CONTACT=2;
    private static final int REQUEST_CONTACTS_PERMISSIONS=3;
    private static final int REQUEST_PHOTO=4;

    private boolean mIsLargeLayout;
    private Callbacks mCallbacks;
    private String mSuspectId;

    public interface Callbacks{
        void onCrimeDate(Crime crime);
        void onCrimeIdSelected(UUID id);
    }

    private static final String[] CONTACTS_PERMISSIONS= new String[]{
            Manifest.permission.READ_CONTACTS
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        mIsLargeLayout = getResources().getBoolean(R.bool.large_layout);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        EditText titleField = v.findViewById(R.id.crime_title);
        titleField.setText(mCrime.getTitle());
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton =  v.findViewById(R.id.crime_date);
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCallbacks.onCrimeDate(mCrime);
                /*Intent intent = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                startActivityForResult(intent,0);*/
                if (mIsLargeLayout) {
                    FragmentManager manager = getFragmentManager();
                    Fragment newDetail = DatePickerFragment.newInstance(mCrime.getDate());
                    manager.beginTransaction()
                            .replace(R.id.date_fragment_container,newDetail)
                            .commit();

                } else {
                    Intent intent = DatePickerActivity.newIntent(getContext(), mCrime.getDate());
                    startActivityForResult(intent, REQUEST_DATE);
                }

            }
        });

        mTimeButton = v.findViewById(R.id.crime_time);
        mTimeButton.setText(mCrime.getTimeString());
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getFragmentManager();
                TimePickerFragment fragment = TimePickerFragment.newInstance(mCrime.getCalendar());
                fragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                fragment.show(manager,DIALOG_TIME);

            }
        });

        CheckBox solved = v.findViewById(R.id.crime_solved);
        solved.setChecked(mCrime.isSolved());
        solved.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        final CheckBox callPolice = v.findViewById(R.id.crime_call_police);
        callPolice.setChecked(mCrime.isRequirePolice());
        callPolice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setRequirePolice(isChecked);
            }
        });


        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_suspect));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);*/
                ShareCompat.IntentBuilder i = ShareCompat.IntentBuilder.from(getActivity());
                i.setType("text/plain");
                i.setText(getReport());
                i.setSubject(getString(R.string.crime_report_suspect,mCrime.getSuspect()));
                i.createChooserIntent();
                i.startChooser();
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        final Intent callSuspect = new Intent(Intent.ACTION_DIAL);
        mCallButton = (Button) v.findViewById(R.id.crime_call_suspect);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Uri number=Uri.parse("tel:"+mCrime.getPhoneNumber());
                callSuspect.setData(number);
                startActivity(callSuspect);
            }
        });


        mCamera = (ImageButton) v.findViewById(R.id.crime_camera);

        /**
         * 拍照，存储图片到相应路径
         */
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager)!=null;
        mCamera.setEnabled(canTakePhoto);

        mCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.zhang.shaon.criminalintent.fileprovider", mPhotoFile);

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo rs:cameraActivities){
                    getActivity().grantUriPermission(rs.activityInfo.packageName,uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhoto = (ImageView) v.findViewById(R.id.crime_photo);
        updatePhotoView();
        return v;
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!=Activity.RESULT_OK){
            return;
        }else if(requestCode==REQUEST_DATE){
            Date date=(Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mCrime.setDateShow();
            updateDate();
        }else if (requestCode == REQUEST_TIME) {
            Calendar time = (Calendar) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateTime();
        }else if(requestCode==REQUEST_CONTACT && data!=null){
            String suspectName = getSuspectName(data);
            mCrime.setSuspect(suspectName);
            mSuspectButton.setText(suspectName);
            if (hasContactPermission()) {
                updateSuspectPhone();
            } else {
                // This will call onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults).
                requestPermissions(CONTACTS_PERMISSIONS, REQUEST_CONTACTS_PERMISSIONS);
            }
        } else if (requestCode == REQUEST_PHOTO) {
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.zhang.shaon.criminalintent.fileprovider", mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePhotoView();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    private void updateDate() {
        updateDate(mCrime.getDateShow());
    }

    private void updateDate(String text) {
        mDateButton.setText(text);
    }

    public void returnResult(){
        getActivity().setResult(Activity.RESULT_OK,null);
    }

    private void updateTime() {
        mTimeButton.setText(mCrime.getTimeString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_crime:
                mCallbacks.onCrimeIdSelected(mCrime.getId());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getReport(){
        String solvedString=null;
        if(mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        }else{
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String crimeType=null;
        if(mCrime.isRequirePolice()){
            crimeType = getString(R.string.crime_report_police_crime);
        }else{
            crimeType = getString(R.string.crime_report_normal_crime);
        }
        String dateFormat="EEE,MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect=mCrime.getSuspect();
        if(suspect==null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect,suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, crimeType,suspect);

        return report;
    }

    private String getSuspectName(Intent data) {
        Uri contactUri = data.getData();

        // Specify which fields you want your query to return values for
        String[] queryFields = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        // Perform your query - the contactUri is like a "where" clause here.
        Cursor c = getActivity().getContentResolver()
                .query(contactUri, queryFields, null, null, null);

        try {
            // Double-check that you actually got results.
            if (c.getCount() == 0) {
                return null;
            }

            // Pull out the first column of the first row of data -
            // that is your suspect's name
            c.moveToFirst();

            mSuspectId = c.getString(0);
            String suspectName = c.getString(1);
            return suspectName;
        } finally {
            c.close();
        }
    }

        private String getSuspectPhoneNumber(String contactId) {
            String suspectPhoneNumber = null;

            // The content URI of the CommonDataKinds.Phone
            Uri phoneContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

            // The columns to return for each row
            String[] queryFields = new String[] {
                    ContactsContract.Data.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,   // which is the default phone number.
                    ContactsContract.CommonDataKinds.Phone.TYPE,
            };

            // Selection criteria
            String mSelectionClause = ContactsContract.Data.CONTACT_ID + " = ?";

            // Selection criteria
            String[] mSelectionArgs = {contactId};


            // Does a query against the table and returns a Cursor object
            Cursor c = getActivity().getContentResolver()
                    .query(phoneContactUri,queryFields, mSelectionClause, mSelectionArgs, null );

            try {
                // Double-check that you actually got results.
                if (c.getCount() == 0) {
                    return null;
                }

                while (c.moveToNext()) {
                    int phoneType = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    if (phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                        suspectPhoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                        break;
                    }
                }
            } finally {
                c.close();
            }

            return suspectPhoneNumber;
        }

        private void updateSuspectPhone () {
            String suspectPhoneNumber = getSuspectPhoneNumber(mSuspectId);
            mCrime.setPhoneNumber(suspectPhoneNumber);
        }

    private boolean hasContactPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), CONTACTS_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACTS_PERMISSIONS:
                if (hasContactPermission()) {
                    updateSuspectPhone();
                }
        }
    }

    public void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhoto.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),
                    getActivity());
            mPhoto.setImageBitmap(bitmap);
        }
    }
    }

