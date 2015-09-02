package music.real.com.realmusic.recycler;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;

import music.real.com.realmusic.MainActivity;
import music.real.com.realmusic.R;

/**
 * Created by skyfishjy on 10/31/14.
 */
public class GridCursorAdapter extends CursorRecyclerViewAdapter<GridCursorAdapter.ViewHolder>{

    public  Context context;

    public GridCursorAdapter(Context context, Cursor cursor){
       super(context,cursor);
        this.context=context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public int mWidth;
        public int mHeight;
        View mView;
        public String albumId;
        Uri imageUri;

        public ViewHolder(View view) {
            super(view);
            DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
            mWidth = ((metrics.widthPixels)/2)-10;
            mHeight = mWidth ;
            mTextView=(TextView)view.findViewById(R.id.gridViewTitleText);
            mImageView=(ImageView)view.findViewById(R.id.imageView);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mImageView.getLayoutParams();
            params.width=mWidth;
            params.height=mHeight;
            mImageView.setLayoutParams(params);
            this.mView=view;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_album, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        MyListItem myListItem = MyListItem.fromCursor(cursor);
        viewHolder.mTextView.setText(myListItem.getName());
        viewHolder.albumId=myListItem.getAlbumId();
        Picasso.with(context).load(myListItem.getUri()).into(viewHolder.mImageView);
        viewHolder.imageUri=myListItem.getUri();

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
//                Fragment fragment=new TestFragment();
                switchFragment(viewHolder.imageUri,viewHolder.albumId);
            }
        });
    }
    public void switchFragment(Uri  imageUri,String position)
    {
        MainActivity mainActivity=(MainActivity)context;
        Log.v("Grdcursor",""+imageUri);
        mainActivity.albumClick(1,imageUri,position);

    }

}