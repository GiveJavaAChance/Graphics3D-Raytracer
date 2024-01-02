package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector3f;

class PosCol {
    public Vector3f pos;
    public ColorObject col;
    public PosCol(Vector3f pos, ColorObject col) {
        this.pos = pos;
        this.col = col;
    }
}
