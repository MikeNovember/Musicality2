package com.mikenovember.musicality;

import org.w3c.dom.Document;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

public interface ITrackRepository {
    class StreamInfo{
        public StreamInfo(Document xml){
            // do magic here
            mMusicalityData = new TrackMusicalityData(xml);
        }

        public String mUriString;
        public String mTitle;
        public TrackMusicalityData mMusicalityData;
    }

    StreamInfo getRandomTrack();
    StreamInfo findByTitle(String title);
    List<StreamInfo> getAllTracks();
    List<StreamInfo> lookupByTitle(String title);
}
