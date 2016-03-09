package com.example.moose.personanongrata;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class VisualActivity extends Activity {

    private CanvasView customCanvas;
    private Switch themeToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_visual);

        customCanvas = (CanvasView) findViewById(R.id.signature_canvas);
        themeToggle = (Switch) findViewById(R.id.themeSwitch);

        themeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                int cTheme;
                if(isChecked){
                    cTheme = R.style.AppThemeNegative;
                }
                else{
                    cTheme = R.style.AppTheme;
                }
                setTheme(cTheme);
                setContentView(R.layout.content_visual);
            }
        });
    }


}