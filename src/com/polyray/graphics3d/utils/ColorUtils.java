package com.polyray.graphics3d.utils;

import com.polyray.graphics3d.Vector3f;

public class ColorUtils {
    public static int mulColor(int color, float t) {
        int r = (int)(((color >> 16) & 0xFF) * t);
        int g = (int)(((color >> 8) & 0xFF) * t);
        int b = (int)((color & 0xFF) * t);
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    public static int mulColor(int color, float rt, float gt, float bt) {
        int r = (int)(((color >> 16) & 0xFF) * rt);
        int g = (int)(((color >> 8) & 0xFF) * gt);
        int b = (int)((color & 0xFF) * bt);
        return color & 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    public static int toColor(Vector3f v) {
        int r = (int)(v.x * 255.0f) & 0xFF;
        int g = (int)(v.y * 255.0f) & 0xFF;
        int b = (int)(v.z * 255.0f) & 0xFF;
        return 0xFF000000 | r << 16 | g << 8 | b;
    }
    
    public static Vector3f toVector3f(int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return new Vector3f(r / 255.0f, g / 255.0f, b / 255.0f);
    }
    
    public static int mixColor(int colorA, int colorB, float t) {
        int aa = (colorA >> 24) & 0xFF;
        int ra = (colorA >> 16) & 0xFF;
        int ga = (colorA >> 8) & 0xFF;
        int ba = colorA & 0xFF;
        int ab = (colorB >> 24) & 0xFF;
        int rb = (colorB >> 16) & 0xFF;
        int gb = (colorB >> 8) & 0xFF;
        int bb = colorB & 0xFF;
        int colA = (int)(aa + t * (ab - aa));
        int colR = (int)(ra + t * (rb - ra));
        int colG = (int)(ga + t * (gb - ga));
        int colB = (int)(ba + t * (bb - ba));
        return colA << 24 | colR << 16 | colG << 8 | colB;
    }
    
    public static int lightColor(int color, Vector3f lightColor, Vector3f ambientColor, float t) {
        return mulColor(color, ambientColor.x + t * (lightColor.x - ambientColor.x), ambientColor.y + t * (lightColor.y - ambientColor.y), ambientColor.z + t * (lightColor.z - ambientColor.z));
    }
}
