package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector3f;
import java.util.ArrayList;

public class RaySolver {

    public ArrayList<Triangle> triangles = new ArrayList<>();
    public Triangle[] fixedTriangles;
    public float renderDistance = 1000.0f, camDist, width, height, FOV;
    public Vector3f camPos = new Vector3f(0.0f, 0.0f, 0.0f), camCenter = new Vector3f(0.0f, 0.0f, 0.0f);
    public long raysFired;
    private Vector3f normal, maxPos, minPos;
    private Bound[][][] grid;
    private boolean[][][] gridIsEmpty;
    private int sub;
    private final float EPSILON = 0.0000001f;

    public RaySolver(float renderDist, int sub) {
        this.renderDistance = renderDist;
        this.sub = sub;
    }

    public void setCameraPos(Vector3f pos, float FOV, float width, float height) {
        this.camPos = pos;
        this.width = width;
        this.height = height;
        this.FOV = FOV;
        float dist = width / (float) (2.0 * Math.tan(Math.PI * this.FOV / 360.0));
        this.camDist = dist;
        this.camCenter = new Vector3f(camPos.x, camPos.y, camPos.z - dist);
    }

    public void addObject(Triangle t) {
        triangles.add(t);
    }

    public void setFixed(Triangle[] t) {
        fixedTriangles = t;
        //getBounds();
        //separateTriangles(sub);
    }

    private void getBounds() {
        minPos = fixedTriangles[0].a;
        maxPos = minPos;
        for (Triangle t : fixedTriangles) {
            Vector3f a = t.a, b = t.b, c = t.c;
            Vector3f ma = new Vector3f(Math.max(Math.max(a.x, b.x), c.x), Math.max(Math.max(a.x, b.x), c.y), Math.max(Math.max(a.x, b.x), c.z));
            Vector3f mi = new Vector3f(Math.min(Math.min(a.x, b.x), c.x), Math.min(Math.min(a.x, b.x), c.y), Math.min(Math.min(a.x, b.x), c.z));
            maxPos = new Vector3f(Math.max(ma.x, maxPos.x), Math.max(ma.y, maxPos.y), Math.max(ma.z, maxPos.z));
            minPos = new Vector3f(Math.min(mi.x, minPos.x), Math.min(mi.y, minPos.y), Math.min(mi.z, minPos.z));
        }
        if (triangles.isEmpty()) {
            return;
        }
        for (Triangle t : triangles) {
            Vector3f a = t.a, b = t.b, c = t.c;
            Vector3f ma = new Vector3f(Math.max(Math.max(a.x, b.x), c.x), Math.max(Math.max(a.x, b.x), c.y), Math.max(Math.max(a.x, b.x), c.z));
            Vector3f mi = new Vector3f(Math.min(Math.min(a.x, b.x), c.x), Math.min(Math.min(a.x, b.x), c.y), Math.min(Math.min(a.x, b.x), c.z));
            maxPos = new Vector3f(Math.max(ma.x, maxPos.x), Math.max(ma.y, maxPos.y), Math.max(ma.z, maxPos.z));
            minPos = new Vector3f(Math.min(mi.x, minPos.x), Math.min(mi.y, minPos.y), Math.min(mi.z, minPos.z));
        }
    }

    private void separateTriangles(int subDivide) {
        Vector3f size = Vector3f.sub(maxPos, minPos);
        Vector3f boxSize = Vector3f.div(size, (float) subDivide);
        grid = new Bound[subDivide][subDivide][subDivide];
        gridIsEmpty = new boolean[subDivide][subDivide][subDivide];
        for (int z = 0; z < subDivide; z++) {
            for (int y = 0; y < subDivide; y++) {
                for (int x = 0; x < subDivide; x++) {
                    Vector3f pos = Vector3f.add(minPos, new Vector3f(lerp(minPos.x, maxPos.x, (float) x / subDivide), lerp(minPos.y, maxPos.y, (float) y / subDivide), lerp(minPos.z, maxPos.z, (float) z / subDivide)));
                    Bound b = new Bound(pos, Vector3f.add(pos, boxSize));
                    b.addTriangles(fixedTriangles);
                    grid[x][y][z] = b;
                    gridIsEmpty[x][y][z] = b.isEmpty();
                    System.out.println(b.isEmpty());
                }
            }
        }
    }

