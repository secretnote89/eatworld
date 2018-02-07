package com.chul.chul_eatworldcup;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by leeyc on 2017. 12. 29..
 */

public class DBHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "DBHelper.db";

    public static final String Create_Table_FOOD_LIST = "CREATE TABLE FOOD_LIST(FOOD_CD TEXT, FOOD_NM TEXT, FOOD_PRI INTEGER);";
   // public static final String Create_Table_FOOD_LIST_KorName = "CREATE TABLE FOOD_LIST_KOR(FOOD_CD TEXT, FOOD_NM TEXT, FOOD_PRI INTEGER);";

    //public static final String Insert_FOOD_LIST_KOR = "INSERT INTO FOOD_LIST VALUES('')";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.i("abcTest", "XXXXX");
        db.execSQL(Create_Table_FOOD_LIST);
  //      db.execSQL(Create_Table_FOOD_LIST_KorName);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

        String sql = "DROP TABLE FOOD_LIST";
        db.execSQL(sql);
        onCreate(db);

    }

    public void onInsert(String table, String food_cd, String food_nm, int food_pri){
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT INTO ";
        String insertComp = insert+table+" VALUES('"+food_cd+"','"+food_nm+"',"+food_pri+");";
Log.d("abcTest", "Query "+insertComp);

       db.execSQL(insertComp);
       //db.close();
    }

    public void onInsertKorName(String table, String food_cd, String food_nm, int food_pri){
        SQLiteDatabase db = getWritableDatabase();
        String insert = "INSERT INTO ";
        String insertComp = insert+table+" VALUES('"+food_cd+"','"+food_nm+"',"+food_pri+");";
        Log.d("abcTest", "QueryKor "+insertComp);

        db.execSQL(insertComp);
        //db.close();
    }

    public ArrayList<FoodListC> getList(){
        ArrayList<FoodListC>list = new ArrayList<FoodListC>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FOOD_LIST",null);

        int LigCount = 0;

        while(cursor.moveToNext()) { // 음식 종류 다 가져오기
            FoodListC foodListC = new FoodListC();
            foodListC.setFood_CD(cursor.getString(0));//cd
            foodListC.setFood_NM(cursor.getString(1));//nm
            foodListC.setFood_PRI(cursor.getInt(2));//pri
            list.add(foodListC);
            LigCount++;
        }

        Log.d("abcTest", "Number of rows "+LigCount );
        db.close();
        return list;

    }

    public ArrayList<FoodListC> getList(String food_cd, int food_num){
        ArrayList<FoodListC> list = new ArrayList<FoodListC>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM FOOD_LIST WHERE FOOD_CD ='"+food_cd+"' ORDER BY RANDOM() LIMIT "+food_num,null);

        while(cursor.moveToNext()) { // 음식 종류 다 가져오기
            FoodListC foodListC = new FoodListC();
            foodListC.setFood_CD(cursor.getString(0));//cd
            foodListC.setFood_NM(cursor.getString(1));//nm
            foodListC.setFood_NM_KOR(cursor.getString(2));
//            foodListC.setFood_PRI(cursor.getInt(3));//pri
            list.add(foodListC);
        }
        db.close();
        return list;
    }



    ////maybe no use

}
