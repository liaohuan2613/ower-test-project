package com.lhk.regex;

import java.util.List;

public class EventRegex {
    private String eventCode;
    private String eventName;
    private String eventRegex;
    private List<String> topTwoWords;
    private List<String> lastTwoWords;

    public EventRegex(String eventCode, String eventName, String eventRegex, List<String> topTwoWords, List<String> lastTwoWords) {
        this.eventCode = eventCode;
        this.eventName = eventName;
        this.eventRegex = eventRegex;
        this.topTwoWords = topTwoWords;
        this.lastTwoWords = lastTwoWords;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventRegex() {
        return eventRegex;
    }

    public void setEventRegex(String eventRegex) {
        this.eventRegex = eventRegex;
    }

    public List<String> getTopTwoWords() {
        return topTwoWords;
    }

    public void setTopTwoWords(List<String> topTwoWords) {
        this.topTwoWords = topTwoWords;
    }

    public List<String> getLastTwoWords() {
        return lastTwoWords;
    }

    public void setLastTwoWords(List<String> lastTwoWords) {
        this.lastTwoWords = lastTwoWords;
    }
}
