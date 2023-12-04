import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;
import java.awt.Graphics2D;

public class Room {
    Player player;
    Polygon[] polygons;
    ConcurrentHashMap.KeySetView<Projectile, Boolean> projectiles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Entity, Boolean> entities = ConcurrentHashMap.newKeySet();

    Room(String name) {
        player = new Player(
                new Point[] { new Point(200, 200), new Point(232, 200), new Point(232, 232), new Point(200, 232) });
        entities.add(player);
        Obstacle obstacle = new Obstacle(new Point[] { new Point(300, 300), new Point(332, 300), new Point(332, 332), new Point(300, 332) }, 3);
        Obstacle obstacle2 = new Obstacle(new Point[] { new Point(400, 400), new Point(432, 400), new Point(432, 432), new Point(400, 432) }, 3);
        entities.add(obstacle);
        entities.add(obstacle2);
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
        for (Entity entity: entities) {
            entity.process();
        }
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
    void draw(Graphics2D g2d){
        for (Polygon polygon : polygons) {
            polygon.draw(g2d);
            polygon.fill(g2d);
        }
        for (Line line : projectiles) {
            line.draw(g2d);
        }
        for (Entity entity: entities) {
            entity.draw(g2d);
            entity.fill(g2d);
        }
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
