package com.appspot.ludounchained.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class BackgroundTask {
	public class SilentTask extends AsyncTask<Void, Void, Object> {

		@Override
		protected Object doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onCancelled(final Object result) {
			super.onCancelled(result);
		}
		
		@Override
		protected void onPostExecute(final Object result) {
			super.onPostExecute(result);
		}
	}

	public class Task extends AsyncTask<Void, Void, Object> {
		private ProgressDialog dialog;
		
		public Task(Context context) {
			this.dialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("Loading...");
			dialog.show();
		}

		@Override
		protected Object doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onCancelled(final Object result) {
			super.onCancelled(result);
			dialog.dismiss();
		}
		
		@Override
		protected void onPostExecute(final Object result) {
			super.onPostExecute(result);

			dialog.dismiss();
		}
	}
}