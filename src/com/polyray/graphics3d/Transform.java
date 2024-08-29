package com.polyray.graphics3d;

import java.awt.Color;
import java.awt.Graphics2D;
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
            x1 -= minX;
            y1 -= minY;
            x2 -= minX;
            y2 -= minY;
            x3 -= minX;
            y3 -= minY;
            x4 -= minX;
            y4 -= minY;
            final int nWidth = W;
            final int nHeight = H;
            BufferedImage imageOut = new BufferedImage(nWidth, nHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imageOut.createGraphics();
            float dx1 = (float) (x4 - x1) / height;
            float dy1 = (float) (y4 - y1) / height;
            float dx2 = (float) (x3 - x2) / height;
            float dy2 = (float) (y3 - y2) / height;
            for (int y = 0; y < height; y++) {
                float p1x = x1 + dx1 * y;
                float p1y = y1 + dy1 * y;
                float p2x = x2 + dx2 * y;
                float p2y = y2 + dy2 * y;
                float kx1 = (p2x - p1x) / width;
                float ky1 = (p2y - p1y) / width;
                float p3x = x1 + dx1 * (y + 1.0f);
                float p3y = y1 + dy1 * (y + 1.0f);
                float p4x = x2 + dx2 * (y + 1.0f);
                float p4y = y2 + dy2 * (y + 1.0f);
                float kx2 = (p4x - p3x) / width;
                float ky2 = (p4y - p3y) / width;
                for (int x = 0; x < width; x++) {
                    int[] Xcomp = new int[4];
                    int[] Ycomp = new int[4];
                    Xcomp[0] = (int) (p1x + kx1 * x);
                    Ycomp[0] = (int) (p1y + ky1 * x);
                    Xcomp[1] = (int) (p3x + kx2 * x);
                    Ycomp[1] = (int) (p3y + ky2 * x);
                    // Flip order for square
                    Xcomp[3] = (int) (p1x + kx1 * (x + 1.0f));
                    Ycomp[3] = (int) (p1y + ky1 * (x + 1.0f));
                    Xcomp[2] = (int) (p3x + kx2 * (x + 1.0f));
                    Ycomp[2] = (int) (p3y + ky2 * (x + 1.0f));
                    g.setColor(new Color(image.getRGB(x, y)));
                    g.fillPolygon(Xcomp, Ycomp, 4);
                }
            }
            return imageOut;
        }
        return null;
    }
}
