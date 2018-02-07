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
    private String resMapX;
    private String resMapY;

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

    public String getResMapX() {
        return resMapX;
    }

    public void setResMapX(String resMapX) {
        this.resMapX = resMapX;
    }

    public String getResMapY() {
        return resMapY;
    }

    public void setResMapY(String resMapY) {
        this.resMapY = resMapY;
    }



}
