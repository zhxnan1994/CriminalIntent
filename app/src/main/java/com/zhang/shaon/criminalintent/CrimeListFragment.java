package com.zhang.shaon.criminalintent;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhang on 2017-10-30.
 */

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeAdapter;
    private boolean mSubtitleVisible;
    private int mPosition;
    private static final int REQUEST_CRIME=1;
    private static final int REQUEST_DELETE=3;
    private Callbacks mCallbacks;
    private OnDeleteCrimeListener mDeleteCallBacks;
    private ItemTouchHelper mItemTouchHelper;
    private static final String EXTRA_POSITION = "com.zhang.shaon.criminalintent.position";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private ConstraintLayout mEmptyView;
    private Button mNewCrimeButton;




    public interface Callbacks{
        void onCrimeSelected(Crime crime,int position);
    }

    public interface OnDeleteCrimeListener{
        void onCrimeDelete(Crime crime);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks=(Callbacks) context;
        mDeleteCallBacks=(OnDeleteCrimeListener) context;
    }

    public void onDetach() {

        super.onDetach();
        mCallbacks=null;
        mDeleteCallBacks=null;
    }

    public enum ITEM_TYPE{
        ITEM_CRIME,
        ITEM_REQUIRE_POLICE
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = v.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        mEmptyView = (ConstraintLayout) v.findViewById(R.id.empty_list_layout);

        mNewCrimeButton = (Button) v.findViewById(R.id.add_crime_button);
        mNewCrimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCrime();
            }
        });
        initItemTouchHelper();
        return v;
    }

    private void initItemTouchHelper() {
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {


            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if(direction==ItemTouchHelper.END){
                    final int pos = viewHolder.getAdapterPosition();
                    Crime crime = mCrimeAdapter.mCrimes.get(pos);
                    if(getResources().getBoolean(R.bool.large_layout)){
                        mDeleteCallBacks.onCrimeDelete(crime);
                    }else{
                        deleteCrime(crime);//防止小屏右滑删除闪退
                    }
                    updateUI();
                    mCrimeAdapter.notifyItemRemoved(pos);

                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);
    }

    public void updateUI(){
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes=crimeLab.getCrimeList();
        mEmptyView.setVisibility(View.VISIBLE);
        if (crimes.size() > 0 ) {
            mEmptyView.setVisibility(View.INVISIBLE);
        }
        if(mCrimeAdapter==null){
            mCrimeAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeAdapter);
            initItemTouchHelper();
        }else{
            mCrimeAdapter.setCrimes(crimes);
            mCrimeRecyclerView.setHasFixedSize(true);
            if(mPosition>=0){
                mCrimeAdapter.notifyItemChanged(mPosition);
            }
            else{
                mCrimeAdapter.notifyDataSetChanged();
            }
        updateSubtitle();
        }


    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCrimeSolved;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_crime,parent,false));
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCrimeSolved = itemView.findViewById(R.id.crime_solved);
            itemView.setOnClickListener(this);
        }
        public void bind(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDateShow());
            mCrimeSolved.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);

        }
        @Override
        public void onClick(View v) {
            /*Toast.makeText(getActivity(),
                    mCrime.getTitle()+" Clicked!",Toast.LENGTH_SHORT)
                    .show();*/
            /*Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);*/
            mPosition=this.getAdapterPosition();
            /*Intent intent = CrimePageActivity.newIntent(getActivity(),mCrime.getId());
            intent.putExtra(EXTRA_POSITION,mPosition);
            startActivity(intent);*/
            mCallbacks.onCrimeSelected(mCrime,mPosition);

            //startActivityForResult(intent,REQUEST_CRIME);
        }



    }

    private class PoliceCrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCrimeSolved;
        private Crime mCrime;

        public PoliceCrimeHolder(LayoutInflater inflater,ViewGroup parent) {
            super(inflater.inflate(R.layout.list_require_police,parent,false));
            mTitleTextView = itemView.findViewById(R.id.crime_title);
            mDateTextView = itemView.findViewById(R.id.crime_date);
            mCrimeSolved = itemView.findViewById(R.id.police_crime_solved);
            itemView.setOnClickListener(this);
        }
        public void bind(Crime crime){
            mCrime=crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDateShow());
            mCrimeSolved.setVisibility(crime.isSolved()?View.VISIBLE:View.GONE);

        }

        @Override
        public void onClick(View v) {
            /*Toast.makeText(getActivity(),
                    mCrime.getTitle()+" Clicked!",Toast.LENGTH_SHORT)
                    .show();*/
            /*Intent intent = CrimeActivity.newIntent(getActivity(), mCrime.getId());
            startActivity(intent);*/

            mPosition=this.getAdapterPosition();
            /*Intent intent = CrimePageActivity.newIntent(getActivity(),mCrime.getId());
            intent.putExtra(EXTRA_POSITION,mPosition);
            startActivity(intent);*/
            mCallbacks.onCrimeSelected(mCrime,mPosition);
            /*String s=getActivity().getIntent().getSerializableExtra(CrimeListActivity.REQUEST_DELETE).toString();
            Log.d("onClick", "requestcode" + s);*/
            //startActivityForResult(intent,REQUEST_CRIME);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==REQUEST_CRIME){
            if(resultCode== Activity.RESULT_OK){
                updateUI();
            }
        }else if(requestCode==REQUEST_DELETE){
            if (data == null) {
                return;
            }

            UUID crimeId = (UUID) data.getSerializableExtra(CrimePageActivity.EXTRA_CRIME_ID);
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            Crime crime = crimeLab.getCrime(crimeId);
            crimeLab.delteCrime(crime);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes){
            mCrimes=crimes;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if(viewType==ITEM_TYPE.ITEM_REQUIRE_POLICE.ordinal()){
                return new PoliceCrimeHolder(layoutInflater, parent);
            }else{
                return new CrimeHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            if(holder instanceof CrimeHolder){
                ((CrimeHolder) holder).bind(crime);
            }else if(holder instanceof PoliceCrimeHolder){
                ((PoliceCrimeHolder) holder).bind(crime);
            }
        }

        @Override
        public int getItemViewType(int position) {
            Crime crime = mCrimes.get(position);
            if(!crime.isRequirePolice())
            {return ITEM_TYPE.ITEM_CRIME.ordinal();}
            else
            {return ITEM_TYPE.ITEM_REQUIRE_POLICE.ordinal();}
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes=crimes;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);

        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_crime:
                addCrime();
                updateUI();
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible=!mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        //return super.onOptionsItemSelected(item);
    }

    private void addCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent intent = CrimePageActivity.newIntent(getActivity(), crime.getId(),getPosition());
        startActivity(intent);
    }

    public void deleteCrime(Crime crime){
        CrimeLab.get(getActivity()).delteCrime(crime);
    }

    public int getPosition(){
        List<Crime> mList = CrimeLab.get(getActivity()).getCrimeList();
        if(mList.isEmpty()){
            return 0;
        }else{
            return mList.size();
        }
    }

    public void updateSubtitle(){
        int crimeSiza=CrimeLab.get(getActivity()).getCrimeList().size();

        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeSiza, crimeSiza);
        if (!mSubtitleVisible) {
            subtitle=null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(subtitle);
    }

}
