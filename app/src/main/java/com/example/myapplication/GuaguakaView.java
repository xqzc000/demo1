package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GuaguakaView extends View {
    public static final String TAG="GuaguakaView";
    private int guaguatextcolor = 0x00000000;
    private int guaguatextSize = 30;
    private String guaguatext = "谢谢惠顾";

    private int lastX;
    private int lastY;
    private Paint innnerPaint;
    private Paint outerPaint;
    private Path mPath;
    private Bitmap innnerBitmap;
    private Canvas mCanvas;
    private Bitmap outBitmap;
    private Rect mTextBounds;  //innner字体
    private volatile boolean mIsComplete;

    public void setCompleteListener(OnCompleteListener completeListener) {
        this.completeListener = completeListener;
    }

    private OnCompleteListener completeListener;


    public GuaguakaView(Context context) {
        this(context, null);
    }

    public GuaguakaView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GuaguakaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = null;
        try {
            array = context.getTheme().obtainStyledAttributes
                    (attrs, R.styleable.GuaguakaView, defStyleAttr, 0);
            for (int i = 0; i < array.length(); i++) {
                int attr = array.getIndex(i);
                switch (attr) {
                    case R.styleable.GuaguakaView_guaguatext:
                        guaguatext = array.getString(attr);

                        break;
                    case R.styleable.GuaguakaView_guaguatextcolor:
                        guaguatextcolor = array.getColor(attr, Color.GREEN);
                        break;
                    case R.styleable.GuaguakaView_guaguatextSize:
                        guaguatextSize = (int) array.getDimension(attr, TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_SP, 22, getResources().getDisplayMetrics()));
                        break;
                }
            }
        } finally {
            if (array != null)
                array.recycle();
        }

        init();
    }

    private void init() {
        mPath=new Path();

        setOutPaint();
        setBackPaint();


    }

    private void setBackPaint() {
        innnerPaint=new Paint();
        innnerPaint.setColor(Color.DKGRAY);
        innnerPaint.setTextSize(guaguatextSize);
        mTextBounds=new Rect();
        innnerPaint.getTextBounds(guaguatext,0,guaguatext.length(),mTextBounds);
    }

    private void setOutPaint() {
        outerPaint=new Paint();
        outerPaint.setColor(Color.RED);
        outerPaint.setAntiAlias(true);
        outerPaint.setDither(true);
        outerPaint.setStrokeCap(Paint.Cap.ROUND); //笔刷
        outerPaint.setStrokeJoin(Paint.Join.ROUND); //线条
//        outerPaint.setStyle(Paint.Style.STROKE); //只绘制图形轮廓
        outerPaint.setStyle(Paint.Style.FILL); //填充
        outerPaint.setStrokeWidth(20);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG,"canvas:"+canvas);


//        canvas.drawColor(Color.parseColor("#c0c0c0"));
        canvas.drawBitmap(innnerBitmap,0,0,null); //底层图片
//        canvas.drawText(guaguatext,getWidth()/2-mTextBounds.width()/2,getHeight()/2-mTextBounds.height()/2,innnerPaint);//文字
            outerPaint.setXfermode(new PorterDuffXfermode((PorterDuff.Mode.DST_OUT)));
        if(!mIsComplete) {
            mCanvas.drawPath(mPath, outerPaint);
            canvas.drawBitmap(outBitmap, 0, 0, null);
        }else{
            if(completeListener!=null)
                completeListener.onResult("haha:"+guaguatext);

        }



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=getMeasuredWidth();
        int height=getMeasuredHeight();
        Log.i(TAG,"onMeasure:"+width+","+height);
        innnerBitmap= resizeBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.a1),width,height);

        outBitmap=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas=new Canvas(outBitmap);
        mCanvas.drawRoundRect(new RectF(0,0,width,height),30,30,outerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            lastX=(int)event.getX();
            lastY=(int)event.getY();
                mPath.moveTo(lastX,lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                int x=Math.abs(lastX-(int)event.getX());
                int y=Math.abs(lastY-(int)event.getY());
                if(x>3&&y>3)
                mPath.lineTo(lastX,lastY);
                lastX=(int)event.getX();
                lastY=(int)event.getY();
                break;


            case MotionEvent.ACTION_UP:
                //大于百分之60，全部显示
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int percent=0;
                        int width=outBitmap.getWidth();
                        int height=outBitmap.getHeight();
                        Log.i(TAG,"Runnable width="+width+",height="+height);
                        int[] totalArea=new int[width*height];
                        int wipeAreaSize=0;

                        outBitmap.getPixels(totalArea,0,width,0,0,width,height);
                        for (int i=0;i<width;i++){
                            for(int j=0;j<height;j++){
                                int index=i+j*width;
                                if(totalArea[index]==0){
                                    wipeAreaSize++;
                                }
                            }
                        }
                        Log.i(TAG, "wipeAreaSize="+wipeAreaSize+",totalArea="+totalArea.length);
                        percent=wipeAreaSize*100/totalArea.length;
                        Log.i(TAG, "wipeAreaSize="+wipeAreaSize+",percent="+percent);

                        if(percent>60){
                            mIsComplete=true;
                            postInvalidate();
                        }

                    }
                }).start();
                break;
        }

        invalidate();

        return true;

    }


    public Bitmap resizeBitmap(Bitmap bitmap,int newWidth,int newHeigth)
    {
        if(bitmap!=null)
        {
//            WindowManager wm = (WindowManager) getContext().getSystemService(
//                    Context.WINDOW_SERVICE);
//            int newWidth=wm.getDefaultDisplay().getWidth();
//            int newHeigth=wm.getDefaultDisplay().getHeight();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeigth)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;

        }
        else{
            return null;
        }
    }


    public interface OnCompleteListener{
        void onResult(String text);
    }


}
