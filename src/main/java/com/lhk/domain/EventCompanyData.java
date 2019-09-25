package com.lhk.domain;

public class EventCompanyData {
    private String company;
    private String companyCode;
    private String originalText;
    private String typeStr;
    private TendencyFlag pointOfView;

    public EventCompanyData() {
    }

    public EventCompanyData(String company, String companyCode, String originalText, String typeStr,
                            String pointOfView) {
        this.company = company;
        this.companyCode = companyCode;
        this.originalText = originalText.replace("\"", "'");
        this.typeStr = typeStr;
        this.pointOfView = TendencyFlag.parse(pointOfView);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText.replace("\"", "'");
    }

    public String getTypeStr() {
        return typeStr;
    }

    public void setTypeStr(String typeStr) {
        this.typeStr = typeStr;
    }

    public TendencyFlag getPointOfView() {
        return pointOfView;
    }

    public void setPointOfView(TendencyFlag pointOfView) {
        this.pointOfView = pointOfView;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            EventCompanyData typedObject = (EventCompanyData) anObject;
            equalObjects = this.getCompany().equals(typedObject.getCompany()) &&
                    this.getOriginalText().equals(typedObject.getOriginalText()) &&
                    this.getPointOfView().equals(typedObject.getPointOfView());
        }

        return equalObjects;
    }
}
