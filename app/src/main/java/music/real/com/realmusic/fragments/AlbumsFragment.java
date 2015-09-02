package music.real.com.realmusic.fragments;


import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import music.real.com.realmusic.R;
import music.real.com.realmusic.recycler.GridCursorAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumsFragment extends Fragment {
    Cursor cursor;
    RecyclerView gridView;
    private GridLayoutManager mLayoutManager;
    public AlbumsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_albums, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
      gridView=(RecyclerView)view.findViewById(R.id.gridView);
        gridView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        gridView.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     cursor=getActivity().managedQuery(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,null,null,null, MediaStore.Audio.Media.ALBUM);
        String[] from=new String[] {MediaStore.Audio.Media.ALBUM};
        int[] to =new int []{android.R.id.text1};
//        GridAlbumAdapter albumAdapter=new GridAlbumAdapter(getActivity(),android.R.layout.simple_gallery_item,cursor,from,to);
//        gridView.setAdapter(albumAdapter);
        GridCursorAdapter adapter=new GridCursorAdapter(getActivity(),cursor);
        gridView.setAdapter(adapter);
    }
}
