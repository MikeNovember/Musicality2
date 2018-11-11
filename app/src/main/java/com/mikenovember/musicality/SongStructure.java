package com.mikenovember.musicality;

import java.util.List;

public class SongStructure {
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
}
