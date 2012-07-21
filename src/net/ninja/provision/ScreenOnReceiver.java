package net.ninja.provision;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class ScreenOnReceiver extends BroadcastReceiver {	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("ninja","screenon");
		disableKeyguard(context);
	}
	
    public void disableKeyguard(Context context) {
        KeyguardManager km = (KeyguardManager)context.getSystemService(context.KEYGUARD_SERVICE);
        KeyguardLock kgl = km.newKeyguardLock("ninja");
    	kgl.disableKeyguard();
    }
}
