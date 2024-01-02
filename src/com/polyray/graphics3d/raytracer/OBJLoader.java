package com.polyray.graphics3d.raytracer;

import com.polyray.graphics3d.Vector2f;
import com.polyray.graphics3d.Vector3f;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class OBJLoader {

    private final ArrayList<Vector3f> vertices = new ArrayList<>();
    private final ArrayList<Triangle> triangles = new ArrayList<>();
    private final ArrayList<ArrayList<Triangle>> objects = new ArrayList<>();
    private final ArrayList<String> objectNames = new ArrayList<>();
    private final ArrayList<Vector2f> colorPos = new ArrayList<>();
    private final ArrayList<Vector3f> normals = new ArrayList<>();

    public void loadOBJFile(File file) {
        try {
            String filePath = file.getAbsolutePath();
            String mtlFilePath = filePath.replace("obj", "mtl");
            String folder = file.getParentFile().getAbsolutePath();
            String texturePath = findTexturesDirectory(folder);
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileReader mtlFileReader = new FileReader(mtlFilePath);
            BufferedReader mtlBufferedReader = new BufferedReader(mtlFileReader);
            String line;
            ArrayList<String> filepaths = new ArrayList<>();
            String name = "";
            while ((line = mtlBufferedReader.readLine()) != null) {
                if (line.startsWith("newmtl ")) {
                    name = line.replace("newmtl ", "").trim().toLowerCase();
                }
                if (line.startsWith("map_Kd ")) {
                    String filepath = line.replace("map_Kd ", "");
                    File filep = new File(filepath);
                    String fileName = filep.getName();
                    filepaths.add(name + " " + fileName.trim());
                }
            }
            BufferedImage image = null;
            int width = 0, height = 0;
            ArrayList<Triangle> objectTriangles = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String data = line.replace("v ", "");
                    int separator1 = data.indexOf(" ");
                    String x = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String y = data.substring(separator1, separator2).trim();
                    String z = data.substring(separator2).trim();
                    float X = Float.parseFloat(x);
                    float Y = Float.parseFloat(y);
                    float Z = -Float.parseFloat(z);
                    Vector3f v = new Vector3f(X, Y, Z);
                    vertices.add(v);
                } else if (line.startsWith("vt ")) {
                    String data = line.replace("vt ", "");
                    int separator1 = data.indexOf(" ");
                    String a = data.substring(0, separator1).trim();
                    String b = data.substring(separator1).trim();
                    float x = Float.parseFloat(a);
                    float y = Float.parseFloat(b);
                    Vector2f v = new Vector2f(x, y);
                    colorPos.add(v);
                } else if (line.startsWith("f ")) {
                    String data = line.replace("f ", "");
                    int separator1 = data.indexOf(" ");
                    String a = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String b = data.substring(separator1, separator2).trim();
                    String c = data.substring(separator2).trim();
                    // Indexes
                    int vIndexA = a.indexOf("/");
                    int vIndexB = b.indexOf("/");
                    int vIndexC = c.indexOf("/");
                    int colA = a.indexOf("/", vIndexA + 1);
                    int colB = b.indexOf("/", vIndexB + 1);
                    int colC = c.indexOf("/", vIndexC + 1);
                    // Parsing
                    String aVIdx = a.substring(0, vIndexA);
                    String bVIdx = b.substring(0, vIndexB);
                    String cVIdx = c.substring(0, vIndexC);
                    int vAi = Integer.parseInt(aVIdx) - 1;
                    int vBi = Integer.parseInt(bVIdx) - 1;
                    int vCi = Integer.parseInt(cVIdx) - 1;
                    String icolA = a.substring(vIndexA + 1, colA);
                    String icolB = b.substring(vIndexB + 1, colB);
                    String icolC = c.substring(vIndexC + 1, colC);
                    int aColI = Integer.parseInt(icolA) - 1;
                    int bColI = Integer.parseInt(icolB) - 1;
                    int cColI = Integer.parseInt(icolC) - 1;
                    Vector2f posA = colorPos.get(aColI);
                    Vector2f posB = colorPos.get(bColI);
                    Vector2f posC = colorPos.get(cColI);
                    ColorObject cA = ColorObject.toColorObject(new Color(image.getRGB((int) (posA.x * (width - 1.0f)), (int) ((1.0f - posA.y) * (height - 1.0f)))));
                    ColorObject cB = ColorObject.toColorObject(new Color(image.getRGB((int) (posB.x * (width - 1.0f)), (int) ((1.0f - posB.y) * (height - 1.0f)))));
                    ColorObject cC = ColorObject.toColorObject(new Color(image.getRGB((int) (posC.x * (width - 1.0f)), (int) ((1.0f - posC.y) * (height - 1.0f)))));
                    ColorObject triangleCol = ColorObject.add(ColorObject.add(cA, cB), cC).mul(1.0f / 3.0f);
                    Triangle t = new Triangle(vertices.get(vAi), vertices.get(vBi), vertices.get(vCi), triangleCol);
                    triangles.add(t);
                    objectTriangles.add(t);
                } else if (line.startsWith("vn ")) {
                    String data = line.replace("vn ", "");
                    int separator1 = data.indexOf(" ");
                    String x = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String y = data.substring(separator1, separator2).trim();
                    String z = data.substring(separator2).trim();
                    float X = Float.parseFloat(x);
                    float Y = Float.parseFloat(y);
                    float Z = Float.parseFloat(z);
                    Vector3f v = new Vector3f(X, Y, Z);
                    normals.add(v);
                } else if (line.startsWith("o ")) {
                    String data = line.replace("o ", "").trim().toLowerCase();
                    if (!objectTriangles.isEmpty()) {
                        objects.add(objectTriangles);
                        objectNames.add(data);
                        objectTriangles.clear();
                    }
                    for (String filepath : filepaths) {
                        String objName = filepath.substring(0, filepath.indexOf(" ")).trim();
                        String fileName = filepath.substring(filepath.indexOf(" ") + 1).trim();
                        if (data.contains(objName)) {
                            File f = new File(texturePath + "\\" + fileName);
                            if (f.exists()) {
                                image = ImageIO.read(f);
                                width = image.getWidth();
                                height = image.getHeight();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        colorPos.clear();
    }

    public void loadOBJFile(File file, Vector3f translate, float scale) {
        try {
            String filePath = file.getAbsolutePath();
            String mtlFilePath = filePath.replace("obj", "mtl");
            String folder = file.getParentFile().getAbsolutePath();
            String texturePath = findTexturesDirectory(folder);
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileReader mtlFileReader = new FileReader(mtlFilePath);
            BufferedReader mtlBufferedReader = new BufferedReader(mtlFileReader);
            String line;
            ArrayList<String> filepaths = new ArrayList<>();
            String name = "";
            while ((line = mtlBufferedReader.readLine()) != null) {
                if (line.startsWith("newmtl ")) {
                    name = line.replace("newmtl ", "").trim().toLowerCase();
                }
                if (line.startsWith("map_Kd ")) {
                    String filepath = line.replace("map_Kd ", "");
                    File filep = new File(filepath);
                    String fileName = filep.getName();
                    filepaths.add(name + " " + fileName.trim());
                }
            }
            BufferedImage image = null;
            int width = 0, height = 0;
            ArrayList<Triangle> objectTriangles = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("v ")) {
                    String data = line.replace("v ", "");
                    int separator1 = data.indexOf(" ");
                    String x = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String y = data.substring(separator1, separator2).trim();
                    String z = data.substring(separator2).trim();
                    float X = Float.parseFloat(x);
                    float Y = Float.parseFloat(y);
                    float Z = -Float.parseFloat(z);
                    Vector3f v = Vector3f.add(new Vector3f(X * scale, Y * scale, Z * scale), translate);
                    vertices.add(v);
                } else if (line.startsWith("vt ")) {
                    String data = line.replace("vt ", "");
                    int separator1 = data.indexOf(" ");
                    String a = data.substring(0, separator1).trim();
                    String b = data.substring(separator1).trim();
                    float x = Float.parseFloat(a);
                    float y = Float.parseFloat(b);
                    Vector2f v = new Vector2f(x, y);
                    colorPos.add(v);
                } else if (line.startsWith("f ")) {
                    String data = line.replace("f ", "");
                    int separator1 = data.indexOf(" ");
                    String a = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String b = data.substring(separator1, separator2).trim();
                    String c = data.substring(separator2).trim();
                    // Indexes
                    int vIndexA = a.indexOf("/");
                    int vIndexB = b.indexOf("/");
                    int vIndexC = c.indexOf("/");
                    int colA = a.indexOf("/", vIndexA + 1);
                    int colB = b.indexOf("/", vIndexB + 1);
                    int colC = c.indexOf("/", vIndexC + 1);
                    // Parsing
                    String aVIdx = a.substring(0, vIndexA);
                    String bVIdx = b.substring(0, vIndexB);
                    String cVIdx = c.substring(0, vIndexC);
                    int vAi = Integer.parseInt(aVIdx) - 1;
                    int vBi = Integer.parseInt(bVIdx) - 1;
                    int vCi = Integer.parseInt(cVIdx) - 1;
                    String icolA = a.substring(vIndexA + 1, colA);
                    String icolB = b.substring(vIndexB + 1, colB);
                    String icolC = c.substring(vIndexC + 1, colC);
                    int aColI = Integer.parseInt(icolA) - 1;
                    int bColI = Integer.parseInt(icolB) - 1;
                    int cColI = Integer.parseInt(icolC) - 1;
                    Vector2f posA = colorPos.get(aColI);
                    Vector2f posB = colorPos.get(bColI);
                    Vector2f posC = colorPos.get(cColI);
                    ColorObject triangleCol = new ColorObject(1.0f, 1.0f, 1.0f);
                    if (image != null) {
                        ColorObject cA = ColorObject.toColorObject(new Color(image.getRGB((int) (posA.x * (width - 1.0f)), (int) ((1.0f - posA.y) * (height - 1.0f)))));
                        ColorObject cB = ColorObject.toColorObject(new Color(image.getRGB((int) (posB.x * (width - 1.0f)), (int) ((1.0f - posB.y) * (height - 1.0f)))));
                        ColorObject cC = ColorObject.toColorObject(new Color(image.getRGB((int) (posC.x * (width - 1.0f)), (int) ((1.0f - posC.y) * (height - 1.0f)))));
                        triangleCol = ColorObject.add(ColorObject.add(cA, cB), cC).mul(1.0f / 3.0f);
                    }
                    Triangle t = new Triangle(vertices.get(vAi), vertices.get(vBi), vertices.get(vCi), triangleCol);
                    triangles.add(t);
                    objectTriangles.add(t);
                } else if (line.startsWith("vn ")) {
                    String data = line.replace("vn ", "");
                    int separator1 = data.indexOf(" ");
                    String x = data.substring(0, separator1).trim();
                    int separator2 = data.indexOf(" ", separator1 + 1);
                    String y = data.substring(separator1, separator2).trim();
                    String z = data.substring(separator2).trim();
                    float X = Float.parseFloat(x);
                    float Y = Float.parseFloat(y);
                    float Z = Float.parseFloat(z);
                    Vector3f v = new Vector3f(X, Y, Z);
                    normals.add(v);
                } else if (line.startsWith("o ")) {
                    String data = line.replace("o ", "").trim().toLowerCase();
                    if (!objectTriangles.isEmpty()) {
                        objects.add(objectTriangles);
                        objectNames.add(data);
                        objectTriangles.clear();
                    }
                    for (String filepath : filepaths) {
                        String objName = filepath.substring(0, filepath.indexOf(" ")).trim();
                        String fileName = filepath.substring(filepath.indexOf(" ") + 1).trim();
                        if (data.contains(objName)) {
                            File f = new File(texturePath + "\\" + fileName);
                            if (f.exists()) {
                                image = ImageIO.read(f);
                                width = image.getWidth();
                                height = image.getHeight();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
        }
        colorPos.clear();
    }

    private String findTexturesDirectory(String startingPath) {
        File startDirectory = new File(startingPath);
        if (!startDirectory.isDirectory()) {
            return null;
        }
        return findTexturesDirectoryRecursive(startDirectory);
    }

    private String findTexturesDirectoryRecursive(File directory) {
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().equalsIgnoreCase("textures")) {
                return file.getAbsolutePath();
            } else if (file.isDirectory()) {
                String result = findTexturesDirectoryRecursive(file);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    public void center() {
        Vector3f ver = vertices.get(0);
        float minX = ver.x, maxX = ver.x, minY = ver.y, maxY = ver.y, minZ = ver.z, maxZ = ver.z;
        for (Vector3f v : vertices) {
            if (v.x < minX) {
                minX = v.x;
            }
            if (v.x > maxX) {
                maxX = v.x;
            }
            if (v.y < minY) {
                minY = v.y;
            }
            if (v.y > maxY) {
                maxY = v.y;
            }
            if (v.z < minZ) {
                minZ = v.z;
            }
            if (v.z > maxZ) {
                maxZ = v.z;
            }
        }
        vertices.clear();
        Vector3f minPos = new Vector3f(minX, minY, minZ);
        Vector3f maxPos = new Vector3f(maxX, maxY, maxZ);
        Vector3f center = Vector3f.div(Vector3f.add(maxPos, minPos), 2.0f);
        for (int i = 0; i < triangles.size(); i++) {
            Triangle t = triangles.get(i);
            Vector3f a = Vector3f.sub(t.a, center);
            Vector3f b = Vector3f.sub(t.b, center);
            Vector3f c = Vector3f.sub(t.c, center);
            Triangle k = new Triangle(a, b, c, t.col);
            triangles.set(i, k);
        }
    }

    public ArrayList<Triangle> get() {
        return triangles;
    }

    public int getSize() {
        return triangles.size();
    }

    public ArrayList<Triangle> getObject(int index) {
        if (index < 0 || index >= objects.size()) {
            String err = "There are only " + objects.size() + " number of objects!";
            throw new ArrayIndexOutOfBoundsException(err);
        }
        return objects.get(index);
    }

    public ArrayList<String> getObjectNames() {
        return objectNames;
    }

    public int getObjectAmount() {
        return objects.size();
    }

    public String getExtension() {
        return "obj";
    }
}
