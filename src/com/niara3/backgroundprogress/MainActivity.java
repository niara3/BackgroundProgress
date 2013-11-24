package com.niara3.backgroundprogress;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private final String TAG = "BGP@"+this.getClass().getSimpleName();

	private BroadcastReceiver mReceiver;

	private static final int DLGID_PROGRESS = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Background.getInstance().init(this);
	}

	@Override
	protected void onRestart() {
		Log.d(TAG, "onRestart");
		super.onRestart();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();

		IntentFilter filter = new IntentFilter(Background.ACTION);
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent == null) {
					return;
				}
				String action = intent.getAction();
				if (Background.ACTION.equals(action)) {
					int state = intent.getIntExtra(Background.EXTRA_STATE, Background.UNKNOWN);
					if (state == Background.FINISHED) {
						removeDialog(DLGID_PROGRESS);
					}
					return;
				}
			}
		};
		Intent stickyIntent = registerReceiver(mReceiver, filter);
		if (stickyIntent !=  null) {
			int state = stickyIntent.getIntExtra(Background.EXTRA_STATE, Background.UNKNOWN);
			switch (state) {
			case Background.NOT_START:
				Background.getInstance().start();
				showDialog(DLGID_PROGRESS);
				break;
			case Background.STARTED:
				break;
			case Background.FINISHED:
				removeDialog(DLGID_PROGRESS);
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();

		unregisterReceiver(mReceiver);
		if (isFinishing()) {
			Background.getInstance().clear();
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DLGID_PROGRESS:
			ProgressDialog dlg = new ProgressDialog(this);
			return dlg;
		default:
			break;
		}
		return super.onCreateDialog(id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
