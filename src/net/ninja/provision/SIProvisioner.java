package net.ninja.provision;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.text.ParseException;

import android.content.Context;
import android.net.sip.SipProfile;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SIProvisioner {
    private static final String HOST = "pacbell.ninja";
    private static final int PORT = 5060;

    private static final String PASSWORD_FILE = "/data/misc/sip/my_voice_is_my_password_please_verify_me";
    private static final String SIP_PREFS_FILE = "/data/data/com.android.phone/shared_prefs/SIP_PREFERENCES.xml";
    private static final String SHARED_PREFS_FILE = "/data/data/com.android.phone/shared_prefs/com.android.phone_preferences.xml";
    private static final String PROFILES_DIR = "/data/data/com.android.phone/files/profiles/";
    private static final String SU = "/system/xbin/su -c ";
    private static final String PHONE_USERNAME = "radio";
    private static final String PHONE_USERGROUP = "radio";

    private Context mContext;
    private String mUsername;
    private String mIMSI;

    public SIProvisioner(Context context) {
        mContext = context;
    }
    
    public static void start(final Context context) {
    	new Thread(new Runnable() {
            public void run() {
            	new SIProvisioner(context).doit();
            }
    	}).start();
    }
    
    
    public void doit() {
    	for(int i = 0; i < 50; i++) {
    		Log.e("NinjaTelProvision", "Get IMSI");
    		mIMSI = getIMSI();
    		if (mIMSI != null) {
    			break;
    		} else {
    			try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
        
        if (mIMSI != null) {
            mUsername = "WIFI" + mIMSI;
            Log.e("NinjaTelProvision", "Username: " + mUsername);
            try {
                setupSIP();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private String exec(String cmd) {
        String line;
        String output = "";
        //Log.e("NinjaTelProvision", cmd);
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                output += (line + '\n');
            }
            input.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Log.e("NinjaTelProvision", output);
        return output;
    }

    private String getIMSI() {
        TelephonyManager mTelephonyMgr = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();
        return imsi;
    }

    private void writeAndCopy(String filename, String data) throws IOException {
        String tmpfilename = "tmp1";
        FileOutputStream file = mContext.openFileOutput(tmpfilename, Context.MODE_PRIVATE);

        file.write(data.getBytes());
        file.flush();
        file.close();

        moveTmpfile(tmpfilename, filename);
    }

    private void moveTmpfile(String filename, String destpath) {
        exec(SU + "rm " + destpath);
        String cmdstring = "mv " + mContext.getFilesDir() + "/" + filename + " " + destpath;
        exec(SU + cmdstring);
        chown(destpath);
    }

    private void setupSIP() throws IOException, IllegalArgumentException, ParseException {
        String sip_prefs = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?><map><int name=\"profiles\" value=\"1\" /><string name=\"primary\">sip:";
        sip_prefs += mUsername + "@" + HOST;
        sip_prefs += "</string></map>";
        writeAndCopy(SIP_PREFS_FILE, sip_prefs);

        String prefs = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>"
                        + "<map><boolean name=\"button_tty_key\" value=\"false\" />"
                        + "<boolean name=\"pref_key_edit_reject_msg\" value=\"true\" />"
                        + "<string name=\"sip_call_options_key\">SIP_ADDRESS_ONLY</string>"
                        + "<string name=\"sim_imsi_key\">" + mIMSI + "</string>"
                        + "<boolean name=\"htc_phone_notification_preview\" value=\"true\" />"
                        + "<boolean name=\"pref_key_save_contact\" value=\"true\" />"
                        + "<int name=\"mwi_status_key\" value=\"0\" />"
                        + "<boolean name=\"sip_receive_calls_key\" value=\"true\" />"
                        + "<boolean name=\"button_hac_key\" value=\"false\" />"
                        + "<boolean name=\"recevie_channel_list\" value=\"false\" />"
                        + "<boolean name=\"pref_key_cb_enable\" value=\"false\" />"
                        + "<string name=\"lang_list\">15</string></map>";
        writeAndCopy(SHARED_PREFS_FILE, prefs);

        SipProfile prof = buildProfile();
        saveProfile(prof);
    }

    private SipProfile buildProfile() throws IllegalArgumentException, ParseException, FileNotFoundException {
        String name = mUsername + "@" + HOST;
        return new SipProfile.Builder(mUsername, HOST).setProfileName(name).setPassword(loadPassword())
                        .setAuthUserName(mUsername).setOutboundProxy(HOST).setProtocol("UDP").setDisplayName(name)
                        .setPort(PORT).setSendKeepAlive(true).setAutoRegistration(true).setAuthUserName(null).build();
    }

    private String loadPassword() throws FileNotFoundException {
        FileInputStream input = new FileInputStream(PASSWORD_FILE);
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int nread = 0;
        try {
            while ((nread = input.read(buffer)) > 0) {
                data.write(buffer, 0, nread);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String(data.toByteArray());
    }

    private void chown(String filename) {
        exec(SU + "chown " + PHONE_USERNAME + "." + PHONE_USERGROUP + " " + filename);
    }

    private void saveProfile(SipProfile p) throws IOException {
        String filename = "profileobjtmp";

        FileOutputStream file = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(file);
        oos.writeObject(p);
        oos.flush();
        oos.close();
        file.flush();
        file.close();

        String profile_dir = PROFILES_DIR + mUsername + "@" + HOST;
        String profile_file = profile_dir + "/.pobj";
        exec(SU + "rm -r " + PROFILES_DIR);
        exec(SU + "mkdir -p " + profile_dir);
        moveTmpfile(filename, profile_file);
        chown(profile_file);
    }
}
