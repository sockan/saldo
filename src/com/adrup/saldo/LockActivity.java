package com.adrup.saldo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
/**
 * Main entry for application.
 * 
 * @author Sockan
 *
 */
public class LockActivity extends Activity{

	private SharedPreferences mPrefs;
	private final static String TAG = "Protect";
	private final static long LOCK_TIME = 3000;
	private EditText et;

	
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		final Context ctx=this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lock);
		if(!lock(ctx)){
			finish();
		}
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		et = (EditText)findViewById(R.id.edit_pincode);
		Button btn = (Button)findViewById(R.id.button_pincode);
		btn.setOnClickListener(new OnClickListener(){
			
				@Override
				public void onClick(View v) {
					if(et.getText().toString().equals(mPrefs.getString(Constants.LOCK_PASSWORD, ""))){
						lockTime(ctx);
						Log.d(TAG, "Password OK");
						finish();
					}else{
						et.setText("");
						Log.d(TAG, "Password BAD");
					}				
			}});
			
	}
	
	
	public static void lockTime(Context ctx){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		if(sp.getBoolean(Constants.LOCK_PROTECT, new Boolean(false))){
			sp.edit().putLong(Constants.LOCK_TIME, System.currentTimeMillis()).commit();
		}
	}
	
	public static boolean lock(Context ctx){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		long mTime  = sp.getLong(Constants.LOCK_TIME, 0); 
		Log.d(TAG,(System.currentTimeMillis() - mTime) +  "  " + ((System.currentTimeMillis() - mTime) > LOCK_TIME));
		if(sp.getBoolean(Constants.LOCK_PROTECT, new Boolean(false)) && ((System.currentTimeMillis() - mTime) > LOCK_TIME)){
			return true;
		}
		return false;
	}
		
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
}
