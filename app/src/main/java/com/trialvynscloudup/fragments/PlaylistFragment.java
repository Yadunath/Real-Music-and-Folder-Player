package com.trialvynscloudup.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.adapter.ArtistListCursorAdapter;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaylistFragment extends Fragment {
    RecyclerView mRecyclerView;
    private LinearLayoutManager mlayoutManager;
    private Cursor mCursor;

    public PlaylistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmenfrt
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = (Toolbar)view. findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab =  ((MainActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerViewList);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mlayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            LauncherApplication.getInstance().trackScreenView("Playlist Fragment");
        }
        mCursor=getActivity().managedQuery(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,null,null,null,null);
        ArtistListCursorAdapter listCursorAdapter=new ArtistListCursorAdapter(getActivity(),mCursor,3);
        mRecyclerView.setAdapter(listCursorAdapter);

    }
    @Override
    public void onResume() {
        super.onResume();
        CommonUtility commonUtility=new CommonUtility();
        commonUtility.setCurrentFragmentId(1);

    }
}
