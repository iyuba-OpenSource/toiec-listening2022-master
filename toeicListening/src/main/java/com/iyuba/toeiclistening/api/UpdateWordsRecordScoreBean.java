package com.iyuba.toeiclistening.api;

public class UpdateWordsRecordScoreBean {

    private String Score;
    private String category;
    private String lessontype;
    private String testCnt;

    public String getScore() {
        return Score;
    }

    public void setScore(String score) {
        Score=score;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category=category;
    }

    public String getLessontype() {
        return lessontype;
    }

    public void setLessontype(String lessontype) {
        this.lessontype=lessontype;
    }

    public String getTestCnt() {
        return testCnt;
    }

    public void setTestCnt(String testCnt) {
        this.testCnt=testCnt;
    }
}
