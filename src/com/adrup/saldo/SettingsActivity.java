package com.adrup.saldo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

public class SettingsActivity extends Activity {
	private static final String TAG = "SettingsActivity";
	
	private Spinner mSpinner;
	private CheckBox mNotifyCheckBox;
	private CheckBox mSoundCheckBox;
	private SharedPreferences mPrefs;
	private CheckedTextView ctv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate()");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		ctv = (CheckedTextView) findViewById(R.id.password_lock);
		ctv.setOnClickListener(new OnClickListener () {

			@Override
			public void onClick(View v) {
				if(((CheckedTextView)v).isChecked()){
					mPrefs.edit().putBoolean(Constants.LOCK_PROTECT, new Boolean(false)).commit();
				}else{
					showPasswordDialog();
				}
				
			}});
		
		
		mSpinner = (Spinner) findViewById(R.id.settings_autoupdate_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.update_interval_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		if(mPrefs.getBoolean(Constants.LOCK_PROTECT, new Boolean(false))){
			ctv.setChecked(true);
		}
		mNotifyCheckBox = (CheckBox) findViewById(R.id.settings_notifications_enable_checkbox);
		mSoundCheckBox = (CheckBox) findViewById(R.id.settings_notifications_sound_checkbox);
	}


	@Override
	protected void onResume() {
		Log.d(TAG, "onResume()");
		super.onResume();
		if(LockActivity.lock(this)){
			startActivity(new Intent(this, LockActivity.class));
		}
		mSpinner.setSelection(mPrefs.getInt(Constants.PREF_UPDATE_INTERVAL, 0));
		mNotifyCheckBox.setChecked(mPrefs.getBoolean(Constants.PREF_NOTIFY, true));
		mSoundCheckBox.setChecked(mPrefs.getBoolean(Constants.PREF_SOUND, true));
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause()");
		super.onPause();
		if(!LockActivity.lock(this)){
			LockActivity.lockTime(this);
		}
		int interval = mSpinner.getSelectedItemPosition();
		int prevInterval = mPrefs.getInt(Constants.PREF_UPDATE_INTERVAL, 0);
		if (interval != prevInterval) {
			mPrefs.edit().putInt(Constants.PREF_UPDATE_INTERVAL, interval).commit();
			AutoUpdateReceiver.setAlarm(this);
		}
		mPrefs.edit().putBoolean(Constants.PREF_NOTIFY, mNotifyCheckBox.isChecked()).commit();
		mPrefs.edit().putBoolean(Constants.PREF_SOUND, mSoundCheckBox.isChecked()).commit();
	}
	protected void showPasswordDialog(){
		Builder builder = new AlertDialog.Builder(this);
		FrameLayout fl = new FrameLayout(this);
		final EditText input = new EditText(this);
		fl.addView(input);
		builder.setView(fl);
		builder.setTitle("Choose pincode");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	          @Override
	          public void onClick(DialogInterface d, int which) {
	               d.dismiss();
	               mPrefs.edit().putBoolean(Constants.LOCK_PROTECT, new Boolean(true)).commit();
	               mPrefs.edit().putString(Constants.LOCK_PASSWORD, input.getText().toString()).commit();
	               ctv.setChecked(true);
	          }
	     });
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
	          @Override
	          public void onClick(DialogInterface d, int which) {
	               d.dismiss();
	               ctv.setChecked(false);
	          }
	     });
		builder.create();
		builder.show();
	}
}
