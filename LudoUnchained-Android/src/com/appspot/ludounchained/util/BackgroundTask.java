package com.appspot.ludounchained.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class BackgroundTask {
	public static class SilentTask<T> extends AsyncTask<Void, Void, T> {

		@Override
		protected T doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onCancelled(final T result) {
			super.onCancelled(result);
		}
		
		@Override
		protected void onPostExecute(final T result) {
			super.onPostExecute(result);
		}
	}

	public static class Task<T> extends AsyncTask<Void, Void, T> {
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
		protected T doInBackground(Void... params) {
			return null;
		}
		
		@Override
		protected void onCancelled(final T result) {
			super.onCancelled(result);
			dialog.dismiss();
		}
		
		@Override
		protected void onPostExecute(final T result) {
			super.onPostExecute(result);

			dialog.dismiss();
		}
	}
}