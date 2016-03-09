package com.example.moose.personanongrata;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/*
This is the landing page. From here, a user can:
-go to game.
-go to an about page.
-go to a settings page, which will allow the user t change text size, invert colours, etc.
The settings page is currently unfinished
 */


public class MainMenu extends AppCompatActivity {

    private Button buttonToGame, buttonToVisual, buttonToAbout;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/Bookerly-Regular.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_menu);

        buttonToGame = (Button) findViewById(R.id.buttonToGame);
        buttonToVisual = (Button) findViewById(R.id.buttonToVisual);
        buttonToAbout = (Button) findViewById(R.id.buttonToAbout);

        buttonToGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg1) {
                int theme = R.style.AppThemeNegative;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("THEME", theme);
                startActivity(intent);
            }
        });
        buttonToVisual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VisualActivity.class);
                startActivity(intent);
            }
        });
        buttonToAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg1) {
                int theme = R.style.AppTheme;
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("THEME", theme);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}


