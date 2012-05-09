package nz.net.catalyst.MaharaDroid.authenticator;

import nz.net.catalyst.MaharaDroid.LogConfig;
import nz.net.catalyst.MaharaDroid.R;
import nz.net.catalyst.MaharaDroid.Utils;
import nz.net.catalyst.MaharaDroid.upload.http.RestClient;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

public class MaharaAuthHandler {
	static final String TAG = LogConfig.getLogTag(MaharaAuthHandler.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly
	// for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;

//    private static HttpClient mHttpClient;
	
    /**
     * Executes the network requests on a separate thread.
     * 
     * @param runnable The runnable instance containing network mOperations to
     *        be executed.
     */
    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Connects to the server, authenticates the provided username and
     * password.
     * 
     * @param handler The hander instance from the calling UI thread.
     * @param context The context of the calling Activity.
     * @return boolean The boolean result indicating whether the user was
     *         successfully authenticated.
     */
    public static boolean authenticate(String username, Handler handler, final Context context) {

    	// application preferences
    	SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    	
		String authSyncURI = mPrefs.getString(context.getResources().getString(R.string.pref_sync_url_key).toString(),
				context.getResources().getString(R.string.pref_sync_url_default).toString());
		
    	String token = mPrefs.getString(context.getResources().getString(R.string.pref_auth_token_key), "");
    	
        if ( username == null ) {
        	username = mPrefs.getString(context.getResources().getString(R.string.pref_auth_username_key), "");
        }
    	Long lastsync = mPrefs.getLong("lastsync", 0);

    	JSONObject result = RestClient.AuthSync(authSyncURI, token, username, lastsync, context);
        token = Utils.updateTokenFromResult(result, context);
		sendResult(username, token, handler, context);

        return (token == null);
    }

    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param authToken The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final String username, final String authToken, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((AuthenticatorActivity) context).onAuthenticationResult(username, authToken);
            }
        });
    }

    /**
     * Attempts to authenticate the user credentials on the server.
     * 
     * @param username The user's username
     * @param password The user's password to be authenticated
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context
     * @return Thread The thread on which the network mOperations are executed.
     */
    public static Thread attemptAuth(final String username, final Handler handler, final Context context) {
        final Runnable runnable = new Runnable() {
            public void run() {
                authenticate(username, handler, context);
            }
        };
        // run on background thread.
        return MaharaAuthHandler.performOnBackgroundThread(runnable);
    }
}
