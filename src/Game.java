import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.Timer;

import Geo.Point;

class Game {
    final static int delay = 20;
    private static Game instance = new Game();
    JFrame frame;
    Panel panel;
    Room room;

    private Game() {
        frame = new JFrame();
        panel = new Panel();
        room = new Room(new Point[] { new Point(10, 10), new Point(777, 10), new Point(777, 700), new Point(10, 700) }, 6);
        frame.add(panel);
        frame.pack();
        frame.setTitle("Game");
        frame.setBackground(Color.black);
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Timer timer = new Timer(Game.delay, new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                panel.repaint();
            }
        });
        timer.start();
    }
    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public static void main(String[] args) {
        Game.getInstance().room.process();
    }

    static String parseStr(Scanner data) {
        data.next();
        return data.next();
    }

    static int parseInt(Scanner data) {
        data.next();
        return Integer.parseInt(data.next());
    }

    static double parseDouble(Scanner data) {
        data.next();
        return Double.parseDouble(data.next());
    }

    static Point parsePoint(Scanner data) {
        data.next();
        return new Point(Double.parseDouble(data.next()), Double.parseDouble(data.next()));
    }

    // https://stackoverflow.com/questions/37758061/rotate-a-buffered-image-in-java
    static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, Game.getInstance().panel);
        // g2d.setColor(Color.RED);
        // g2d.drawRect(0, 0, newWidth - 1, newHeight - 1);
        g2d.dispose();

        return rotated;
    }
}
