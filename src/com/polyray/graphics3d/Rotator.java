package com.polyray.graphics3d;

public class Rotator {

    private Vector3f a = new Vector3f(1.0f, 0.0f, 0.0f), b = new Vector3f(0.0f, 1.0f, 0.0f), c = new Vector3f(0.0f, 0.0f, 1.0f);
    public Vector3f calcRot(Vector3f v, Vector3f ang, int prioAxis) {
        Vector3f nV = new Vector3f(0.0f, 0.0f, 0.0f);
        switch (prioAxis) {
            case 0 -> {
                nV = rotate(b, v, ang.y);
                nV = rotate(c, nV, ang.z);
                nV = rotate(a, nV, ang.x);
            }

            case 1 -> {
                nV = rotate(c, v, ang.z);
                nV = rotate(a, nV, ang.x);
                nV = rotate(b, nV, ang.y);
            }

            case 2 -> {
                nV = rotate(a, v, ang.x);
                nV = rotate(b, nV, ang.y);
                nV = rotate(c, nV, ang.z);
            }
        }
        return nV;
    }

    public Vector3f calcInverseRot(Vector3f v, Vector3f ang, int prioAxis) {
        Vector3f nV = new Vector3f(0.0f, 0.0f, 0.0f);
        switch (prioAxis) {
            case 0 -> {
                nV = rotate(a, v, -ang.x);
                nV = rotate(c, nV, -ang.z);
                nV = rotate(b, nV, -ang.y);
            }

            case 1 -> {
                nV = rotate(b, v, -ang.y);
                nV = rotate(a, nV, -ang.x);
                nV = rotate(c, nV, -ang.z);
            }

            case 2 -> {
                nV = rotate(c, v, -ang.z);
                nV = rotate(b, nV, -ang.y);
                nV = rotate(a, nV, -ang.x);
            }
        }
        return nV;
    }

    private Vector3f rotate(Vector3f k, Vector3f v, float ang) {
        return Vector3f.add(Vector3f.mul(v, (float) Math.cos(ang)), Vector3f.add(Vector3f.mul(Vector3f.cross(k, v), (float) Math.sin(ang)), Vector3f.mul(k, Vector3f.dot(k, v) * (float) (1.0 - Math.cos(ang)))));
    }
}
