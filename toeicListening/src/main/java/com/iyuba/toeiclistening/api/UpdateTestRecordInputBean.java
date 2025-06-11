package com.iyuba.toeiclistening.api;

public class UpdateTestRecordInputBean {

    private String uid;
    private String UserAnswer;
    private String TestTime;
    private String TestNumber;
    private String RightAnswer;
    private String LessonId;
    private String BeginTime;
    private String AppName;
    private String AnswerResut;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid=uid;
    }

    public String getUserAnswer() {
        return UserAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        UserAnswer=userAnswer;
    }

    public String getTestTime() {
        return TestTime;
    }

    public void setTestTime(String testTime) {
        TestTime=testTime;
    }

    public String getTestNumber() {
        return TestNumber;
    }

    public void setTestNumber(String testNumber) {
        TestNumber=testNumber;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        RightAnswer=rightAnswer;
    }

    public String getLessonId() {
        return LessonId;
    }

    public void setLessonId(String lessonId) {
        LessonId=lessonId;
    }

    public String getBeginTime() {
        return BeginTime;
    }

    public void setBeginTime(String beginTime) {
        BeginTime=beginTime;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName=appName;
    }

    public String getAnswerResut() {
        return AnswerResut;
    }

    public void setAnswerResut(String answerResut) {
        AnswerResut=answerResut;
    }
}
