package com.example.soundvisualizer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private CDrawer.CDrawThread mDrawThread;
	private CDrawer mdrawer;

	private View.OnClickListener listener;
	private Boolean m_bStart = Boolean.valueOf(false);
	private Boolean recording;
	private CSampler sampler;
	private Button StartButton;
	private Button StopButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mdrawer = (CDrawer) findViewById(R.id.drawer);
		m_bStart = Boolean.valueOf(false);

		StopButton = (Button) findViewById(R.id.stopButton);
		StopButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				StartButton.setEnabled(true);
				StopButton.setEnabled(false);

				System.out.println("stsrt button");
				sampler.SetRun(Boolean.valueOf(false));
				mDrawThread.setRun(Boolean.valueOf(false));
				sampler.SetSleeping(Boolean.valueOf(true));
				mDrawThread.SetSleeping(Boolean.valueOf(true));
				Boolean.valueOf(false);

			}
		});

		StartButton = (Button) findViewById(R.id.startButton);
		StartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				StartButton.setEnabled(false);
				StopButton.setEnabled(true);

				System.out.println("onResume");
				int i = 0;
				while (true) {
					if ((sampler.GetDead2().booleanValue())
							&& (mdrawer.GetDead2().booleanValue())) {
						System.out.println(sampler.GetDead2() + ", "
								+ mdrawer.GetDead2());
						sampler.Restart();
						if (!m_bStart.booleanValue())
							mdrawer.Restart(Boolean.valueOf(true));
						sampler.SetSleeping(Boolean.valueOf(false));
						mDrawThread.SetSleeping(Boolean.valueOf(false));
						m_bStart = Boolean.valueOf(false);
						return;
					}
					try {
						Thread.sleep(500L);
						System.out.println("Hang on..");
						i++;
						if (!sampler.GetDead2().booleanValue())
							System.out.println("sampler not DEAD!!!");
						if (!mdrawer.GetDead2().booleanValue()) {
							System.out.println("mDrawer not DeAD!!");
							mdrawer.SetRun(Boolean.valueOf(false));
						}
						if (i <= 4)
							continue;
						mDrawThread.SetDead2(Boolean.valueOf(true));
					} catch (InterruptedException localInterruptedException) {
						localInterruptedException.printStackTrace();
					}
				}

			}
		});

		while (true) {
			recording = Boolean.valueOf(false);
			run();
			System.out.println("mDrawThread NOT NULL");
			System.out.println("recorder NOT NULL");
			return;
		}

	}

	/**
	 * Pause the visualizer when the app is paused
	 */
	@Override
	protected void onPause() {
		System.out.println("onPause");
		sampler.SetRun(Boolean.valueOf(false));
		mDrawThread.setRun(Boolean.valueOf(false));
		sampler.SetSleeping(Boolean.valueOf(true));
		mDrawThread.SetSleeping(Boolean.valueOf(true));
		Boolean.valueOf(false);
		super.onPause();
	}

	/**
	 * Resters the visualizer when the app restarts
	 */
	@Override
	protected void onRestart() {
		m_bStart = Boolean.valueOf(true);
		System.out.println("onRestart");
		super.onRestart();
	}

	/**
	 * Resume the visualizer when the app resumes
	 */
	@Override
	protected void onResume() {
		System.out.println("onResume");
		int i = 0;
		while (true) {
			if ((sampler.GetDead2().booleanValue())
					&& (mdrawer.GetDead2().booleanValue())) {
				System.out.println(sampler.GetDead2() + ", "
						+ mdrawer.GetDead2());
				sampler.Restart();
				if (!m_bStart.booleanValue())
					mdrawer.Restart(Boolean.valueOf(true));
				sampler.SetSleeping(Boolean.valueOf(false));
				mDrawThread.SetSleeping(Boolean.valueOf(false));
				m_bStart = Boolean.valueOf(false);
				super.onResume();
				return;
			}
			try {
				Thread.sleep(500L);
				System.out.println("Hang on..");
				i++;
				if (!sampler.GetDead2().booleanValue())
					System.out.println("sampler not DEAD!!!");
				if (!mdrawer.GetDead2().booleanValue()) {
					System.out.println("mDrawer not DeAD!!");
					mdrawer.SetRun(Boolean.valueOf(false));
				}
				if (i <= 4)
					continue;
				mDrawThread.SetDead2(Boolean.valueOf(true));
			} catch (InterruptedException localInterruptedException) {
				localInterruptedException.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		System.out.println("onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		System.out.println("onStop");
		super.onStop();
	}

	/**
	 * Recives the buffert from the sampler
	 * 
	 * @param buffert
	 */
	public void setBuffer(short[] paramArrayOfShort) {
		mDrawThread = mdrawer.getThread();
		mDrawThread.setBuffer(paramArrayOfShort);
	}

	/**
	 * Called by OnCreate to get everything up and running
	 */
	public void run() {
		try {
			if (mDrawThread == null) {
				mDrawThread = mdrawer.getThread();
			}
			if (sampler == null)
				sampler = new CSampler(this);
			Context localContext = getApplicationContext();
			Display localDisplay = getWindowManager().getDefaultDisplay();
			Toast localToast = Toast.makeText(localContext,
					"Please make some noise..", Toast.LENGTH_LONG);
			localToast.setGravity(48, 0, localDisplay.getHeight() / 8);
			localToast.show();
			mdrawer.setOnClickListener(listener);
			if (sampler != null) {
				try {
					sampler.Init();
				} catch (Exception e) {
					Toast.makeText(
							this,
							"Could not instance the sampler. Could not access the device audio-drivers",
							Toast.LENGTH_LONG).show();
				}
				sampler.StartRecording();
				sampler.StartSampling();
			}
		} catch (NullPointerException e) {
			Log.e("Main_Run", "NullPointer: " + e.getMessage());
		}
	}

}
