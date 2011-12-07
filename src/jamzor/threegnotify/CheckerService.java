package jamzor.threegnotify;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CheckerService extends Service {
	// State:
	private boolean mHas3G = false;
	private boolean mHasWifi = false;
	private boolean mIsRoaming = false;
	
	private Timer mTimer = null;
	private static final int DELAY = 15000;
	private static boolean mIsInitialized = false;
	private Context mContext;
	
	private ICheckerService.Stub mStub = new ICheckerService.Stub() {
		public void enable() {
			CheckerService.this.enable();
		}
		
		public void disable() {
			CheckerService.this.disable();
		}
		
		public void vibrate(String pref) {
			CheckerService.this.vibrate(pref);
		}
	};

	protected void enable() {
		if(mTimer == null) {
			/*
			 * Prevent a "shock" from being re-enabling after a disable by
			 * updating our cache of the phone state without notifying.
			 */
			update(false);
			
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					update(true);
				}
			}, DELAY, DELAY);
		}
	}
	
	protected void disable() {
		if(mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mStub;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(mIsInitialized) return;
		mIsInitialized = true;
		
		mContext = getApplicationContext();
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(prefs.getBoolean("enabled", true))
			enable();
	}
	
	protected void update(boolean notify) {
		// Airplane Mode means we don't need to update at all.
		boolean airplaneMode = Settings.System.getInt(mContext.getContentResolver(),
									Settings.System.AIRPLANE_MODE_ON, 0) != 0;
		if(airplaneMode) {
			Log.d("3GN", "Skipping, airplane mode is active.");
			return;
		}
		
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		boolean had3G = mHas3G;
		boolean hadWifi = mHasWifi;
		boolean wasRoaming = mIsRoaming;
		
		int netType = tm.getNetworkType();
		if(netType == TelephonyManager.NETWORK_TYPE_HSPA ||
		   netType == TelephonyManager.NETWORK_TYPE_HSDPA ||
		   netType == TelephonyManager.NETWORK_TYPE_HSUPA ||
		   netType == TelephonyManager.NETWORK_TYPE_1xRTT ||
		   netType == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
		   netType == TelephonyManager.NETWORK_TYPE_EVDO_A ||
		   netType == TelephonyManager.NETWORK_TYPE_UMTS) {
			mHas3G = true;
		} else {
			mHas3G = false;
		}
		
		if(connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
			mHasWifi = true;
		} else {
			mHasWifi = false;
		}
		
		if(tm.isNetworkRoaming()) {
			mIsRoaming = true;
		} else {
			mIsRoaming = false;
		}
		
		if(notify) {
			if(mHas3G && !had3G) {
				playRingtone("3g_acquired_ringtone");
				vibrate("3g_acquired_vibrate");
			} else if(!mHas3G && had3G) {
				playRingtone("3g_lost_ringtone");
				vibrate("3g_lost_vibrate");
			}
			
			if(mHasWifi && !hadWifi) {
				playRingtone("wifi_acquired_ringtone");
				vibrate("wifi_acquired_vibrate");
			} else if(!mHasWifi && hadWifi) {
				playRingtone("wifi_lost_ringtone");
				vibrate("wifi_lost_vibrate");
			}
			
			if(mIsRoaming && !wasRoaming) {
				playRingtone("roaming_ringtone");
				vibrate("roaming_vibrate");
			} else if(!mIsRoaming && wasRoaming) {
				playRingtone("not_roaming_ringtone");
				vibrate("not_roaming_vibrate");
			}
		}
	}
	
	private void playRingtone(String pref) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String ringtoneURL = prefs.getString(pref, "");
		if(ringtoneURL.equals("")) return;
		
		Ringtone tone = RingtoneManager.getRingtone(mContext, Uri.parse(ringtoneURL));
		tone.play();
	}
	
	protected void vibrate(String pref) {
		final long QUICK_DURATION = 250;
		final long LONG_DURATION = 750;
		final long INTERVAL = 250;
		
		Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		String value = prefs.getString(pref, "none");
		if(value.equals("none")) return;
		
		/*
		 * We have to allocate enough room for 2*the pattern length
		 * because we need to define the OFF duration for each bit
		 * as well, excluding the last bit, leaving us room for the
		 * first 0 bit.
		 */
		long pattern[] = new long[value.length() * 2];
		pattern[0] = 0;
		
		int i = 1; // pattern array index
		int charIndex = 0; // pattern string index
		for(char letter : value.toCharArray()) {
			// On:
			if(letter == '.')
				pattern[i] = QUICK_DURATION;
			else if(letter == '-')
				pattern[i] = LONG_DURATION;
			
			if(charIndex < value.length() - 1)	{
				// Off:
				pattern[i + 1] = INTERVAL;
			}
			
			i += 2;
			charIndex++;
		}
		
		vb.vibrate(pattern, -1);
	}
}
