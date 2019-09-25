package com.lhk.domain;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class AutoTagArticle {
    private static final Gson GSON = new Gson();

    private String id;
    private String itemId;
    private String title;
    private String brief;
    private String content;
    private String url;
    private String coverUrl;
    private String category;
    private String publishTime;
    private String supplier;
    private String source;
    private String author;
    private String tags;
    private String stock = "";
    private String status;
    private String endTime;
    private String summary;
    private String nature = "";
    private String operationType;
    private String visibility = "";
    private String isDup = "0";
    private int type;
    private String autoTags;
    private String showTags;
    private String simHash;
    private String quality = "";
    private String subjectCategory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getIsDup() {
        return isDup;
    }

    public void setIsDup(String isDup) {
        this.isDup = isDup;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAutoTags() {
        return autoTags;
    }

    public void setAutoTags(String autoTags) {
        this.autoTags = autoTags;
    }

    public String getShowTags() {
        return showTags;
    }

    public void setShowTags(String showTags) {
        this.showTags = showTags;
    }

    public String getSimHash() {
        return simHash;
    }

    public void setSimHash(String simHash) {
        this.simHash = simHash;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSubjectCategory() {
        return subjectCategory;
    }

    public void setSubjectCategory(String subjectCategory) {
        this.subjectCategory = subjectCategory;
    }

    @Override
    public String toString() {
        Map<String, Object> map = new HashMap<>();
        map.put("itemId", this.getItemId());
        map.put("title", this.getTitle());
        map.put("content", this.getContent());
        return GSON.toJson(map);
    }
}