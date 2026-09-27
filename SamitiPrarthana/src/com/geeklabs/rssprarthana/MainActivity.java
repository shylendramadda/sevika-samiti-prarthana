package com.geeklabs.rssprarthana;

import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.geeklabs.sevika.rssprarthana.R;

public class MainActivity extends AppCompatActivity {

	private TextView prairText;
	private AssetFileDescriptor descriptor;
	private SeekBar seekBar;
	private MediaPlayer mp;
	private AudioManager audioManager;
	private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;

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

		final Handler mHandler = new Handler(Looper.getMainLooper());
		// Make sure you update Seek bar on UI thread
		MainActivity.this.runOnUiThread(new Runnable() {

		    @Override
		    public void run() {
		        if (mp != null && mp.isPlaying()) {
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

		// Manage Audio Focus for call/interruption handling
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int focusChange) {
				if (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
					focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
					focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
					if (mp != null && mp.isPlaying()) {
						mp.pause();
						findViewById(R.id.pauseButton).setVisibility(View.INVISIBLE);
						findViewById(R.id.play_button).setVisibility(View.VISIBLE);
					}
				}
			}
		};

		if (audioManager != null) {
			audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		}
	}

	private void init() {
		mp = new MediaPlayer();
		mp.reset();
		AudioAttributes audioAttributes = new AudioAttributes.Builder()
				.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
				.setUsage(AudioAttributes.USAGE_MEDIA)
				.build();
		mp.setAudioAttributes(audioAttributes);

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
		if (item.getItemId() == R.id.bg1) {
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.dwajam);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#FFFFFF"));
		}
		if (item.getItemId() == R.id.bg2) {
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.om);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#FFFFFF"));
		}
		if (item.getItemId() == R.id.bg3) {
			RelativeLayout rLayout = (RelativeLayout) findViewById(R.id.mainActivity);
			Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bharathmata);
			rLayout.setBackground(drawable);
			prairText.setTextColor(Color.parseColor("#19070B"));
		}
		return super.onOptionsItemSelected(item);
	}

	public static Locale[] getAvailableLocales() {
		return Locale.getAvailableLocales();
	}

	@SuppressLint("GestureBackNavigation")
    @Override
	public void onBackPressed() {
		super.onBackPressed();
		if (mp != null && mp.isPlaying()) {
			mp.stop();
		}
		moveTaskToBack(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mp != null) {
			if (mp.isPlaying()) {
				mp.stop();
			}
			mp.release();
			mp = null;
		}
		if (audioManager != null && audioFocusChangeListener != null) {
			audioManager.abandonAudioFocus(audioFocusChangeListener);
		}
	}
}
