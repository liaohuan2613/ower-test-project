package com.lhk.response;

public class ArticleDupResponse {
    private String status;
    private ArticleDupResult result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArticleDupResult getResult() {
        return result;
    }

    public void setResult(ArticleDupResult result) {
        this.result = result;
    }
}