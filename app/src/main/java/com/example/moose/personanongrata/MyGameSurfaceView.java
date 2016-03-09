package com.example.moose.personanongrata;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyGameSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    SurfaceHolder surfaceHolder;

    Thread myGameThread = null;

    int myCanvas_w, myCanvas_h;
    Bitmap myCanvasBitmap = null;
    Canvas myCanvas = null;
    Matrix identityMatrix;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Random random;

    public MyGameSurfaceView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }

    public MyGameSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
    }

    public MyGameSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
// TODO Auto-generated constructor stub
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
// TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println("created");
        myCanvas_w = getWidth();
        myCanvas_h = getHeight();
        myCanvasBitmap = Bitmap.createBitmap(myCanvas_w, myCanvas_h, Bitmap.Config.ARGB_8888);
        myCanvas = new Canvas();
        myCanvas.setBitmap(myCanvasBitmap);

        identityMatrix = new Matrix();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
// TODO Auto-generated method stub

    }

    public void MyGameSurfaceView_OnResume(){

        random = new Random();
        surfaceHolder = getHolder();
        getHolder().addCallback(this);

//Create and start background Thread
        myGameThread = new Thread();
        myGameThread.start();

    }

    public void MyGameSurfaceView_OnPause(){
//Kill the background Thread
        boolean retry = true;

        while(retry){
            try {
                myGameThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        myGameThread = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("opened");
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        //int w = myCanvas.getWidth();
        //int h = myCanvas.getHeight();
        int x = random.nextInt(myCanvas_w-1);
        int y = random.nextInt(myCanvas_h-1);
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);

        paint.setColor(0xff000000 + (r << 16) + (g << 8) + b);
        myCanvas.drawPoint(x, y, paint);

        canvas.drawBitmap(myCanvasBitmap, identityMatrix, null);

    }

    public void updateStates(){
//Dummy method() to handle the States
    }

    public void updateSurfaceView(){
//The function run in background thread, not ui thread.

        Canvas canvas = null;

        try{
            canvas = surfaceHolder.lockCanvas();

            synchronized (surfaceHolder) {
                updateStates();
                onDraw(canvas);
            }
        }finally{
            if(canvas != null){
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

}