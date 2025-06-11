package com.iyuba.core.event;

import com.iyuba.core.common.sqlite.mode.EvaluateRecord;
import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeResponse;
import com.iyuba.core.me.protocol.GetVoaTestRecordResponse;

import java.util.ArrayList;

public class UpdateEvaluateRecordEvent {

    public GetVoaTestRecordResponse response;

    public ArrayList<EvaluateRecord> deliver;

    public UpdateEvaluateRecordEvent(ArrayList<EvaluateRecord> deliver, GetVoaTestRecordResponse response) {
        this.deliver = deliver;
        this.response = response;

    }
}
