package com.example.moose.personanongrata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class CanvasView extends View {


    public int height;
    Context context;
    private Paint paint;
    private VectorAnim vec1, vec2, centre;
    float lg_diam, lg_rad, cx, cy, cx2, cy2;
    int count = 0;
    boolean isDimsSetup = false;

    float i = 64;
    float j = 128;

    VectorAnim oldFractalPath1, oldFractalPath2, oldFractalPath3, oldFractalPath4;

    Bitmap myCanvasBitmap = null;
    Canvas canvas = null;
    Matrix identityMatrix;
    Handler h;
    Runnable r;
    private final int FRAME_RATE = 30;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;
        paint = new Paint();

        h = new Handler();
        identityMatrix = new Matrix();

        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        Thread t = new Thread(r);
        t.start();
    }

    class Runner extends Thread {
        public void run(){
            invalidate();

        }

    }

    // override onDraw
    @Override
    protected void onDraw(Canvas myCanvas) {
        super.onDraw(myCanvas);


        if(canvas == null){
            myCanvasBitmap = Bitmap.createBitmap(myCanvas.getWidth(), myCanvas.getHeight(), Bitmap.Config.ARGB_4444);
            canvas = new Canvas();
            canvas.setBitmap(myCanvasBitmap);
        }
        if(count %15 == 0) {
            paint.setARGB(1, 0, 0, 0);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        }

        if(!isDimsSetup){
            setupDims(canvas);
        }
        int sinRgbI =  (int) Math.round(Math.sin(i) * 32) + 223;
        int sinRgbJ =  (int) Math.round(Math.sin(j) * 32) + 223;
        int negSinRgbI = (int) (Math.round(Math.sin(i) * 32) * -1) + (255);

        double angle = i * 1;
        float angle2 = j * 4;

        double x = cx + Math.sin(angle) * lg_rad;
        double y = cy + Math.cos(angle) * lg_rad;

        double x2 = (cx + (Math.cos(angle2) * (lg_rad / 2)));
        double y2 = (cy + (Math.sin(angle2) * (lg_rad / 2)));

        vec1.setXY((int) Math.round(x), (int) Math.round(y));
        vec2.setXY((int) Math.round(x2), (int) Math.round(y2));
        centre.setXY(canvas.getWidth() / 2, canvas.getHeight() / 2);

        VectorAnim fractalPath1, fractalPath2, fractalPath3, fractalPath4;
        fractalPath1 = centre.lerp(vec1, vec2, 0.25005f);    //    1/4
        fractalPath2 = centre.lerp(vec1, vec2, 0.1667f);     //    1/6
        fractalPath3 = centre.lerp(vec1, vec2, 0.333f);      //    1/3
        fractalPath4 = centre.lerp(vec1, vec2, 0.5f);        //    1/2

            if(oldFractalPath1 != null) {
                paint.setARGB(255, negSinRgbI, sinRgbJ, sinRgbI);
                 canvas.drawLine(fractalPath1.getX(), fractalPath1.getY(), oldFractalPath1.getX(), oldFractalPath1.getY(), paint);
                 canvas.drawCircle(fractalPath1.getX(), fractalPath1.getY(), 2, paint);
                 paint.setARGB(255, sinRgbI, sinRgbJ, negSinRgbI);
                 canvas.drawLine(fractalPath2.getX(), fractalPath2.getY(), oldFractalPath2.getX(), oldFractalPath2.getY(), paint);
                 canvas.drawCircle(fractalPath2.getX(), fractalPath2.getY(), 2, paint);
                 paint.setARGB(255, sinRgbJ, negSinRgbI, sinRgbI);
                canvas.drawLine(fractalPath3.getX(), fractalPath3.getY(), oldFractalPath3.getX(), oldFractalPath3.getY(), paint);
                canvas.drawCircle(fractalPath3.getX(), fractalPath3.getY(), 2, paint);
                 paint.setARGB(255, negSinRgbI, sinRgbI, sinRgbJ);
                 canvas.drawLine(fractalPath4.getX(), fractalPath4.getY(), oldFractalPath4.getX(), oldFractalPath4.getY(), paint);
                 canvas.drawCircle(fractalPath4.getX(), fractalPath4.getY(), 2, paint);
                paint.setARGB(55, negSinRgbI, sinRgbI, sinRgbJ);
                canvas.drawLine(vec2.getX(), vec2.getY(), vec1.getX(), vec1.getY(), paint);
            }

        oldFractalPath1 = fractalPath1;
        oldFractalPath3 = fractalPath3;
        oldFractalPath2 = fractalPath2;
        oldFractalPath4 = fractalPath4;
        i += .03;
        j += .05;
        count++;
       // myCanvasBitmap = applyFleaEffect(myCanvasBitmap);
        myCanvas.drawBitmap(myCanvasBitmap, identityMatrix, paint);
        h.postDelayed(r, FRAME_RATE);

    }

    void setupDims(Canvas canvasForSetup){

        centre = new VectorAnim(100, 100);
        vec1 = new VectorAnim(0,0);
        vec2 = new VectorAnim(0,0);

        lg_diam = canvasForSetup.getWidth();
        if(lg_diam > canvasForSetup.getHeight()){
            lg_diam = canvasForSetup.getHeight();
        }
        lg_rad = lg_diam / 2;

        cx = canvasForSetup.getWidth() / 2;
        cy = canvasForSetup.getHeight() / 2;

        cx2 = cx;
        cy2 = cy;
    }

    public static final int COLOR_MIN = 0x00;
    public static final int COLOR_MAX = 0xFF;

    public static Bitmap applyFleaEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }
}