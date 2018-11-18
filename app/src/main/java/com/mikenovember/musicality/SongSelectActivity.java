package com.mikenovember.musicality;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.stream.Stream;

public class SongSelectActivity extends AppCompatActivity {
    private static final String LOGGER_TAG = "MainActivity";

    private ITrackRepository mRepository;
    private StreamInfoAdapter mAdapter;
    private ListView mListView;

    public static class StreamInfoAdapter extends ArrayAdapter<ITrackRepository.StreamInfo> {

        private static class ViewHolder {
            private TextView title;
            private TextView url;
        }

        public StreamInfoAdapter(Context context, int textViewResourceId,
                                 ArrayList<ITrackRepository.StreamInfo> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ITrackRepository.StreamInfo streamInfo = getItem(position);
            ViewHolder viewHolder;

            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.track_list_row, parent, false);
                viewHolder.title = convertView.findViewById(R.id.track_title);
                viewHolder.url = convertView.findViewById(R.id.track_url);
                convertView.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) convertView.getTag();

            viewHolder.title.setText(streamInfo.mTitle);
            viewHolder.url.setText(streamInfo.mUriString);
            return convertView;
        }
    }
    
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    private void onListItemClick(ITrackRepository.StreamInfo streamInfo) {
        Intent output = new Intent();
        output.putExtra(MainActivity.TRACK_URL_KEY, streamInfo.mUriString);
        setResult(RESULT_OK, output);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_select);

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

        mAdapter = new StreamInfoAdapter(this, 0, mRepository.getAllTracks());
        mListView = findViewById(R.id.track_list);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ITrackRepository.StreamInfo streamInfo = (ITrackRepository.StreamInfo) mListView.getItemAtPosition(i);
                onListItemClick(streamInfo);
            }
        });

    }

}
