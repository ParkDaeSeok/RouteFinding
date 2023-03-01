package com.example.routefinding;

import java.io.Serializable;

public class MainData implements Serializable {

    private String text;
    private String cancel;
    private String voice;

    public MainData(String text, String cancel, String voice) {
        this.text = text;
        this.cancel = cancel;
        this.voice = voice;
    }

    public MainData() {
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }
}
