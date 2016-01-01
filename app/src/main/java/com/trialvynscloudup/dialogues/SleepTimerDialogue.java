package com.trialvynscloudup.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.trialvynscloudup.R;
import com.trialvynscloudup.utilities.UiUpdater;

import java.util.ArrayList;

/**
 * Created by yedunath on 25/12/15.
 */

public class SleepTimerDialogue extends Dialog implements UiUpdater.timerInterface {

    View v = null;
    Spinner spinner;
    TextView mTextField;
    Button cancelButton,startButton;
    public SleepTimerDialogue(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sleeptimer);
        v = getWindow().getDecorView();

        mTextField=(TextView)findViewById(R.id.textView6);
        UiUpdater uiUpdater=new UiUpdater();
        uiUpdater.setupdateTimerText(this);
//        v.setBackgroundResource(android.R.color.transparent);
    }


    @Override
    public void updateTimerText(String  millisUntilFinished) {

        mTextField.setText(""+millisUntilFinished);
    }
}
