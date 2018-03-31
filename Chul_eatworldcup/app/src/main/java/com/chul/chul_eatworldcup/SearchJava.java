package com.chul.chul_eatworldcup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import static com.chul.chul_eatworldcup.MainActivity.dong;
import static com.chul.chul_eatworldcup.MainActivity.si;

/**
 * Created by leeyc on 2017. 12. 20..
 */

public class SearchJava extends Activity{
    private static final String CLIENT_ID = "wLIIkD1v3F7aYIpTjdXF";//"BDfRJ_qbTvaVbD3QdC6Y";
    private static final String clientSecret = "CIMd9Lu_vl";//"4fwk2LFzPr";

    String str;
    String foodname;
    ArrayList<restaurantList>resList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.search_test);
        ////
        Intent getintent = getIntent();
        foodname= getintent.getStringExtra("foodName");

        Log.d("abcTest","foodname = "+foodname);

        str = dong;

        gofind();
    }

    public void gofind(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("abcA1","test");

                    resList = getXmlData(str);

                if(resList.size()>1){
                    Log.d("abcTest","SearchJava resList.size>1");
                    Intent intent = new Intent(SearchJava.this,restaurantResult.class);
                    intent.putExtra("resList",resList);
                    startActivity(intent);
                }else if(resList.size()<2){
                    Log.d("abcTest","SearchJava resList.size<2");
                    str = si;

                    Log.d("abcTest","till si si = ? ");

                    resList = getXmlData(str);
                    Intent intent = new Intent(SearchJava.this,restaurantResult.class);
                    intent.putExtra("resList",resList);
                    startActivity(intent);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("abcA2","test");
                    }
                });
            }
        }).start();
        try{
            Thread.sleep(1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        ///Thread.interrupted();
    }


    public ArrayList<restaurantList> getXmlData(String str){
        boolean addrChk = false;

        Log.d("getXmlData","getXmlData()");

           restaurantList rList  =new restaurantList();
           ArrayList<restaurantList>fList = new ArrayList<>();
          // Log.d("abcA3","test");
            str= str+" "+foodname; //get foodName for str

        Log.d("abcTest","str = ?"+str);

           //String str= et1.getText().toString(); //EditText에 작성된 Text얻어오기
            String location = URLEncoder.encode(str); //한글의 경우 인식이 안되기에 utf-8 방식으로 encoding..
          // Log.d("abcA6","test");

           String queryUrl= "https://openapi.naver.com/v1/search/local.xml?" +
                   "query="+location+"&display=10&start=1&sort=random";

           Log.d("abcA7","test");

            try {
                Log.d("abcAt1","test");

                URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
                Log.d("abcAt2","test");

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                connection.setRequestMethod("GET");
                Log.d("abcB1_3","test");
                connection.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
                Log.d("abcB1_4","test");
                connection.setRequestProperty("X-Naver-Client-Secret", clientSecret);//발급받은PW
                Log.d("abcB1_5","test");

                int responseCode = connection.getResponseCode();

               Log.d("abcAt3","test");

                XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
                Log.d("abcAt4","test");
                XmlPullParser xpp= factory.newPullParser();
                Log.d("abcAt5","test");


                if(responseCode==200)
                {
                    xpp.setInput( new InputStreamReader(connection.getInputStream()) );  //inputstream 으로부터 xml 입력받기
                }else {
                    xpp.setInput( new InputStreamReader(connection.getErrorStream()));
                }
                Log.d("abcAt6","test");

                String tag;

                xpp.next();

                int eventType= xpp.getEventType();

                while( eventType != XmlPullParser.END_DOCUMENT ){

                    switch( eventType ){

                        case XmlPullParser.START_DOCUMENT:

                            Log.d("abcde","start = "+"start NAVER XML parsing...\n\n");

                            break;

                        case XmlPullParser.START_TAG:

                            tag= xpp.getName();    //테그 이름 얻어오기

                            if(tag.equals("item")) ;// 첫번째 검색결과

                            else if(tag.equals("title")){

                                xpp.next();

                                if(xpp.getText().contains("Naver Open API"))
                                {

                                }
                                else{

                                    fList.add(rList);

                                    rList=new restaurantList();

                                    rList.setResNM(xpp.getText());

                                }
                            }

                            else if(tag.equals("category")){

                                xpp.next();

                                Log.d("abcTest","cate tag = "+tag+"& "+xpp.getText());

                                rList.setResCategory(xpp.getText());
                                Log.d("abcTest","rListCa"+rList.getResCategory());

                            }

                            else if(tag.equals("description")){

                                xpp.next();
                                Log.d("abcTest","tagA = "+tag);

                            }

                            else if(tag.equals("telephone")){

                                xpp.next();

                                Log.d("abcTest","tele tag = "+tag+"& "+xpp.getText());

                                rList.setResPhonNum(xpp.getText());

                            }

                            else if(tag.equals("address")){
                                xpp.next();

                                Log.d("abcTest","address tag = "+tag+"& "+xpp.getText());

                                //test 20180312 addr
                                AddrPoint addrPoint = getAddr(xpp.getText());
                                rList.setResAddr(xpp.getText());
                                Log.d("abcTest","rListAddr"+rList.getResAddr());
                                rList.setResMapX(addrPoint.getAddrx());
                                Log.d("abcTest","mapX = ? "+addrPoint.getAddrx());
                                rList.setResMapY(addrPoint.getAddry());
                                Log.d("abcTest","mapY = ? "+addrPoint.getAddry());

                                if(addrPoint.getAddrx()!=null || addrPoint.getAddrx()!=""){
                                        addrChk=true;
                                }

                            }

//                            else if(tag.equals("mapx")){
//                                xpp.next();
//
//                                Log.d("abcTest","MAPX tag = "+tag+"& "+xpp.getText());
//                                rList.setResMapX(xpp.getText());
//                            }
//
//                            else if(tag.equals("mapy")){
//                                xpp.next();
//
//                                Log.d("abcTest","MAPY tag = "+tag+"& "+xpp.getText());
//                                rList.setResMapY(xpp.getText());
//                            }
                            break;
                        case XmlPullParser.TEXT:
                            break;
                        case XmlPullParser.END_TAG:

                            break;
                    }

                    eventType= xpp.next();
                    if(eventType==XmlPullParser.END_DOCUMENT){
                        Log.d("abcTest","END_DOCU");
                        if(addrChk){
                            fList.add(rList);
                            addrChk=false;
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("abcE1","test");
            }
            Log.d("abcTest","fList, size = "+fList.size());
            return fList;
        }//getXmlData method....

    AddrPoint getAddr(String xpp){
        String result="";
        AddrPoint addrPoint = new AddrPoint();
        try{
            Log.d("abcTest","test getAddr in searchjava");
            Log.d("abcTest","xpp = ? "+xpp);
            String addr = URLEncoder.encode(xpp, "UTF-8");
            String apiURL = "https://openapi.naver.com/v1/map/geocode?encoding=utf-8&coordType=latlng&query=" + addr; //json
            //String apiURL = "https://openapi.naver.com/v1/map/geocode.xml?query=" + addr; // xml
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                Log.d("abcTest","response code = 200 in getAddr");
                br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine = null;
            StringBuffer response = new StringBuffer();
            Log.d("abcTest","br.readLine = ? "+br.readLine());
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine.trim());
                Log.d("abcTest","inputLine = ? "+inputLine);
//                if(br.readLine().equals("x")){
//                    Log.d("abcTest","x = ? "+br.readLine());
//                    addrPoint.setAddrx(br.readLine());
//                }else if(br.readLine().equals("y")){
//                    Log.d("abcTest","y = ? "+br.readLine());
//                    addrPoint.setAddry(br.readLine());
//                }
            }
            br.close();
            result = "{"+response.toString();
            //result = result.replace(" ","");
            Log.d("abcTest","searchJava getAddr= "+result);

            doJsonParser(result);

        } catch (Exception e) {
            System.out.println(e);
        }
    return addrPoint;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  
    }

    void doJsonParser(String json) {
        String json_str = json;
        Gson gson = new Gson();

//        JsonArray ja = gson.fromJson(json_str, JsonElement.class).getAsJsonArray();
//        for(int i = 0; i < ja.size();i++){
//            JsonObject market = ja.get(i).getAsJsonObject();
//            System.out.println(market.get("symbol") + " : " + market);
//        }

        JsonObject market = gson.fromJson(json_str,JsonElement.class).getAsJsonObject();

       // HashMap map = (HashMap) market.get("items");


    }

}

