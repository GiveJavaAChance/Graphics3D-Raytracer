package com.polyray.graphics3d;

import java.awt.Color;
import java.util.ArrayList;

public class Graphics3DObject {

    public ArrayList<Vector3f> vertices = new ArrayList<>();
    public ArrayList<Color> colors = new ArrayList<>();
    public float radius;
    public Color c = Color.BLACK;
    public boolean fill = false;
    public boolean gradient = false;
    private Vector3f add = new Vector3f(0.0f, 0.0f, 0.0f);
    private float depth;

    public void addVertex(Vector3f vertex) {
        this.vertices.add(vertex);
        this.add = Vector3f.add(vertex, add);
    }

    public void addVertex(Vector3f vertex, Color c) {
        colors.add(c);
        this.gradient = true;
        this.vertices.add(vertex);
        this.add = Vector3f.add(vertex, add);
    }

    public void addVertex(Vector3f vertex, float radius) {
        this.vertices.add(vertex);
        this.radius = radius;
        this.add = Vector3f.add(vertex, add);
    }

    public void doFill(boolean doFill) {
        this.fill = doFill;
    }

    public Vector3f centerOfMass() {
        if (vertices.isEmpty()) {
            return new Vector3f(0.0f, 0.0f, 0.0f); // Handle division by zero
        }
        return Vector3f.div(add, vertices.size());
    }
    
    public Color[] getColors() {
        return colors.toArray(Color[]::new);
    }

    public void setColor(Color c) {
        this.c = c;
    }
    
    void setDepth(float depth) {
        this.depth = depth;
    }

    float getDepth() {
        return depth;
    }
}
