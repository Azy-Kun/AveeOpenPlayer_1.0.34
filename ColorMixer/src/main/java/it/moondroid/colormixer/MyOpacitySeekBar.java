package it.moondroid.colormixer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by marco.granatiero on 05/08/2014.
 */
public class MyOpacitySeekBar extends ColorSeekBar {

    int alpha = 0xff;
    public static final int ALPHA_COLOR_MAX = 255;

    public static int argb(int alpha, int rgb) {
        return (alpha << 24) | (rgb & 0x00ffffff);
    }

    public static final Drawable getOpacityBar(HSLColor color, int width, int height) {
        int[] cols = new int[2];

//        cols[0] = argb(0, color.getRGB());
//        cols[1] = argb(ALPHA_COLOR_MAX, color.getRGB());

        //MYEDIT
        cols[0] = HSLColor.toRGB(color.getHue(), 100.0f, 50.0f, 0.0f);
        cols[1] = HSLColor.toRGB(color.getHue(), 100.0f, 50.0f, ALPHA_COLOR_MAX);

        return ColorDrawableBuilder.buildDrawable(cols, width, height);
    }

    public MyOpacitySeekBar(Context context) {
        super(context);
        setMax(ALPHA_COLOR_MAX);
    }

    public MyOpacitySeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMax(ALPHA_COLOR_MAX);
    }

    public MyOpacitySeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setMax(ALPHA_COLOR_MAX);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        setProgressDrawable(getOpacityBar(mHSL, width, height));
        setMax(ALPHA_COLOR_MAX);

    }

    @Override
    public float getHue() {
        return mHSL.getHue();
    }

    @Override
    public float getSaturation(){
        //mHSL.setSaturation(getProgress());
        return mHSL.getSaturation();
    }

    public int getOpacityColor(){
        alpha = getProgress();
        return alpha;
    }

    @Override
    public float getLightness() {
        return mHSL.getLuminance();
    }

    @Override
    public void initWithColor(int color){

        setColor(color);
        setProgress((int) Color.alpha(color));
    }

    @Override
    public void setColor(int color){
        alpha = Color.alpha(color);
        mHSL = new HSLColor(HSLColor.fromRGB(color));
        setProgressDrawable(getOpacityBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public void setColor(HSLColor color) {
        mHSL = color;
        //alpha = alpha;//alpha doesnt change
        setProgressDrawable(getOpacityBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public int getColor(){
        alpha = getProgress();
        return argb(alpha, mHSL.getRGB());
    }
}
