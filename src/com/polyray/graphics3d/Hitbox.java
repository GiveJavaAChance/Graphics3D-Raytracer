package com.polyray.graphics3d;

public class Hitbox {
    private final Vector3f pos;
    private final Vector3f size;
    private final boolean inside;
    public Hitbox(Vector3f pos, Vector3f size, boolean inside) {
        this.pos = pos;
        this.size = size;
        this.inside = inside;
    }
    
    public Vector3f applyHitbox(Vector3f pos) {
        Vector3f s = this.size;
        Vector3f p = this.pos;
        if (this.inside) {
            if (pos.x < p.x) {
                pos.x = p.x;
            } else if (pos.x > p.x + s.x) {
                pos.x = p.x + s.x;
            }
            if (pos.y < p.y) {
                pos.y = p.y;
            } else if (pos.y > p.y + s.y) {
                pos.y = p.y + s.y;
            }
            if (pos.z < p.z) {
                pos.z = p.z;
            } else if (pos.z > p.z + s.z) {
                pos.z = p.z + s.z;
            }
        } else {
            float errX = 0.0f, errY = 0.0f, errZ = 0.0f;
            if (pos.y > p.y && pos.y < p.y + s.y && pos.z > p.z && pos.z < p.z + s.z) {
                if (pos.x < p.x + s.x && pos.x > p.x + s.x / 2.0f) {
                    errX = Math.abs(pos.x - p.x - s.x);
                } else if (pos.x > p.x) {
                    errX = Math.abs(pos.x - p.x);
                }
            }
            if (pos.x > p.x && pos.x < p.x + s.x && pos.z > p.z && pos.z < p.z + s.z) {
                if (pos.y < p.y + s.y && pos.y > p.y + s.y / 2.0f) {
                    errY = Math.abs(pos.y - p.y - s.y);
                } else if (pos.y > p.y) {
                    errY = Math.abs(pos.y - p.y);
                }
            }
            if (pos.x > p.x && pos.x < p.x + s.x && pos.y > p.y && pos.y < p.y + s.y) {
                if (pos.z < p.z + s.z && pos.z > p.z + s.z / 2.0f) {
                    errZ = Math.abs(pos.z - p.z - s.z);
                } else if (pos.z > p.z) {
                    errZ = Math.abs(pos.z - p.z);
                }
            }
            if (errX < errY && errX < errZ) {
                if (pos.y > p.y && pos.y < p.y + s.y && pos.z > p.z && pos.z < p.z + s.z) {
                    if (pos.x < p.x + s.x && pos.x > p.x + s.x / 2.0f) {
                        pos.x = p.x + s.x;
                    } else if (pos.x > p.x) {
                        pos.x = p.x;
                    }
                }
            } else if (errY < errX && errY < errZ) {
                if (pos.x > p.x && pos.x < p.x + s.x && pos.z > p.z && pos.z < p.z + s.z) {
                    if (pos.y < p.y + s.y && pos.y > p.y + s.y / 2.0f) {
                        pos.y = p.y + s.y;
                    } else if (pos.y > p.y) {
                        pos.y = p.y;
                    }
                }
            } else {
                if (pos.x > p.x && pos.x < p.x + s.x && pos.y > p.y && pos.y < p.y + s.y) {
                    if (pos.z < p.z + s.z && pos.z > p.z + s.z / 2.0f) {
                        pos.z = p.z + s.z;
                    } else if (pos.z > p.z) {
                        pos.z = p.z;
                    }
                }
            }
        }
        return pos;
    }
}
