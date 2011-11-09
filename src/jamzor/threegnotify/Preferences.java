package jamzor.threegnotify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.method.DigitsKeyListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;

public class Preferences extends PreferenceActivity {
	/**
	 * Shared Preference Change Listener
	 */
	private OnSharedPreferenceChangeListener mPrefListener =  new OnSharedPreferenceChangeListener() {
    	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    		updatePreferences();

    		if(key == "enabled") {
	    		boolean enabled = prefs.getBoolean("enabled", true);
	    		
	    		ICheckerService checker = ((App)getApplication()).mCheckerService;
	    		if(enabled) {
	    			try {
						checker.enable();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		} else {
	    			try {
						checker.disable();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}
    		} else if(key.endsWith("_vibrate")) {
	    		ICheckerService checker = ((App)getApplication()).mCheckerService;
	    		try {
					checker.vibrate(key);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

        SharedPreferences settings = getPreferenceScreen().getSharedPreferences();
        
        settings.registerOnSharedPreferenceChangeListener(mPrefListener);
        
        Preference contactBtn = (Preference) findPreference("contact_pref");
        contactBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:faultexceptionapps@gmail.com"));
				Preferences.this.startActivity(intent);
	    		
				return true;
			}
        });

        Preference blogBtn = (Preference) findPreference("blog_pref");
        blogBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://faultexception.wordpress.com"));
				Preferences.this.startActivity(intent);
	    		
				return true;
			}
        });

        Preference resetBtn = (Preference) findPreference("reset_pref");
        resetBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				resetPreferences();
				
				return true;
			}
        });
        
        updatePreferences();
    }
    
    private void updatePreferences() {
        ListPreference mobile_acquired_vibrate = (ListPreference) findPreference("3g_acquired_vibrate");
        if(mobile_acquired_vibrate.getValue().equals("none"))
        	mobile_acquired_vibrate.setSummary(mobile_acquired_vibrate.getEntry());
        else
        	mobile_acquired_vibrate.setSummary("Pattern: " + mobile_acquired_vibrate.getEntry());

        ListPreference mobile_lost_vibrate = (ListPreference) findPreference("3g_lost_vibrate");
        if(mobile_lost_vibrate.getValue().equals("none"))
        	mobile_lost_vibrate.setSummary(mobile_lost_vibrate.getEntry());
        else
        	mobile_lost_vibrate.setSummary("Pattern: " + mobile_lost_vibrate.getEntry());

        ListPreference wifi_acquired_vibrate = (ListPreference) findPreference("wifi_acquired_vibrate");
        if(wifi_acquired_vibrate.getValue().equals("none"))
        	wifi_acquired_vibrate.setSummary(wifi_acquired_vibrate.getEntry());
        else
        	wifi_acquired_vibrate.setSummary("Pattern: " + wifi_acquired_vibrate.getEntry());

        ListPreference wifi_lost_vibrate = (ListPreference) findPreference("wifi_lost_vibrate");
        if(wifi_lost_vibrate.getValue().equals("none"))
        	wifi_lost_vibrate.setSummary(wifi_lost_vibrate.getEntry());
        else
        	wifi_lost_vibrate.setSummary("Pattern: " + wifi_lost_vibrate.getEntry());

        ListPreference roaming_vibrate = (ListPreference) findPreference("roaming_vibrate");
        if(roaming_vibrate.getValue().equals("none"))
        	roaming_vibrate.setSummary(roaming_vibrate.getEntry());
        else
        	roaming_vibrate.setSummary("Pattern: " + roaming_vibrate.getEntry());

        ListPreference not_roaming_vibrate = (ListPreference) findPreference("not_roaming_vibrate");
        if(not_roaming_vibrate.getValue().equals("none"))
        	not_roaming_vibrate.setSummary(not_roaming_vibrate.getEntry());
        else
        	not_roaming_vibrate.setSummary("Pattern: " + not_roaming_vibrate.getEntry());
    }
    
    private void resetPreferences() {
    	final PreferenceScreen screen = getPreferenceScreen();
        SharedPreferences.Editor prefs = screen.getSharedPreferences().edit();
    	prefs.remove("3g_acquired_ringtone");
    	prefs.remove("3g_acquired_vibrate");
    	prefs.remove("3g_lost_ringtone");
    	prefs.remove("3g_lost_vibrate");
    	prefs.remove("wifi_acquired_ringtone");
    	prefs.remove("wifi_acquired_vibrate");
    	prefs.remove("wifi_lost_ringtone");
    	prefs.remove("wifi_lost_vibrate");
    	prefs.remove("roaming_ringtone");
    	prefs.remove("roaming_vibrate");
    	prefs.remove("not_roaming_ringtone");
    	prefs.remove("not_roaming_vibrate");
    	prefs.commit();

		Intent intent = new Intent(this, Preferences.class);
		startActivity(intent);
		
		finish();
    }
}