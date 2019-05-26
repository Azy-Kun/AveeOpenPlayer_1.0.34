package it.moondroid.colormixer;

import android.graphics.Color;

public class HSLColor {
    public static final int COLOR_MAX = 255;
    public static final int HUE_MAX = 360;
    public static final int LIGHTNESS_MAX = 100;
    public static final int SATURATION_MAX = 100;
    private float[] hsl;
    HSLColor mDiffHSL;
    private int rgb;
    float tmpDif;

    private HSLColor() {
        this.tmpDif = 0.0f;
        this.hsl = null;
        this.rgb = 0;
    }

    public HSLColor(float h, float s, float l) {
        this(h, s, l, 255.0f);
    }

    public HSLColor(float h, float s, float l, float alpha) {
        this.tmpDif = 0.0f;
        float[] fArr = new float[4];
        fArr[0] = h;
        fArr[1] = s;
        fArr[2] = l;
        fArr[3] = alpha % 256.0f;
        this.hsl = fArr;
        this.rgb = toRGB(this.hsl);
    }

    public HSLColor(int rgb) {
        this.tmpDif = 0.0f;
        this.rgb = rgb;
        this.hsl = fromRGB(rgb);
    }

    public HSLColor(float[] hsl) {
        this(hsl, 255.0f);
    }

    public HSLColor(float[] hsl, float alpha) {
        this.tmpDif = 0.0f;
        if (hsl.length == 4) {
            this.hsl = hsl;
        } else if (hsl.length == 3) {
            this.hsl = new float[4];
            this.hsl[0] = hsl[0];
            this.hsl[1] = hsl[1];
            this.hsl[2] = hsl[2];
            this.hsl[3] = alpha % 256.0f;
        } else {
            throw new IllegalArgumentException("HSLA params insufficient");
        }
        this.rgb = toRGB(this.hsl);
    }

    private static float HueToRGB(float p, float q, float h) {
        if (h < 0.0f) {
            h += 1.0f;
        }
        if (h > 1.0f) {
            h -= 1.0f;
        }
        if (6.0f * h < 1.0f) {
            return p + ((q - p) * 6.0f) * h;
        }
        if (2.0f * h < 1.0f) {
            return q;
        }
        return 3.0f * h < 2.0f ? p + ((q - p) * 6.0f) * (0.6666667f - h) : p;
    }

    public static float[] fromRGB(int color) {
        float s;
        float r = ((float) Color.red(color)) / 255.0f;
        float g = ((float) Color.green(color)) / 255.0f;
        float b = ((float) Color.blue(color)) / 255.0f;
        float a = (float) Color.alpha(color);
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float h = 0.0f;
        if (max == min) {
            h = 0.0f;
        } else if (max == r) {
            h = ((((g - b) * 60.0f) / (max - min)) + 360.0f) % 360.0f;
        } else if (max == g) {
            h = ((b - r) * 60.0f) / (max - min) + 120.0f;
        } else if (max == b) {
            h = ((r - g) * 60.0f) / (max - min) + 240.0f;
        }
        float l = (max + min) / 2.0f;
        if (max == min) {
            s = 0.0f;
        } else if (l <= 0.5f) {
            s = (max - min) / (max + min);
        } else {
            s = (max - min) / ((2.0f - max) - min);
        }
        float[] fArr = new float[4];
        fArr[0] = h;
        fArr[1] = s * 100.0f;
        fArr[2] = l * 100.0f;
        fArr[3] = a;
        return fArr;
    }

    public static int toRGB(float h, float s, float l) {
        return toRGB(h, s, l, 255.0f);
    }

    public static int toRGB(float h, float s, float l, float alpha) {
        if (s < 0.0f || s > 100.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
        } else if (l < 0.0f || l > 100.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Luminance");
        } else if (alpha < 0.0f || alpha > 255.0f) {
            throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
        } else {
            float q;
            h = (h % 360.0f) / 360.0f;
            s /= 100.0f;
            l /= 100.0f;
            q = ((double) l) < 0.5d ? l * (1.0f + s) : l + s - s * l;
            float p = 2.0f * l - q;
            return Color.argb((int) alpha, (int) (Math.min(Math.max(0.0f, HueToRGB(p, q, 0.33333334f + h)), 1.0f) * 255.0f), (int) (Math.min(Math.max(0.0f, HueToRGB(p, q, h)), 1.0f) * 255.0f), (int) (Math.min(Math.max(0.0f, HueToRGB(p, q, h - 0.33333334f)), 1.0f) * 255.0f));
        }
    }

    public static int toRGB(float[] hsl) {
        if (hsl.length == 3) {
            return toRGB(hsl, 255.0f);
        }
        if (hsl.length == 4) {
            return toRGB(hsl[0], hsl[1], hsl[2], hsl[3] % 256.0f);
        }
        throw new IllegalArgumentException("HSL colors insufficient");
    }

    public static int toRGB(float[] hsl, float alpha) {
        return toRGB(hsl[0], hsl[1], hsl[2], alpha % 256.0f);
    }

