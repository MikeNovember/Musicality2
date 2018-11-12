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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.stream.Stream;

public class SongSelectActivity extends AppCompatActivity {
    private static final String LOGGER_TAG = "MainActivity";

    private ITrackRepository mRepository;

    /*
    public static class StreamInfoAdapter extends ArrayAdapter<ITrackRepository.StreamInfo> {

        private static class ViewHolder {
            private TextView itemView;
        }

        public StreamInfoAdapter(Context context, int textViewResourceId,
                                 ArrayList<ITrackRepository.StreamInfo> items) {
            super(context, textViewResourceId, items);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(this.getContext())
                        .inflate(R.layout.listview_association, parent, false);

                viewHolder = new ViewHolder();
                viewHolder.itemView = (TextView) convertView.findViewById(R.id.ItemView);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            MyClass item = getItem(position);
            if (item!= null) {
                // My layout has only one TextView
                // do whatever you want with your string and long
                viewHolder.itemView.setText(String.format("%s %d", item.reason, item.long_val));
            }

            return convertView;

        }
    }
    */
    
    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
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

        //StreamInfoAdapter adapter = new StreamInfoAdapter(this, 0, mRepository.getAllTracks());
        //ListView lv = findViewById(R.id.song_select);
        //lv.setAdapter(adapter);

        Intent output = new Intent();
        output.putExtra(MainActivity.TRACK_URL_KEY, mRepository.getAllTracks().get(0).mUriString);
        setResult(RESULT_OK, output);
        finish();
    }

}
