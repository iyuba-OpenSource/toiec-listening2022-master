package com.iyuba.toeiclistening.event;

public class ResetPlayDataEvent {
    public int position;
    public boolean isNest;  //true 就是下一题  false 就是上一题

    public ResetPlayDataEvent(int position,boolean isNest){
        this.position=position;
        this.isNest=isNest;
    }
}