    private float lerp(float a, float b, float factor) {
        return a + factor * (b - a);
    }

    private void printCol(ColorObject col) {
        System.out.println("Out: R: " + col.R + " G: " + col.G + " B: " + col.B);
    }

    public ColorObject castRay(float px, float py) {
        Vector3f dir = Vector3f.normalize(Vector3f.sub(Vector3f.add(new Vector3f(px, py, 0.0f), camPos), camCenter));
        Ray ray = new Ray(camCenter, dir, new ColorObject(1.0f, 1.0f, 1.0f));
        ColorObject col = ray.c;
        for (int i = 0; i < 10; i++) {
            ray = castRay(ray);
            col = col.mul(ray.c);
            raysFired++;
            if (ray.c.maxIntensity() > 1.0f || col.isBlack()) {
                break;
            }
        }
        //ColorObject col = recursiveCast(ray, 0, 3, 2);
        return col;
    }

    private ColorObject recursiveCast(Ray ray, int pos, int maxiter, int amt) {
        ray = castRay(ray);
        if (pos == maxiter || ray.c.R == 0.0f || ray.c.G == 0.0f || ray.c.B == 0.0f || ray.c.maxIntensity() > 1.0f) {
            return ray.c;
        }
        ColorObject out = new ColorObject(0.0f, 0.0f, 0.0f);
        for (int i = 0; i < amt; i++) {
            out = ColorObject.add(out, recursiveCast(ray, pos + 1, maxiter, amt).mul(1.0f / amt));
        }
        return out;
    }

    private Ray castRay(Ray ray) {
        Vector3f in = ray.dir;
        PosCol p = traceRay(ray);
        if (p == null) {
            return new Ray(ray.pos, ray.dir, new ColorObject(0.0f, 0.0f, 0.0f));
        }
        if (Vector3f.dot(ray.dir, normal) > 0.0f) {
            normal = Vector3f.mul(normal, -1.0f);
        }
        ray.dir = reflectRay(ray.dir, normal);
        float brdf = BRDF(in, ray.dir, normal, new BRDFVals(0.0f, 0.0f, 1.0f));
        ColorObject newCol = p.col.mul(brdf);
        return new Ray(p.pos, ray.dir, newCol);
    }

    private Vector3f reflectRay(Vector3f dir, Vector3f normal) {
        /*float spread = 2.0f;
        float d = spread / 2.0f;
        float dot = Vector3f.dot(dir, normal);
        Vector3f reflected = Vector3f.normalize(Vector3f.sub(dir, Vector3f.mul(normal, 2.0f * dot)));
        return Vector3f.normalize(Vector3f.add(reflected, new Vector3f((float) Math.random() * spread - d, (float) Math.random() * spread - d, (float) Math.random() * spread - d)));*/
        while (Vector3f.dot(dir, normal) < 0.0f) {
            dir = Vector3f.normalize(new Vector3f((float) Math.random() * 2.0f - 1.0f, (float) Math.random() * 2.0f - 1.0f, (float) Math.random() * 2.0f - 1.0f));
        }
        return dir;
    }

    private PosCol traceRay(Ray ray) {
        Vector3f start = ray.pos;
        Vector3f end = Vector3f.mul(ray.dir, renderDistance);
        PosCol newPos = getNewPos(start, end);
        return newPos;
    }

    private float BRDF(Vector3f in, Vector3f out, Vector3f nor, BRDFVals material) {
        float dot = Vector3f.dot(in, nor);
        Vector3f reflectedRay = Vector3f.normalize(Vector3f.sub(in, Vector3f.mul(nor, 2.0f * dot)));
        float offset = Math.max(Vector3f.dot(out, reflectedRay), 0.0f);
        float value = material.specStrength / (material.specular * (float) (Math.pow(1.0f - offset, 2.0f)) + 1.0f) + material.diffuse - material.specStrength / (1.0f + material.specular);
        return value;
    }

