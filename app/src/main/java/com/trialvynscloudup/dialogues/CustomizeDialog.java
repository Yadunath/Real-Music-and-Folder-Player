package com.trialvynscloudup.dialogues;


import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.trialvynscloudup.R;


public class CustomizeDialog extends Dialog implements View.OnClickListener {
    Button okButton;
    Button okButton1; 
    Context mContext;  
    TextView mTitle = null;  
    EditText mMessage = null;  
    View v = null;  
    public CustomizeDialog(Context context) {  
        super(context);  
        mContext = context;  
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        /** Design the dialog in main.xml file */  
        setContentView(R.layout.dialog_5);
        v = getWindow().getDecorView();  
        v.setBackgroundResource(android.R.color.transparent);  
        mTitle = (TextView) findViewById(R.id.textView1);
        mMessage = (EditText) findViewById(R.id.textView2);  
        okButton = (Button) findViewById(R.id.button1); 
        okButton1 = (Button) findViewById(R.id.button2);
        okButton1.setOnClickListener(this);
    }
    public void onClick1(View v) {  
        /** When OK Button is clicked, dismiss the dialog */  
        if (v == okButton)  
            dismiss();  
        okButton.setOnClickListener(this);  
    }  
    public void onClick(View v) {  
        /** When OK Button is clicked, dismiss the dialog */  
        if (v == okButton)  
            dismiss();  
    }  
      
    public void setTitle(CharSequence title) {  
        super.setTitle(title);  
        mTitle.setText(title);  
    }  
      
    public void setTitle(int titleId) {  
        super.setTitle(titleId);  
        mTitle.setText(mContext.getResources().getString(titleId));  
    }  
    /**  
     * Set the message text for this dialog's window.  
     *   
     * @param message  
     *      - The new message to display in the title.  
     */  
    public void setMessage(CharSequence message) {  
        mMessage.setText(message);  
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());  
    }  
    /**  
     * Set the message text for this dialog's window. The text is retrieved from the resources with the supplied  
     * identifier.  
     *   
     * @param messageId  
     *      - the message's text resource identifier <br>  
     * @see <b>Note : if resourceID wrong application may get crash.</b><br>  
     *   Exception has not handle.  
     */  
    public void setMessage(int messageId) {  
        mMessage.setText(mContext.getResources().getString(messageId));  
        mMessage.setMovementMethod(ScrollingMovementMethod.getInstance());  
    }  
}  