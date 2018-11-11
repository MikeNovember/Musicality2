package com.mikenovember.musicality;

import java.util.ArrayList;
import java.util.List;

public class SongStructure {
    public SongStructure(long firstBeat, int beatCount, float bpm) {
        mAllBeats = new ArrayList<>();
        mAllBeats.add(firstBeat);
        long beatOffset = (long)(60000.0f / bpm);
        for (int i = 1 ; i < beatCount ; ++i)
            mAllBeats.add(firstBeat + beatOffset*i);
    }

    public enum PhraseType
    {
        INTRO,
        VERSE,
        CHORUS,
        BRIDGE
    }

    public class Phrase {
        public PhraseType mType;
        public List<Long> mBeatOffsets;
    }

    public List<Phrase> mPhrases;
    List<Long> mAllBeats;
}
