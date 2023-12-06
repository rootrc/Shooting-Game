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
    HashMap<Enemy, Double> enemySpawns = new HashMap<>();
    Player player;
    int score;

    int scoreForNextRoom;
    
    Room(Point[] points, int id) {
        super(points);
        color = Color.white;
        score = 0;
        player = new Player(
                new Point[] { new Point(200, 200), new Point(232, 200), new Point(232, 232), new Point(200, 232) });
        entities.add(player);
        loadRoom(id);
    }

    //temporary
    void loadRoom(int id) {
        this.id = id;
        enemySpawns.clear();
        try {
            Scanner data = new Scanner(new FileReader("data/rooms/room" + id + ".txt"));
            scoreForNextRoom = Integer.parseInt(data.next());
            int N = Integer.parseInt(data.next());
            for (int i = 0; i < N; i++) {
                String enemyName = data.next();
                double spawnRate = Double.parseDouble(data.next()) * 50;
                switch (enemyName.substring(0, enemyName.length() - 1)) {
                    case "chaser":
                        Chaser chaser = new Chaser(null, enemyName.charAt(enemyName.length() - 1) - '0');
                        enemySpawns.put(chaser, spawnRate);
                        break;
                    case "rifle":
                        Rifle rifle = new Rifle(null, enemyName.charAt(enemyName.length() - 1) - '0');
                        enemySpawns.put(rifle, spawnRate);
                        break;
                    case "sniper":
                        Sniper sniper = new Sniper(null, enemyName.charAt(enemyName.length() - 1) - '0');
                        enemySpawns.put(sniper, spawnRate);
                        break;
                    case "machine":
                        Machine machine = new Machine(null, enemyName.charAt(enemyName.length() - 1) - '0');
                        enemySpawns.put(machine, spawnRate);
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
                    if (Math.random() * e.getValue() < 1) {
                        addEnemy(e.getKey());
                    }
                }
                if (score >= scoreForNextRoom) {
                    loadRoom(id + 1);
                }
            }
        };
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }

    void draw(Graphics2D g2d) {
        super.draw(g2d);
        super.fill(g2d);
        for (Projectile projectile : projectiles) {
            projectile.draw(g2d);
        }
        for (Entity entity : entities) {
            entity.draw(g2d);
            entity.fill(g2d);
        }
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2d.drawString(Integer.toString(player.health), 15, 30);
        g2d.drawString(Integer.toString(score),
                770 - SwingUtilities.computeStringWidth(g2d.getFontMetrics(), Integer.toString(score)), 30);
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
