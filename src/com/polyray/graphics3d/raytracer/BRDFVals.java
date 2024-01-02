package com.polyray.graphics3d.raytracer;

class BRDFVals {
    public float specular, specStrength, diffuse;
    public BRDFVals(float specular, float specStrength, float diffuse) {
        this.specular = specular;
        this.specStrength = specStrength;
        this.diffuse = diffuse;
    }
}
