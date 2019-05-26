package it.moondroid.colormixer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by marco.granatiero on 05/08/2014.
 */
public abstract class ColorSeekBar extends SeekBar {

    protected HSLColor mHSL = new HSLColor(0.0f, 100.0f, 50.0f); //Default color

    public ColorSeekBar(Context context) {
        super(context);
    }

    public ColorSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public abstract float getHue();
    public abstract float getSaturation();
    public abstract float getLightness();

    public abstract void initWithColor(int color);

    public abstract void setColor(int color);

    public abstract void setColor(HSLColor color);

    public abstract int getColor();
}
