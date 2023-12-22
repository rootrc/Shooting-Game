import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Geo.Point;
import Geo.Polygon;

public class Room extends Polygon {
    private int id;
    ConcurrentHashMap.KeySetView<Projectile, Boolean> projectiles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Entity, Boolean> entities = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<MuzzleFlash, Boolean> muzzleFlashes = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Explosion, Boolean> explosions = ConcurrentHashMap.newKeySet();
    private HashMap<String, Double> enemySpawns = new HashMap<>();
    ConcurrentHashMap.KeySetView<Corpse, Boolean> corpses = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<DamageCircle, Boolean> damageCircles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<SpawnCircle, Boolean> spawnCircles = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Particle, Boolean> particles1 = ConcurrentHashMap.newKeySet();
    ConcurrentHashMap.KeySetView<Particle, Boolean> particles2 = ConcurrentHashMap.newKeySet();
    Player player;
    int xAdjust;
    int yAdjust;
    private int score;
    private int scoreForNextRoom;
    private int upgradeCount;
    private String[] updrades;

    Room(Point[] points, int id) {
        super(points);
        setColor(Color.white);
        score = 0;
        player = new Player(this,
                new Point[] { new Point(300, 300), new Point(332, 300), new Point(332, 332), new Point(300, 332) });
        entities.add(player);
        scoreForNextRoom = 0;
        loadRoom(id);
    }

    void loadRoom(int id) {
        this.id = id;
        enemySpawns.clear();
        try {
            Scanner data = new Scanner(new FileReader("data/rooms/room" + id + ".txt"));
            scoreForNextRoom += Game.parseInt(data) * 10;
            int enemyCount = Game.parseInt(data);
            for (int i = 0; i < enemyCount; i++) {
                String enemyName = Game.parseStr(data);
                double spawnRate = Game.parseDouble(data) * 50;
                enemySpawns.put(enemyName, spawnRate);
            }
            upgradeCount = Game.parseInt(data);
            if (upgradeCount != 0) {
                updrades = new String[upgradeCount];
                for (int i = 0; i < upgradeCount; i++) {
                    updrades[i] = Game.parseStr(data);
                }
            }

        } catch (IOException e) {
            System.out.println("Room Loading Error");
            System.exit(-1);
        }
    }

    void process() {
        if (score < scoreForNextRoom) {
            for (Map.Entry<String, Double> e : enemySpawns.entrySet()) {
                if (Math.random() * e.getValue() < 1) {
                    addEnemy(e.getKey());
                }
            }
        }
        if (upgradeCount == 0) {
            if (score >= scoreForNextRoom && entities.size() <= 5) {
                loadRoom(id + 1);
            }
        } else {
            if (score >= scoreForNextRoom && entities.size() == 1) {
                player.pause();
                String upgrade = JOptionPane.showInputDialog(Game.getInstance().frame,
                        "Choose an upgrade: shotgun, marksman, or grenade");
                player.weapon2 = Weapon.createWeapon(Room.this.player, "player_" + upgrade);
                loadRoom(id + 1);
            }
        }
        for (Corpse corpse : corpses) {
            corpse.process();
        }
        for (MuzzleFlash muzzleFlash : muzzleFlashes) {
            muzzleFlash.process();
        }
        for (Particle particle : particles1) {
            particle.process();
        }
        for (Particle particle : particles2) {
            particle.process();
        }
        for (Explosion explosion : explosions) {
            explosion.process();
        }
        for (Projectile projectile : projectiles) {
            projectile.process();
        }
        for (DamageCircle damageCircle : damageCircles) {
            damageCircle.process();
        }
        for (SpawnCircle spawnCircle : spawnCircles) {
            spawnCircle.process();
        }
        for (Entity entity : entities) {
            entity.process();
        }
    }

    void draw(Graphics2D g2d) {
        super.draw(g2d, xAdjust, yAdjust);
        super.fill(g2d, xAdjust, yAdjust);
        for (SpawnCircle spawnCircle : spawnCircles) {
            spawnCircle.fill(g2d, xAdjust, yAdjust);
        }
        for (Corpse corpse : corpses) {
            corpse.draw(g2d, xAdjust, yAdjust);
            corpse.fill(g2d, xAdjust, yAdjust);
        }
        for (MuzzleFlash muzzleFlash : muzzleFlashes) {
            muzzleFlash.draw(g2d, xAdjust, yAdjust);
        }
        for (Particle particle : particles1) {
            particle.draw(g2d, xAdjust, yAdjust);
        }
        for (Explosion explosion : explosions) {
            explosion.draw(g2d, xAdjust, yAdjust);
            explosion.fill(g2d, xAdjust, yAdjust);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g2d, xAdjust, yAdjust);
        }
        for (Entity entity : entities) {
            entity.draw(g2d, xAdjust, yAdjust);
            entity.fill(g2d, xAdjust, yAdjust);
        }
        for (DamageCircle damageCircle : damageCircles) {
            damageCircle.fill(g2d, xAdjust, yAdjust);
        }
        for (Particle particle : particles2) {
            particle.draw(g2d, xAdjust, yAdjust);
        }
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g2d.setColor(Color.black);
        g2d.drawString(Integer.toString(Math.max(player.health, 0)), 15 + xAdjust,
                30 + yAdjust);
        // g2d.drawString(Integer.toString(id), 350 + xAdjust, 30 + yAdjust);
        g2d.drawString(Integer.toString(score),
                770 - SwingUtilities.computeStringWidth(g2d.getFontMetrics(), Integer.toString(score))
                        + xAdjust,
                30 + yAdjust);
    }

    void addEnemy(String name) {
        double x, y;
        final Enemy<?> enemy = Enemy.createEnemy(this, name);
        if (Math.random() < 0.5) {
            x = (777 - 20 - enemy.getPoint(0).getX()) * Math.random() + 10;
            if (30 < x && x < 777 - 50 - enemy.getPoint(0).getX()) {
                if (Math.random() < 0.5) {
                    y = (50 - 10) * Math.random() + 10;
                } else {
                    y = (50 - 10) * Math.random() + 700 - (50 - 10) - 10 - enemy.getPoint(0).getY();
                }
            } else {
                y = (700 - 20 - enemy.getPoint(0).getY()) * Math.random() + 10;
            }
        } else {
            y = (700 - 20 - enemy.getPoint(0).getY()) * Math.random() + 10;
            if (50 < y && y < 700 - 50 - enemy.getPoint(0).getY()) {
                if (Math.random() < 0.5) {
                    x = (50 - 10) * Math.random() + 10;
                } else {
                    x = (50 - 10) * Math.random() + 777 - (50 - 10) - 10 - enemy.getPoint(0).getX();
                }
            } else {
                x = (777 - 20 - enemy.getPoint(0).getX()) * Math.random() + 10;
            }
        }
        new SpawnCircle(enemy.translate(x, y));
    }

    Polygon boundingBox(int width) {
        return new Polygon(new Point[] { getPoint(0).translate(width, width), getPoint(1).translate(-width, width),
                getPoint(2).translate(-width, -width), getPoint(3).translate(width, -width) });
    }

    boolean inside(Point point) {
        return getPoint(0).getX() < point.getX() - point.getWidth() / 2
                && point.getX() + point.getWidth() / 2 < getPoint(2).getX()
                && getPoint(0).getY() < point.getY() - point.getWidth() / 2
                && point.getY() + point.getWidth() / 2 < getPoint(2).getY();
    }

    void increaseScore(int value) {
        score += value;
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