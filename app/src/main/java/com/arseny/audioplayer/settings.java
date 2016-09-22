package com.arseny.audioplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

/**
 * Created by arseny on 09.06.16.
 */
public class settings extends AppCompatActivity {

    public static final String LOG_TAG = settings.class.getSimpleName();

    public MediaPlayer mediaPlayer;
    private AudioManager am;
    private int result;
    public int currentFocus = AudioManager.AUDIOFOCUS_GAIN;
    private AudioFocusListener myAudioFocusListener;

    public RadioButton rb1;
    public RadioButton rb2;
    public RadioButton rb3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        //setVolumeControlStream(AudioManager.STREAM_MUSIC);
        result = am.requestAudioFocus(myAudioFocusListener,
                AudioManager.STREAM_MUSIC, currentFocus);
        myAudioFocusListener = new AudioFocusListener(mediaPlayer,
                am, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        Log.d(LOG_TAG, "Music request focus, result: " + result);

        rb1 = (RadioButton) findViewById(R.id.rb1);
        rb2 = (RadioButton) findViewById(R.id.rb2);
        rb3 = (RadioButton) findViewById(R.id.rb3);
    }

    public void onClick(View view) {
        int durationHint = -1;

        switch (view.getId()){
            case R.id.check_Sound:
                Log.d(LOG_TAG, "start Raw");
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
                    release();
                    mediaPlayer = MediaPlayer.create(this, R.raw.explosion);
                    mediaPlayer.start();
                }
                break;
            case R.id.rb1:
                Log.d(LOG_TAG, "start rb1");
                durationHint = AudioManager.AUDIOFOCUS_GAIN;
                rb1.setEnabled(false);
                rb2.setEnabled(true);
                rb3.setEnabled(true);
                rb2.setActivated(false);
                rb3.setActivated(false);
                break;
            case R.id.rb2:
                Log.d(LOG_TAG, "start rb2");
                durationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
                rb1.setEnabled(true);
                rb2.setEnabled(false);
                rb3.setEnabled(true);
                rb1.setActivated(false);
                rb3.setActivated(false);
                break;
            case R.id.rb3:
                Log.d(LOG_TAG, "start rb3");
                durationHint = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK;
                rb1.setEnabled(true);
                rb2.setEnabled(true);
                rb3.setEnabled(false);
                rb1.setActivated(false);
                rb2.setActivated(false);
                break;
            default:
                Log.e(LOG_TAG, "Mistake with reading choice");
        }

        result = am.requestAudioFocus(myAudioFocusListener,
                AudioManager.STREAM_MUSIC, durationHint);
        if (result == 1) currentFocus = durationHint;
        myAudioFocusListener = new AudioFocusListener(mediaPlayer, am, currentFocus);
        Log.d(LOG_TAG, "Sound request focus, result: " + result);
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        newIntent.putExtra("label", currentFocus);
        finish();
    }

    private void release(){
        if (mediaPlayer != null) {
            try {
                am.abandonAudioFocus(myAudioFocusListener);
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}
