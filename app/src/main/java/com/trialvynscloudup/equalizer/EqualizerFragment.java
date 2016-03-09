package com.trialvynscloudup.equalizer;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.trialvynscloudup.MainActivity;
import com.trialvynscloudup.R;
import com.trialvynscloudup.utilities.CommonUtility;
import com.trialvynscloudup.utilities.LauncherApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A simple {@link Fragment} subclass.
 */
public class EqualizerFragment extends Fragment implements SeekBar.OnSeekBarChangeListener,Switch.OnCheckedChangeListener ,
Spinner.OnItemSelectedListener{

    private String TAG=EqualizerFragment.class.getName();
    SeekBar bass_boost = null;
    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 6; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    int num_sliders = 0;
    
    Switch actionBarSwitch;
    
    private Spinner mSpinner;
    private List<String > presetList;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    public EqualizerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equalizer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        Toolbar toolbar = (Toolbar)view. findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        final ActionBar ab =  ((MainActivity) getActivity()).getSupportActionBar();
        ab.setCustomView(R.layout.equaliser_switch);
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.action_back_button);
        actionBarSwitch=(Switch)view.findViewById(R.id.switchForActionBar);
        actionBarSwitch.setOnCheckedChangeListener(this);

        bass_boost = (SeekBar)view.findViewById(R.id.seekBar7);
        bass_boost.setOnSeekBarChangeListener(this);
        
        mSpinner=(Spinner)view.findViewById(R.id.spinner);        
        mSpinner.setOnItemSelectedListener(this);
        
        sliders[0] = (SeekBar)view.findViewById(R.id.seekBar2);
        
        sliders[1] = (SeekBar)view.findViewById(R.id.seekBar3);
        
        sliders[2] = (SeekBar)view.findViewById(R.id.seekBar4);
        
        sliders[3] = (SeekBar)view.findViewById(R.id.seekBar5);
        
        sliders[4] = (SeekBar)view.findViewById(R.id.seekBar6);
        
        sliders[5] = (SeekBar)view.findViewById(R.id.seekBar2);

        sharedPreferences=getActivity().getSharedPreferences("EQUALIZER", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        

        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presetList=new ArrayList<>();
        addPresetsToList();
        
        ArrayAdapter<String > spinnerAdapter=new ArrayAdapter<String>(getActivity(),R.layout.spinner_item,R.id.textView13,presetList);
        mSpinner.setAdapter(spinnerAdapter);
        eq = new Equalizer (0, 0);
        if (eq != null)
        {
//            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            
            num_sliders = 5;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
//                sliders[i].setEnabled(false);
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
//            sliders[i].setVisibility(View.GONE);
            
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            
        }
        int equalizerPosition=sharedPreferences.getInt("EqualizerType",0);
        mSpinner.setSelection(equalizerPosition);
        setEqualizer(equalizerPosition);
        updateUI();
        trackScreen();
    }

    @Override
    public void onPause() {
        super.onPause();
        
    }
    
    @Override
    public void onStop() {
        super.onStop();
        if (mSpinner.getSelectedItemPosition()==9)
        {
            saveCustom();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int level, boolean userTouch) {

        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            if (userTouch)
            {
                mSpinner.setSelection(9);
            }
            int new_level = min_level + (max_level - min_level) * level / 100;
            
            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        actionBarSwitch.setChecked(eq.getEnabled());
//        enabled.setChecked (eq.getEnabled());
    }

    /*=============================================================================
        formatBandLabel 
    =============================================================================*/
    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    /*=============================================================================
        milliHzToString 
    =============================================================================*/
    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
        updateSliders 
    =============================================================================*/
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }



    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
       eq.setEnabled(isChecked);
        trackEvent("equalizer turnOn");
    }
    
    public void addPresetsToList()
    {
        presetList.add("Normal");
        presetList.add("Classical");
        presetList.add("Dance");
        presetList.add("Flat");
        presetList.add("Folk");
        presetList.add("Heavy Metal");
        presetList.add("Hip Hop");
        presetList.add("Jazz");
        presetList.add("Pop");
        presetList.add("Custom");
//        presetList.add("Rock");
        
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        setEqualizer(position);
        editor.putInt("EqualizerType", position);
        editor.commit();
        
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
       
                eq.setBandLevel((short) i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }
    
    public void setEqualizer(int position)
    {
        
        int [] freqArray=new int[]{50,50,50,50,50};
        switch (position)
        {
            case 0:
//                Normal
                freqArray=new int[]{65,50,50,50,60};
                break;
            case 1:
//                classical
                freqArray=new int[]{75,70,40,60,70};
                break;
            case 2:
//                dance
                freqArray=new int[]{75,50,60,60,50};
                break;
            case 3:
//                flat
                freqArray=new int[]{50,50,50,50,50};
                break;
            case 4:
//                folk
                freqArray=new int[]{65,50,50,55,45};
                break;
            case 5:
//                heavy metal
                freqArray=new int[]{70,50,75,70,48};
                break;
            case 6:                                                                                                        
//                hip hop
                freqArray=new int[]{70,70,50,55,60};
                break;
            case 7:
//                jazz
                freqArray=new int[]{68,60,40,75,75};
                break;
            case 8:
//                pop
                freqArray=new int[]{45,60,60,55,40};
                break;
            case 9:
//                custom
                loadCustomArray();
                break;
                
        }
        if (eq != null)
        {
            
            for (int i = 0; i < num_sliders; i++)
            {
                sliders[i].setProgress(freqArray[i]);
            }

        }
    }
    public void saveCustom()
    {
        int[] customfreqArray=new int[5];
        for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
        {
            int progress=sliders[i].getProgress();
            customfreqArray[i]=progress;
        }
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < customfreqArray.length; i++) {
            str.append(customfreqArray[i]).append(",");
        }
        editor.putString("customequilizer", str.toString()); 
        editor.commit();
        
    }
    public void loadCustomArray()
    {
        String savedString = sharedPreferences.getString("customequilizer", "50,50,50,50,50");
        StringTokenizer st = new StringTokenizer(savedString, ",");
        

        int[] savedList = new int[5];
        for (int i = 0; i < 5; i++) {
            savedList[i] = Integer.parseInt(st.nextToken());
        }
        trackEvent("equalizer custom ");
    }
    public void trackScreen()
    {
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            LauncherApplication.getInstance().trackScreenView("Equalizer Fragment");
        }
    }
    public void trackEvent(String title)
    {
        
        if (CommonUtility.developementStatus)
        {

        }
        else
        {
            String deviceName = Build.BRAND+" "+Build.MODEL;

            LauncherApplication.getInstance().trackEvent("equalizer", title, deviceName);
        }
    }
}

