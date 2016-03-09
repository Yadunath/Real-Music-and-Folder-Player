package com.trialvynscloudup.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.trialvynscloudup.R;
import com.trialvynscloudup.adapter.TrackListCursorAdapter;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.UiUpdater;



/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends Fragment  {

    private static String LOG_TAG=TracksFragment.class.getName();
    FastScrollRecyclerView mRecyclerView;

    private GridLayoutManager mlayoutManager;
    private Cursor mCursor;
    TrackListCursorAdapter listCursorAdapter;
    private Parcelable state;

    private SharedPreferences sharedPreferences;


    private int index = -1;
    private int top = -1;
    
    public TracksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        
        return inflater.inflate(R.layout.fragment_tracks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=(FastScrollRecyclerView)view.findViewById(R.id.recyclerViewList);
        
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager=new GridLayoutManager(getActivity(),1);
        mRecyclerView.setLayoutManager(mlayoutManager);
        sharedPreferences=getActivity().getSharedPreferences("SONGINFO", Context.MODE_PRIVATE);

    }
    

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String order= MediaStore.Audio.Media.TITLE;

        mCursor=getActivity().managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,order+" COLLATE NOCASE ASC; ");

        listCursorAdapter=new TrackListCursorAdapter(getActivity(),mCursor,0,"");
        mRecyclerView.setAdapter(listCursorAdapter);
    }
    @Override
    public void onResume() {
        
        super.onResume();
        CommonUtility commonUtility=new CommonUtility();
        commonUtility.setCurrentFragmentId(1);
        if(index != -1)
        {
            mlayoutManager.scrollToPositionWithOffset( index, top);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        index = mlayoutManager.findFirstVisibleItemPosition();
        View v = mRecyclerView.getChildAt(0);
        top = (v == null) ? 0 : (v.getTop() - mRecyclerView.getPaddingTop());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        index=-1;
        top=-1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        
    }
}
