package com.mikenovember.musicality;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
    public static final String TRACK_URL_KEY = "url";

    private Uri mCurrentTrack;
    private TextView mTrackView;
    private EditText mTrackEdit;
    private Button mPlayButton;
    private Button mSelectButton;
    private MediaPlayer mPlayer;

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

    private void selectTrack() {
        Intent intent = new Intent(this, SongSelectActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0)
            if (resultCode == RESULT_OK)
                mCurrentTrack = Uri.parse(data.getStringExtra(TRACK_URL_KEY));
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
        mSelectButton = findViewById(R.id.select_button);

        mPlayButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playTrack(Uri.parse(mTrackEdit.getText().toString()), 0);
            }
        });

        mSelectButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTrack();
            }
        });

        refreshTrackView();
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
