package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Rotator;
import com.polyray.graphics3d.Vector3f;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class RayTracer {

    ColorObject[][] col;
    RaySolver u = new RaySolver(10000.0f, 10);
    BufferedImage image;
    float percentage;
    int chunksRendered = 0;

    int chunks = 10;
    int samples = 1;
    int chunkSamples = 200;
    int passes = 4;
    int w = 500;
    int h = 500;

    int[][] iter = new int[chunks][chunks];

    public RayTracer(Triangle[] triangles, Vector3f rotation, int prioAxis) {
        u.setFixed(rotateTriangles(triangles, rotation, prioAxis));
        percentage = 0.0f;
    }

    public void setVariables(int width, int height, int chunks, int pixelSamples, int chunkSamples, int passes) {
        this.w = width;
        this.h = height;
        this.chunks = chunks;
        this.samples = pixelSamples;
        this.chunkSamples = chunkSamples;
        this.passes = passes;
    }

    public void render() {
        col = new ColorObject[w][h];
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        iter = allTo(1, iter);
        for (int i = 0; i < passes; i++) {
            u.setCameraPos(new Vector3f(20.0f, 0.0f, 20.0f), 75.0f, w, h);
            for (int dy = chunks - 1; dy >= 0; dy--) {
                Graphics2D g = (Graphics2D) image.getGraphics();
                for (int dx = chunks - 1; dx >= 0; dx--) {
                    int minPosx = (int) Math.floor((float) dx * w / (float) chunks);
                    int maxPosx = (int) Math.floor((float) (dx + 1.0f) * w / (float) chunks);
                    int minPosy = (int) Math.floor((float) dy * h / (float) chunks);
                    int maxPosy = (int) Math.floor((float) (dy + 1.0f) * h / (float) chunks);
                    renderChunk(minPosx, maxPosx, minPosy, maxPosy, dx, dy, g);
                    chunksRendered++;
                    percentage = (float) chunksRendered / (float) (chunks * chunks);
                }
            }
        }
        float px, py;
        Graphics2D g = (Graphics2D) image.getGraphics();
        int errors = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (new Color(image.getRGB(x, y)) == new Color(0, 0, 0)) {
                    errors++;
                    int iters = 1;
                    for (int i = 0; i < chunkSamples * passes; i++) {
                        px = x - w / 2.0f + (float) Math.random();
                        py = y - h / 2.0f + (float) Math.random();
                        ColorObject c = u.castRay(px, py);
                        if (col[x][y] == null) {
                            iters = 1;
                            col[x][y] = c;
                        } else {
                            col[x][y] = ColorObject.add(col[x][y].mul(((float) iters - 1.0f) / (float) iters), c.mul(1.0f / iters));
                        }
                        if (col[x][y] != null) {
                            g.setColor(col[x][y].toColor());
                            g.fillRect(x, y, 1, 1);
                        }
                    }
                }
            }
        }
    }

    public void renderChunk(int minPosx, int maxPosx, int minPosy, int maxPosy, int dx, int dy, Graphics2D g) {
        float px, py;
        int iteration = iter[dx][dy];
        for (int i = 0; i < chunkSamples; i++) {
            for (int y = minPosy; y < maxPosy; y++) {
                for (int x = minPosx; x < maxPosx; x++) {
                    final ColorObject[] cols = new ColorObject[samples];
                    for (int k = 0; k < samples; k++) {
                        px = x - w / 2.0f + (float) Math.random();
                        py = y - h / 2.0f + (float) Math.random();
                        cols[k] = u.castRay(px, py).mul(1.0f / samples);
                    }
                    ColorObject avg = new ColorObject(0.0f, 0.0f, 0.0f);
                    for (int k = 0; k < samples; k++) {
                        avg = ColorObject.add(avg, cols[k]);
                    }
                    if (col[x][y] == null) {
                        iteration = 1;
                        col[x][y] = avg;
                    } else {
                        col[x][y] = ColorObject.add(col[x][y].mul(((float) iteration - 1.0f) / (float) iteration), avg.mul(1.0f / iteration));
                    }
                    if (col[x][y] != null) {
                        g.setColor(col[x][y].toColor());
                        g.fillRect(x, y, 1, 1);
                    }
                }
            }
            iteration++;
        }
        iter[dx][dy] = iteration;
    }

    public int[][] allTo(int to, int[][] g) {
        for (int y = 0; y < g[0].length; y++) {
            for (int[] g1 : g) {
                g1[y] = to;
            }
        }
        return g;
    }

    public Triangle[] rotateTriangles(Triangle[] t, Vector3f ang, int prioAxis) {
        Rotator rot = new Rotator();
        Triangle[] out = new Triangle[t.length];
        for (int i = 0; i < t.length; i++) {
            Triangle tr = t[i];
            out[i] = new Triangle(rot.calcRot(tr.a, ang, prioAxis), rot.calcRot(tr.b, ang, prioAxis), rot.calcRot(tr.c, ang, prioAxis), tr.col);
        }
        return out;
    }
    
    public BufferedImage getRender() {
        return this.image;
    }
    
    public float getPercentage() {
        return this.percentage;
    }
    
    public int getChunksRendered() {
        return this.chunksRendered;
    }
}
