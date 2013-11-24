package com.niara3.backgroundprogress;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class Background {
	public static final String ACTION = Background.class.getName()+".action";
	public static final String EXTRA_STATE = "state";
	public static final int UNKNOWN = -1;
	public static final int NOT_START = 0;
	public static final int STARTED = 1;
	public static final int FINISHED = 2;
	private final String TAG = "BGP@"+this.getClass().getSimpleName();
	private Context mAppContext;

	private static class Singleton {
		private static Background mInstance = new Background();
	}
	public static Background getInstance() {
		return Singleton.mInstance;
	}
	private Background() {
	}
	public enum State {
		NOT_START, STARTED, FINISHED
	}

	public void init(Context context) {
		mAppContext = context.getApplicationContext();
		IntentFilter filter = new IntentFilter(ACTION);
		Intent stickyIntent = mAppContext.registerReceiver(null, filter);
		if (stickyIntent == null) {
			sendStickyBroadcast(NOT_START);
		}
	}
	public void start() {
		if (mAppContext == null) {
			throw new RuntimeException("not init");
		}
		Log.d(TAG, "STARTED");
		sendStickyBroadcast(STARTED);

		HandlerThread wt = new HandlerThread("Background");
		wt.start();
		Handler wh = new Handler(wt.getLooper());
		wh.post(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.d(TAG, "FINISHED");
				sendStickyBroadcast(FINISHED);
			}
		});
	}
	public void clear() {
		Log.d(TAG, "NOT_START");
		sendStickyBroadcast(NOT_START);
	}
	private void sendStickyBroadcast(int state) {
		Intent intent = new Intent(ACTION);
		intent.putExtra(EXTRA_STATE, state);
		mAppContext.sendStickyBroadcast(intent);
	}
}
