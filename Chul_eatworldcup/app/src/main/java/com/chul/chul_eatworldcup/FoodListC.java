package com.chul.chul_eatworldcup;

/**
 * Created by leeyc on 2017. 12. 30..
 */

public class FoodListC {

    private String Food_CD;
    private String Food_NM;
    private String Food_NM_KOR;
    private int Food_PRI;

    public void setFood_NM(String food_NM) {
        Food_NM = food_NM;
    }

    public void setFood_CD(String food_CD) {
        Food_CD = food_CD;
    }

    public void setFood_PRI(int food_PRI) {
        Food_PRI = food_PRI;
    }

    public String getFood_CD() {
        return Food_CD;
    }

    public int getFood_PRI() {
        return Food_PRI;
    }

    public String getFood_NM() {
        return Food_NM;
    }

    public String getFood_NM_KOR() {
        return Food_NM_KOR;
    }

    public void setFood_NM_KOR(String food_NM_KOR) {
        Food_NM_KOR = food_NM_KOR;
    }
}
