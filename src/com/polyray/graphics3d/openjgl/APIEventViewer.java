package com.polyray.graphics3d.openjgl;

import com.polyray.graphics3d.Window;
import static com.polyray.graphics3d.openjgl.Renderer.COLOR_BUFFER_BIT;
import static com.polyray.graphics3d.openjgl.Renderer.DEPTH_BUFFER_BIT;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Iterator;

public class APIEventViewer {

    private ArrayList<APIEvent> events;
    private int idx = 0;
    private Renderer r;
    private boolean signalUpdate = false;
    private BufferedImage depthTexture;
    private int[] pixels;
    private int he;
    private long timePassed = 0l;
    private int stepSize;
    private float depthMul;

    public APIEventViewer(ArrayList<APIEvent> events, Renderer r, int stepSize, float depthMul) {
        this.events = events;
        this.r = r;
        this.depthTexture = new BufferedImage(r.width, r.height, BufferedImage.TYPE_INT_ARGB);
        this.pixels = ((DataBufferInt) depthTexture.getRaster().getDataBuffer()).getData();
        this.he = r.height;
        this.stepSize = stepSize;
        this.depthMul = depthMul;
        Window w = new Window("") {
            @Override
            public void draw(Graphics2D g, int width, int height) {
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, width, height);
                float scale = (float) height / (he * 2.0f);
                g.scale(scale, scale);
                g.drawImage(r.render, 0, 0, null);
                g.drawImage(depthTexture, 0, he, null);
                g.setTransform(new AffineTransform());
                g.setColor(Color.GREEN);
                g.drawString("Time Passed: " + Math.floor(timePassed / 100.0f) / 10.0f + " microseconds", 10, 10);
            }

            @Override
            public void keyPress(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        idx -= e.isControlDown() ? stepSize : 1;
                        if (idx < 0) {
                            idx = 0;
                        }
                        signalUpdate = true;
                    }
                    case KeyEvent.VK_RIGHT -> {
                        idx += e.isControlDown() ? stepSize : 1;
                        if (idx >= events.size()) {
                            idx = events.size() - 1;
                        }
                        signalUpdate = true;
                    }
                }
            }
        };
        w.createFrame(r.width, r.height * 2, false, true, true, 1.0f);
        update();
        while (true) {
            long startTime = System.nanoTime();
            if (signalUpdate) {
                signalUpdate = false;
                update();
            }
            w.render();
            try {
                Thread.sleep((int) Math.max(17.0f - (System.nanoTime() - startTime) / 1000000.0f, 0.0f));
            } catch (InterruptedException e) {
            }
        }
    }

    private void update() {
        r.clear(DEPTH_BUFFER_BIT | COLOR_BUFFER_BIT);
        Iterator<APIEvent> iterator = events.iterator();
        timePassed = 0l;
        for (int i = 0; i <= idx; i++) {
            APIEvent e = iterator.next();
            timePassed += e.time;
            if (e instanceof TriangleDrawEvent td) {
                r.fillTriangle(td.x0, td.y0, td.z0, td.x1, td.y1, td.z1, td.x2, td.y2, td.z2, td.color);
                continue;
            }
            if (e instanceof TextureTriangleDrawEvent td) {
                r.fillTexturedTriangle(td.x0, td.y0, td.z0, td.u0, td.v0, td.x1, td.y1, td.z1, td.u1, td.v1, td.x2, td.y2, td.z2, td.u2, td.v2, td.texture);
            }
        }
        if(iterator.hasNext()) {
            timePassed += iterator.next().time;
        }
        float[] depthBuffer = r.getDepthBuffer();
        /*for (int i = 0; i < depthBuffer.length; i++) {
            float d = depthBuffer[i];
            if(d == Float.MAX_VALUE) {
                pixels[i] = 0xFF000000;
                continue;
            }
            float c = Math.max(Math.min(d / 3.0f / 255.0f, 1.0f), 0.0f);
            int u = (int) (255.0f * c);
            pixels[i] = 0xFF000000 | u << 16 | u << 8 | u;
        }*/
        for (int i = 0; i < depthBuffer.length; i++) {
            float c = 1.0f - Math.max(Math.min(depthBuffer[i] * depthMul, 1.0f), 0.0f);
            int u = (int) (255.0f * c);
            pixels[i] = 0xFF000000 | u << 16 | u << 8 | u;
        }
    }
}
