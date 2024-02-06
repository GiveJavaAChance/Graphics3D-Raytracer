package com.polyray.graphics3d;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class Transform {

    public int wWidth, wHeight;
    public int minX, minY;

    public void setViewSize(int w, int h) {
        this.wWidth = w;
        this.wHeight = h;
    }

    public BufferedImage rectToQuad(BufferedImage image, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        if (image != null) {
            int width = image.getWidth();
            int height = image.getHeight();
            minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
            minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
            int nWidth = Math.max(Math.max(x1, x2), Math.max(x3, x4)) - minX;
            int nHeight = Math.max(Math.max(y1, y2), Math.max(y3, y4)) - minY;
            if (minX < 0) {
                nWidth = nWidth + minX;
                minX = 0;
            }
            if (minX + nWidth > wWidth) {
                nWidth = nWidth - (minX + nWidth - wWidth);
            }
            if (minY < 0) {
                nHeight = nHeight + minY;
                minY = 0;
            }
            if (minY + nHeight > wHeight) {
                nHeight = nHeight - (minY + nHeight - wHeight);
            }
            byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            final int pixelLength = 4; // (ARGB)
            byte[] pixelDataOut = new byte[nWidth * nHeight * pixelLength];
            for (int p = 0; p < (pixels.length - 1) / pixelLength; p++) {
                int pixel = p * 4;
                int red = pixels[pixel] & 0xFF;
                int green = pixels[pixel + 1] & 0xFF;
                int blue = pixels[pixel + 2] & 0xFF;
                int alpha = pixels[pixel + 3] & 0xFF;

                int x = p % width;
                int y = p / width;

                float dx = (float) x / width;
                float dy = (float) y / height;

                float p1x = x1 + (x4 - x1) * dy;
                float p1y = y1 + (y4 - y1) * dy;

                float p2x = x2 + (x3 - x2) * dy;
                float p2y = y2 + (y3 - y2) * dy;

                int ppx = (int) lerp(p1x, p2x, dx) - minX;
                int ppy = (int) lerp(p1y, p2y, dx) - minY;

                int index = (ppy * nWidth + ppx) * pixelLength;
                if (index >= 0 && index < pixelDataOut.length - pixelLength && ppx > 0 && ppx <= nWidth && ppy > 0 && ppy <= nHeight) {
                    pixelDataOut[index + 3] = (byte) red;
                    pixelDataOut[index + 2] = (byte) green;
                    pixelDataOut[index + 1] = (byte) blue;
                    pixelDataOut[index] = (byte) alpha;
                }
            }
            if (nWidth > 0 && nHeight > 0) {
                BufferedImage imageOut = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR);
                WritableRaster raster = imageOut.getRaster();
                raster.setDataElements(0, 0, nWidth, nHeight, pixelDataOut);
                return imageOut;
            }
        }
        return null;
    }

    private float lerp(float a, float b, float f) {
        return a + (b - a) * f;
    }
    
}
