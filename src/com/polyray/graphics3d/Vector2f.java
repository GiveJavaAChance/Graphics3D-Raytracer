package com.polyray.graphics3d;

public class Vector2f {

    public float x;
    public float y;

    public Vector2f() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2f add(Vector2f a, Vector2f b) {
        return new Vector2f(a.x + b.x, a.y + b.y);
    }

    public static Vector2f sub(Vector2f a, Vector2f b) {
        return new Vector2f(a.x - b.x, a.y - b.y);
    }

    public static Vector2f mul(Vector2f v, float scalar) {
        return new Vector2f(v.x * scalar, v.y * scalar);
    }

    public static Vector2f div(Vector2f v, float divisor) {
        return new Vector2f(v.x / divisor, v.y / divisor);
    }

    public static Vector2f lerp(Vector2f a, Vector2f b, float f) {
        float x = a.x + f * (b.x - a.x);
        float y = a.y + f * (b.y - a.y);
        return new Vector2f(x, y);
    }

    public static float length(Vector2f v) {
        return (float) Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public static float dot(Vector2f a, Vector2f b) {
        return a.x * b.x + a.y * b.y;
    }

    public static Vector2f normalize(Vector2f v) {
        float l = length(v);
        if (l == 0.0f) {
            return new Vector2f(0.0f, 0.0f);
        }
        return new Vector2f(v.x / l, v.y / l);
    }

    public static Vector2f invert(Vector2f v) {
        return new Vector2f(-v.x, -v.y);
    }

    public static Vector2f random() {
        return Vector2f.normalize(new Vector2f((float) Math.random() - 0.5f, (float) Math.random() - 0.5f));
    }

    public static float getSlope(Vector2f a, Vector2f b) {
        return (b.y - a.y) / (b.x - a.x);
    }

    public static Vector2f intersect(Vector2f a1, Vector2f b1, Vector2f a2, Vector2f b2) {
        float slope1 = getSlope(a1, b1);
        float slope2 = getSlope(a2, b2);
        float a = slope1 * a1.x;
        float b = slope2 * a2.x;
        float n = a2.y - a1.y + a + b;
        float d = slope1 - slope2;
        float X = n / d;
        float Y = slope1 * (X - a1.x) + a1.y;
        return new Vector2f(X, Y);
    }

    public static Vector2f getPositionRelativeTo(Vector2f a, Vector2f b, Vector2f pos) {
        float slope1 = getSlope(a, b);
        float slope2 = -1.0f / slope1;
        Vector2f refA = add(a, normalize(new Vector2f(1.0f, slope2)));
        Vector2f refB = add(b, normalize(new Vector2f(1.0f, slope2)));
        Vector2f refPos = add(pos, new Vector2f(1.0f, slope1));
        Vector2f i = intersect(a, refA, pos, refPos);
        Vector2f refI = intersect(b, refB, pos, refPos);
        float X = length(sub(a, i));
        float Y = length(sub(pos, i));
        float refY = length(sub(pos, refI));
        float refAB = length(sub(a, b));
        if (Y < refAB && refY > refAB) {
            Y = -Y;
        }
        float Xdist = length(sub(i, refA));
        if (Xdist > 1.0f) {
            X = -X;
        }
        return new Vector2f(X, Y);
    }

    @Override
    public String toString() {
        return "Vector2f: (" + this.x + ", " + this.y + ")";
    }

}
