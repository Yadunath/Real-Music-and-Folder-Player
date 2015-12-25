package com.trialvynscloudup.dialogues;


import android.app.Dialog;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.trialvynscloudup.R;


public class Custom_2_dialog extends Dialog implements View.OnClickListener {
    
   

	Context mContext;  
    TextView mTitle = null;  
    TextView mMessage = null;
    TextView mMessage1 = null;
    TextView mMessage2 = null;  
    TextView mMessage3 = null;  
    TextView mMessage4 = null;  
    ListView mlist,plist=null;

    View v = null;  
    public Custom_2_dialog(Context context) {  
        super(context);  
        mContext = context;  
        /** 'Window.FEATURE_NO_TITLE' - Used to hide the mTitle */  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        /** Design the dialog in main.xml file */  
        setContentView(R.layout.dialog_4);
        v = getWindow().getDecorView();  
        v.setBackgroundResource(android.R.color.transparent);  
        mTitle = (TextView) findViewById(R.id.textView1);  
        plist=(ListView)findViewById(R.id.listView2);
        mlist=(ListView)findViewById(R.id.listView1);
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
    public void setMessage1(CharSequence message) {  
        mMessage1.setText(message);  
        mMessage1.setMovementMethod(ScrollingMovementMethod.getInstance());  
    } 
    public void setMessage1(int messageId) {  
        mMessage1.setText(mContext.getResources().getString(messageId));  
        mMessage1.setMovementMethod(ScrollingMovementMethod.getInstance());  
    }
    
    
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}  
}  