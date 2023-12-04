import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

import Geo.*;

import java.awt.Color;
import java.awt.Graphics2D;

public class Room {
    Player player;
    Polygon[] polygons;
    HashSet<Line> projectiles = new HashSet<>();

    Room(String name) {
        player = new Player(
                new Point[] { new Point(200, 200), new Point(232, 200), new Point(232, 232), new Point(200, 232) });
        try {
            Scanner data = new Scanner(new FileReader("data/rooms/" + name + ".txt"));
            int N = Integer.parseInt(data.next());
            polygons = new Polygon[N];
            for (int i = 0; i < N; i++) {
                int M = Integer.parseInt(data.next());
                Point[] points = new Point[M];
                for (int j = 0; j < M; j++) {
                    points[j] = new Point(Integer.parseInt(data.next()), Integer.parseInt(data.next()));
                }
                polygons[i] = new Polygon(points);
                polygons[i].setColor(Color.white);
            }
        } catch (IOException e) {
            System.out.println("Room Loading Error");
            System.exit(-1);
        }
    }

    void process() {
        player.process();
    }

    boolean intersects(Line line) {
        for (Polygon polygon : polygons) {
            if (polygon.intersects(line)) {
                return true;
            }
        }
        return false;
    }

    boolean intersects(Polygon polygon) {
        for (Polygon polygon2 : polygons) {
            if (polygon2.intersects(polygon)) {
                return true;
            }
        }
        return false;
    }

    void paint(Graphics2D g2d) {
        for (Polygon polygon : polygons) {
            polygon.draw(g2d);
            polygon.fill(g2d);
        }
        for (Line line : projectiles) {
            line.draw(g2d);
        }
        player.paint(g2d);
    }

    void keyPressed(KeyEvent e) {
        player.keyPressed(e);
    }

    void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    void mouseClicked(MouseEvent e) {
        player.mouseClicked(e);
    }
}
