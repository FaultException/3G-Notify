package jamzor.threegnotify;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class Preferences extends PreferenceActivity implements OnPreferenceChangeListener,
        OnPreferenceClickListener {
    private static final String KEY_ENABLED = "enabled";
    private static final String KEY_CONTACT = "contact_pref";
    private static final String KEY_RESET = "reset_pref";
    private static final Uri MAIL_URI = Uri.parse("mailto:faultexceptionapps@gmail.com?subject=3G Notify");

    private CheckBoxPreference mEnabled;
    private Preference mContact;
    private Preference mReset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mEnabled = (CheckBoxPreference) findPreference(KEY_ENABLED);
        mEnabled.setOnPreferenceChangeListener(this);

        mContact = findPreference(KEY_CONTACT);
        mContact.setOnPreferenceClickListener(this);

        mReset = findPreference(KEY_RESET);
        mReset.setOnPreferenceClickListener(this);

        updatePreferences();
    }

    private void updatePreferences() {
        ListPreference mobile_acquired_vibrate = (ListPreference) findPreference("3g_acquired_vibrate");
        if (mobile_acquired_vibrate.getValue().equals("none"))
            mobile_acquired_vibrate.setSummary(mobile_acquired_vibrate.getEntry());
        else
            mobile_acquired_vibrate.setSummary("Pattern: " + mobile_acquired_vibrate.getEntry());

        ListPreference mobile_lost_vibrate = (ListPreference) findPreference("3g_lost_vibrate");
        if (mobile_lost_vibrate.getValue().equals("none"))
            mobile_lost_vibrate.setSummary(mobile_lost_vibrate.getEntry());
        else
            mobile_lost_vibrate.setSummary("Pattern: " + mobile_lost_vibrate.getEntry());

        ListPreference wifi_acquired_vibrate = (ListPreference) findPreference("wifi_acquired_vibrate");
        if (wifi_acquired_vibrate.getValue().equals("none"))
            wifi_acquired_vibrate.setSummary(wifi_acquired_vibrate.getEntry());
        else
            wifi_acquired_vibrate.setSummary("Pattern: " + wifi_acquired_vibrate.getEntry());

        ListPreference wifi_lost_vibrate = (ListPreference) findPreference("wifi_lost_vibrate");
        if (wifi_lost_vibrate.getValue().equals("none"))
            wifi_lost_vibrate.setSummary(wifi_lost_vibrate.getEntry());
        else
            wifi_lost_vibrate.setSummary("Pattern: " + wifi_lost_vibrate.getEntry());

        ListPreference roaming_vibrate = (ListPreference) findPreference("roaming_vibrate");
        if (roaming_vibrate.getValue().equals("none"))
            roaming_vibrate.setSummary(roaming_vibrate.getEntry());
        else
            roaming_vibrate.setSummary("Pattern: " + roaming_vibrate.getEntry());

        ListPreference not_roaming_vibrate = (ListPreference) findPreference("not_roaming_vibrate");
        if (not_roaming_vibrate.getValue().equals("none"))
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

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mContact) {
            startActivity(new Intent(Intent.ACTION_VIEW, MAIL_URI));
            return true;
        } else if (preference == mReset) {
            resetPreferences();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ICheckerService service = ((App) getApplication()).checker;
        if (preference == mEnabled) {
            boolean enabled = (Boolean) newValue;
            try {
                if (enabled) {
                    service.enable();
                } else {
                    service.disable();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}