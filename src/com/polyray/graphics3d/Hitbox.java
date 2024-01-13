package com.polyray.graphics3d;

public class Hitbox {

    public final Vector3f pos;
    public final Vector3f size;
    public final boolean inside;

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
            return pos;
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
            double r = Math.random();
            if (errX < errY && errX < errZ) {
                return correctX(pos, p, s);
            } else {
                if (errX == errY && errX == errZ) {
                    if (r >= 2.0 / 3.0) {
                        return correctX(pos, p, s);
                    } else if (r >= 1.0 / 3.0) {
                        return correctY(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                } else if (errX == errY) {
                    if (r >= 0.5) {
                        return correctX(pos, p, s);
                    } else {
                        return correctY(pos, p, s);
                    }
                } else if (errX == errZ) {
                    if (r >= 0.5) {
                        return correctX(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                }
            }
            if (errY < errX && errY < errZ) {
                return correctY(pos, p, s);
            } else {
                if (errY == errX && errY == errZ) {
                    if (r >= 2.0 / 3.0) {
                        return correctX(pos, p, s);
                    } else if (r >= 1.0 / 3.0) {
                        return correctY(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                } else if (errY == errX) {
                    if (r >= 0.5) {
                        return correctX(pos, p, s);
                    } else {
                        return correctY(pos, p, s);
                    }
                } else if (errY == errZ) {
                    if (r >= 0.5) {
                        return correctY(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                }
            }
            if (errZ < errX && errZ < errY) {
                return correctZ(pos, p, s);
            } else {
                if (errZ == errX && errZ == errY) {
                    if (r >= 2.0 / 3.0) {
                        return correctX(pos, p, s);
                    } else if (r >= 1.0 / 3.0) {
                        return correctY(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                } else if (errZ == errX) {
                    if (r >= 0.5) {
                        return correctX(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                } else if (errZ == errY) {
                    if (r >= 0.5) {
                        return correctY(pos, p, s);
                    } else {
                        return correctZ(pos, p, s);
                    }
                }
            }
        }
        return pos;
    }

    private Vector3f correctX(Vector3f pos, Vector3f p, Vector3f s) {
        if (pos.y > p.y && pos.y < p.y + s.y && pos.z > p.z && pos.z < p.z + s.z) {
            if (pos.x < p.x + s.x && pos.x > p.x + s.x / 2.0f) {
                pos.x = p.x + s.x;
            } else if (pos.x > p.x) {
                pos.x = p.x;
            }
        }
        return pos;
    }

    private Vector3f correctY(Vector3f pos, Vector3f p, Vector3f s) {
        if (pos.x > p.x && pos.x < p.x + s.x && pos.z > p.z && pos.z < p.z + s.z) {
            if (pos.y < p.y + s.y && pos.y > p.y + s.y / 2.0f) {
                pos.y = p.y + s.y;
            } else if (pos.y > p.y) {
                pos.y = p.y;
            }
        }
        return pos;
    }

    private Vector3f correctZ(Vector3f pos, Vector3f p, Vector3f s) {
        if (pos.x > p.x && pos.x < p.x + s.x && pos.y > p.y && pos.y < p.y + s.y) {
            if (pos.z < p.z + s.z && pos.z > p.z + s.z / 2.0f) {
                pos.z = p.z + s.z;
            } else if (pos.z > p.z) {
                pos.z = p.z;
            }
        }
        return pos;
    }
}
