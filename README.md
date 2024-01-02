# Graphics3D
Graphics3D isn't just about 3D graphics; it's an ambitious project aimed at significantly enhancing the speed and performance of graphics in java. This project has been in active development for several months, witnessing numerous enhancements and advancements. It's a work in progress, and the development won't stop untill peak performance has been reached.

## Raytracer
Additionally, this project incorporates a raytracer, a significant work in progress. The current primary focus is to optimize it for GPU utilization, which promises substantial speed improvements over the current multithreadded CPU processing.

## Graphics 3D 1.0
[Graphics3D_1.0.jar](https://github.com/GiveJavaAChance/Graphics3D-Raytracer/releases/tag/Graphics3D_1.0)

# Usage
If your aim is to create a 3D game, here are some simple tips how to get started:

First, initialise Graphics3D and a Window to render on:
```java
public class Example {

    Graphics3D graphics = new Graphics3D();

    public Example() {
      Window w = new Window("name") {
        @Override
        public void draw(Graphics2D g, int width, int height) {
          // TODO: graphics logic here
        }
      };
      // Example, create a 600 by 400 pixel frame without title bar:
      w.createFrame(600, 400, false, true, false, 1.0f);

      new Thread(() -> {
        while(true) {
          w.render(); // Invokes the draw method
          try {
            Thread.sleep(17); // Adjust for desired FPS (approximately 60fps)
          } catch (InterruptedException ex) {
          }
        }
      }).start();
    }

    public static void main(String[] args) {
      Example p = new Example();
    }
}
```

Second, the draw logic:
```java
public class Example {

    Graphics3D graphics = new Graphics3D();

    public Example() {
      Window w = new Window("Example Window") {
        @Override
        public void draw(Graphics2D g, int width, int height) {
          // Setup
          graphics.clearAll();
          graphics.setup(0.1f, 1000.0f); // Minimum and maximum render distance
          float dist = 100.0f; // Camera distance from origin
          graphics.setCameraPosition(-width / 2.0f, -height / 2.0f, width / 2.0f - dist, width / 2.0f, width, height);
          graphics.prioritizeAxis(1); // Set axis rotation priority

          // Example: Render a cube
          graphics.setColor(Color.BLUE);
          graphics.add3DRect(-25.0f, -25.0f, -25.0f, 50.0f, 50.0f, 50.0f, 1);

          // Example: Apply rotation
          graphics.rotateX(0.01);
          graphics.rotateY(0.01);
          graphics.rotateZ(0.01);

          // Render
          graphics.render(g, false); // Adjust render quality (false for recommended results)
        }
      };
      w.createFrame(600, 400, false, true, false, 1.0f);
      new Thread(() -> {
        while(true) {
          w.render();
          try {
            Thread.sleep(17); // Adjust for desired FPS (approximately 60fps)
          } catch (InterruptedException ex) {
          }
        }
      }).start();
    }

    public static void main(String[] args) {
      Example p = new Example();
    }
}
```
Creating a 3D renderer with Graphics3D is straightforward, requiring a comparable amount of code to setting up a JFrame and paintComponent.

# Upcoming features
The goal is to expand on this project as much as possible, integrating everything that a 3D engine needs.

The next feature that will be added is a physics engine, both for 2D and 3D and mabye even 4D. It will include multiple types of physics engines but are unsure which.
