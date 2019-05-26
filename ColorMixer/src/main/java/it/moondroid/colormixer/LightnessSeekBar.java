package it.moondroid.colormixer;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by marco.granatiero on 05/08/2014.
 */
public class LightnessSeekBar extends ColorSeekBar {

    public LightnessSeekBar(Context context) {
        super(context);
    }

    public LightnessSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LightnessSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        setProgressDrawable(ColorDrawableBuilder.getLightnessBar(mHSL, width, height));
        setMax(HSLColor.LIGHTNESS_MAX);

    }

    @Override
    public float getHue() {
        return mHSL.getHue();
    }

    @Override
    public float getSaturation() {
        return mHSL.getSaturation();
    }

    @Override
    public float getLightness(){
        mHSL.setLuminance(getProgress());
        return mHSL.getLuminance();
    }

    @Override
    public void initWithColor(int color){
        setColor(color);
        setProgress((int) mHSL.getLuminance());
    }

    @Override
    public void setColor(int color){
        mHSL = new HSLColor(HSLColor.fromRGB(color));
        setProgressDrawable(ColorDrawableBuilder.getLightnessBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public void setColor(HSLColor color) {
        mHSL = color;
        setProgressDrawable(ColorDrawableBuilder.getLightnessBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public int getColor(){
        mHSL.setLuminance(getProgress());
        return mHSL.getRGB();
    }
}
