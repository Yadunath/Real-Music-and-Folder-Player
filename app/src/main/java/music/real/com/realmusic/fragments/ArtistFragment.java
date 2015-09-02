package music.real.com.realmusic.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import music.real.com.realmusic.adapter.ArtistListCursorAdapter;
import music.real.com.realmusic.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtistFragment extends Fragment {
    RecyclerView mRecyclerView;
    private LinearLayoutManager mlayoutManager;
    private Cursor mCursor;

    public ArtistFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerViewList);
        mRecyclerView.setHasFixedSize(true);
        mlayoutManager=new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mlayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCursor=getActivity().managedQuery(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,null,null,null,null);
        ArtistListCursorAdapter listCursorAdapter=new ArtistListCursorAdapter(getActivity(),mCursor,0);
        mRecyclerView.setAdapter(listCursorAdapter);

    }
}
