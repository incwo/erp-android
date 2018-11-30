package com.incwo.facilescan.activity.scan;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

public class DrawView extends View implements OnTouchListener {
	private static final String TAG = "DrawView";

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Canvas  mCanvas;
	private Path    mPath;
	private Paint       mPaint;   
	private ArrayList<Path> paths = new ArrayList<Path>();


	public DrawView(Context context) {
	    super(context);
	    setFocusable(true);
	    setFocusableInTouchMode(true);
	    setDrawingCacheEnabled(true);

	    this.setOnTouchListener(this);

	    mPaint = new Paint();
	    mPaint.setColor(Color.BLACK);
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(3);
	    mCanvas = new Canvas();
	    mPath = new Path();
	    paths.add(mPath);

	}        
	
	public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
	    setFocusable(true);
	    setFocusableInTouchMode(true);
	    setDrawingCacheEnabled(true);
	    
	    this.setOnTouchListener(this);

	    mPaint = new Paint();
	    mPaint.setColor(Color.BLACK);
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(3);
	    mCanvas = new Canvas();
	    mPath = new Path();
	    paths.add(mPath);

	} 
	
	public DrawView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
	    setFocusable(true);
	    setFocusableInTouchMode(true);
	    setDrawingCacheEnabled(true);

	    this.setOnTouchListener(this);

	    mPaint = new Paint();
	    mPaint.setColor(Color.BLACK);
	    mPaint.setAntiAlias(true);
	    mPaint.setDither(true);
	    mPaint.setStyle(Paint.Style.STROKE);
	    mPaint.setStrokeJoin(Paint.Join.ROUND);
	    mPaint.setStrokeCap(Paint.Cap.ROUND);
	    mPaint.setStrokeWidth(3);
	    mCanvas = new Canvas();
	    mPath = new Path();
	    paths.add(mPath);

	} 
	    @Override
	    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);
	    }

	    @Override
	    protected void onDraw(Canvas canvas) {            

	    	
	        for (Path p : paths){
	            canvas.drawPath(p, mPaint);
	        }
	      
	    }

	    private float mX, mY;
	    private static final float TOUCH_TOLERANCE = 1;

	    private void touch_start(float x, float y) {
	        mPath.reset();
	        mPath.moveTo(x, y);
	        mX = x;
	        mY = y;
	    }
	    private void touch_move(float x, float y) {
	        float dx = Math.abs(x - mX);
	        float dy = Math.abs(y - mY);
	        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
	            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
	            mX = x;
	            mY = y;
	        }
	    }
	    private void touch_up() {
	        mPath.lineTo(mX, mY);
	        // commit the path to our offscreen
	        mCanvas.drawPath(mPath, mPaint);
	        // kill this so we don't double draw            
	        mPath = new Path();
	        paths.add(mPath);
	    }



	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
	      float x = event.getX();
	      float y = event.getY();

	      switch (event.getAction()) {
	          case MotionEvent.ACTION_DOWN:
	              touch_start(x, y);
	              invalidate();
	              break;
	          case MotionEvent.ACTION_MOVE:
	              touch_move(x, y);
	              invalidate();
	              break;
	          case MotionEvent.ACTION_UP:
	              touch_up();
	              invalidate();
	              break;
	      }
	      return true;
	}
	
	public void clearView(){
		paths.clear();
		invalidate();
		touch_up();
	}
	
	
}