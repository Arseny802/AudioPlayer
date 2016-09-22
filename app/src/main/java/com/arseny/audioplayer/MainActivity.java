package com.arseny.audioplayer;

import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.arseny.audioplayer.R.layout.file_manager_activity_helper;

/**
 * Created by arseny on 09.06.16.
 */
public class MainActivity extends AppCompatActivity implements OnPreparedListener,
        OnCompletionListener {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    final String DATA_HTTP = "http://dl.dropboxusercontent.com/u/6197740/explosion.mp3";
    final String DATA_STREAM = "http://online.radiorecord.ru:8101/rr_128";
    private String AUDIO_NAME = "/Heaven Shall Burn - Godiva.mp3";
    final String DATA_SD = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC) + AUDIO_NAME;
    final Uri DATA_URI = ContentUris.withAppendedId(
            android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, 13359);

    private List<String> ListOfMusic = new ArrayList<>();
    private View PREVIOUS_VIEW = null;

    public MediaPlayer mediaPlayer;
    private AudioFocusListener myAudioFocusListener;
    private AudioManager am;
    public SeekBar seekBar;
    private CheckBox chbLoop;
    private String currentPath = "storage/sdcard1/Музыка/Heaven Shall Burn";
    private File current_file = new File(currentPath);
    private boolean paused = false;
    private TextView startTimeView;
    private TextView endTimeView;
    private Handler handlerStartTime;
    private Handler handlerEndTime;
    private Handler handlerSetColorOnItem;
    public ListView list;
    private ArrayAdapter<String> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        chbLoop = (CheckBox) findViewById(R.id.chbLoop);
        chbLoop.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mediaPlayer != null)
                    mediaPlayer.setLooping(isChecked);
            }
        });

        seekBar = (SeekBar)findViewById(R.id.seekBar);
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                seekChange(view);
                return false;
            }
        });

        startTimeView = (TextView)findViewById(R.id.startTime);
        endTimeView = (TextView)findViewById(R.id.endTime);
        assert endTimeView != null;
        endTimeView.setText("99:99");
        startTimeView.setText("00:00");

        list = (ListView) findViewById(android.R.id.list);
        getListOfMusic();
        fillList();

        handlerStartTime = new Handler() {
            public void handleMessage(android.os.Message msg) {
                startTimeView.setText(timeToString(msg.what));
                seekBar.setProgress(msg.what);
            }
        };

        handlerEndTime = new Handler(){
            public void handleMessage(android.os.Message msg){
                endTimeView.setText(timeToString(msg.what));
                seekBar.setMax(msg.what);
            }
        };

        handlerSetColorOnItem = new Handler(){
            public void handleMessage(android.os.Message index){

                //TODO: change this fucking shit. Hint: list.findViewById(ListOfMusic.indexOf(str))
                Log.e(LOG_TAG, list.getParent().toString());
                Log.e(LOG_TAG, list.toString());
                Log.e(LOG_TAG, list.getAdapter().toString());
                Log.e(LOG_TAG, String.valueOf(list.getAdapter().getView(5, null, list)));
                //setColorOnItem(list.getAdapter().getView(5, null, list));
            }
        };

        setColorOnItem(getViewByPosition(6, list));
        View view = null;
        list.getAdapter().getView(5, view, list);
        Log.e(LOG_TAG, String.valueOf(view));
        if(view!=null)setColorOnItem(view);

        View tv= list.getAdapter().getView(5, null, list).findViewById(R.id.list_item);
        Log.e(LOG_TAG, String.valueOf(tv));
        if(tv!=null)setColorOnItem(tv);

        view = list.getChildAt(5);
        Log.e(LOG_TAG, String.valueOf(view));
        if(view!=null)setColorOnItem(view);
        Log.e(LOG_TAG, String.valueOf(list.getChildCount()));
    }

    private void setColorOnItem(View view){
        if (PREVIOUS_VIEW != null) {
            PREVIOUS_VIEW.setBackgroundColor(0xFFFFFFFF);
            PREVIOUS_VIEW.invalidate();
        }
        PREVIOUS_VIEW = view;
        view.setBackgroundColor(0xFFFF8000);
        view.invalidate();
    }

    protected Thread audioThread = new Thread(
            new Runnable() {
                public void run() {
                    if (mediaPlayer!=null)
                        while (mediaPlayer.isPlaying() || paused || chbLoop.isChecked())
                            Thread.yield();

                    if (Thread.currentThread().isInterrupted()) return;
                    for(String audio : ListOfMusic) {
                        if (!Thread.currentThread().isInterrupted()) {
                            Log.i(LOG_TAG, audio);
                            playSong(audio);

                            try {
                                while (mediaPlayer.isPlaying() || paused || chbLoop.isChecked()) {
                                    if (Thread.currentThread().isInterrupted()) {
                                        //releaseMP();
                                        return;
                                    }
                                    Thread.sleep(1);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                if (Thread.currentThread().isInterrupted()) {
                                    //releaseMP();
                                    return;
                                }
                            }
                        } else{
                            //releaseMP();
                            return;
                        }
                    }
                }
            }
    );

    private void seekChange(View view){
        if(mediaPlayer != null) {
            SeekBar sb = (SeekBar)view;
            mediaPlayer.seekTo(sb.getProgress());
            Log.i(LOG_TAG, String.valueOf((sb.getProgress())));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){
            case R.id.action_settings:
                startActivityForResult(new Intent(this, settings.class), 2);
                return true;
            case R.id.file_manage:
                startActivityForResult(new Intent(this, FileManager.class), 1);
                return true;
            case R.id.radio_activity:
                releaseMP();
                Log.d(LOG_TAG, "start Stream");
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(DATA_STREAM);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Log.d(LOG_TAG, "prepareAsync");
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.prepareAsync();
                return true;
            case R.id.search_information:
                Intent intent = new Intent(this, ArtistInformation.class);
                intent.putExtra("Artist name", getArtistNameBySongName(AUDIO_NAME));
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if (data == null) {
                return;
            }
            current_file = new File(data.getStringExtra("path"));
            releaseMP();
            playSong(current_file.getAbsolutePath());

            current_file = new File(current_file.getAbsolutePath().
                    replace(getSongNameByPath(current_file.getAbsolutePath()), ""));

            audioThread.start();
            getListOfMusic();
            fillList();
        } else if (requestCode == 2) {
            myAudioFocusListener = new AudioFocusListener
                    (mediaPlayer, am, data.getIntExtra("label", AudioManager.AUDIOFOCUS_GAIN));
        }
    }

    public void getListOfMusic(){
        this.ListOfMusic.clear();
        String fileName;
        for (File file : current_file.listFiles()) {
            fileName = file.getAbsolutePath();
            if (fileName.endsWith(".mp3")){
                ListOfMusic.add(fileName);
            }
        }
    }

    private void fillList(){
        final String[] stringAdapter = new String[ListOfMusic.size()];
        ListOfMusic.toArray(stringAdapter);
        for (int iter = 0; iter < ListOfMusic.size(); ++iter){
            String[] parts = stringAdapter[iter].split("/");
            stringAdapter[iter] = parts[parts.length-1];
        }
        musicList = new ArrayAdapter<>
                (this, file_manager_activity_helper, stringAdapter);
        if (list != null) {
            list.setAdapter(musicList);
        }
    }

    public void onItemSelected(View view) {
        int position = ((ListView)view.getParent()).getPositionForView(view);

        releaseMP();
        playSong(this.ListOfMusic.get(position));

        if (audioThread.isAlive()) audioThread.interrupt();
        else audioThread.start();
    }

    public void onClickStart(View view) {
        releaseMP();

        try {
            switch (view.getId()) {
                case R.id.btnStartHttp:
                    Log.d(LOG_TAG, "start HTTP");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(DATA_HTTP);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    Log.d(LOG_TAG, "prepareAsync");
                    mediaPlayer.setOnPreparedListener(this);
                    mediaPlayer.prepareAsync();
                    break;
                case R.id.btnStartStream:
                    if(!audioThread.isAlive()) audioThread.start();
                    break;
                case R.id.btnStartSD:
                    Log.d(LOG_TAG, "start SD");
                    playSong("storage/sdcard1/Музыка/Heaven Shall Burn/Heaven Shall Burn - Godiva.mp3");
                    break;
                case R.id.btnStartUri:
                    Log.d(LOG_TAG, "start Uri");
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(this, DATA_URI);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaPlayer == null)
            return;

        mediaPlayer.setLooping(chbLoop.isChecked());
        mediaPlayer.setOnCompletionListener(this);
    }

    public void onClick(View view) throws InterruptedException {
        if (mediaPlayer == null)
            return;
        switch (view.getId()) {
            case R.id.btnPause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    paused = true;
                }
                break;
            case R.id.btnResume:
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    paused = false;
                }
                break;
            case R.id.btnStop:
                paused = false;
                mediaPlayer.stop();
                releaseMP();
                if (audioThread.isAlive()) {
                    audioThread.interrupt();
                    audioThread.join();
                }
                if (PREVIOUS_VIEW != null) {
                    PREVIOUS_VIEW.setBackgroundColor(0xFFFFFFFF);
                    PREVIOUS_VIEW.invalidate();
                    PREVIOUS_VIEW = null;
                }
                startTimeView.setText("00:00");
                endTimeView.setText("99:99");
                break;
            case R.id.btnBackward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3*1000);
                break;
            case R.id.btnForward:
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3*1000);
                break;
            case R.id.btnInfo:
                Log.d(LOG_TAG, "Playing " + mediaPlayer.isPlaying());
                Log.d(LOG_TAG, "Time " + mediaPlayer.getCurrentPosition() + " / "
                        + mediaPlayer.getDuration());
                Log.d(LOG_TAG, "Looping " + mediaPlayer.isLooping());
                Log.d(LOG_TAG, "Volume " + am.getStreamVolume(AudioManager.STREAM_MUSIC));
                break;
        }
    }

    public View getViewByPosition(int position, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position >= firstListItemPosition || position <= lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    Thread timeThread = new Thread(new Runnable() {
        public void run() {
            if (Thread.currentThread().isInterrupted()) return;
            else {
                handlerSetColorOnItem.sendEmptyMessage(getIndexBySongName(AUDIO_NAME));
                handlerEndTime.sendEmptyMessage(mediaPlayer.getDuration());
                while (!Thread.currentThread().isInterrupted() && mediaPlayer != null)
                    if (mediaPlayer.isPlaying())
                        handlerStartTime.sendEmptyMessageDelayed(mediaPlayer.getCurrentPosition(), 500);
            }
        }
    });

    private void playSong(String songPath){
        releaseMP();
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(songPath);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            mediaPlayer.start();

            AUDIO_NAME = getSongNameByPath(songPath);

            if(!timeThread.isAlive()) timeThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String timeToString(int time){
        String minutes = String.valueOf(time/60000);
        String seconds = String.valueOf((time/1000)%60);
        if( (time/600000) < 1)
            if( ((time/1000)%60) < 10) return "0" + minutes + ":" + "0" + seconds;
                else return "0" + minutes + ":" + seconds;
            else return minutes + ":" + seconds;
    }

    private String getSongNameByPath(String path){
        String[] parts = path.split("/");
        return parts[parts.length-1];
    }

    private int getIndexBySongName(String name) {
        assert list != null;
        for (String str : ListOfMusic)
            if(str.equals(current_file.getAbsolutePath() + name))
                return ListOfMusic.indexOf(str);
        return -1;
    }

    private String getArtistNameBySongName(String songName){
        if(songName == null) return "No content";

        String artistName = songName.split("-")[0].replace("/", "");
        char[] charArray;

        while (artistName.contains("  ")) artistName = artistName.replaceAll("  ", " ");
        charArray = artistName.toCharArray();
        while (artistName.endsWith(" ")) {
            artistName = String.copyValueOf(charArray, 0, charArray.length-1);
            charArray = artistName.toCharArray();
        }

        return artistName;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(LOG_TAG, "onPrepared");
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(LOG_TAG, "onCompletion");
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (timeThread.isAlive()) timeThread.interrupt();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
        if (timeThread.isAlive()) timeThread.interrupt();
        if (audioThread.isAlive()) audioThread.interrupt();
    }
}