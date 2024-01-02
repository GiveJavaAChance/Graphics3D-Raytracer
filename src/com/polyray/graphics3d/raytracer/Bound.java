package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector3f;
import java.util.ArrayList;

class Bound {

    public Vector3f max, min;
    private boolean isEmpty;
    public Triangle[] triangles;
    public Vector3f[] boxVertices;

    public Bound(Vector3f maxPos, Vector3f minPos) {
        this.max = maxPos;
        this.min = minPos;
        boxVertices = new Vector3f[]{
            new Vector3f(this.min.x, this.min.y, this.max.z),
            new Vector3f(this.min.x, this.max.y, this.min.z),
            new Vector3f(this.min.x, this.max.y, this.max.z),
            new Vector3f(this.max.x, this.min.y, this.min.z),
            new Vector3f(this.max.x, this.min.y, this.max.z),
            new Vector3f(this.max.x, this.max.y, this.min.z),
            new Vector3f(this.max.x, this.max.y, this.max.z)
        };
    }

    public void addTriangles(Triangle[] t) {
        ArrayList<Triangle> tr = new ArrayList<>();
        for (Triangle t1 : t) {
            if (intersectsBox(t1)) {
                tr.add(t1);
            }
        }
        isEmpty = tr.isEmpty();
        if (isEmpty) {
            triangles = null;
            return;
        }
        triangles = new Triangle[tr.size()];
        for (int i = 0; i < tr.size(); i++) {
            triangles[i] = tr.get(i);
        }
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    private boolean intersectsBox(Triangle t) {
        if (isPointInBox(t.a) || isPointInBox(t.b) || isPointInBox(t.c)) {
            return true;
        }
        return checkEdgesAgainstTriangle(t);
    }

    private boolean isPointInBox(Vector3f point) {
        return (point.x >= this.min.x && point.x <= this.max.x && point.y >= this.min.y && point.y <= this.max.y && point.z >= this.min.z && point.z <= this.max.z);
    }

    private boolean checkEdgesAgainstTriangle(Triangle t) {
        for (int i = 0; i < boxVertices.length - 1; i += 2) {
            Vector3f start = boxVertices[i];
            Vector3f end = boxVertices[i + 1];
            if (checkEdgeAgainstTriangle(start, end, t)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkEdgeAgainstTriangle(Vector3f lineStart, Vector3f lineEnd, Triangle tr) {
        float a, f, u, v;
        Vector3f edge1 = Vector3f.sub(tr.b, tr.a);
        Vector3f edge2 = Vector3f.sub(tr.c, tr.a);
        Vector3f h = Vector3f.cross(lineEnd, edge2);
        float EPSILON = 0.0000001f;
        a = Vector3f.dot(edge1, h);
        if (a > -EPSILON && a < EPSILON) {
            return false;
        }
        f = 1.0f / a;
        Vector3f s = Vector3f.sub(lineStart, tr.a);
        u = f * Vector3f.dot(s, h);
        if (u < 0.0f || u > 1.0f) {
            return false;
        }
        Vector3f q = Vector3f.cross(s, edge1);
        v = f * Vector3f.dot(lineEnd, q);
        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }
        float t = f * Vector3f.dot(edge2, q);
        return t > EPSILON;
    }

    private boolean isInside(Vector3f p) {
        return (p.x > this.min.x && p.x < this.max.x && p.y > this.min.y && p.y < this.max.y && p.z > this.min.z && p.z < this.max.z);
    }

    public boolean isIntersecting(Vector3f lineStart, Vector3f lineEnd) {
        if(isInside(lineStart) || isInside(lineEnd)) {
            return true;
        }
        if (lineStart.x > this.max.x && lineEnd.x > this.max.x) {
            return false;
        }
        if (lineStart.y > this.max.y && lineEnd.y > this.max.y) {
            return false;
        }
        if (lineStart.z > this.max.z && lineEnd.z > this.max.z) {
            return false;
        }
        if (lineStart.x < this.min.x && lineEnd.x < this.min.x) {
            return false;
        }
        if (lineStart.y < this.min.y && lineEnd.y < this.min.y) {
            return false;
        }
        if (lineStart.z < this.min.z && lineEnd.z < this.min.z) {
            return false;
        }
        if ((lineStart.z < this.min.z && lineEnd.z > this.min.z) || (lineStart.z > this.min.z && lineEnd.z < this.min.z)) {
            float t = (this.min.z - lineStart.z) / (lineEnd.z - lineStart.z);
            float intersectX = lineStart.x + t * (lineEnd.x - lineStart.x);
            float intersectY = lineStart.y + t * (lineEnd.y - lineStart.y);
            if (intersectX >= this.min.x && intersectX <= this.max.x && intersectY >= this.min.y && intersectY <= this.max.y) {
                return true;
            }
        }
        if ((lineStart.y < this.min.y && lineEnd.y > this.min.y) || (lineStart.y > this.min.y && lineEnd.y < this.min.y)) {
            float t = (this.min.y - lineStart.y) / (lineEnd.y - lineStart.y);
            float intersectX = lineStart.x + t * (lineEnd.x - lineStart.x);
            float intersectZ = lineStart.z + t * (lineEnd.z - lineStart.z);
            if (intersectX >= this.min.x && intersectX <= this.max.x && intersectZ >= this.min.z && intersectZ <= this.max.z) {
                return true;
            }
        }
        if ((lineStart.x < this.min.x && lineEnd.x > this.min.x) || (lineStart.x > this.min.x && lineEnd.x < this.min.x)) {
            float t = (this.min.x - lineStart.x) / (lineEnd.x - lineStart.x);
            float intersectY = lineStart.y + t * (lineEnd.y - lineStart.y);
            float intersectZ = lineStart.z + t * (lineEnd.z - lineStart.z);
            if (intersectY >= this.min.y && intersectY <= this.max.y && intersectZ >= this.min.z && intersectZ <= this.max.z) {
                return true;
            }
        }
        return false;
    }
}