    /*private float BRDF(Vector3f in, Vector3f out, Vector3f normal, BRDFVals material) {
        float dot = Vector3f.dot(in, normal);
        Vector3f reflectedRay = Vector3f.normalize(Vector3f.sub(in, Vector3f.mul(normal, 2.0f * dot)));
        float specularIntensity = calculateSpecular(reflectedRay, out, material);
        float diffuseIntensity = calculateDiffuse(material);
        float brdf = specularIntensity + diffuseIntensity;
        return brdf;
    }*/
    private float calculateSpecular(Vector3f reflectedRay, Vector3f out, BRDFVals material) {
        float offset = Math.max(0, Vector3f.dot(reflectedRay, out)); // Angle between reflected and outgoing rays
        return material.specStrength * (float) Math.pow(offset, material.specular);
    }

    private float calculateDiffuse(BRDFVals material) {
        return material.diffuse * (1.0f - material.specular);
    }

    private PosCol getNewPos(Vector3f lnS, Vector3f lnE) {
        ArrayList<PosCol> i = new ArrayList<>();
        ArrayList<Vector3f> t = new ArrayList<>();
        /*for (int z = 0; z < sub; z++) {
            for (int y = 0; y < sub; y++) {
                for (int x = 0; x < sub; x++) {
                    if (gridIsEmpty[x][y][z]) {
                        continue;
                    }
                    if (grid[x][y][z].isIntersecting(lnS, lnE)) {
                        for (Triangle triangle : grid[x][y][z].triangles) {
                            PosCol pos = getIntersectionPoint(lnS, lnE, triangle);
                            if (pos != null) {
                                i.add(pos);
                                t.add(getNormal(triangle));
                            }
                        }
                    }
                }
            }
        }*/
        for (Triangle triangle : fixedTriangles) {
            PosCol pos = getIntersectionPoint(lnS, lnE, triangle);
            if (pos != null) {
                i.add(pos);
                t.add(getNormal(triangle));
            }
        }
        for (Triangle triangle : triangles) {
            PosCol pos = getIntersectionPoint(lnS, lnE, triangle);
            if (pos != null) {
                i.add(pos);
                t.add(getNormal(triangle));
            }
        }
        if (i.isEmpty()) {
            return null;
        }
        // Add optional mixing based on the opacity of the triangle
        PosCol closest = i.get(0);
        float dist = dist(lnS, lnE);
        for (PosCol iP : i) {
            float d = dist(lnS, iP.pos);
            if (dist > d && d > EPSILON) {
                dist = dist(lnS, iP.pos);
                closest = iP;
            }
        }
        normal = t.get(i.indexOf(closest));
        return closest;
    }

    private Vector3f getNormal(Triangle triangle) {
        Vector3f u = Vector3f.sub(triangle.b, triangle.a);
        Vector3f v = Vector3f.sub(triangle.c, triangle.a);
        Vector3f n = Vector3f.normalize(Vector3f.cross(u, v));
        return n;
    }

    private float dist(Vector3f a, Vector3f b) {
        return (float) Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y) + (b.z - a.z) * (b.z - a.z));
    }

    private PosCol getIntersectionPoint(Vector3f lineStart, Vector3f rayVector, Triangle triangle) {
        float a, f, u, v;
        Vector3f edge1 = Vector3f.sub(triangle.b, triangle.a);
        Vector3f edge2 = Vector3f.sub(triangle.c, triangle.a);
        Vector3f h = Vector3f.cross(rayVector, edge2);
        a = Vector3f.dot(edge1, h);
        if (a > -EPSILON && a < EPSILON) {
            return null;
        }
        f = 1.0f / a;
        Vector3f s = Vector3f.sub(lineStart, triangle.a);
        u = f * Vector3f.dot(s, h);
        if (u < 0.0f || u > 1.0f) {
            return null;
        }
        Vector3f q = Vector3f.cross(s, edge1);
        v = f * Vector3f.dot(rayVector, q);
        if (v < 0.0f || u + v > 1.0f) {
            return null;
        }
        float t = f * Vector3f.dot(edge2, q);
        if (t > EPSILON) {
            Vector3f point = Vector3f.add(Vector3f.mul(rayVector, t), lineStart);
            return new PosCol(point, triangle.col);
        } else {
            return null;
        }
    }
}
