package jamzor.threegnotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;

public class BootLoader extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			ComponentName name = new ComponentName(context.getPackageName(), CheckerService.class.getName());
			context.startService(new Intent().setComponent(name));
		}
	}

}
