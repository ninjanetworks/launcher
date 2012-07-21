package net.ninja.provision;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

public class EmergencyCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        // Try to read the phone number from previous receivers.
        String phoneNumber = getResultData();

        if (phoneNumber == null) {
            // We could not find any previous data. Use the original phone number in this case.
            phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        }

        Handler handler = new Handler();

        if (phoneNumber.equals("911") || phoneNumber.equals("112")) {
            setResultData(null);
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    Uri uri = Uri.fromParts("tel", "999", null);
                    Intent newIntent = new Intent(Intent.ACTION_CALL, uri);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(newIntent);
                }
            }, 5000);
        }
    }
}