    public HSLColor addAlpha(float alpha) {
        this.hsl[3] = (this.hsl[3] + alpha) % 256.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor addHue(float hue) {
        this.hsl[0] = (this.hsl[0] + hue) % 360.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor addLuminance(float l) {
        this.hsl[2] = (this.hsl[2] + l) % 101.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor addSaturation(float s) {
        this.hsl[1] = (this.hsl[1] + s) % 101.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public int adjust(float hue, float s, float l, float alpha) {
        return toRGB((this.hsl[0] + hue) % 360.0f, (this.hsl[1] + s) % 101.0f, (this.hsl[2] + l) % 101.0f, (this.hsl[3] + alpha) % 256.0f);
    }

    public int adjustHue(float degrees) {
        return toRGB(degrees, this.hsl[1], this.hsl[2], this.hsl[3]);
    }

    public int adjustLuminance(float percent) {
        return toRGB(this.hsl[0], this.hsl[1], percent, this.hsl[3]);
    }

    public int adjustSaturation(float percent) {
        return toRGB(this.hsl[0], percent, this.hsl[2], this.hsl[3]);
    }

    public int adjustShade(float percent) {
        return toRGB(this.hsl[0], this.hsl[1], Math.max(0.0f, this.hsl[2] * ((100.0f - percent) / 100.0f)), this.hsl[3]);
    }

    public int adjustTone(float percent) {
        return toRGB(this.hsl[0], this.hsl[1], Math.min(100.0f, this.hsl[2] * ((100.0f + percent) / 100.0f)), this.hsl[3]);
    }

    public HSLColor clone() throws CloneNotSupportedException {
        HSLColor hslColor = new HSLColor();
        hslColor.hsl = (float[]) this.hsl.clone();
        hslColor.rgb = this.rgb;
        return hslColor;
    }

    public float getAlpha() {
        return this.hsl[3];
    }

    public int[] getAnalogous() {
        int[] colors = new int[2];
        colors[0] = adjustHue(getHue() - 30.0f);
        colors[1] = adjustHue(getHue() + 30.0f);
        return colors;
    }

    public int getBlue() {
        return Color.blue(this.rgb);
    }

    public float getColorDiff(HSLColor compare) {
        this.tmpDif = Math.abs(this.hsl[0] - compare.getHue());
        if (this.tmpDif > 180.0f) {
            this.tmpDif = 360.0f - this.tmpDif;
        }
        this.tmpDif /= 180.0f;
        return this.tmpDif * 3.0f + Math.abs(this.hsl[1] - compare.getSaturation()) + Math.abs(this.hsl[2] - compare.getLuminance()) * 4.0f;
    }

    public int getComplementary() {
        return toRGB((this.hsl[0] + 180.0f) % 360.0f, this.hsl[1], this.hsl[2]);
    }

    public int getGreen() {
        return Color.green(this.rgb);
    }

    public float[] getHSL() {
        return this.hsl;
    }

    public int[] getHarmonies() {
        int[] colors = new int[12];
        colors[0] = getRGB();
        int i = 1;
        while (i < colors.length) {
            colors[i] = adjustHue(getHue() + ((float) (i * 30)));
            i++;
        }
        return colors;
    }

    public float getHue() {
        return this.hsl[0];
    }

    public float getLuminance() {
        return this.hsl[2];
    }

    public int getRGB() {
        return this.rgb;
    }

    public int getRed() {
        return Color.red(this.rgb);
    }

    public float getSaturation() {
        return this.hsl[1];
    }

    public int[] getSplitComplements() {
        int[] colors = new int[2];
        colors[0] = adjustHue(getHue() - 150.0f);
        colors[1] = adjustHue(getHue() + 150.0f);
        return colors;
    }

    public int[] getTriadicColors() {
        int[] colors = new int[2];
        colors[0] = adjustHue(getHue() - 120.0f);
        colors[1] = adjustHue(getHue() + 120.0f);
        return colors;
    }

    public int lighten(float by) {
        return toRGB(this.hsl[0], this.hsl[1], (this.hsl[2] + by) % 101.0f, this.hsl[3]);
    }

    public int rotate(float degrees) {
        return toRGB((this.hsl[0] + degrees) % 360.0f, this.hsl[1], this.hsl[2], this.hsl[3]);
    }

    public int saturate(float by) {
        return toRGB(this.hsl[0], (this.hsl[1] + by) % 101.0f, this.hsl[2], this.hsl[3]);
    }

    public HSLColor setAlpha(float alpha) {
        this.hsl[3] = alpha % 256.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor setHSL(float[] hsl) {
        if (hsl.length == 4) {
            this.hsl = hsl;
        } else if (hsl.length == 3) {
            this.hsl = new float[4];
            this.hsl[0] = hsl[0];
            this.hsl[1] = hsl[1];
            this.hsl[2] = hsl[2];
            this.hsl[3] = 255.0f;
        } else {
            throw new IllegalArgumentException("HSLA params insufficient");
        }
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor setHue(float hue) {
        this.hsl[0] = hue % 360.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor setLuminance(float l) {
        this.hsl[2] = l<100.0f? l % 100.0f : 100.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public HSLColor setRGB(int color) {
        this.rgb = color;
        this.hsl = fromRGB(this.rgb);
        return this;
    }

    public HSLColor setSaturation(float s) {

        this.hsl[1] = s<100.0f? s % 100.0f : 100.0f;
        this.rgb = toRGB(this.hsl);
        return this;
    }

    public String toString() {
        return new StringBuilder("HSLColor[h=").append(this.hsl[0]).append(", s=").append(this.hsl[1]).append(", l=").append(this.hsl[2]).append(", a=").append(this.hsl[3]).append("]").toString();
    }

    public String toRGBString(){
        return new StringBuilder("HSLColor[r=").append(Color.red(rgb)).append(", g=").append(Color.green(rgb)).append(", b=").append(Color.blue(rgb)).append("]").toString();

    }

}