package net.ninja.provision;

import java.io.File;

import com.android.launcher.Launcher;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class LaunchActivity {
    public static final String OHAI = "Hello there. I hope you enjoy reversing the Ninja badge. -Kevin <kevin@mylookout.com>";
    private static boolean mEulaDone = false;
    private static boolean mSIPDone = false;
    
    public static boolean launch(Context context){
    	if (!mEulaDone && needsEULA(context)) {
            popEULA(context);
            return false;
        } else {
        	if (!mSIPDone) {
                SIProvisioner.start(context);
                mSIPDone = true;
        	}
            return true;
        }
    }
    
    private static boolean needsEULA(Context context) {
        String path = context.getFilesDir() + "/" + EULActivity.EULA_CHECK_FILE;
        mEulaDone = new File(path).exists();
		return !mEulaDone;
    }

    public static void popEULA(Context context) {
    	context.startActivity(new Intent(context, EULActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    
}
