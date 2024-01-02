package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector3f;

class Ray {

    public Vector3f pos, dir;
    public ColorObject c;

    public Ray(Vector3f pos, Vector3f dir, ColorObject col) {
        this.pos = pos;
        this.dir = dir;
        this.c = col;
    }

    public Ray mulCol(Ray ray) {
        return new Ray(this.pos, this.dir, this.c.mul(ray.c));
    }
}
