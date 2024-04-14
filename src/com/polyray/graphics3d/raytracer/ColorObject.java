package com.polyray.graphics3d.raytracer;

import java.awt.Color;

public class ColorObject {

    public float R, G, B;

    public ColorObject(float R, float G, float B) {
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public ColorObject mul(float factor) {
        float nR = this.R * factor;
        float nG = this.G * factor;
        float nB = this.B * factor;
        return new ColorObject(nR, nG, nB);
    }

    public ColorObject mul(ColorObject c) {
        float nR = this.R * c.R;
        float nG = this.G * c.G;
        float nB = this.B * c.B;
        return new ColorObject(nR, nG, nB);
    }

    public static ColorObject add(ColorObject a, ColorObject b) {
        return new ColorObject(a.R + b.R, a.G + b.G, a.B + b.B);
    }

    public static ColorObject avg(ColorObject a, ColorObject b, float factor) {
        return new ColorObject(a.R + factor * (b.R - a.R), a.G + factor * (b.G - a.G), a.B + factor * (b.B - a.B));
    }

    public float maxIntensity() {
        return Math.max(Math.max(this.R, this.G), this.B);
    }

    public boolean isBlack() {
        return (this.R == 0.0f && this.G == 0.0f && this.B == 0.0f);
    }

    public ColorObject normalize() {
        float c = (float) Math.sqrt(this.R * this.R + this.G * this.G + this.B * this.B);
        float nR = this.R * c;
        float nG = this.G * c;
        float nB = this.B * c;
        return new ColorObject(nR, nG, nB);
    }

    public Color toColor() {
        int nR = (int) Math.max(0, Math.min(255, Math.floor(this.R * 255.0f)));
        int nG = (int) Math.max(0, Math.min(255, Math.floor(this.G * 255.0f)));
        int nB = (int) Math.max(0, Math.min(255, Math.floor(this.B * 255.0f)));
        return new Color(nR, nG, nB);
    }

    public static ColorObject toColorObject(Color c) {
        return new ColorObject(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
    }
}
