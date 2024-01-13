package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Triangle {

    public Vector3f a, b, c;
    public ColorObject col;
    private BufferedImage texture;
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
        /*Vector3f v0 = Vector3f.sub(this.b, this.a);
        Vector3f v1 = Vector3f.sub(this.c, this.a);
        Vector3f v2 = Vector3f.sub(pos, this.a);
        float dot00 = Vector3f.dot(v0, v0);
        float dot01 = Vector3f.dot(v0, v1);
        float dot02 = Vector3f.dot(v0, v2);
        float dot11 = Vector3f.dot(v1, v1);
        float dot12 = Vector3f.dot(v1, v2);
        float denom = (dot00 * dot11 - dot01 * dot01);
        float u = (dot11 * dot02 - dot01 * dot12) / denom;
        float v = (dot00 * dot12 - dot01 * dot02) / denom;
        float w = 1.0f - u - v;*/
        // Calculate the vectors AB and AC
        Vector3f AB = Vector3f.sub(this.b, this.a);
        Vector3f AC = Vector3f.sub(this.c, this.a);

        // Calculate the lengths of sides AB and AC
        float lengthAB = Vector3f.length(AB);
        float lengthAC = Vector3f.length(AC);

        // Calculate the dot products for projections
        float dotAB = Vector3f.dot(AB, Vector3f.sub(pos, this.a));
        float dotAC = Vector3f.dot(AC, Vector3f.sub(pos, this.a));

        // Calculate normalized coordinates
        float xNorm = dotAB / (lengthAB * lengthAB);
        float yNorm = dotAC / (lengthAC * lengthAC);
        float x = xNorm * texture.getWidth();
        float y = yNorm * texture.getHeight();
        int X = Math.max(Math.min((int) x, texture.getWidth()), 0);
        int Y = Math.max(Math.min((int) y, texture.getHeight()), 0);
        return ColorObject.toColorObject(new Color(texture.getRGB(X, Y)));
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
        hasTexture = true;
    }
}
