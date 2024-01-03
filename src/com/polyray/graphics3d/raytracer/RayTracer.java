package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Rotator;
import com.polyray.graphics3d.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class RayTracer {

    private ColorObject[][] col;
    private final RaySolver u;
    private BufferedImage image;
    private Thread[] threads;
    private float percentage;
    private int chunksRendered = 0;

    private int chunks = 10;
    private int samples = 1;
    private int chunkSamples = 200;
    private int passes = 4;
    private int w = 500;
    private int h = 500;

    private int[][] iter = new int[chunks][chunks];

    public RayTracer(Triangle[] triangles, float renderDistance, Vector3f rotation, int prioAxis) {
        this.u = new RaySolver(renderDistance, 10);
        u.setFixed(rotateTriangles(triangles, rotation, prioAxis));
        this.percentage = 0.0f;
    }

    public void setVariables(int width, int height, int threads, int pixelSamples, int chunkSamples, int passes) {
        this.w = width;
        this.h = height;
        this.chunks = threads;
        this.samples = pixelSamples;
        this.chunkSamples = chunkSamples;
        this.passes = passes;
        this.threads = new Thread[threads];
    }

    public void setCameraPos(Vector3f pos, float FOV) {
        u.setCameraPos(pos, FOV, w, h);
    }

    public void render() {
        if (threads != null) {
            col = new ColorObject[w][h];
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            iter = allTo(1, iter);
            for (int i = 0; i < passes; i++) {
                for (int dy = chunks - 1; dy >= 0; dy--) {
                    final int cDy = dy;
                    threads[dy] = new Thread(() -> {
                        for (int dx = chunks - 1; dx >= 0; dx--) {
                            int minPosx = (int) Math.floor((float) dx * w / (float) chunks);
                            int maxPosx = (int) Math.floor((float) (dx + 1.0f) * w / (float) chunks);
                            int minPosy = (int) Math.floor((float) cDy * h / (float) chunks);
                            int maxPosy = (int) Math.floor((float) (cDy + 1.0f) * h / (float) chunks);
                            renderChunk(minPosx, maxPosx, minPosy, maxPosy, dx, cDy);
                            chunksRendered++;
                            percentage = (float) chunksRendered / (float) (chunks * chunks);
                        }
                    });
                    threads[dy].start();
                }
                for (Thread thread : threads) {
                    try {
                        if (thread != null) {
                            thread.join();
                        }
                    } catch (InterruptedException e) {
                    }
                }
            }
            float px, py;
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
                                image.setRGB(x, y, col[x][y].toColor().getRGB());
                            }
                        }
                    }
                }
            }
        }
    }

    public void renderChunk(int minPosx, int maxPosx, int minPosy, int maxPosy, int dx, int dy) {
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
                        image.setRGB(x, y, col[x][y].toColor().getRGB());
                    }
                }
            }
            iteration++;
        }
        iter[dx][dy] = iteration;
    }

    private int[][] allTo(int to, int[][] g) {
        for (int y = 0; y < g[0].length; y++) {
            for (int[] g1 : g) {
                g1[y] = to;
            }
        }
        return g;
    }

    private Triangle[] rotateTriangles(Triangle[] t, Vector3f ang, int prioAxis) {
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

    public float getPercentageDone() {
        return this.percentage;
    }

    public int getChunksRendered() {
        return this.chunksRendered;
    }
    
    public long getRaysFired() {
        return this.u.raysFired;
    }
}
