package com.lhk.response;

import java.util.List;

public class ArticleDupResult {
    private Boolean isDup;
    private List<ArticleDupId> dupIds;

    public ArticleDupResult(Boolean isDup, List<ArticleDupId> dupIds) {
        this.isDup = isDup;
        this.dupIds = dupIds;
    }

    public Boolean isIsDup() {
        return isDup;
    }

    public void setIsDup(Boolean dup) {
        this.isDup = dup;
    }

    public List<ArticleDupId> getDupIds() {
        return dupIds;
    }

    public void setDupIds(List<ArticleDupId> dupIDs) {
        this.dupIds = dupIds;
    }

    public ArticleDupResult() {
    }

    @Override
    public String toString() {
        return "ArticleDupResult{" +
                "isDup=" + isDup +
                ", dupIds=" + dupIds +
                '}';
    }
}