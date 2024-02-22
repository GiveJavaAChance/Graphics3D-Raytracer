package com.polyray.graphics3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
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
            final int width = image.getWidth();
            final int height = image.getHeight();
            if (width <= 128 && height <= 128) {
                return rectToQuadSimple(image, x1, y1, x2, y2, x3, y3, x4, y4);
            } else {
                minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
                minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
                int W = Math.max(Math.max(x1, x2), Math.max(x3, x4)) - minX;
                int H = Math.max(Math.max(y1, y2), Math.max(y3, y4)) - minY;
                if (minX < 0) {
                    W = W + minX;
                    minX = 0;
                }
                if (minX + W > wWidth) {
                    W = W - (minX + W - wWidth);
                }
                if (minY < 0) {
                    H = H + minY;
                    minY = 0;
                }
                if (minY + H > wHeight) {
                    H = H - (minY + H - wHeight);
                }
                if (W > 0 && H > 0) {
                    final int nWidth = W;
                    final int nHeight = H;
                    final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
                    final int pixelLength = 4; // (ARGB)
                    byte[] pixelDataOut = new byte[W * H * pixelLength];
                    final int len = (pixels.length - 1) / pixelLength;
                    Thread[] t = new Thread[4];
                    for (int i = 0; i < 4; i++) {
                        final int minVal = len / 4 * i;
                        final int maxVal = len / 4 * (i + 1);
                        t[i] = new Thread(() -> {
                            for (int p = minVal; p < maxVal; p++) {
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

                                int ppx = (int) (p1x + (p2x - p1x) * dx) - minX;
                                int ppy = (int) (p1y + (p2y - p1y) * dx) - minY;

                                int index = (ppy * nWidth + ppx) * pixelLength;
                                if (index >= 0 && index < pixelDataOut.length - pixelLength && ppx > 0 && ppx <= nWidth && ppy > 0 && ppy <= nHeight) {
                                    pixelDataOut[index + 3] = (byte) red;
                                    pixelDataOut[index + 2] = (byte) green;
                                    pixelDataOut[index + 1] = (byte) blue;
                                    pixelDataOut[index] = (byte) alpha;
                                }
                            }
                        });
                    }
                    for (int i = 0; i < 4; i++) {
                        t[i].start();
                    }
                    for (int i = 0; i < 4; i++) {
                        try {
                            t[i].join();
                        } catch (InterruptedException ex) {
                        }
                    }
                    BufferedImage imageOut = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_4BYTE_ABGR);
                    WritableRaster raster = imageOut.getRaster();
                    raster.setDataElements(0, 0, nWidth, nHeight, pixelDataOut);
                    return imageOut;
                }
            }
        }
        return null;
    }

    private BufferedImage rectToQuadSimple(BufferedImage image, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        final int width = image.getWidth();
        final int height = image.getHeight();
        minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
        minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
        int W = Math.max(Math.max(x1, x2), Math.max(x3, x4)) - minX;
        int H = Math.max(Math.max(y1, y2), Math.max(y3, y4)) - minY;
        if (minX < 0) {
            W = W + minX;
            minX = 0;
        }
        if (minX + W > wWidth) {
            W = W - (minX + W - wWidth);
        }
        if (minY < 0) {
            H = H + minY;
            minY = 0;
        }
        if (minY + H > wHeight) {
            H = H - (minY + H - wHeight);
        }
        if (W > 0 && H > 0) {
            final int nWidth = W;
            final int nHeight = H;
            BufferedImage imageOut = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imageOut.createGraphics();
            for (int y = 0; y < height; y++) {
                float dy = (float) y / height;
                float p1x = x1 + (x4 - x1) * dy;
                float p1y = y1 + (y4 - y1) * dy;
                float p2x = x2 + (x3 - x2) * dy;
                float p2y = y2 + (y3 - y2) * dy;
                float dy1 = (float) (y + 1.0f) / height;
                float p3x = x1 + (x4 - x1) * dy1;
                float p3y = y1 + (y4 - y1) * dy1;
                float p4x = x2 + (x3 - x2) * dy1;
                float p4y = y2 + (y3 - y2) * dy1;
                for (int x = 0; x < width; x++) {
                    float dx = (float) x / width;
                    int ppx1 = (int) (p1x + (p2x - p1x) * dx) - minX;
                    int ppy1 = (int) (p1y + (p2y - p1y) * dx) - minY;
                    int ppx2 = (int) (p3x + (p4x - p3x) * dx) - minX;
                    int ppy2 = (int) (p3y + (p4y - p3y) * dx) - minY;
                    float dx1 = (float) (x + 1.0f) / width;
                    int ppx3 = (int) (p1x + (p2x - p1x) * dx1) - minX;
                    int ppy3 = (int) (p1y + (p2y - p1y) * dx1) - minY;
                    int ppx4 = (int) (p3x + (p4x - p3x) * dx1) - minX;
                    int ppy4 = (int) (p3y + (p4y - p3y) * dx1) - minY;
                    Polygon p = new Polygon();
                    p.addPoint(ppx1, ppy1);
                    p.addPoint(ppx3, ppy3);
                    p.addPoint(ppx4, ppy4);
                    p.addPoint(ppx2, ppy2);
                    g.setColor(new Color(image.getRGB(x, y)));
                    g.fillPolygon(p);
                }
            }
            return imageOut;
        }
        return null;
    }
}
