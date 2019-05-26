package it.moondroid.colormixer;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by marco.granatiero on 05/08/2014.
 */
public class SaturationSeekBar extends ColorSeekBar {

    public SaturationSeekBar(Context context) {
        super(context);
    }

    public SaturationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaturationSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);

        setProgressDrawable(ColorDrawableBuilder.getSaturationBar(mHSL, width, height));
        setMax(HSLColor.SATURATION_MAX);

    }

    @Override
    public float getHue() {
        return mHSL.getHue();
    }

    @Override
    public float getSaturation(){
        mHSL.setSaturation(getProgress());
        return mHSL.getSaturation();
    }

    @Override
    public float getLightness() {
        return mHSL.getLuminance();
    }

    @Override
    public void initWithColor(int color){
        setColor(color);
        setProgress((int) mHSL.getSaturation());
    }

    @Override
    public void setColor(int color){
        mHSL = new HSLColor(HSLColor.fromRGB(color));
        setProgressDrawable(ColorDrawableBuilder.getSaturationBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public void setColor(HSLColor color) {
        mHSL = color;
        setProgressDrawable(ColorDrawableBuilder.getSaturationBar(mHSL, getWidth(), getHeight()));
    }

    @Override
    public int getColor(){
        mHSL.setSaturation(getProgress());
        return mHSL.getRGB();
    }
}
