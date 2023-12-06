import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Color;
import java.awt.Graphics2D;

public class Room extends Polygon {
    Timer timer = new Timer();
    Player player;
    ConcurrentHashMap.KeySetView<Projectile, Boolean> projectiles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Entity, Boolean> entities = ConcurrentHashMap.newKeySet();
    HashMap<Enemy, Double> enemySpawns = new HashMap<>();

    Room(Point[] points, String name) {
        super(points);
        color = Color.white;
        player = new Player(
                new Point[] { new Point(200, 200), new Point(232, 200), new Point(232, 232), new Point(200, 232) });
        entities.add(player);
        try {
            Scanner data = new Scanner(new FileReader("data/rooms/" + name + ".txt"));
            int N = Integer.parseInt(data.next());
            for (int i = 0; i < N; i++) {
                String enemyName = data.next();
                double spawnRate = Double.parseDouble(data.next()) * 50;
                Scanner data2 = new Scanner(new FileReader("data/enemies/" + enemyName + ".txt"));
                int M = Integer.parseInt(data2.next());
                Point[] points2 = new Point[M];
                for (int j = 0; j < M; j++) {
                    points2[j] = new Point(Double.parseDouble(data2.next()), Double.parseDouble(data2.next()));
                }
                switch (enemyName.substring(0, enemyName.length() - 1)) {
                    case "chaser":
                        Chaser enemy = new Chaser(points2, enemyName.charAt(enemyName.length() - 1) - '0');
                        enemySpawns.put(enemy, spawnRate);
                        break;
                }
            }

        } catch (IOException e) {
            System.out.println("Room Loading Error");
            System.exit(-1);
        }
    }

    void process() {
        player.process();
        TimerTask timertask = new TimerTask() {
            public void run() {
                for (Map.Entry<Enemy, Double> e : enemySpawns.entrySet()) {
                    if (Math.random() * e.getValue() <= 1) {
                        addEnemy(e.getKey());
                    }
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }

    void draw(Graphics2D g2d) {
        super.draw(g2d);
        super.fill(g2d);
        for (Line line : projectiles) {
            line.draw(g2d);
        }
        for (Entity entity : entities) {
            entity.draw(g2d);
            entity.fill(g2d);
        }
    }

    void addEnemy(Enemy enemy) {
        double x, y;
        if (Math.random() < 0.5) {
            x = (777 - 20 - enemy.points[0].x) * Math.random() + 10;
            if (30 < x && x < 777 - 50 - enemy.points[0].x) {
                if (Math.random() < 0.5) {
                    y = (50 - 10) * Math.random() + 10;
                } else {
                    y = (50 - 10) * Math.random() + 700 - (50 - 10) - 10 - enemy.points[0].y;
                }
            } else {
                y = (700 - 20 - enemy.points[0].y) * Math.random() + 10;
            }
        } else {
            y = (700 - 20 - enemy.points[0].y) * Math.random() + 10;
            if (50 < y && y < 700 - 50 - enemy.points[0].y) {
                if (Math.random() < 0.5) {
                    x = (50 - 10) * Math.random() + 10;
                } else {
                    x = (50 - 10) * Math.random() + 777 - (50 - 10) - 10 - enemy.points[0].x;
                }
            } else {
                x = (777 - 20 - enemy.points[0].x) * Math.random() + 10;
            }
        }
        enemy = enemy.translate(x, y);
        entities.add(enemy);
        enemy.process();
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
