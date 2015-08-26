package com.geeklabs.rssprarthana;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.geeklabs.sevika.rssprarthana.R;

public class MainActivity extends Activity {

	private TextView prairText;
	private AssetFileDescriptor descriptor;
	private SeekBar seekBar;
	private MediaPlayer mp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Keep screen active
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		findViewById(R.id.play_button).setVisibility(View.INVISIBLE);
		findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);

		init();

		// Play prayer
		if (mp != null) {
			mp.start();
		}
		
		final Handler mHandler = new Handler();
		//Make sure you update Seek bar on UI thread
		MainActivity.this.runOnUiThread(new Runnable() {

		    @Override
		    public void run() {
		        if(mp != null){
		            int mCurrentPosition = mp.getCurrentPosition();
		            seekBar.setProgress(mCurrentPosition);
		        }
		        mHandler.postDelayed(this, 1000);
		    }
		});

		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (mp != null && fromUser) {
					mp.seekTo(progress);
					mp.start();
					findViewById(R.id.play_button).setVisibility(View.INVISIBLE);
					findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
				}

			}
		});
		findViewById(R.id.play_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mp != null && !mp.isPlaying()) {
					mp.start();
					findViewById(R.id.play_button).setVisibility(View.INVISIBLE);
					findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
				}
			}
		});

		findViewById(R.id.pauseButton).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp != null && mp.isPlaying()) {
					mp.pause();
					findViewById(R.id.pauseButton).setVisibility(View.INVISIBLE);
					findViewById(R.id.play_button).setVisibility(View.VISIBLE);
				}
			}
		});
		findViewById(R.id.restart_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mp != null) {
					mp.seekTo(0);
					mp.start();
					findViewById(R.id.play_button).setVisibility(View.INVISIBLE);
					findViewById(R.id.pauseButton).setVisibility(View.VISIBLE);
				}
			}
		});

		// Listen call states

		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		PhoneStateListener callStateListener = new PhoneStateListener() {
			boolean toTrack = false; // to prevent triggering in onCreate

			public void onCallStateChanged(int state, String incomingNumber) {
				// Phone Is Ringing
				if (state == TelephonyManager.CALL_STATE_RINGING) {
					mp.pause();
				}
				// Phone is Currently in A call
				if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
					if (toTrack) {
						mp.pause();
					}
					toTrack = true;
				}
				if (state == TelephonyManager.CALL_STATE_IDLE) {
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		if (telephonyManager != null) {
			telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		}

	}

	private void init() {
		mp = new MediaPlayer();
		mp.reset();
		prairText = (TextView) findViewById(R.id.prairText);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		try {
			descriptor = getAssets().openFd("Prarthana.mp3");
			mp.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();
			mp.prepare();
			mp.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int duration = mp.getDuration();
		seekBar.setMax(duration);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/*if (item.getItemId() == R.id.Telugu) {
			prairText.setText(getText(R.string.teluguPrair));
		}*/
		/*if (item.getItemId() == R.id.Hindi) {
			prairText.setText(getText(R.string.hindiPrair));
		}*/
		if (item.getItemId() == R.id.bg1) {
			// change back ground image
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.dwajam);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#FFFFFF"));
		}
		if (item.getItemId() == R.id.bg2) {
			// change back ground image
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.om);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#FFFFFF"));
		}
		if (item.getItemId() == R.id.bg3) {
			// change back ground image
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bharathmata);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#19070B"));
		}
		/*if (item.getItemId() == R.id.English) {
			prairText.setText(getText(R.string.englishPrair));
		}
		if (item.getItemId() == R.id.Malayalam) {
			prairText.setText(getText(R.string.malayalamPrayer));
		}
		if (item.getItemId() == R.id.Kannada) {
			prairText.setText(getText(R.string.kannadaPrayer));
		}
		if (item.getItemId() == R.id.Bangla) {
			prairText.setText(getText(R.string.banglaPrayer));
		}
		if (item.getItemId() == R.id.Tamil) {
			prairText.setText(getText(R.string.tamilPrayer));
		}
		if (item.getItemId() == R.id.Meaning) {
			prairText.setText(getText(R.string.meaningText));

			// change back ground image
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.flag);

			rLayout.setBackground(drawable);
		}*/
		return super.onOptionsItemSelected(item);
	}
	public static Locale[] getAvailableLocales (){
		Locale[] availableLocales = Locale.getAvailableLocales();
		return availableLocales;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mp.isPlaying()) {
			mp.stop();
		}
		moveTaskToBack(true);
	}
}
