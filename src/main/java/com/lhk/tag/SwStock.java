package com.lhk.tag;


import com.lhk.util.Utils;

import java.io.Serializable;
import java.util.Date;

public class SwStock implements Serializable {

    private String stockCode;

    private String stockName;

    private String companyName;

    private String exchCode;

    private String exchName;

    private String swIndustryCode;

    private String usedNames;

    private Date nameUpdatedTime;

    private Date industryUpdatedTime;

    private Date companyNameUpdateTime;

    private Date createdDate;

    private Date lastModifiedDate;

    private String industryCode;

    public SwStock() {

    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getExchCode() {
        return exchCode;
    }

    public void setExchCode(String exchCode) {
        this.exchCode = exchCode;
    }

    public String getExchName() {
        return exchName;
    }

    public void setExchName(String exchName) {
        this.exchName = exchName;
    }

    public String getSwIndustryCode() {
        return swIndustryCode;
    }

    public void setSwIndustryCode(String swIndustryCode) {
        this.swIndustryCode = swIndustryCode;
        if(swIndustryCode != null && swIndustryCode.length() >= 4) {
            this.industryCode = Utils.concat("SW_", swIndustryCode.substring(0, 4), "00");  //申万个股关联到二级行业
        }
    }

    public String getUsedNames() {
        return usedNames;
    }

    public void setUsedNames(String usedNames) {
        this.usedNames = usedNames;
    }

    public Date getNameUpdatedTime() {
        return nameUpdatedTime;
    }

    public void setNameUpdatedTime(Date nameUpdatedTime) {
        this.nameUpdatedTime = nameUpdatedTime;
    }

    public Date getIndustryUpdatedTime() {
        return industryUpdatedTime;
    }

    public void setIndustryUpdatedTime(Date industryUpdatedTime) {
        this.industryUpdatedTime = industryUpdatedTime;
    }

    public Date getCompanyNameUpdateTime() {
        return companyNameUpdateTime;
    }

    public void setCompanyNameUpdateTime(Date companyNameUpdateTime) {
        this.companyNameUpdateTime = companyNameUpdateTime;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    @Override
    public String toString() {
        return "SwStock{" +
            "stockCode='" + stockCode + '\'' +
            ", stockName='" + stockName + '\'' +
            ", companyName='" + companyName + '\'' +
            ", exchCode='" + exchCode + '\'' +
            ", exchName='" + exchName + '\'' +
            ", swIndustryCode='" + swIndustryCode + '\'' +
            ", nameUpdatedTime=" + nameUpdatedTime +
            ", industryUpdatedTime=" + industryUpdatedTime +
            ", createdDate=" + createdDate +
            ", lastModifiedDate=" + lastModifiedDate +
            ", t006='" + industryCode + '\'' +
            '}';
    }
}
