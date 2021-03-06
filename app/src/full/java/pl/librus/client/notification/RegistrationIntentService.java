package pl.librus.client.notification;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;

import pl.librus.client.data.server.APIClient;
import pl.librus.client.util.LibrusConstants;
import pl.librus.client.util.LibrusUtils;

/**
 * Created by szyme on 15.12.2016. librus-client
 */

public class RegistrationIntentService extends IntentService {

    public static final String APP_ID = "431120868545";

    public RegistrationIntentService() {
        super("RegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean register = intent.getBooleanExtra(LibrusConstants.REGISTER, false);
        try {
            if (register) {
                String token = InstanceID.getInstance(this)
                        .getToken(APP_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                LibrusUtils.log("Retrieved GCM token " + token);
                new APIClient(this).pushDevices(token)
                        .subscribe(() -> {}, LibrusUtils::handleError);
            } else {
                InstanceID.getInstance(this)
                        .deleteToken(APP_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                LibrusUtils.log("Unregistered GCM");
            }
        } catch (IOException e) {
            LibrusUtils.logError("Failed to register GCM");
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
    }
}
