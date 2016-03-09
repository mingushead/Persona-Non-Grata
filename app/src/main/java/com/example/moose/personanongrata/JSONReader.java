package com.example.moose.personanongrata;

import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Moose on 06/11/2015.
 */
public class JSONReader {

    protected AssetManager assetManager;

    public JSONReader(AssetManager _assetManager){

        assetManager = _assetManager;
    }

    public JSONObject getGame(String gameTitle){
        JSONObject game = null;
        try {
            game = new JSONObject(getFileFromAssets(gameTitle));

            //System.out.println(content.getJSONObject("glossary").get("title"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return game;
    }
    public JSONArray getNewGame(String gameTitle){
        JSONArray game = null;
        try {
            game = new JSONArray(getFileFromAssets(gameTitle));

            //System.out.println(content.getJSONObject("glossary").get("title"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return game;
    }

    public String getFileFromAssets(String fileName) {
        try {

            InputStream myFile = assetManager.open(fileName);
            BufferedReader br = null;
            StringBuilder jsonFeedBuilder = new StringBuilder();

            try {

                String sCurrentLine;

                br = new BufferedReader(new BufferedReader(new InputStreamReader(myFile)));

                while ((sCurrentLine = br.readLine()) != null) {
                    jsonFeedBuilder.append(sCurrentLine);

                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return jsonFeedBuilder.toString();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
