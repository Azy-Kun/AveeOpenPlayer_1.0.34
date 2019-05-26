package it.moondroid.colormixer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * Created by marco.granatiero on 22/09/2014.
 */
public class ColorTextView extends TextView {

    public ColorTextView(Context context) {
        this(context, null);
    }

    public ColorTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setGravity(Gravity.CENTER);
    }

    public void setColor(int color){
        HSLColor hslColor = new HSLColor(HSLColor.fromRGB(color));
        setColor(hslColor);
    }

    public void setColor(HSLColor color){
        int rgb = color.getRGB();
        this.setBackgroundColor(rgb);
        String hexColor = String.format("#%06X", (0xFFFFFF & rgb));
        this.setText(hexColor);
        if(color.getSaturation()<50.0f){
            if(color.getLuminance()>50.0f){
                this.setTextColor(Color.BLACK);
            }else {
                this.setTextColor(Color.WHITE);
            }

        }else {
            HSLColor textColor = new HSLColor(HSLColor.fromRGB(color.getComplementary()));
            textColor.setLuminance(100.0f - color.getLuminance());
            this.setTextColor(textColor.getRGB());
        }
    }
}
