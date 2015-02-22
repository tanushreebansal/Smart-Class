package com.example.SmartNotes;
import java.util.ArrayList;

import android.content.Context;
import android.view.View.OnTouchListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View implements OnTouchListener 
{

	
	private Path drawPath;
	
	private Paint drawPaint, canvasPaint;
	
	private int paintColor = 0xFF660000;
	
	private Canvas drawCanvas;
	
	private Bitmap canvasBitmap;
	
	private float brushSize, lastBrushSize;
	
	private ArrayList<Pair> paths = new ArrayList<Pair>();
	 
	private float mX, mY;

	private static final float TOUCH_TOLERANCE = 4;
	
	private static boolean erase=false;

	
	
	public DrawingView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setFocusable(true);
		setFocusableInTouchMode(true);
		this.setOnTouchListener(this);

		setupDrawing();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void setupDrawing()
	{

		brushSize = getResources().getInteger(R.integer.small_size);
		lastBrushSize = brushSize;
		drawPath = new Path();
		drawCanvas = new Canvas();
		drawPaint = new Paint();
		drawPaint.setColor(paintColor);
		drawPaint.setAntiAlias(true);
		drawPaint.setStrokeWidth(brushSize);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.ROUND);
		drawPaint.setStrokeCap(Paint.Cap.ROUND);
		drawPaint.setDither(true);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		
		
		Paint newPaint = new Paint(drawPaint);
		paths.add(new Pair(drawPath, newPaint));
	}

	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	
	
	public boolean onTouch(View arg0, MotionEvent event) 
	{
		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) 
		{
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
	
	@SuppressWarnings("rawtypes")
	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		for (Pair p : paths) {
			
			canvas.drawPath((Path)p.first,(Paint) p.second);
		}
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void touch_start(float x, float y) 
	{

		if (erase) 
		{
			drawPaint.setColor(Color.WHITE);
			//drawPaint.setStrokeWidth(6);
			Paint newPaint = new Paint(drawPaint);
			paths.add(new Pair(drawPath, newPaint));
		} 
		else 
		{ 
			Paint newPaint = new Paint(drawPaint);
			paths.add(new Pair(drawPath, newPaint));
		}
 
		drawPath.reset();
		drawPath.moveTo(x, y);
		mX = x;
		mY = y;
	}
	
	
	
	private void touch_move(float x, float y) 
	{
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
		{
			drawPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void touch_up() 
	{
		drawPath.lineTo(mX, mY);
        drawCanvas.drawPath(drawPath, drawPaint);

		drawPath = new Path();
		Paint newPaint = new Paint(drawPaint); // Clones the mPaint object
		paths.add(new Pair(drawPath, newPaint));
	}   
	
	

	//update color
	public void setColor(String newColor)
	{
		invalidate();
		paintColor = Color.parseColor(newColor);
		drawPaint.setColor(paintColor);
	}

	//set brush size
	public void setBrushSize(float newSize)
	{
		float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
				newSize, getResources().getDisplayMetrics());
		brushSize=pixelAmount;
		drawPaint.setStrokeWidth(brushSize);
	}

	//get and set last brush size
	public void setLastBrushSize(float lastSize)
	{
		lastBrushSize=lastSize;
	}
	
	public float getLastBrushSize(){
		return lastBrushSize;
	}
	
	public ArrayList<Pair> getPaths(){
		return paths;
	}

	//set erase true or false
	public void setErase(boolean isErase)
	{
		erase=isErase;
	
	}

	//start new drawing
	@SuppressWarnings("rawtypes")
	public void startNew()
	{
		drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		paths = new ArrayList<Pair>();
		invalidate();
	}
	
	public void smartNotes(ArrayList<Pair> paths)
	{
			drawCanvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
			for (Pair p : paths) 
			{
				
				drawCanvas.drawPath((Path)p.first,(Paint) p.second);
			}
		
	}
	
	public void setBitmap(Bitmap b)
	{
		 drawCanvas.drawBitmap(b, 0, 0, null);
	}
	public Bitmap getBitmap()
	{
		return canvasBitmap;
	}
}
