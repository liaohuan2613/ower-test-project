package com.lhk.response;

/**
 * @author CHEN FAN
 * @create 2018/9/12 15:18
 */
public class ArticleDupId {
    private String id;
    private String source;

    public ArticleDupId() {
    }

    public ArticleDupId(String id, String source) {
        this.id = id;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "ArticleDupId{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                '}';
    }
}