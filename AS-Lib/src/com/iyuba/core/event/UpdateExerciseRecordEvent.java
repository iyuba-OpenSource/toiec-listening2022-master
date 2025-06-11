package com.iyuba.core.event;

import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.me.protocol.GetStudyRecordByTestModeResponse;
import com.iyuba.core.me.protocol.GetTestRecordDetailResponse;

import java.util.ArrayList;

public class UpdateExerciseRecordEvent {

    public GetTestRecordDetailResponse response;

    public ArrayList<ExerciseRecord> deliver;

    public UpdateExerciseRecordEvent(ArrayList<ExerciseRecord> deliver, GetTestRecordDetailResponse response) {
        this.deliver = deliver;
        this.response = response;

    }
}
