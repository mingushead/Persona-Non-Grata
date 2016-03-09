package com.example.moose.personanongrata;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Moose on 07/11/2015.
 */
public class AnimationHandler extends SurfaceView implements SurfaceHolder.Callback  {


    public AnimationHandler(Context context) {
        super(context);
        getHolder().addCallback(this);
    }


    @Override
    public void onDraw(Canvas canvas) {
        //do drawing stuff here.
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {


    }

    class PanelThread extends Thread {
        private SurfaceHolder _surfaceHolder;
        private AnimationHandler _panel;
        private boolean _run = false;


        public PanelThread(SurfaceHolder surfaceHolder, AnimationHandler panel) {
            _surfaceHolder = surfaceHolder;
            _panel = panel;
        }


        public void setRunning(boolean run) { //Allow us to stop the thread
            _run = run;
        }


        @Override
        public void run() {
            Canvas c;
            while (_run) {     //When setRunning(false) occurs, _run is
                c = null;      //set to false and loop ends, stopping thread


                try {


                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder) {


                        //Insert methods to modify positions of items in onDraw()
                        postInvalidate();


                    }
                } finally {
                    if (c != null) {
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
    PanelThread _thread;
    @Override
    public void surfaceCreated(SurfaceHolder holder) {


        setWillNotDraw(false); //Allows us to use invalidate() to call onDraw()


        _thread = new PanelThread(getHolder(), this); //Start the thread that
        _thread.setRunning(true);                     //will make calls to
        _thread.start();                              //onDraw()
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            _thread.setRunning(false);                //Tells thread to stop
            _thread.join();                           //Removes thread from mem.
        } catch (InterruptedException e) {}
    }
}
