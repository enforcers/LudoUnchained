package com.appspot.ludounchained.endpoint;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appspot.ludounchained.GameActivity;
import com.appspot.ludounchained.LudoUnchainedApplication;
import com.appspot.ludounchained.controllerEndpoint.ControllerEndpoint;
import com.appspot.ludounchained.controllerEndpoint.model.Session;
import com.appspot.ludounchained.util.CloudEndpointUtils;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;

public class GCMIntentService extends GCMBaseIntentService {

	protected static final String PROJECT_NUMBER = "257487468170";

	public GCMIntentService() {
		super(PROJECT_NUMBER);
	}
	
	public static void register(Context context) {
		GCMRegistrar.unregister(context);
		GCMRegistrar.register(context, PROJECT_NUMBER);
	}

	/**
	 * Called on registration error. This is called in the context of a Service
	 * - no dialog or UI.
	 * 
	 * @param context
	 *            the Context
	 * @param errorId
	 *            an error message
	 */
	@Override
	protected void onError(Context context, String errorId) {
		Log.e(EndpointServiceStub.class.getName(), "Registration with Google Cloud Messaging Service failed");
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called when a cloud message has been received.
	 */
	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i("onMessage called", "NOW");
		Intent notificationIntent = new Intent(context, GameActivity.class);
		notificationIntent.putExtra("message", intent.getStringExtra("message"));
		notificationIntent.putExtra("requestJoinUser", intent.getStringExtra("requestJoinUser"));
		notificationIntent.putExtra("requestJoinSession", intent.getStringExtra("requestJoinSession"));

		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//startActivity(notificationIntent);
		this.sendBroadcast(intent);
		//startActivity(notificationIntent);
		//Log.i(EndpointServiceStub.class.getName(), "Message received:" + intent.getStringExtra("message"));
		// TODO Auto-generated method stub
		
	}

	/**
	 * Called back when a registration token has been received from the Google
	 * Cloud Messaging service.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	protected void onRegistered(Context context, String registrationId) {

		ControllerEndpoint endpoint = getEndpoint();
		LudoUnchainedApplication appState = (LudoUnchainedApplication) context.getApplicationContext();

		Session session = appState.getSession();

    	try {
    		session = endpoint.registerDevice(session.getSessionId(), registrationId).execute();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		Log.i(EndpointServiceStub.class.getName(), "Cloud registration succeed: " + registrationId);
		// TODO Auto-generated method stub

	}

	/**
	 * Called back when the Google Cloud Messaging service has unregistered the
	 * device.
	 * 
	 * @param context
	 *            the Context
	 */
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(EndpointServiceStub.class.getName(), "Cloud unregistration succeed: " + registrationId);
		// TODO Auto-generated method stub
		
	}
	
	private ControllerEndpoint getEndpoint() {
		ControllerEndpoint.Builder endpointBuilder = new ControllerEndpoint.Builder(
    			AndroidHttp.newCompatibleTransport(),
    			new JacksonFactory(),
    			new HttpRequestInitializer() {
    				public void initialize(HttpRequest httpRequest) { }
    			});

		return CloudEndpointUtils.updateBuilder(endpointBuilder).build();
	}

}
