package com.trialvynscloudup.fragments_folder_test;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.trialvynscloudup.R;
import com.trialvynscloudup.activities.PlayBackActivity;
import com.trialvynscloudup.fragments_folder.FoldersListViewAdapter;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileChooser extends Fragment implements AdapterView.OnItemClickListener {

	private String TAG="Filechooser";
	private File currentDir;
	private FileArrayAdapter adapter;
	ListView listView;
	int number;
	ArrayList<String> subFolderList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_folders,container,false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (CommonUtility.developementStatus)
		{

		}
		else
		{
			LauncherApplication.getInstance().trackScreenView("Files Fragment");
		}

		listView=(ListView)view.findViewById(R.id.folders_list_view);
		currentDir = new File("/storage/");

		fill(currentDir);
	}


	private void fill(File f)
	{

		number=0;
		subFolderList=new ArrayList<>();
		File[]dirs = f.listFiles();

//		this.setTitle("Current Dir: " + f.getName());
		List<Item>dir = new ArrayList<Item>();
		List<Item>fls = new ArrayList<Item>();
		try{
			for(File fileName: dirs)
			{
				Date lastModDate = new Date(fileName.lastModified());
				DateFormat formater = DateFormat.getDateTimeInstance();
				String date_modify = formater.format(lastModDate);
				if(fileName.isDirectory()){

					boolean isAnyMusic=false;
					File[] fbuf = fileName.listFiles();

					/*		adding root folder to list*/
					if (fileName.getParent().equalsIgnoreCase("/storage")
							||fileName.getParent().equalsIgnoreCase("/storage/emulated"))
					{
						isAnyMusic = true;

					}
					else {
						/*			Check whether folder contains any music if no it will be neglected from list*/
						for (File folderContent : fbuf) {
							if (getFileExtension(folderContent.getAbsolutePath()).equalsIgnoreCase("mp3")) {
								isAnyMusic = true;
								break;

							} else {
								isAnyMusic = false;
							}
						}
					}

					int buf = 0;
					if(fbuf != null){
						buf = fbuf.length;
					}
					else buf = 0;
					String num_item = String.valueOf(buf);
					if(buf == 0)
					{

						num_item = num_item + " item";
					}

					else
					{
						/*		adding folders	*/
						if (isAnyMusic)
						{
							num_item = num_item + " items";
							dir.add(new Item(fileName.getName(),num_item,date_modify,fileName.getAbsolutePath(),"directory_icon"));
							number++;
							subFolderList.add(fileName.getAbsolutePath());
						}


					}
					//String formated = lastModDate.toString();
				}
				else
				{
					/*		Adding music files only mp3 */
					if (getFileExtension(fileName.getAbsolutePath()).equalsIgnoreCase("mp3") )
					{
						fls.add(new Item(fileName.getName(), fileName.length() + " Byte", date_modify, fileName.getAbsolutePath(), "file_icon"));

					}
				}
			}
		}catch(Exception e)
		{

		}

		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));
		adapter = new FileArrayAdapter(getActivity(), R.layout.file_view,dir);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
		Item o = adapter.getItem(position);


		/*		check whether item is directory or back button	*/
		if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
			if (currentDir!=null)
				currentDir = new File(o.getPath());
			/*	check whether its in root folder*/
			if (currentDir.getName().equalsIgnoreCase(""))
			{

			}
			else
			{
				/*	changing internal storage directory to absolute internal storage path directory	*/
				if (currentDir.getAbsolutePath().equalsIgnoreCase("/storage/sdcard0")||currentDir.getAbsolutePath().equalsIgnoreCase("/storage/emulated/legacy"))
				{
					currentDir=new File("/storage/emulated/0");
				}
				fill(currentDir);

			}

		} else {
			play(1, position - 1-number, o.getPath().replace(o.getName(), ""));

		}
		if (CommonUtility.developementStatus)
		{

		}
		else
		{
			String deviceName = Build.BRAND+" "+Build.MODEL;

			LauncherApplication.getInstance().trackEvent(o.getPath(), o.getName(),deviceName );
		}

	}

	public void play(int itemType, int index, String folderPath) {
		//Build the query's selection clause.
		String querySelection = MediaStore.Audio.Media.DATA + " LIKE "
				+ "'" + folderPath.replace("'", "''") + "/%'";

/*
		//Exclude all subfolders from this playback sequence if we're playing a file.
		if (itemType==AUDIO_FILE) {
			for (int i = 0; i < fileFolderPathList.size(); i++) {
				if (fileFolderTypeList.get(i) == FOLDER)
				{
					querySelection += " AND " + MediaStore.Audio.Media.DATA + " NOT LIKE "
							+ "'" + fileFolderPathList.get(i).replace("'", "''") + "/%'";

					CommonUtility commonUtility=new CommonUtility();
					commonUtility.setSubFolderName(fileFolderPathList.get(i));

				}

//                Log.v("falder",""+fileFolderPathList.get(i).replace("'", "''"));
			}

		}
		else {
		}*/
		CommonUtility commonUtility=new CommonUtility();
//		commonUtility.setSubFolderName(subFolderList.get(0));
		commonUtility.setSubFolderList(subFolderList);
		Intent intent=new Intent(getActivity(), PlayBackActivity.class);
		intent.putExtra("position",index);
		intent.putExtra("type",5);
		intent.putExtra("playlistid", folderPath);
		startActivity(intent);
	}
	public String getFileExtension(String fileName) {
		String fileNameArray[] = fileName.split("\\.");
		String extension = fileNameArray[fileNameArray.length-1];

		return extension;

	}


}
