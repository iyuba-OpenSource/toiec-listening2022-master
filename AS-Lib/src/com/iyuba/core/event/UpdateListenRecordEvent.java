package com.iyuba.core.event;

import com.iyuba.core.common.protocol.base.RegistResponse;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeResponse;

import java.util.ArrayList;
import java.util.List;

public class UpdateListenRecordEvent {

    public GetStudyRecordByTestModeResponse response;

    public ArrayList<ListeningTestRecord> deliver;

    public UpdateListenRecordEvent(ArrayList<ListeningTestRecord> deliver, GetStudyRecordByTestModeResponse response) {
        this.deliver = deliver;
        this.response = response;

    }
}
