/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.trialvynscloudup.fragments_folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.trialvynscloudup.R;
import com.trialvynscloudup.fragments_folder_test.FileChooser;

import java.io.File;
import java.util.List;


public class FoldersListViewAdapter extends ArrayAdapter<String> {

	private Context mContext;
    private FilesFoldersFragment mFragment;
    FileChooser mFragment1;
    private int mItemType;
    private String mItemPath;
    private int mItemPosition;

	private List<String> mFileFolderNameList;
	private List<Integer> mFileFolderTypeList;
	private List<String> mFileFolderSizeList;
	private List<String> mFileFolderPathsList;
   
    public FoldersListViewAdapter(Context context,
                                  FilesFoldersFragment fragment,
    							  List<String> nameList, 
    							  List<Integer> fileFolderTypeList,
    							  List<String> sizeList, 
    							  List<String> fileFolderPathsList) {
    	
    	super(context, -1, nameList);
    	
    	mContext = context;
        mFragment = fragment;

    	mFileFolderNameList = nameList;
    	mFileFolderTypeList = fileFolderTypeList;
    	mFileFolderSizeList = sizeList;
    	mFileFolderPathsList = fileFolderPathsList;
    	
    }

    @Override
    public int getCount() {

        return super.getCount();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	FoldersViewHolder holder = null;
		if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_view_item, parent, false);
            ListView.LayoutParams params = (ListView.LayoutParams) convertView.getLayoutParams();
            params.height = 250;
            convertView.setLayoutParams(params);

			holder = new FoldersViewHolder();
			holder.fileFolderIcon = (ImageView) convertView.findViewById(R.id.listViewLeftIcon);
			holder.fileFolderSizeText = (TextView) convertView.findViewById(R.id.listViewSubText);
			holder.fileFolderNameText = (TextView) convertView.findViewById(R.id.listViewTitleText);
            holder.overflowButton = (ImageButton) convertView.findViewById(R.id.listViewOverflow);
            holder.rightSubText = (TextView) convertView.findViewById(R.id.listViewRightSubText);

            holder.fileFolderIcon.setScaleX(0.5f);
            holder.fileFolderIcon.setScaleY(0.55f);
            holder.rightSubText.setVisibility(View.INVISIBLE);


            holder.overflowButton.setFocusable(false);
            holder.overflowButton.setFocusableInTouchMode(false);
			holder.overflowButton.setOnClickListener(overflowClickListener);

			convertView.setTag(holder);
		} else {
		    holder = (FoldersViewHolder) convertView.getTag();
		}
		
		holder.fileFolderNameText.setText(mFileFolderNameList.get(position));
		holder.fileFolderSizeText.setText(mFileFolderSizeList.get(position));
		
		//Set the icon based on whether the item is a folder or a file.
		if (mFileFolderTypeList.get(position)==FilesFoldersFragment.FOLDER) {
			holder.fileFolderIcon.setImageResource(R.drawable.new_folder_icon);
			convertView.setTag(R.string.folder_list_item_type, FilesFoldersFragment.FOLDER);


		} else if (mFileFolderTypeList.get(position)==FilesFoldersFragment.AUDIO_FILE) {
			holder.fileFolderIcon.setImageResource(R.drawable.icon_folderblue);
			convertView.setTag(R.string.folder_list_item_type, FilesFoldersFragment.AUDIO_FILE);


		} else if (mFileFolderTypeList.get(position)==FilesFoldersFragment.PICTURE_FILE) {
			holder.fileFolderIcon.setImageResource(R.drawable.icon_folderblue);
			convertView.setTag(R.string.folder_list_item_type, FilesFoldersFragment.PICTURE_FILE);


		} else if (mFileFolderTypeList.get(position)==FilesFoldersFragment.VIDEO_FILE) {
			holder.fileFolderIcon.setImageResource(R.drawable.icon_folderblue);
			convertView.setTag(R.string.folder_list_item_type, FilesFoldersFragment.VIDEO_FILE);


		} else {
			holder.fileFolderIcon.setImageResource(R.drawable.icon_folderblue);
			convertView.setTag(R.string.folder_list_item_type, FilesFoldersFragment.FILE);


		}
    	
    	return convertView;
	}

    /**
     * Click listener for overflow button.
     */
    private View.OnClickListener overflowClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            PopupMenu menu = new PopupMenu(mContext, v);
            menu.inflate(R.menu.file_folder_overflow_menu);
            menu.setOnMenuItemClickListener(popupMenuItemClickListener);
            mItemType = (Integer) ((View) v.getParent()).getTag(R.string.folder_list_item_type);

            menu.show();

        }

    };

    /**
     * Menu item click listener for the pop up menu.
     */
    private PopupMenu.OnMenuItemClickListener popupMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch(item.getItemId()) {
                case R.id.play:
                    int fileIndex;
                    String folderPath;
                    if (mItemType==FilesFoldersFragment.AUDIO_FILE) {
                        fileIndex = 0;
                        folderPath = FilesFoldersFragment.currentDir;
                        for (int i=0; i < mItemPosition; i++) {
                            if (mFileFolderTypeList.get(i)==FilesFoldersFragment.AUDIO_FILE)
                                fileIndex++;
                        }


                    } else {
                        fileIndex = 0;
                        folderPath = mItemPath;
                    }

                    mFragment.play(mItemType, fileIndex, folderPath);
                    break;
                case R.id.rename:
                    mFragment.rename(mItemPath);
                    break;
                case R.id.copy:
                    mFragment.copyMove(mItemPath, false);
                    break;
                case R.id.move:
                    mFragment.copyMove(mItemPath, true);
                    break;
                case R.id.delete:
                    mFragment.deleteFile(new File(mItemPath));
                    break;
            }

            return false;
        }

    };

    static class FoldersViewHolder {
    	public TextView fileFolderNameText;
    	public TextView fileFolderSizeText;
    	public ImageView fileFolderIcon;
        public ImageButton overflowButton;
        public TextView rightSubText;
    }
   
}
