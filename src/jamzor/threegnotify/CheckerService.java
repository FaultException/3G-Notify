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
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

public class CheckerService extends Service {
	private boolean has3G = false;
	private boolean hasWifi = false;
	private boolean isRoaming = false;
	private Timer timer = null;
	private static final int DELAY = 15000;
	private static boolean isInitialized = false;
	
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
		if(timer == null) {
			/*
			 * Refresh our records, so if someone turns it off,
			 * then back on later it won't freak out.
			 */
			check(false);
			
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					check(true);
				}
			}, DELAY, DELAY);
		}
	}
	
	protected void disable() {
		if(timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mStub;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if(isInitialized) return;
		isInitialized = true;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(prefs.getBoolean("enabled", true))
			enable();
	}
	
	protected void check(boolean notify) {
		TelephonyManager tm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
		ConnectivityManager connectivity = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		
		boolean had3G = has3G;
		boolean hadWifi = hasWifi;
		boolean wasRoaming = isRoaming;
		
		int netType = tm.getNetworkType();
		if(netType == TelephonyManager.NETWORK_TYPE_HSPA ||
		   netType == TelephonyManager.NETWORK_TYPE_HSDPA ||
		   netType == TelephonyManager.NETWORK_TYPE_HSUPA ||
		   netType == TelephonyManager.NETWORK_TYPE_1xRTT ||
		   netType == TelephonyManager.NETWORK_TYPE_EVDO_0 ||
		   netType == TelephonyManager.NETWORK_TYPE_EVDO_A ||
		   netType == TelephonyManager.NETWORK_TYPE_UMTS) {
			has3G = true;
		} else {
			has3G = false;
		}
		
		if(connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
			hasWifi = true;
		} else {
			hasWifi = false;
		}
		
		if(tm.isNetworkRoaming()) {
			isRoaming = true;
		} else {
			isRoaming = false;
		}
		
		if(notify) {
			if(has3G && !had3G) {
				playRingtone("3g_acquired_ringtone");
				vibrate("3g_acquired_vibrate");
			} else if(!has3G && had3G) {
				playRingtone("3g_lost_ringtone");
				vibrate("3g_lost_vibrate");
			}
			
			if(hasWifi && !hadWifi) {
				playRingtone("wifi_acquired_ringtone");
				vibrate("wifi_acquired_vibrate");
			} else if(!hasWifi && hadWifi) {
				playRingtone("wifi_lost_ringtone");
				vibrate("wifi_lost_vibrate");
			}
			
			if(isRoaming && !wasRoaming) {
				playRingtone("roaming_ringtone");
				vibrate("roaming_vibrate");
			} else if(!isRoaming && wasRoaming) {
				playRingtone("not_roaming_ringtone");
				vibrate("not_roaming_vibrate");
			}
		}
	}
	
	private void playRingtone(String pref) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		String ringtoneURL = prefs.getString(pref, "");
		if(ringtoneURL.equals("")) return;
		
		Ringtone tone = RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(ringtoneURL));
		tone.play();
	}
	
	protected void vibrate(String pref) {
		final long QUICK_DURATION = 250;
		final long LONG_DURATION = 750;
		final long INTERVAL = 250;
		
		Vibrator vb = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
