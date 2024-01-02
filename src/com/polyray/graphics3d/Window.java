package com.polyray.graphics3d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Window extends JPanel {

    private BufferedImage image;
    private final JFrame frame;
    private Dimension s;
    private int w, h;

    public Window(String name) {
        frame = new JFrame(name);
    }

    public void addComponent(Component comp) {
        frame.add(comp);
    }

    public void createFrame(int width, int height, boolean title, boolean exitOnClose, boolean fullscreen, float opacity) {
        w = width;
        h = height;
        s = new Dimension(w, h);
        if (fullscreen) {
            s = Toolkit.getDefaultToolkit().getScreenSize();
            w = s.width;
            h = s.height;
        }
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Dimension d = new Dimension(width, height);
        frame.setSize(d);
        frame.setPreferredSize(d);
        if (fullscreen) {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        frame.setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
        frame.setUndecorated(!title);
        if (!title) {
            frame.setOpacity(Math.max(Math.min(opacity, 1.0f), 0.0f));
        }
        setFocusable(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDown(e);
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseRelease(e);
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                move(e);
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                drag(e);
            }
        });
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                keyType(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyPress(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyRelease(e);
            }
        });
        frame.add(this);
        frame.setVisible(true);
    }

    public void render() {
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage buffer = gfxConfig.createCompatibleImage(w, h);
        Graphics2D g = buffer.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, w, h);
        draw(g, w, h);
        image = buffer;
        g.dispose();
        repaint();
    }

    public void draw(Graphics2D g, int width, int height) {
    }

    public void mouseDown(MouseEvent e) {
    }
    
    public void mouseRelease(MouseEvent e) {
    }

    public void move(MouseEvent e) {
    }
    
    public void drag(MouseEvent e) {
    }
    
    public void keyType(KeyEvent e) {
    }

    public void keyPress(KeyEvent e) {
    }
    
    public void keyRelease(KeyEvent e) {
    }

    public Dimension getImageSize() {
        return s;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        }
    }
}
