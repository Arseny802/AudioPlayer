package com.arseny.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by arseny on 26.06.16.
 */
public class AudioFocusListener implements AudioManager.OnAudioFocusChangeListener {

    int label;
    MediaPlayer mediaPlayer;

    public AudioFocusListener(MediaPlayer mediaPlayer, AudioManager audioManager, int label) {
        this.label = label;
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                switch(label){
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        mediaPlayer.stop();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        mediaPlayer.stop();
                        break;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                switch(label){
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        mediaPlayer.pause();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        mediaPlayer.pause();
                        break;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                switch(label){
                    case AudioManager.AUDIOFOCUS_GAIN:
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        //audioManager.vol
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        //audioManager.vol
                        break;
                }
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                switch(label){
                    case AudioManager.AUDIOFOCUS_GAIN:
                        mediaPlayer.start();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                        mediaPlayer.start();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                        mediaPlayer.start();
                        break;
                }
                break;
        }
    }
}