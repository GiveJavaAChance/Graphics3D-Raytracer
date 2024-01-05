package com.polyray.graphics3d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Graphics3D {

    public static final int PRIOAXIS_X = 0, PRIOAXIS_Y = 1, PRIOAXIS_Z = 2;
    public float cameraX = 0.0f, cameraY = 0.0f, cameraZ = 0.0f, cameraDist = 100.0f, minRendDist = 1.0f, renderDist = 1000.0f;
    private float windowX, windowY, renderTime;
    public int prioAxis = 2, verticesRendered = 0, totalVertices = 0;
    public Color color = Color.BLACK;
    public final ArrayList<Graphics3DObject> objects = new ArrayList<>();
    private ArrayList<Graphics3DObject> rotated = new ArrayList<>();
    private Graphics3DObject polygon = new Graphics3DObject();
    private final Rotator r = new Rotator();
    private Vector3f ang = new Vector3f(0.0f, 0.0f, 0.0f);
    public float RTime, ZTime, REMTime, STime;

    /**
     * Sets up the renderer
     *
     * @param minRendDist Only renders objects that are minRendDist units away
     * from the camera. Minimum value: 0.1f
     * @param renderDist The maximum render distance.
     */
    public void setup(float minRendDist, float renderDist) {
        if (minRendDist < 0.1f) {
            minRendDist = 0.1f;
        }
        if (renderDist < 0.1f) {
            renderDist = 0.1f;
        }
        this.minRendDist = minRendDist;
        this.renderDist = renderDist;
    }

    /**
     * The default camera position is x = 0, y = 0, z = 0.The default camera
     * distance is 100, that corresponds to the normal size.The coordinates
     * specify the top left corner of the window.
     *
     * @param x Sets the camera x position
     * @param y Sets the camera y position
     * @param z Sets the camera z position away from the distance parameter
     * @param dist Sets the fov to how far away the camera is from the center
     * @param windowW The width of the window
     * @param windowH The height of the window
     */
    public void setCameraPosition(float x, float y, float z, float dist, float windowW, float windowH) {
        this.cameraX = x;
        this.cameraY = y;
        this.cameraZ = z;
        this.cameraDist = dist;
        this.windowX = windowW;
        this.windowY = windowH;

        /*System.out.println("cameraX : " + cameraX);
        System.out.println("cameraY : " + cameraY);
        System.out.println("cameraZ : " + cameraZ);
        System.out.println("cameraDist : " + cameraDist);
        System.out.println("windowX : " + windowX);
        System.out.println("windowY : " + windowY);*/
    }

    public void rotate(Vector3f ang) {
        this.ang = Vector3f.add(this.ang, ang);
    }

    /**
     * Rotates around the x axis
     *
     * @param ang How much the angle changes
     */
    public void rotateX(double ang) {
        this.ang = new Vector3f(this.ang.x + (float) ang, this.ang.y, this.ang.z);
    }

    /**
     * Rotates around the y axis
     *
     * @param ang How much the angle changes
     */
    public void rotateY(double ang) {
        this.ang = new Vector3f(this.ang.x, this.ang.y + (float) ang, this.ang.z);
    }

    /**
     * Rotates around the z axis
     *
     * @param ang How much the angle changes
     */
    public void rotateZ(double ang) {
        this.ang = new Vector3f(this.ang.x, this.ang.y, this.ang.z + (float) ang);
    }

    /**
     * Sets angles to a specified amount
     *
     * @param ang the amount to rotate
     */
    public void setAngle(Vector3f ang) {
        this.ang = ang;
    }

    /**
     * Sets the axis to be prioritized when rotating. Z axis is the default
     *
     * @param axis the axis to prioritize. x -> 0, y -> 1, z -> 2
     */
    public void prioritizeAxis(int axis) {
        prioAxis = axis;
    }

    /**
     * @param col The color
     */
    public void setColor(Color col) {
        color = col;
    }

    /**
     * @param col The color name
     */
    public void setColor(String col) {

        col = col.toUpperCase().trim();
        switch (col) {
            case "BLACK" -> {
                color = Color.BLACK;
            }
            case "BLUE" -> {
                color = Color.BLUE;
            }
            case "CYAN" -> {
                color = Color.CYAN;
            }
            case "DARK_GRAY" -> {
                color = Color.DARK_GRAY;
            }
            case "GRAY" -> {
                color = Color.GRAY;
            }
            case "GREEN" -> {
                color = Color.GREEN;
            }
            case "LIGHT_GRAY" -> {
                color = Color.LIGHT_GRAY;
            }
            case "MAGENTA" -> {
                color = Color.MAGENTA;
            }
            case "ORANGE" -> {
                color = Color.ORANGE;
            }
            case "PINK" -> {
                color = Color.PINK;
            }
            case "RED" -> {
                color = Color.RED;
            }
            case "WHITE" -> {
                color = Color.WHITE;
            }
            case "YELLOW" -> {
                color = Color.YELLOW;
            }
            default -> {
                System.err.println("Error: Could not find color");
                System.exit(1);
            }
        }
    }

    /**
     * Clears all 3D Objects
     */
    public void clearAll() {
        objects.clear();
        polygon = new Graphics3DObject();
    }

    /**
     * @param precision How many decimal points are rounded to.
     * @return The time it took to render the frame.
     */
    public float getRenderingTimeInMillis(int precision) {
        return (float) (Math.floor((double) renderTime * Math.pow(10.0, (double) precision)) / Math.pow(10.0, (double) precision));
    }

    /**
     * Renders objects that are added to Graphics3D buffer
     *
     * @param g2d The current Graphics content to be rendered on
     * @param quality Takes longer to render if set to true, but gives better
     * results.
     */
    public void render(Graphics2D g2d, boolean quality) {
        long startTime = System.nanoTime();
        RenderingHints rh;
        if (!quality) {
            rh = new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
            rh.put(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_SPEED);
            rh.put(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_DISABLE);
            rh.put(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            rh.put(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_SPEED);
            rh.put(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            rh.put(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_PURE);
            rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        } else {
            rh = new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION,
                    RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            rh.put(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);
            rh.put(RenderingHints.KEY_DITHERING,
                    RenderingHints.VALUE_DITHER_ENABLE);
            rh.put(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            rh.put(RenderingHints.KEY_COLOR_RENDERING,
                    RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            rh.put(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            rh.put(RenderingHints.KEY_STROKE_CONTROL,
                    RenderingHints.VALUE_STROKE_NORMALIZE);
            rh.put(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        g2d.setRenderingHints(rh);

        // Rotate
        ArrayList<Graphics3DObject> rCopy = new ArrayList<>(rotated);
        Thread t = new Thread(() -> {
            totalVertices = 0;
            rotated = new ArrayList<>();
            for (Graphics3DObject obj : objects) {
                Graphics3DObject newObject = new Graphics3DObject();
                newObject.doFill(obj.fill);
                newObject.setColor(obj.c);
                if (obj.gradient) {
                    for (int i = 0; i < obj.vertices.size(); i++) {
                        newObject.addVertex(r.calcRot(obj.vertices.get(i), ang, prioAxis), obj.colors.get(i));
                    }
                } else {
                    for (Vector3f v : obj.vertices) {
                        totalVertices++;
                        newObject.addVertex(r.calcRot(v, ang, prioAxis), obj.radius);
                    }
                }
                rotated.add(newObject);
            }
        });
        t.start();
        long rTime = System.nanoTime();
        // Calulate Z-Depth
        float[] dist = new float[rCopy.size()];
        verticesRendered = 0;
        for (int i = 0; i < rCopy.size(); i++) {
            Graphics3DObject obj = rCopy.get(i);
            if (!obj.vertices.isEmpty()) {
                Vector3f center = obj.centerOfMass();
                dist[i] = getZDepth(center);
                // Cache depth to avoid recalculating
                obj.setDepth(dist[i]);
            }
        }
        long zTime = System.nanoTime();
        ArrayList<Graphics3DObject> renderObjects = new ArrayList<>();
        for (int i = 0; i < rCopy.size(); i++) {
            if (dist[i] < renderDist && isForward(rCopy.get(i))) {
                renderObjects.add(rCopy.get(i));
            }
        }

        dist = new float[renderObjects.size()];
        for (int i = 0; i < renderObjects.size(); i++) {
            dist[i] = renderObjects.get(i).getDepth(); // Reuse cached depth
        }
        long remTime = System.nanoTime();

        // Sort
        /*Graphics3DObject[] sorted = new Graphics3DObject[renderObjects.size()];
        for (int i = 0; i < renderObjects.size(); i++) {
            sorted[i] = renderObjects.get(i);
        }
        sorted = sortGraphics3DObjects(sorted, dist);*/
        Graphics3DObject[] sorted = renderObjects.toArray(Graphics3DObject[]::new);
        Arrays.sort(sorted, Comparator.comparingDouble(Graphics3DObject::getDepth).reversed());
        long sTime = System.nanoTime();
        
        // Project
        for (Graphics3DObject obj : sorted) { // Sorted
            g2d.setColor(obj.c);
            int vLen = obj.vertices.size();
            ArrayList<Vector2f> projected = new ArrayList<>();
            int off = 0;
            if (obj.vertices.size() > 1) {
                boolean prevZ1 = false;
                for (int j = 0; j < vLen; j++) {
                    Vector3f v0 = obj.vertices.get((j + vLen - 1) % vLen);
                    Vector3f v1 = obj.vertices.get(j);
                    Vector3f v2 = obj.vertices.get((j + 1) % vLen);
                    boolean z0 = getZDepth(v0) > minRendDist;
                    boolean z1 = getZDepth(v1) > minRendDist;
                    boolean z2 = getZDepth(v2) > minRendDist;
                    if (j == 0) {
                        prevZ1 = z0;
                    }
                    if (!z1) {
                        if (z0 && !z2) {  // If right neighbour is inside -> move towards it
                            projected.add(tryProject(v1, v0, false));
                        } else if (!z0 && z2) {  // If left neighbour is inside -> move towards it
                            projected.add(tryProject(v1, v2, false));
                        } else if (z0 && z2) { // If both neighbours are inside -> :
                            if (!prevZ1) { // If the previous "current" was outside -> move to the left neighbour
                                projected.add(tryProject(v1, v0, false));
                            } else if (prevZ1) { // If the previus "current" was inside -> split up to 2 points and move towards each neighbour
                                projected.add(tryProject(v1, v0, false));
                                projected.add(tryProject(v1, v2, false));
                                off++;
                            }
                        } else if (!z0 && !z2) { // If both neighbours are outside -> remove current
                            projected.add(null);
                        }
                    } else {
                        try {
                            projected.add(project2D(v1));
                        } catch (ProjectionException ex) {
                        }
                    }
                    prevZ1 = z1;
                }
                /*for (int j = 0; j < obj.vertices.size(); j++) {
                Vector3f v = obj.vertices.get(j);
                Vector3f rotated = calcRot(v, angX, angY, angZ);
                Vector3f prev = calcRot(obj.vertices.get((j + obj.vertices.size() - 1) % obj.vertices.size()), angX, angY, angZ);
                vec[i][j] = tryProject(rotated, prev, false);
                }*/
            } else {
                projected.add(tryProject(obj.vertices.get(0), null, true));
            }
            Vector2f[] veci = projected.toArray(Vector2f[]::new);
            boolean allXBelowZero = true;
            boolean allXAboveWindowX = true;
            boolean allYBelowZero = true;
            boolean allYAboveWindowY = true;

            for (Vector2f v : veci) {
                if (v.x >= 0) {
                    allXBelowZero = false;
                }
                if (v.x <= windowX) {
                    allXAboveWindowX = false;
                }
                if (v.y >= 0) {
                    allYBelowZero = false;
                }
                if (v.y <= windowY) {
                    allYAboveWindowY = false;
                }
            }
            if (allXBelowZero || allXAboveWindowX || allYBelowZero || allYAboveWindowY) {
                continue;
            }
            if (veci.length > 1) { // Consolidated the polygon creation logic
                if (obj.gradient) { // Is made of multiple colors
                    BufferedImage image = gradientPolygon(veci, obj.getColors(), obj.fill);
                    g2d.drawImage(image, 0, 0, null);
                } else {
                    Polygon p = new Polygon();
                    for (Vector2f v : veci) {
                        if (v != null) {
                            p.addPoint((int) v.x, (int) v.y);
                            verticesRendered++;
                        }
                    }
                    if (veci.length > 2) { // Closes the polygon and fills/draws
                        if (obj.fill) {
                            g2d.fillPolygon(p);
                        } else {
                            g2d.drawPolygon(p);
                        }
                    } else { // If it's a line
                        if (veci[0] != null && veci[1] != null) {
                            g2d.drawLine((int) veci[0].x, (int) veci[0].y, (int) veci[1].x, (int) veci[1].y);
                        }
                    }
                }
            } else if (veci[0] != null) { // Draw a circle if only one point exists
                float radius = calcPointRadius(obj.vertices.get(0), obj.radius);
                int rx = (int) (veci[0].x - radius / 2.0f);
                int ry = (int) (veci[0].y - radius / 2.0f);
                g2d.fillOval(rx, ry, (int) radius, (int) radius);
                verticesRendered++;
            }
        }
        try {
            t.join();
        } catch (InterruptedException ex) {
        }
        long endTime = System.nanoTime();
        renderTime = (endTime - startTime) / 1000000.0f;
        RTime = (rTime - startTime) / 1000000.0f;
        ZTime = (zTime - rTime) / 1000000.0f;
        REMTime = (remTime - zTime) / 1000000.0f;
        STime = (sTime - remTime) / 1000000.0f;
    }

    private BufferedImage gradientPolygon(Vector2f[] p, Color[] colors, boolean fill) {
        if (p.length > colors.length || p.length == 0) {
            return null;
        }
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (Vector2f v : p) {
            minX = Math.min(minX, v.x);
            minY = Math.min(minY, v.y);
            maxX = Math.max(maxX, v.x);
            maxY = Math.max(maxY, v.y);
        }

        int w = (int) Math.ceil(maxX - minX);
        int h = (int) Math.ceil(maxY - minY);
        if (p.length == 2) {
            BufferedImage img = new BufferedImage((int) maxX + 1, (int) maxY + 1, BufferedImage.TYPE_INT_ARGB);
            float x1 = p[0].x;
            float y1 = p[0].y;
            float x2 = p[1].x;
            float y2 = p[1].y;
            GradientPaint gradient = new GradientPaint(x1, y1, colors[0], x2, y2, colors[1]);
            Graphics2D g = img.createGraphics();
            g.setPaint(gradient);
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            return img;
        }
        BufferedImage tmp = new BufferedImage((int) Math.min(minX + w + 1, windowX), (int) Math.min(minY + h + 1, windowY), BufferedImage.TYPE_INT_ARGB);
        Polygon poly = new Polygon();
        for (Vector2f p1 : p) {
            poly.addPoint((int) (p1.x - minX), (int) (p1.y - minY));
        }
        Graphics2D g = tmp.createGraphics();
        for (int i = 0; i < (int) Math.ceil(p.length / 2.0f); i++) {
            float x1 = p[i].x - minX;
            float y1 = p[i].y - minY;
            float x2 = p[i + (int) Math.ceil((p.length - 1.0f) / 2.0f)].x - minX;
            float y2 = p[i + (int) Math.ceil((p.length - 1.0f) / 2.0f)].y - minY;
            Color c1 = colors[i];
            Color c2 = colors[i + (int) Math.ceil((p.length - 1.0f) / 2.0f)];
            int alpha = (int) (255.0f / p.length); // Adjust alpha based on the number of shapes
            Color a = new Color(c1.getRed(), c1.getGreen(), c1.getBlue());
            Color b = new Color(c2.getRed(), c2.getGreen(), c2.getBlue());
            GradientPaint gradient = new GradientPaint(x1, y1, a, x2, y2, b);

            // Apply alpha compositing
            AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            g.setComposite(alphaComposite);

            g.setPaint(gradient);
            if (fill) {
                g.fill(poly);
            } else {
                g.draw(poly);
            }
        }
        g.dispose();
        BufferedImage out = new BufferedImage((int) windowX, (int) windowY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = out.createGraphics();
        g2d.drawImage(tmp, (int) minX, (int) minY, null);
        g2d.dispose();
        return out;
    }

    private Vector2f tryProject(Vector3f pos, Vector3f to, boolean single) {
        float t = getZDepth(pos);
        Vector2f vec = null;
        if (single) {
            if (t > minRendDist) {
                try {
                    vec = (Vector2f) project2D(pos);
                } catch (ProjectionException ex) {
                }
            }
        } else {
            if (t <= minRendDist && getZDepth(to) <= minRendDist) {
                return null;
            }
            /*Vector3f delta = Vector3f.sub(pos, prev);
            int maxIter = 100;
            delta = Vector3f.div(delta, maxIter);
            int iter = 0;
            while ((cameraDist - (pos.z + cameraZ)) <= minRendDist && iter < maxIter) {
                pos = Vector3f.sub(pos, delta);
                iter++;
            }
            if (iter != maxIter) {
                vec = (Vector2f) project2D(pos);
            }*/
            if (t < minRendDist) {
                Vector3f delta = Vector3f.sub(pos, to);
                delta = Vector3f.div(delta, delta.z);
                float k = -t + minRendDist + 0.1f;
                pos = Vector3f.sub(pos, Vector3f.mul(delta, k));
            }
            try {
                vec = (Vector2f) project2D(pos);
            } catch (ProjectionException ex) {
            }
        }
        return vec;
    }

    private Vector2f tryBounds(Vector3f pos, Vector3f to) {
        return null;
    }

    private Vector2f findIntersection(Vector2f a, Vector2f b) {
        Vector2f delta = Vector2f.sub(b, a);
        Vector2f vx = null, vy = null;
        if (delta.x != 0.0f) {
            vx = Vector2f.div(delta, delta.x);
        }
        if (delta.y != 0.0f) {
            vy = Vector2f.div(delta, delta.y);
        }
        float dxMax = a.x - windowX;
        float dxMin = a.x;
        float dyMax = a.y - windowY;
        float dyMin = a.y;
        Vector2f iXMax = null, iXMin = null, iYMax = null, iYMin = null;
        if (vx != null) {
            if (dxMax >= 0.0f) {
                iXMax = Vector2f.sub(a, Vector2f.mul(vx, dxMax));
            } else if (dxMin <= 0.0f) {
                iXMin = Vector2f.sub(a, Vector2f.mul(vx, dxMin));
            }
        }
        if (vy != null) {
            if (dyMax >= 0.0f) {
                iYMax = Vector2f.sub(a, Vector2f.mul(vy, dyMax));
            } else if (dyMin <= 0.0f) {
                iYMin = Vector2f.sub(a, Vector2f.mul(vy, dyMax));
            }
        }
        if (iXMax != null) {
            if (!(iXMax.x >= 0.0f && iXMax.y > 0.0f && iXMax.x <= windowX && iXMax.y <= windowY)) {
                iXMax = null;
            }
        }
        if (iXMin != null) {
            if (!(iXMin.x >= 0.0f && iXMin.y > 0.0f && iXMin.x <= windowX && iXMin.y <= windowY)) {
                iXMin = null;
            }
        }
        if (iYMax != null) {
            if (!(iYMax.x >= 0.0f && iYMax.y > 0.0f && iYMax.x <= windowX && iYMax.y <= windowY)) {
                iYMax = null;
            }
        }
        if (iYMin != null) {
            if (!(iYMin.x >= 0.0f && iYMin.y > 0.0f && iYMin.x <= windowX && iYMin.y <= windowY)) {
                iYMin = null;
            }
        }
        float d1 = -1.0f, d2 = -1.0f, d3 = -1.0f, d4 = -1.0f;
        if (iXMax != null) {
            d1 = Vector2f.length(Vector2f.sub(iXMax, a));
        }
        if (iXMin != null) {
            d2 = Vector2f.length(Vector2f.sub(iXMin, a));
        }
        if (iYMax != null) {
            d3 = Vector2f.length(Vector2f.sub(iYMax, a));
        }
        if (iYMin != null) {
            d4 = Vector2f.length(Vector2f.sub(iYMin, a));
        }
        float minDistance = Math.max(Math.max(d1, d2), Math.max(d3, d4));
        Vector2f closest = null;

        if (d1 >= 0 && d1 < minDistance) {
            minDistance = d1;
            closest = iXMax;
        }
        if (d2 >= 0 && d2 < minDistance) {
            minDistance = d2;
            closest = iXMin;
        }
        if (d3 >= 0 && d3 < minDistance) {
            minDistance = d3;
            closest = iYMax;
        }
        if (d4 >= 0 && d4 < minDistance) {
            closest = iYMin;
        }
        return closest;
    }

    private void quickSort(Graphics3DObject[] objects, float[] dist, int low, int high) {
        if (low < high) {
            int pi = partition(objects, dist, low, high);
            quickSort(objects, dist, low, pi - 1);
            quickSort(objects, dist, pi + 1, high);
        }
    }

    private int partition(Graphics3DObject[] objects, float[] dist, int low, int high) {
        double pivot = dist[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (dist[j] > pivot) {
                i++;
                Graphics3DObject tempObj = objects[i];
                objects[i] = objects[j];
                objects[j] = tempObj;
                float tempDist = dist[i];
                dist[i] = dist[j];
                dist[j] = tempDist;
            }
        }
        Graphics3DObject tempObj = objects[i + 1];
        objects[i + 1] = objects[high];
        objects[high] = tempObj;

        float tempDist = dist[i + 1];
        dist[i + 1] = dist[high];
        dist[high] = tempDist;

        return i + 1;
    }

    private Graphics3DObject[] sortGraphics3DObjects(Graphics3DObject[] objects, float[] dist) {
        quickSort(objects, dist, 0, objects.length - 1);
        return objects;
    }

    private boolean isForward(Graphics3DObject obj) {
        for (Vector3f v : obj.vertices) {
            if (getZDepth(v) > minRendDist) {
                return true;
            }
        }
        return false;
    }

    public boolean isInside(Vector2f v) {
        return v.x >= 0 && v.y >= 0 && v.x <= windowX && v.y <= windowY;
    }

    /**
     * Draws a point in 3D at x, y, z.
     *
     * @param x The x coordinate of the point
     * @param y The y coordinate of the point
     * @param z The z coordinate of the point
     * @param size The size of the point
     */
    public void add3DPoint(float x, float y, float z, float size) {
        Graphics3DObject obj = new Graphics3DObject();
        obj.addVertex(new Vector3f(x, y, z), size);
        obj.setColor(color);
        objects.add(obj);
    }

    /**
     * Draws the x, y and z axis.x axis appear red, y axis appear blue and z
     * axis appear green.
     *
     * @param len How far the axis extends
     * @param res The resolution of the axis, how many segments to divide the
     * lines up in. Higher resolution gives more acuracy when overlapping
     * objects are present
     */
    public void addXYZAxis(float len, int res) {
        float len2 = len / res * 2.0f;
        for (int i = 0; i < res; i++) {
            Graphics3DObject obj1 = new Graphics3DObject();
            Graphics3DObject obj2 = new Graphics3DObject();
            Graphics3DObject obj3 = new Graphics3DObject();
            obj1.addVertex(new Vector3f(-len + i * len2, 0.0f, 0.0f));
            obj1.addVertex(new Vector3f(-len + (i + 1) * len2, 0.0f, 0.0f));
            obj1.setColor(Color.RED);
            obj2.addVertex(new Vector3f(0.0f, -len + i * len2, 0.0f));
            obj2.addVertex(new Vector3f(0.0f, -len + (i + 1) * len2, 0.0f));
            obj2.setColor(Color.BLUE);
            obj3.addVertex(new Vector3f(0.0f, 0.0f, -len + i * len2));
            obj3.addVertex(new Vector3f(0.0f, 0.0f, -len + (i + 1) * len2));
            obj3.setColor(Color.GREEN);
            objects.add(obj1);
            objects.add(obj2);
            objects.add(obj3);
        }
    }

    /**
     * Draws a line between the starting point (x1, y1, z1) to the ending point
     * (x2, y2, z2).
     *
     * @param x1 The x coordinate of the starting point
     * @param y1 The y coordinate of the starting point
     * @param z1 The z coordinate of the starting point
     * @param x2 The x coordinate of the ending point
     * @param y2 The y coordinate of the ending point
     * @param z2 The z coordinate of the ending point
     */
    public void add3DLine(float x1, float y1, float z1, float x2, float y2, float z2) {
        Graphics3DObject obj = new Graphics3DObject();
        obj.addVertex(new Vector3f(x1, y1, z1));
        obj.addVertex(new Vector3f(x2, y2, z2));
        obj.setColor(color);
        objects.add(obj);
    }

    /**
     * Draws a line between the starting vertex a, to the ending vertex b.
     *
     * @param a
     * @param b
     */
    public void add3DLine(Vector3f a, Vector3f b) {
        Graphics3DObject obj = new Graphics3DObject();
        obj.addVertex(a);
        obj.addVertex(b);
        obj.setColor(color);
        objects.add(obj);
    }

    /**
     * Draws a framed cube
     *
     * @param x The x coordinate of the top left corner
     * @param y The y coordinate of the top left corner
     * @param z The z coordinate of the top left corner
     * @param width The width of the cube
     * @param height The height of the cube
     * @param depth The depth of the cube
     * @param detail The detail of the cube;
     */
    public void add3DRect(float x, float y, float z, float width, float height, float depth, int detail) {
        float w = width / detail;
        float h = height / detail;
        float d = depth / detail;
        for (int i = 0; i < detail; i++) {
            float W = i * w;
            float H = i * h;
            float D = i * d;
            float we = (i + 1.0f) * w;
            float he = (i + 1.0f) * h;
            float de = (i + 1.0f) * d;
            add3DLine(x + W, y, z, x + we, y, z);
            add3DLine(x + width, y + H, z, x + width, y + he, z);
            add3DLine(x + we, y + height, z, x + W, y + height, z);
            add3DLine(x, y + he, z, x, y + H, z);
            add3DLine(x + W, y, z + depth, x + we, y, z + depth);
            add3DLine(x + width, y + H, z + depth, x + width, y + he, z + depth);
            add3DLine(x + we, y + height, z + depth, x + W, y + height, z + depth);
            add3DLine(x, y + he, z + depth, x, y + H, z + depth);
            add3DLine(x, y, z + D, x, y, z + de);
            add3DLine(x + width, y, z + D, x + width, y, z + de);
            add3DLine(x + width, y + height, z + D, x + width, y + height, z + de);
            add3DLine(x, y + height, z + D, x, y + height, z + de);
        }
    }

    /**
     * Draws a framed cube
     *
     * @param v the position of the 3DRect
     * @param size the size of the 3DRect
     * @param detail The detail of the cube;
     */
    public void add3DRect(Vector3f v, Vector3f size, int detail) {
        float w = size.x / detail;
        float h = size.y / detail;
        float d = size.z / detail;
        float x3 = v.x + size.x;
        float y3 = v.y + size.y;
        float z3 = v.z + size.z;
        for (int i = 0; i < detail; i++) {
            float x1 = v.x + (i * w);
            float x2 = v.x + ((i + 1.0f) * w);
            float y1 = v.y + (i * h);
            float y2 = v.y + ((i + 1.0f) * h);
            float z1 = v.z + (i * d);
            float z2 = v.z + ((i + 1.0f) * d);
            add3DLine(x1, v.y, v.z, x2, v.y, v.z);
            add3DLine(x3, y1, v.z, x3, y2, v.z);
            add3DLine(x2, y3, v.z, x1, y3, v.z);
            add3DLine(v.x, y2, v.z, v.x, y1, v.z);

            add3DLine(x1, v.y, z3, x2, v.y, z3);
            add3DLine(x3, y1, z3, x3, y2, z3);
            add3DLine(x2, y3, z3, x1, y3, z3);
            add3DLine(v.x, y2, z3, v.x, y1, z3);

            add3DLine(v.x, v.y, z1, v.x, v.y, z2);
            add3DLine(x3, v.y, z1, x3, v.y, z2);
            add3DLine(x3, y3, z1, x3, y3, z2);
            add3DLine(v.x, y3, z1, v.x, y3, z2);
        }
    }

    public void steve(float x, float y, float z) {
        add3DRect(x, y, z, 5.0f, 10.0f, 2.5f, 1);
        add3DRect(x, y + 10.0f, z, 2.5f, 10.0f, 2.5f, 1);
        add3DRect(x + 2.5f, y + 10.0f, z, 2.5f, 10.0f, 2.5f, 1);
        add3DRect(x + 5.0f, y, z, 2.5f, 10.0f, 2.5f, 1);
        add3DRect(x - 2.5f, y, z, 2.5f, 10.0f, 2.5f, 1);
        add3DRect(x, y - 5.0f, z - 1.0f, 5.0f, 5.0f, 5.0f, 1);
    }

    public void addObject(Graphics3DObject obj) {
        objects.add(obj);
    }

    /**
     * Adds all objects
     *
     * @param objects the objects that are going to be added
     */
    public void addAllObjects(Graphics3DObject[] objects) {
        this.objects.addAll(Arrays.asList(objects));
    }

    /**
     * Resets the 3D polygon to begin a new one. To add vertices to the polygon
     * use {@link #add3DPolygonVertex()}.
     */
    public void beginShape() {
        polygon = new Graphics3DObject();
    }

    /**
     * Ends shape and allows to be rendered. To add vertices to the polygon use
     * use {@link #add3DPolygonVertex()}.
     *
     * @param fill
     */
    public void endShape(boolean fill) {
        polygon.doFill(fill);
        polygon.setColor(color);
        objects.add(polygon);
    }

    /**
     * adds a vertex to the polygon created from {@link #add3DPolygonVertex()}.
     *
     * @param x The x coordinate of the vertex
     * @param y The y coordinate of the vertex
     * @param z The z coordinate of the vertex
     */
    public void add3DPolygonVertex(float x, float y, float z) {
        polygon.addVertex(new Vector3f(x, y, z));
    }

    /**
     * adds a vertex to the polygon created from {@link #add3DPolygonVertex()}.
     *
     * @param v the vertex
     */
    public void add3DPolygonVertex(Vector3f v) {
        polygon.addVertex(v);
    }

    /**
     * adds a vertex to the polygon created from {@link #add3DPolygonVertex()}.
     *
     * @param v the vertex
     * @param c the color of the vertex
     */
    public void add3DPolygonVertex(Vector3f v, Color c) {
        polygon.addVertex(v, c);
    }

    private Vector2f project2D(Vector3f v) throws ProjectionException {
        float x1 = ((cameraDist * v.x) / (cameraDist - (v.z + cameraZ))) - cameraX;
        float y1 = ((cameraDist * v.y) / (cameraDist - (v.z + cameraZ))) - cameraY;
        if ((cameraDist - (v.z + cameraZ)) > minRendDist) {
            return new Vector2f(x1, y1);
        } else {
            String errorMessage = "Could not project with t = " + (cameraDist - (v.z + cameraZ))
                    + " for position v(" + v.x + ", " + v.y + ", " + v.z + ")"
                    + ". Camera parameters: cameraDist=" + cameraDist
                    + ", cameraZ=" + cameraZ
                    + ". minRendDist=" + minRendDist;
            throw new ProjectionException(errorMessage);
        }
    }

    /*private Vector3f calcRot(Vector3f v, Vector3f ang) {
        float dx = 0.0f, dy = 0.0f, dz = 0.0f;
        switch (prioAxis) {
            case 0 -> {
                dx = appRotX(0, 1, 0, v.x, v.y, v.z, ang.y);
                dy = appRotY(0, 1, 0, v.x, v.y, v.z, ang.y);
                dz = appRotZ(0, 1, 0, v.x, v.y, v.z, ang.y);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
            }

            case 1 -> {
                dx = appRotX(0, 0, 1, v.x, v.y, v.z, ang.z);
                dy = appRotY(0, 0, 1, v.x, v.y, v.z, ang.z);
                dz = appRotZ(0, 0, 1, v.x, v.y, v.z, ang.z);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, ang.x);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
            }

            case 2 -> {
                dx = appRotX(1, 0, 0, v.x, v.y, v.z, ang.x);
                dy = appRotY(1, 0, 0, v.x, v.y, v.z, ang.x);
                dz = appRotZ(1, 0, 0, v.x, v.y, v.z, ang.x);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, ang.y);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, ang.z);
            }
        }

        return new Vector3f(dx, dy, dz);
    }*/
    public Vector3f calcInverseRot(float x, float y, float z) {
        float dx = 0.0f, dy = 0.0f, dz = 0.0f;
        switch (prioAxis) {
            case 0 -> {
                dx = appRotX(1, 0, 0, x, y, z, -ang.x);
                dy = appRotY(1, 0, 0, x, y, z, -ang.x);
                dz = appRotZ(1, 0, 0, x, y, z, -ang.x);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
            }

            case 1 -> {
                dx = appRotX(0, 1, 0, x, y, z, -ang.y);
                dy = appRotY(0, 1, 0, x, y, z, -ang.y);
                dz = appRotZ(0, 1, 0, x, y, z, -ang.y);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, -ang.z);
            }

            case 2 -> {
                dx = appRotX(0, 0, 1, x, y, z, -ang.z);
                dy = appRotY(0, 0, 1, x, y, z, -ang.z);
                dz = appRotZ(0, 0, 1, x, y, z, -ang.z);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, -ang.y);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, -ang.x);
            }
        }
        return new Vector3f(dx, dy, dz);
    }

    public Vector3f calcInverseRot(Vector3f v) {
        return r.calcInverseRot(v, ang, prioAxis);
    }

    private float getZDepth(Vector3f v) {
        return (float) (cameraDist - (v.z + cameraZ));
    }

    public float calcZDepth(Vector3f v) {
        Vector3f rot = r.calcRot(v, ang, prioAxis);
        return getZDepth(rot);
    }

    private float calcPointRadius(Vector3f v, float radius) {
        if (getZDepth(v) > minRendDist) {
            try {
                Vector2f v1 = project2D(v);
                Vector2f v2 = project2D(new Vector3f(v.x + radius, v.y, v.z));
                return Vector2f.length(Vector2f.sub(v1, v2));
            } catch (ProjectionException ex) {
            }
        }
        return 0.0f;
    }

    // Legacy:

    /*private Object calcX(float x, float y, float z) {
        float dx = 0.0f, dy, dz = 0.0f;
        switch (prioAxis) {
            case 0 -> {
                dx = appRotX(0, 1, 0, x, y, z, angY);
                dy = appRotY(0, 1, 0, x, y, z, angY);
                dz = appRotZ(0, 1, 0, x, y, z, angY);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, angX);
            }

            case 1 -> {
                dx = appRotX(0, 0, 1, x, y, z, angZ);
                dy = appRotY(0, 0, 1, x, y, z, angZ);
                dz = appRotZ(0, 0, 1, x, y, z, angZ);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, angY);
            }

            case 2 -> {
                dx = appRotX(1, 0, 0, x, y, z, angX);
                dy = appRotY(1, 0, 0, x, y, z, angX);
                dz = appRotZ(1, 0, 0, x, y, z, angX);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
            }
        }

        if ((double) (cameraDist - (dz + cameraZ)) > 1.0) {
            return (float) ((cameraDist * dx) / (float) (cameraDist - (dz + cameraZ))) - cameraX;
        } else {
            return "err";
        }
    }

    private Object calcY(float x, float y, float z) {
        float dx, dy = 0.0f, dz = 0.0f;
        switch (prioAxis) {
            case 0 -> {
                dx = appRotX(0, 1, 0, x, y, z, angY);
                dy = appRotY(0, 1, 0, x, y, z, angY);
                dz = appRotZ(0, 1, 0, x, y, z, angY);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, angX);
            }

            case 1 -> {
                dx = appRotX(0, 0, 1, x, y, z, angZ);
                dy = appRotY(0, 0, 1, x, y, z, angZ);
                dz = appRotZ(0, 0, 1, x, y, z, angZ);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dy = appRotY(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                dz = appRotZ(1, 0, 0, tmpX, tmpY, tmpZ, angX);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, angY);
            }

            case 2 -> {
                dx = appRotX(1, 0, 0, x, y, z, angX);
                dy = appRotY(1, 0, 0, x, y, z, angX);
                dz = appRotZ(1, 0, 0, x, y, z, angX);
                float tmpX = dx;
                float tmpY = dy;
                float tmpZ = dz;
                dx = appRotX(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dy = appRotY(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                dz = appRotZ(0, 1, 0, tmpX, tmpY, tmpZ, angY);
                tmpX = dx;
                tmpY = dy;
                tmpZ = dz;
                dy = appRotY(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
                dz = appRotZ(0, 0, 1, tmpX, tmpY, tmpZ, angZ);
            }
        }
        if ((double) (cameraDist - (dz + cameraZ)) > 1.0) {
            return (float) ((cameraDist * dy) / (float) (cameraDist - (dz + cameraZ))) + cameraY;
        } else {
            return "err";
        }
    }*/
    private float appRotX(double a, double b, double c, float x, float y, float z, double ang) {
        return (float) (x * Math.cos(ang) + (b * z - c * y) * Math.sin(ang) + a * (a * x + b * y + c * z) * (1.0 - Math.cos(ang)));
    }

    private float appRotY(double a, double b, double c, float x, float y, float z, double ang) {
        return (float) (y * Math.cos(ang) + (c * x - a * z) * Math.sin(ang) + b * (a * x + b * y + c * z) * (1.0 - Math.cos(ang)));
    }

    private float appRotZ(double a, double b, double c, float x, float y, float z, double ang) {
        return (float) (z * Math.cos(ang) + (a * y - b * x) * Math.sin(ang) + c * (a * x + b * y + c * z) * (1.0 - Math.cos(ang)));
    }
}
