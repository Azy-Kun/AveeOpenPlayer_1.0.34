package it.moondroid.colormixer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by marco.granatiero on 16/09/2014.
 */
public class VennColorsWidget extends View {

    Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    Canvas tempCanvas = new Canvas();
    Paint paint = new Paint();

    private static final float EXPAND = 1.5f;

    private float cx, cy, tx, ty, r;

    private int color1 = Color.MAGENTA;
    private int color2 = Color.YELLOW;
    private int color3 = Color.CYAN;

    private PorterDuffXfermode mPorterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DARKEN);

    private GestureDetector mGestureDetector;

    private ColorsInterface mListener;

    public interface ColorsInterface {

        public static final int CIRCLE1 = 1;
        public static final int CIRCLE2 = 2;
        public static final int CIRCLE3 = 3;
        public static final int CIRCLE1_2 = 4;
        public static final int CIRCLE2_3 = 5;
        public static final int CIRCLE3_1 = 6;
        public static final int CIRCLE1_2_3 = 7;

        public void onCircleClick(int which, int color);
    }

    public VennColorsWidget(Context context) {
        this(context, null);
    }

    public VennColorsWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VennColorsWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        paint.setAntiAlias(true);
        paint.setXfermode(mPorterDuffXfermode);

        mGestureDetector = new GestureDetector(getContext(), new VennColorsGestureListener());

    }

    public void setOnCircleClickListener(ColorsInterface listener){
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //clear previous drawings
        tempCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        paint.setColor(color1);
        tempCanvas.drawCircle(cx, cy - r, EXPAND * r, paint); //circle 1
        paint.setColor(color2);
        tempCanvas.drawCircle(cx - tx, cy + ty, EXPAND * r, paint); //circle 2
        paint.setColor(color3);
        tempCanvas.drawCircle(cx + tx, cy + ty, EXPAND * r, paint); //circle 3

        canvas.drawBitmap(tempBmp, 0, 0, null);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);

        tempBmp.recycle();
        tempBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        tempCanvas.setBitmap(tempBmp);


        cx = size / 2f;
        cy = size / 2f;
        r = size / 5f;
        tx = (float) (r * Math.cos(30 * Math.PI / 180));
        ty = (float) (r * Math.sin(30 * Math.PI / 180));
    }

    public void setColor(int which, int color){
        if (which== ColorsInterface.CIRCLE1){
            color1 = color;
        }
        if (which== ColorsInterface.CIRCLE2){
            color2 = color;
        }
        if (which== ColorsInterface.CIRCLE3){
            color3 = color;
        }
        invalidate();
    }

    public void setPorterDuffXfermode(PorterDuffXfermode xfermode){
        mPorterDuffXfermode = xfermode;
        paint.setXfermode(mPorterDuffXfermode);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return mGestureDetector.onTouchEvent(event);
    }

    private class VennColorsGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent event) {
            Log.d("VennColorsWidget", "onDown");
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            Log.d("VennColorsWidget", "onSingleTapUp");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {

            float x = event.getX();
            float y = event.getY();

            boolean inCircle1 = isInsideCircle(cx, cy - r, EXPAND * r, x, y);
            boolean inCircle2 = isInsideCircle(cx - tx, cy + ty, EXPAND * r, x, y);
            boolean inCircle3 = isInsideCircle(cx + tx, cy + ty, EXPAND * r, x, y);

            int color = tempBmp.getPixel((int)x, (int)y);
            int circle = getTouchedCircle(inCircle1, inCircle2, inCircle3);

            if (mListener!=null){
                mListener.onCircleClick(circle, color);
            }

            if(inCircle1 || inCircle2 || inCircle3){
                return true;
            }else {
                return super.onSingleTapConfirmed(event);
            }
        }

        private boolean isInsideCircle(float cx, float cy, float r, float x, float y){
            float d = (float) Math.sqrt(Math.pow(x - cx, 2) +  Math.pow(y - cy, 2));
            return d<r;
        }

        private int getTouchedCircle(boolean inCircle1, boolean inCircle2, boolean inCircle3){
            if (inCircle1 && !inCircle2 && !inCircle3){
                return ColorsInterface.CIRCLE1;
            }
            if (!inCircle1 && inCircle2 && !inCircle3){
                return ColorsInterface.CIRCLE2;
            }
            if (!inCircle1 && !inCircle2 && inCircle3){
                return ColorsInterface.CIRCLE3;
            }

            if (inCircle1 && inCircle2 && !inCircle3){
                return ColorsInterface.CIRCLE1_2;
            }
            if (!inCircle1 && inCircle2 && inCircle3){
                return ColorsInterface.CIRCLE2_3;
            }
            if (inCircle1 && !inCircle2 && inCircle3){
                return ColorsInterface.CIRCLE3_1;
            }

            if (inCircle1 && inCircle2 && inCircle3){
                return ColorsInterface.CIRCLE1_2_3;
            }
            return 0;
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("color1", this.color1);
        bundle.putInt("color2", this.color2);
        bundle.putInt("color3", this.color3);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {

        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.color1 = bundle.getInt("color1");
            this.color2 = bundle.getInt("color2");
            this.color3 = bundle.getInt("color3");

            state = bundle.getParcelable("instanceState");
        }
        super.onRestoreInstanceState(state);
    }
}
