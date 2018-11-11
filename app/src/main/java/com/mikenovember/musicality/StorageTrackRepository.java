package com.mikenovember.musicality;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class StorageTrackRepository implements ITrackRepository {
    private static final String LOGGER_TAG = "MainActivity";
    static final File DEFAULT_LOCATION;

    static {
        DEFAULT_LOCATION = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "musicalityTracks");
    }

    public StorageTrackRepository() {
        boolean result = DEFAULT_LOCATION.mkdirs();
        Log.d(LOGGER_TAG, "mkdirs result: " + result);
    }

    @Override
    public StreamInfo getRandomTrack() {
        return null;
    }

    @Override
    public StreamInfo findByTitle(String title) {
        return null;
    }

    @Override
    public List<StreamInfo> getAllTracks(){
        List<File> tracks = getFileList(DEFAULT_LOCATION);
        List<StreamInfo> streams = new ArrayList<>();

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            for (File track : tracks) {
                try {
                    Document document = documentBuilder.parse(track);
                    streams.add(new StreamInfo(document));
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return streams;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<StreamInfo> lookupByTitle(String title) {
        return null;
    }

    private List<File> getFileList(File parentDir) {
        List<File> foundFiles = new ArrayList();
        File[] files = parentDir.listFiles();

        if (files == null)
            return foundFiles;

        for (File file : files) {
            if (!file.isDirectory())
                foundFiles.add(file);
        }

        return foundFiles;
    }
}
