package com.mikenovember.musicality;

import org.w3c.dom.Document;

public class TrackMusicalityData {
    public TrackMusicalityData(Document xml) {
        long firstBeat = Long.parseLong(xml.getDocumentElement().getAttribute("firstBeat"));
        int beatCount = Integer.parseInt(xml.getDocumentElement().getAttribute("beatCount"));
        float bpm = Float.parseFloat(xml.getDocumentElement().getAttribute("bpm"));
        mStructure = new SongStructure(firstBeat, beatCount, bpm);
    }

    public SongStructure mStructure;
}
