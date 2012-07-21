package net.ninja.provision;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.android.launcher.Launcher;
import com.android.launcher.R;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class EULActivity extends Activity {

    public static final String EULA_CHECK_FILE = "all_your_rights_are_belong_to_us";
    public static EULActivity instance = null;
    public static Intent service = null;
    
    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        instance = this;

        setContentView(R.layout.eula_layout);
    }
    
    public void accept(View view) {
        touchFile();
        finish();
        startActivity(new Intent(this, Launcher.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
    
    public void decline (View view) {
       
        try {
			Runtime.getRuntime().exec("/system/xbin/su -c /system/bin/shutdown");
		} catch (IOException e) {
			e.printStackTrace();
		}
        finish();
    }
   

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	  if(keyCode==KeyEvent.KEYCODE_BACK){
    	   return true;
    	  }
    	  if(keyCode==KeyEvent.KEYCODE_HOME){
    	   return true;
    	  }

    	  return super.onKeyDown(keyCode, event);
    }

    public static void restart(Context context) {
        Log.e("Eula", "Restarting eula");
        Intent intent = new Intent(context, EULActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void touchFile() {
        try {
            openFileOutput(EULA_CHECK_FILE, MODE_PRIVATE).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
