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
//		Locale[] availableLocales = getAvailableLocales();
		/*String [] allLanguages = {"af", "af_NA", "af_ZA", "agq", "agq_CM", "ak", "ak_GH", "am", 
				"am_ET", "ar", "ar_001", "ar_AE", "ar_BH", "ar_DJ", "ar_DZ", "ar_EG", "ar_EH",
				"ar_ER", "ar_IL", "ar_IQ", "ar_JO", "ar_KM", "ar_KW", "ar_LB", "ar_LY", "ar_MA",
				"ar_MR", "ar_OM", "ar_PS", "ar_QA", "ar_SA", "ar_SD", "ar_SO", "ar_SS", "ar_SY",
				"ar_TD", "ar_TN", "ar_YE", "as", "as_IN", "asa", "asa_TZ", "az", "az__#Cyrl",
				"az_AZ_#Cyrl", "az__#Latn", "az_AZ_#Latn", "bas", "bas_CM", "be", "be_BY", "bem",
				"bem_ZM", "bez", "bez_TZ", bg, bg_BG, bm, bm_ML, bn, bn_BD, bn_IN, bo, bo_CN, bo_IN, br, br_FR, brx, brx_IN, bs, bs__#Cyrl, bs_BA_#Cyrl, bs__#Latn, bs_BA_#Latn, ca, ca_AD, ca_ES, ca_FR, ca_IT, cgg, cgg_UG, chr, chr_US, cs, cs_CZ, cy, cy_GB, da, da_DK, da_GL, dav, dav_KE, de, de_AT, de_BE, de_CH, de_DE, de_LI, de_LU, dje, dje_NE, dua, dua_CM, dyo, dyo_SN, dz, dz_BT, ebu, ebu_KE, ee, ee_GH, ee_TG, el, el_CY, el_GR, en, en_001, en_150, en_AG, en_AI, en_AS, en_AU, en_BB, en_BE, en_BM, en_BS, en_BW, en_BZ, en_CA, en_CC, en_CK, en_CM, en_CX, en_DG, en_DM, en_ER, en_FJ, en_FK, en_FM, en_GB, en_GD, en_GG, en_GH, en_GI, en_GM, en_GU, en_GY, en_HK, en_IE, en_IM, en_IN, en_IO, en_JE, en_JM, en_KE, en_KI, en_KN, en_KY, en_LC, en_LR, en_LS, en_MG, en_MH, en_MO, en_MP, en_MS, en_MT, en_MU, en_MW, en_NA, en_NF, en_NG, en_NR, en_NU, en_NZ, en_PG, en_PH, en_PK, en_PN, en_PR, en_PW, en_RW, en_SB, en_SC, en_SD, en_SG, en_SH, en_SL, en_SS, en_SX, en_SZ, en_TC, en_TK, en_TO, en_TT, en_TV, en_TZ, en_UG, en_UM, en_US, en_US_POSIX, en_VC, en_VG, en_VI, en_VU, en_WS, en_ZA, en_ZM, en_ZW, eo, es, es_419, es_AR, es_BO, es_CL, es_CO, es_CR, es_CU, es_DO, es_EA, es_EC, es_ES, es_GQ, es_GT, es_HN, es_IC, es_MX, es_NI, es_PA, es_PE, es_PH, es_PR, es_PY, es_SV, es_US, es_UY, es_VE, et, et_EE, eu, eu_ES, ewo, ewo_CM, fa, fa_AF, fa_IR, ff, ff_SN, fi, fi_FI, fil, fil_PH, fo, fo_FO, fr, fr_BE, fr_BF, fr_BI, fr_BJ, fr_BL, fr_CA, fr_CD, fr_CF, fr_CG, fr_CH, fr_CI, fr_CM, fr_DJ, fr_DZ, fr_FR, fr_GA, fr_GF, fr_GN, fr_GP, fr_GQ, fr_HT, fr_KM, fr_LU, fr_MA, fr_MC, fr_MF, fr_MG, fr_ML, fr_MQ, fr_MR, fr_MU, fr_NC, fr_NE, fr_PF, fr_PM, fr_RE, fr_RW, fr_SC, fr_SN, fr_SY, fr_TD, fr_TG, fr_TN, fr_VU, fr_WF, fr_YT, ga, ga_IE, gl, gl_ES, gsw, gsw_CH, gsw_LI, gu, gu_IN, guz, guz_KE, gv, gv_IM, ha, ha__#Latn, ha_GH_#Latn, ha_NE_#Latn, ha_NG_#Latn, haw, haw_US, iw, iw_IL, hi, hi_IN, hr, hr_BA, hr_HR, hu, hu_HU, hy, hy_AM, in, in_ID, ig, ig_NG, ii, ii_CN, is, is_IS, it, it_CH, it_IT, it_SM, ja, ja_JP, jgo, jgo_CM, jmc, jmc_TZ, ka, ka_GE, kab, kab_DZ, kam, kam_KE, kde, kde_TZ, kea, kea_CV, khq, khq_ML, ki, ki_KE, kk, kk__#Cyrl, kk_KZ_#Cyrl, kkj, kkj_CM, kl, kl_GL, kln, kln_KE, km, km_KH, kn, kn_IN, ko, ko_KP, ko_KR, kok, kok_IN, ks, ks__#Arab, ks_IN_#Arab, ksb, ksb_TZ, ksf, ksf_CM, kw, kw_GB, ky, ky__#Cyrl, ky_KG_#Cyrl, lag, lag_TZ, lg, lg_UG, lkt, lkt_US, ln, ln_AO, ln_CD, ln_CF, ln_CG, lo, lo_LA, lt, lt_LT, lu, lu_CD, luo, luo_KE, luy, luy_KE, lv, lv_LV, mas, mas_KE, mas_TZ, mer, mer_KE, mfe, mfe_MU, mg, mg_MG, mgh, mgh_MZ, mgo, mgo_CM, mk, mk_MK, ml, ml_IN, mn, mn__#Cyrl, mn_MN_#Cyrl, mr, mr_IN, ms, ms__#Latn, ms_BN_#Latn, ms_MY_#Latn, ms_SG_#Latn, mt, mt_MT, mua, mua_CM, my, my_MM, naq, naq_NA, nb, nb_NO, nb_SJ, nd, nd_ZW, ne, ne_IN, ne_NP, nl, nl_AW, nl_BE, nl_BQ, nl_CW, nl_NL, nl_SR, nl_SX, nmg, nmg_CM, nn, nn_NO, nnh, nnh_CM, nus, nus_SD, nyn, nyn_UG, om, om_ET, om_KE, or, or_IN, pa, pa__#Arab, pa_PK_#Arab, pa__#Guru, pa_IN_#Guru, pl, pl_PL, ps, ps_AF, pt, pt_AO, pt_BR, pt_CV, pt_GW, pt_MO, pt_MZ, pt_PT, pt_ST, pt_TL, rm, rm_CH, rn, rn_BI, ro, ro_MD, ro_RO, rof, rof_TZ, ru, ru_BY, ru_KG, ru_KZ, ru_MD, ru_RU, ru_UA, rw, rw_RW, rwk, rwk_TZ, saq, saq_KE, sbp, sbp_TZ, seh, seh_MZ, ses, ses_ML, sg, sg_CF, shi, shi__#Latn, shi_MA_#Latn, shi__#Tfng, shi_MA_#Tfng, si, si_LK, sk, sk_SK, sl, sl_SI, sn, sn_ZW, so, so_DJ, so_ET, so_KE, so_SO, sq, sq_AL, sq_MK, sq_XK, sr, sr__#Cyrl, sr_BA_#Cyrl, sr_ME_#Cyrl, sr_RS_#Cyrl, sr_XK_#Cyrl, sr__#Latn, sr_BA_#Latn, sr_ME_#Latn, sr_RS_#Latn, sr_XK_#Latn, sv, sv_AX, sv_FI, sv_SE, sw, sw_KE, sw_TZ, sw_UG, swc, swc_CD, ta, ta_IN, ta_LK, ta_MY, ta_SG, te, te_IN, teo, teo_KE, teo_UG, th, th_TH, ti, ti_ER, ti_ET, to, to_TO, tr, tr_CY, tr_TR, twq, twq_NE, tzm, tzm__#Latn, tzm_MA_#Latn, ug, ug__#Arab, ug_CN_#Arab, uk, uk_UA, ur, ur_IN, ur_PK, uz, uz__#Arab, uz_AF_#Arab, uz__#Cyrl, uz_UZ_#Cyrl, uz__#Latn, uz_UZ_#Latn, vai, vai__#Latn, vai_LR_#Latn, vai__#Vaii, vai_LR_#Vaii, vi, vi_VN, vun, vun_TZ, xog, xog_UG, yav, yav_CM, yo, yo_BJ, yo_NG, zgh, zgh_MA, zh, zh__#Hans, zh_CN_#Hans, zh_HK_#Hans, zh_MO_#Hans, zh_SG_#Hans, zh__#Hant, zh_HK_#Hant, zh_MO_#Hant, zh_TW_#Hant, zu, zu_ZA};
		*/
		/*if (item.getItemId() == R.id.Telugu) {
			for (Locale locale : availableLocales) {
				locale.getDisplayLanguage().contains(allLanguages);
			}
			prairText.setText(getText(R.string.teluguPrair));
		}*/
		if (item.getItemId() == R.id.Hindi) {
			prairText.setText(getText(R.string.hindiPrair));
		}
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
