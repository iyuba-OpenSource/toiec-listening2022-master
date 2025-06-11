package com.iyuba.toeiclistening.event;

public class TextParagraphEvent {
    public int currParagraph;

    public TextParagraphEvent(int position){
        this.currParagraph=position;
    }
}
