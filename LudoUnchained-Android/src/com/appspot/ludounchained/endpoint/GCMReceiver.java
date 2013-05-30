package com.appspot.ludounchained.endpoint;

import android.content.Context;

import com.google.android.gcm.GCMBroadcastReceiver;

public class GCMReceiver extends GCMBroadcastReceiver {
	@Override
	protected String getGCMIntentServiceClassName(Context context) {
		return "com.appspot.ludounchained.endpoint.GCMIntentService";
	}

}
