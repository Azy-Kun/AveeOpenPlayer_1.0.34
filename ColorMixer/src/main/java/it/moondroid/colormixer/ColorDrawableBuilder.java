package it.moondroid.colormixer;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;

/**
 * Created by marco.granatiero on 05/08/2014.
 */
public class ColorDrawableBuilder {

    private static final float PADDING_TOP_BOTTOM_PERCENT = 0.4f;

    public static final Drawable getHueBar(int width, int height) {
        HSLColor hsl = new HSLColor(0.0f, 100.0f, 50.0f);
        int[] cols = new int[24];
        int idx = 0;
        while (idx < cols.length) {
            cols[idx] = hsl.rotate((float) (idx * 15));
            idx++;
        }

        return buildDrawable(cols, width, height);
    }


    public static final Drawable getLightnessBar(HSLColor color, int width, int height) {
        int[] cols = new int[3];
//        cols[0] = color.adjustLuminance(0.0f);
//        cols[1] = color.getRGB();
//        cols[2] = color.adjustLuminance(100.0f);

        //MYEDIT
        cols[0] = HSLColor.toRGB(color.getHue(), 10.0f, 0.0f);
        cols[1] = HSLColor.toRGB(color.getHue(), 100.0f, 50.0f);
        cols[2] = HSLColor.toRGB(color.getHue(), 100.0f, 100.0f);

        return buildDrawable(cols, width, height);
    }

    public static final Drawable getSaturationBar(HSLColor color, int width, int height) {
        int[] cols = new int[2];
//        cols[0] = color.adjustSaturation(0.0f);
//        cols[1] = color.adjustSaturation(100.0f);

        //MYEDIT
        cols[0] = HSLColor.toRGB(color.getHue(), 0.0f, 50.0f);
        cols[1] = HSLColor.toRGB(color.getHue(), 100.0f, 50.0f);

        return buildDrawable(cols, width, height);
    }

    public static Drawable buildDrawable(int[] cols, int width, int height){
        GradientDrawable dr = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, cols);
        dr.setShape(GradientDrawable.RECTANGLE);

        int paddingTopBottom = (int) (height*PADDING_TOP_BOTTOM_PERCENT / 2.0f);
        InsetDrawable idr =  new InsetDrawable(dr, 0,paddingTopBottom,0,paddingTopBottom);

        return idr;
    }

}
