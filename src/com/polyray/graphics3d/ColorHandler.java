package com.polyray.graphics3d;

import java.awt.Color;

public class ColorHandler {

    public static Color mixColor(Color a, Color b, float factor) {
        int aR = a.getRed();
        int aG = a.getGreen();
        int aB = a.getBlue();
        int bR = b.getRed();
        int bG = b.getGreen();
        int bB = b.getBlue();
        int nR = Math.max(0, Math.min(255, lerp(aR, bR, factor)));
        int nG = Math.max(0, Math.min(255, lerp(aG, bG, factor)));
        int nB = Math.max(0, Math.min(255, lerp(aB, bB, factor)));
        return new Color(nR, nG, nB);
    }

    public static Color averageColors(Color[] colors) {
        float R = 0.0f, G = 0.0f, B = 0.0f;
        for (Color c : colors) {
            R += (float) c.getRed() / colors.length;
            G += (float) c.getGreen() / colors.length;
            B += (float) c.getBlue() / colors.length;
        }
        return new Color(lim(R), lim(G), lim(B));
    }

    public static Color bright(Color c, float factor) {
        int nR = lim(c.getRed() * factor);
        int nG = lim(c.getGreen() * factor);
        int nB = lim(c.getBlue() * factor);
        return new Color(nR, nG, nB);
    }

    public static Color max(Color a, Color b) {
        int aR = a.getRed();
        int aG = a.getGreen();
        int aB = a.getBlue();
        int bR = b.getRed();
        int bG = b.getGreen();
        int bB = b.getBlue();
        int nR = Math.max(0, Math.min(255, Math.max(aR, bR)));
        int nG = Math.max(0, Math.min(255, Math.max(aG, bG)));
        int nB = Math.max(0, Math.min(255, Math.max(aB, bB)));
        return new Color(nR, nG, nB);
    }

    public static Color multiply(Color a, Color b) {
        float aR = a.getRed() / 255.0f;
        float aG = a.getGreen() / 255.0f;
        float aB = a.getBlue() / 255.0f;
        float bR = b.getRed() / 255.0f;
        float bG = b.getGreen() / 255.0f;
        float bB = b.getBlue() / 255.0f;
        int nR = (int) (aR * bR * 255.0f);
        int nG = (int) (aG * bG * 255.0f);
        int nB = (int) (aB * bB * 255.0f);
        return new Color(nR, nG, nB);
    }

    public static Color add(Color a, Color b) {
        int aR = a.getRed();
        int aG = a.getGreen();
        int aB = a.getBlue();
        int bR = b.getRed();
        int bG = b.getGreen();
        int bB = b.getBlue();
        int nR = lim(aR + bR);
        int nG = lim(aG + bG);
        int nB = lim(aB + bB);
        return new Color(nR, nG, nB);
    }

    public static Color divide(Color a, Color b) {
        float aR = a.getRed() / 255.0f;
        float aG = a.getGreen() / 255.0f;
        float aB = a.getBlue() / 255.0f;
        float bR = b.getRed() / 255.0f;
        float bG = b.getGreen() / 255.0f;
        float bB = b.getBlue() / 255.0f;
        int nR = (int) Math.max(0, Math.min(255, aR / bR * 255.0f));
        int nG = (int) Math.max(0, Math.min(255, aG / bG * 255.0f));
        int nB = (int) Math.max(0, Math.min(255, aB / bB * 255.0f));
        return new Color(nR, nG, nB);
    }

    private static int lim(float a) {
        return (int) Math.max(Math.min(a, 255.0f), 0.0f);
    }

    private static int lerp(int a, int b, float f) {
        return (int) (a + f * (b - a));
    }
}
