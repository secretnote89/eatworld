package com.chul.chul_eatworldcup;

import java.io.Serializable;

/**
 * Created by leeyc on 2018. 1. 12..
 */
public class restaurantList implements Serializable {
    private String resNM;
    private String resCategory;
    private String resPhonNum;
    private String resAddr;
    private Double resMapX;
    private Double resMapY;

    public String getResNM() {
        return resNM;
    }

    public void setResNM(String resNM) {
        this.resNM = resNM;
    }

    public String getResCategory() {
        return resCategory;
    }

    public void setResCategory(String resCategory) {
        this.resCategory = resCategory;
    }

    public String getResPhonNum() {
        return resPhonNum;
    }

    public void setResPhonNum(String resPhonNum) {
        this.resPhonNum = resPhonNum;
    }

    public String getResAddr() {
        return resAddr;
    }

    public void setResAddr(String resAddr) {
        this.resAddr = resAddr;
    }

    public Double getResMapX() {
        return resMapX;
    }

    public void setResMapX(Double resMapX) {
        this.resMapX = resMapX;
    }

    public Double getResMapY() {
        return resMapY;
    }

    public void setResMapY(Double resMapY) {
        this.resMapY = resMapY;
    }



}
