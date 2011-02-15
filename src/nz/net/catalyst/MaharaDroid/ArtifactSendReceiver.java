/*
 * MaharaDroid -  Artefact uploader
 * 
 * This file is part of MaharaDroid.
 * 
 *   Copyright [2010] [Catalyst IT Limited]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package nz.net.catalyst.MaharaDroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

/*
 * The ArtifactSendReceiver class is taken from the PictureSendReceiver class
 * written by Russel Stewart (rnstewart@gmail.com) as part of the Flickr Free
 * Android application.
 *
 * @author	Alan McNatty (alan.mcnatty@catalyst.net.nz)
 */

public class ArtifactSendReceiver extends Activity {

	static final String TAG = LogConfig.getLogTag(ArtifactSendReceiver.class);
	// whether DEBUG level logging is enabled (whether globally, or explicitly for this log tag)
	static final boolean DEBUG = LogConfig.isDebug(TAG);
	// whether VERBOSE level logging is enabled
	static final boolean VERBOSE = LogConfig.VERBOSE;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String [] uris = null;
		
		if ( DEBUG ) 
			Log.d(TAG, "Type: " + intent.getType() + 
					 ", Stream: " + intent.hasExtra("android.intent.extra.STREAM") +
					 ", Data: " + intent.getDataString() + 
					 ", Flag(s): " + intent.getFlags());
        
		if (intent.getAction().equals(Intent.ACTION_SEND)) {
			if (extras.containsKey("android.intent.extra.STREAM")) {
				Uri uri = (Uri) extras.get("android.intent.extra.STREAM");
				uris = new String[] { uri.toString() };
			}
		} else if (intent.getAction().equals(Intent.ACTION_SEND_MULTIPLE)) {
			if (extras.containsKey("android.intent.extra.STREAM")) {
				ArrayList<Parcelable> list = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
				int c = 0;
				uris = new String[list.size()];
				for (Parcelable p : list) {
					Uri uri = (Uri) p;
					uris[c++] = uri.toString();
				}
			}
		}

		if ( uris == null ) {
			Toast.makeText(getApplicationContext(), R.string.uploadnotavailable, Toast.LENGTH_SHORT).show();
		} else {
			Intent i = new Intent(this, ArtifactSettings.class);
			i.putExtra("uri", uris);
			startActivity(i);
		}
		finish();
	}
}
