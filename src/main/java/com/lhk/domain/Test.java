package com.lhk.domain;

public class Test {
    private String userName;
    private String idNumber;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public String toString() {
        return "Test{" +
                "userName='" + userName + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
