package com.iyuba.core.event;

import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.common.sqlite.mode.test.WordsUpdateRecord;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeResponse;
import com.iyuba.core.me.protocol.GetWordsResponse;

import java.util.ArrayList;

public class UpdateWordsRecordEvent {

    public GetWordsResponse response;

    public ArrayList<WordsUpdateRecord> deliver;

    public UpdateWordsRecordEvent(ArrayList<WordsUpdateRecord> deliver, GetWordsResponse response) {
        this.deliver = deliver;
        this.response = response;

    }
}
