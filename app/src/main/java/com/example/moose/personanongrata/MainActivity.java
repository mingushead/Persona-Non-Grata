package com.example.moose.personanongrata;

/*
This is the main game view. From here the game is loaded from the JSON blob in /assets. Then, individual slides are loaded
into the view from this JSON blob, according to the user's position in the game, decisions, etc.

 */
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends Activity {
    //These JSON objects both represent entire games. We have included two different formats (JSONObject + JSONArray) during the beta
    //phase of the app, currently we are using 'newContent', although this may change during the course of development.
    protected JSONObject content;
    protected JSONArray newContent;
    //this is the main view for the game, it is repopulated with new content during the course of the game.
    LinearLayout mainView;
    protected AssetManager assetManager;
    //Buttons 1-5 represent the possible paths a user may take through the story, forward and toHistory are for navigating through
    // the already visited slides.
    Button button1, button2, button3, button4, button5, forwardButton, toHistoryButton;
    Button[] buttonArray;
    ImageView backgroundImage, foregroundImage;

    //slidePlaceholder is set to a1 to represent the default slide for a new game.
    // TODO: update this to get the user's current location from memory upon returning to a previously started game.
    String slidePlaceholder = "a1";
    int userPlaceholder = -1;
    List<String> history;
    TextView mainText;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Signika-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        super.onCreate(savedInstanceState);
        //Utils.onActivityCreateSetTheme(this, 2);
        //Utils.changeToTheme(this, 2);
        int cTheme = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cTheme = extras.getInt("THEME");
        }
        this.setTheme(cTheme);
        setContentView(R.layout.activity_main);
        assetManager = getBaseContext().getAssets();

        //not used currently
        content = getGame("sampleGame.json");

        //this is our game, in it's entirety
        newContent = getNewGame("newNonGrata.json");

        scrollView = (ScrollView) findViewById(R.id.myScrollView);
        scrollView.computeScroll();
        scrollView.setSmoothScrollingEnabled(true);
        mainText = (TextView) findViewById(R.id.mainText);
        mainView = (LinearLayout) findViewById(R.id.mainUIView);
       // buttonView = (LinearLayout) findViewById(R.id.buttonLinearLayout);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
       // backButton = (Button) findViewById(R.id.backButton);
        forwardButton = (Button) findViewById(R.id.forwardButton);
        toHistoryButton = (Button) findViewById(R.id.toHistoryButton);

        forwardButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //user's place in the list of visited slides
                userPlaceholder++;
                try {
                    if(userPlaceholder == history.size()-1){
                        //loadNewContent will redraw the buttons required to mavigate to an unvisited slide.
                        //this is called if the user has to decide which path to take, as opposed to scrolling through his history.
                        loadNewContent(history.get(history.size() - 1));
                        scrollView = (ScrollView) findViewById(R.id.myScrollView);
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                    else {
                        loadOldContent();
                        scrollView = (ScrollView) findViewById(R.id.myScrollView);
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Unable to locate destination slide");
                }
            }
        });
        //back button allows user to view his previously visited slides
        toHistoryButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                userPlaceholder--;
                loadOldContent();
                scrollView = (ScrollView) findViewById(R.id.myScrollView);
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        buttonArray = new Button[]{button1,button2,button3,button4,button5};
        backgroundImage = (ImageView) findViewById(R.id.bgImg);
        foregroundImage = (ImageView) findViewById(R.id.fgImg);
        //history holds a list of all the slides a user visited to reach his current point
        //this enables navigation of previously visited slides, without allowing a user to change any of his previous
        // decisions
        history = new ArrayList<>();
        history.add("a1");
        userPlaceholder = history.size() -1;
        try {
            //This to be used at a later date, once the app has been opensourced to allow dynamic app generation
            String title = content.getString("gameTitle");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //loads the slide
        loadNewContent(slidePlaceholder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        System.out.println("changed");
        super.onConfigurationChanged(newConfig);
        loadNewContent(slidePlaceholder);
        System.out.println( slidePlaceholder);
    }

    //this function is not used currently, see getNewGame()
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

    //this gets a game from memory.
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
    //this loads a new slide
    private void loadNewContent(String _slideID){
        int noOfButtons;
        String slideID;
        //remove forward button, as no forward slides exist in the users history
        forwardButton.setVisibility(View.GONE);
        try {
            JSONObject currentSlide = loadSlide(_slideID);
            slideID = currentSlide.getString("slide");
            if (!history.get(history.size() - 1).equals(slideID)) {
                history.add(slideID);
                userPlaceholder = history.size()-1;
            }
            try{
                mainText.setText(currentSlide.getString("body"));
                mainText.setVisibility(View.VISIBLE);
            }
            catch (Exception e){
                e.printStackTrace();
                mainText.setVisibility(View.GONE);
            }
            try{
                String bgImage = currentSlide.getString("backgroundImage");
                int bgImagePath = getResources().getIdentifier(bgImage, "drawable", getPackageName());
                System.out.println(slideID);
                backgroundImage.setImageResource(bgImagePath);
                backgroundImage.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("No background image found");
                backgroundImage.setVisibility(View.GONE);
            }
            try{
                String fgImage = currentSlide.getString("backgroundImage");
                int fgImagePath = getResources().getIdentifier(fgImage, "drawable", getPackageName());
                foregroundImage.setImageResource(fgImagePath);
                foregroundImage.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("No foreground image found");
                foregroundImage.setVisibility(View.GONE);
            }

            removeButtons();

            JSONArray jsonButtons = currentSlide.getJSONArray("paths");
            noOfButtons = jsonButtons.length();
            for(int i = 0; i < noOfButtons; i++){
                final JSONObject thisButton = jsonButtons.getJSONObject(i);
                buttonArray[i].setText(thisButton.getString("pathText"));
                buttonArray[i].setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            // chapterPlaceholder = thisButton.getString("destinationChapter");
                            slidePlaceholder = thisButton.getString("targetSlide");
                            loadNewContent(slidePlaceholder);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            System.out.println("Unable to locate destination slide");
                        }
                    }
                });
                buttonArray[i].setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Could not load slide");
        }
        if(history.size() != 0) for(String element : history) {System.out.println("Hist: " + element);}
        System.out.println("Size: " + history.size());
        scrollView = (ScrollView) findViewById(R.id.myScrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    //load an old slide, from the user's history. If a background/foreground/body text object is found, load it.
    private void loadOldContent(){
        removeButtons();
        String slideID = history.get(userPlaceholder);
        try {
            JSONObject currentSlide = loadSlide(slideID);
            slideID = currentSlide.getString("slide");
            try{
                mainText.setText(currentSlide.getString("body"));
                mainText.setVisibility(View.VISIBLE);
            }
            catch (Exception e){
                e.printStackTrace();
                mainText.setVisibility(View.GONE);
            }
            try{
                String bgImage = currentSlide.getString("backgroundImage");
                int bgImagePath = getResources().getIdentifier(bgImage, "drawable", getPackageName());
                System.out.println(slideID);
                // backgroundImage.setImageResource(bgImagePath);
                backgroundImage.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("No background image found");
                backgroundImage.setVisibility(View.GONE);
            }
            try{
                String fgImage = currentSlide.getString("backgroundImage");
                int fgImagePath = getResources().getIdentifier(fgImage, "drawable", getPackageName());
                foregroundImage.setImageResource(fgImagePath);
                foregroundImage.setVisibility(View.VISIBLE);
            }
            catch(Exception e){
                e.printStackTrace();
                System.out.println("No foreground image found");
                foregroundImage.setVisibility(View.GONE);
            }

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Unable to load slide from history");
        }
        forwardButton.setVisibility(View.VISIBLE);
       /* int id = getResources().getIdentifier(slideID, "drawable", getPackageName());
        backgroundImage.setImageResource(id);*/
    }

    //grab the slide details from the JSON object, and pass them back to loadNewContent()
    JSONObject loadSlide(String _slideId){
        JSONObject slide = null;
        try {
            int i = 0;
            while (i < newContent.length()) {
                System.out.println("testing " + i);
                if (newContent.getJSONObject(i).getString("slide").equals(_slideId)) {
                    slide = newContent.getJSONObject(i);
                    break;
                }
                i++;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return slide;
    }

    //add/remove UI button elements
    void removeButtons(){
        for(int i = 0; i < buttonArray.length; i++){
            buttonArray[i].setVisibility(View.GONE);
        }
        if(userPlaceholder > 0){
            toHistoryButton.setVisibility(View.VISIBLE);
        }
        else{
            toHistoryButton.setVisibility(View.GONE);
        }
        if(userPlaceholder > history.size()-1){
            forwardButton.setVisibility(View.GONE);
        }
        else {
            forwardButton.setVisibility(View.GONE);
        }
    }
}


