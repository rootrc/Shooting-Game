import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Room extends Polygon {
    int id;
    Timer timer = new Timer();
    ConcurrentHashMap.KeySetView<Projectile, Boolean> projectiles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Entity, Boolean> entities = ConcurrentHashMap.newKeySet();
    HashMap<String, Double> enemySpawns = new HashMap<>();
    ConcurrentHashMap.KeySetView<Polygon, Boolean> polygons = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Particle, Boolean> particles = ConcurrentHashMap.newKeySet();
    Player player;
    int xAdjust;
    int yAdjust;
    int score;
    int scoreForNextRoom;

    Room(Point[] points, int id) {
        super(points);
        color = Color.white;
        score = 0;
        player = new Player(this, 
                new Point[] { new Point(300, 300), new Point(332, 300), new Point(332, 332), new Point(300, 332) });
        entities.add(player);
        scoreForNextRoom = 0;
        loadRoom(id);
    }

    // temporary
    void loadRoom(int id) {
        this.id = id;
        enemySpawns.clear();
        try {
            Scanner data = new Scanner(new FileReader("data/rooms/room" + id + ".txt"));
            scoreForNextRoom += Integer.parseInt(data.next()) * 10;
            int N = Integer.parseInt(data.next());
            for (int i = 0; i < N; i++) {
                String enemyName = data.next();
                double spawnRate = Double.parseDouble(data.next()) * 50;
                enemySpawns.put(enemyName, spawnRate);
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
                for (Map.Entry<String, Double> e : enemySpawns.entrySet()) {
                    if (Math.random() * e.getValue() < 1) {
                        addEnemy(e.getKey());
                    }
                }
                if (score >= scoreForNextRoom) {
                    loadRoom(id + 1);
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
    }

    void draw(Graphics2D g2d) {
        super.draw(g2d);
        super.fill(g2d);
        for (Polygon polygon : polygons) {
            polygon.draw(g2d);
            polygon.fill(g2d);
        }
        for (Particle particle : particles) {
            particle.draw(g2d);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g2d);
        }
        for (Entity entity : entities) {
            entity.draw(g2d);
            entity.fill(g2d);
        }
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2d.setColor(Color.black);
        g2d.drawString(Integer.toString(Math.max(player.health, 0)), 15 + xAdjust,
                30 + yAdjust);
        g2d.drawString(Integer.toString(score),
                770 - SwingUtilities.computeStringWidth(g2d.getFontMetrics(), Integer.toString(score))
                        + xAdjust,
                30 + yAdjust);
    }

    void addEnemy(String name) {
        double x, y;
        Enemy<?> enemy = null;
        switch (name.substring(0, name.length() - 1)) {
            case "chaser":
                enemy = new Chaser(this, name.charAt(name.length() - 1) - '0');
                break;
            case "rifle":
                enemy = new Rifle(this, name.charAt(name.length() - 1) - '0');
                break;
            case "sniper":
                enemy = new Sniper(this, name.charAt(name.length() - 1) - '0');
                break;
            case "machine":
                enemy = new Machine(this, name.charAt(name.length() - 1) - '0');
                break;
            case "sharp":
                enemy = new Sharp(this, name.charAt(name.length() - 1) - '0');
                break;
        }
        if (Math.random() < 0.5) {
            x = (777 - 20 - enemy.points[0].getX()) * Math.random() + 10;
            if (30 < x && x < 777 - 50 - enemy.points[0].getX()) {
                if (Math.random() < 0.5) {
                    y = (50 - 10) * Math.random() + 10;
                } else {
                    y = (50 - 10) * Math.random() + 700 - (50 - 10) - 10 - enemy.points[0].getY();
                }
            } else {
                y = (700 - 20 - enemy.points[0].getY()) * Math.random() + 10;
            }
        } else {
            y = (700 - 20 - enemy.points[0].getY()) * Math.random() + 10;
            if (50 < y && y < 700 - 50 - enemy.points[0].getY()) {
                if (Math.random() < 0.5) {
                    x = (50 - 10) * Math.random() + 10;
                } else {
                    x = (50 - 10) * Math.random() + 777 - (50 - 10) - 10 - enemy.points[0].getX();
                }
            } else {
                x = (777 - 20 - enemy.points[0].getX()) * Math.random() + 10;
            }
        }
        enemy = enemy.translate(x, y);
        entities.add(enemy);
        enemy.process();
    }

    Polygon boundingBox(int width) {
        return new Polygon(new Point[] { points[0].translate(width, width), points[1].translate(-width, width),
                points[2].translate(-width, -width), points[3].translate(width, -width) });
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
