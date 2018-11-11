package com.mikenovember.musicality;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LOGGER_TAG = "MainActivity";
    private static final String CURRENT_TRACK_URI = "currentTrackUri";

    private Uri mCurrentTrack;
    private TextView mTrackView;
    private EditText mTrackEdit;
    private Button mPlayButton;
    private MediaPlayer mPlayer;
    private ITrackRepository mRepository;

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void refreshTrackView(){
        mTrackView.setText(mCurrentTrack.toString());
    }

    private void killPlayer() {
        if (mPlayer != null) {
            try {
                mPlayer.release();
                mPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean startPlayer(final int msec) {
        try {
            mPlayer = new MediaPlayer();
            mPlayer.reset();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(getApplicationContext(), mCurrentTrack);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPlayer.seekTo(msec);
                }
            });
            mPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mPlayer.start();
                }
            });
            mPlayer.prepareAsync();
        } catch (IOException e)
        {
            Log.v(LOGGER_TAG, e.getMessage());
            return false;
        }
        return true;
    }

    private void playTrack(Uri uri, int msec) {
        if (!uri.toString().isEmpty())
            mCurrentTrack = uri;
        refreshTrackView();
        killPlayer();
        startPlayer(msec);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        mCurrentTrack = Uri.parse(sharedPref.getString(CURRENT_TRACK_URI, ""));

        mTrackView = findViewById(R.id.track_view);
        mTrackEdit = findViewById(R.id.track_edit);
        mPlayButton = findViewById(R.id.play_button);

        mPlayButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrack(Uri.parse(mTrackEdit.getText().toString()), 0);
            }
        });

        refreshTrackView();

        if (isExternalStorageReadable()) {
            mRepository = new StorageTrackRepository();
            for (ITrackRepository.StreamInfo track : mRepository.getAllTracks()) {
                Log.d(LOGGER_TAG, "title: " + track.mTitle);
                Log.d(LOGGER_TAG, "url: " + track.mUriString);
                for (long beat : track.mMusicalityData.mStructure.mAllBeats)
                    Log.d(LOGGER_TAG, "beat: " + beat);
                Log.d(LOGGER_TAG, "beatCount: " + track.mMusicalityData.mStructure.mAllBeats.size());
            }
        }
        else
            Log.e(LOGGER_TAG, "Cannot read from external storage");
    }

    @Override
    protected void onPause() {
        Log.v(LOGGER_TAG, "onPause()");
        super.onPause();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedPref.edit();
        ed.putString(CURRENT_TRACK_URI, mCurrentTrack.toString());
        ed.commit();
    }
}
