package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector2f;
import com.polyray.graphics3d.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Triangle {

    public Vector3f a, b, c;
    public Vector2f ta, tb, tc;
    public ColorObject col;
    private BufferedImage texture;
    private int width, height;
    public boolean hasTexture = false;

    public Triangle(Vector3f a, Vector3f b, Vector3f c, ColorObject col) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.col = col;
    }

    public Vector3f getNormal() {
        Vector3f u = Vector3f.sub(this.b, this.a);
        Vector3f v = Vector3f.sub(this.c, this.a);
        return Vector3f.normalize(Vector3f.cross(u, v));
    }
    
    public Triangle[] subdivide(int amt) {
        ArrayList<Triangle> initT = new ArrayList<>();
        initT.add(this);
        for (int i = 0; i < amt; i++) {
            ArrayList<Triangle> subT = new ArrayList<>();
            for (Triangle t : initT) {
                Vector3f ma = Vector3f.lerp(t.a, t.b, 0.5f);
                Vector3f mb = Vector3f.lerp(t.b, t.c, 0.5f);
                Vector3f mc = Vector3f.lerp(t.c, t.a, 0.5f);
                subT.add(new Triangle(mc,t.a,ma,t.col));
                subT.add(new Triangle(ma,t.b,mb,t.col));
                subT.add(new Triangle(mb,t.c,mc,t.col));
                subT.add(new Triangle(t.a,t.b,t.c,t.col));
            }
            initT = subT;
        }
        return initT.toArray(Triangle[]::new);
    }

    public ColorObject getTextureColor(Vector3f pos) {
        if (texture == null) {
            return null;
        }
        Vector3f v = toBary(pos);
        if (v.x < 0.0f || v.x > 1.0f || v.y < 0.0f || v.y > 1.0f || v.z < 0.0f || v.z > 1.0f) {
            return new ColorObject(0.0f, 0.0f, 0.0f);
        }
        Vector2f p = Vector2f.add(Vector2f.mul(ta, v.x), Vector2f.add(Vector2f.mul(tb, v.y), Vector2f.mul(tc, v.z)));
        int x = Math.min(Math.max((int) (p.x * width), 0), width - 1);
        int y = Math.min(Math.max((int) (p.y * height), 0), height - 1);
        return ColorObject.toColorObject(new Color(texture.getRGB(x, y)));
    }

    private Vector3f toBary(Vector3f P) {
        Vector3f v0 = Vector3f.sub(b, a);
        Vector3f v1 = Vector3f.sub(c, a);
        Vector3f v2 = Vector3f.sub(P, a);
        float dot00 = Vector3f.dot(v0, v0);
        float dot01 = Vector3f.dot(v0, v1);
        float dot02 = Vector3f.dot(v0, v2);
        float dot11 = Vector3f.dot(v1, v1);
        float dot12 = Vector3f.dot(v1, v2);
        float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
        float v = (dot00 * dot12 - dot01 * dot02) * invDenom;
        float w = 1.0f - u - v;
        return new Vector3f(u, v, w);
    }

    public void setTexture(BufferedImage texture, float u1, float v1, float u2, float v2, float u3, float v3) {
        this.texture = texture;
        width = texture.getWidth();
        height = texture.getHeight();
        hasTexture = true;
        /*this.to = new Vector2f(u1, v1);
        Vector2f tb = new Vector2f(u2, v2);
        Vector2f tc = new Vector2f(u3, v3);
        this.tx = Vector2f.sub(tb, to);
        this.ty = Vector2f.sub(tc, to);*/
        this.ta = new Vector2f(u1, v1);
        this.tb = new Vector2f(u2, v2);
        this.tc = new Vector2f(u3, v3);
    }

    public boolean hasTeture() {
        return this.hasTexture && this.texture != null;
    }
}
