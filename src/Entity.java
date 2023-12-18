import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;

import Geo.Point;
import Geo.Polygon;

abstract class Entity extends Polygon {
    private Room room;
    protected Timer timer = new Timer();
    protected Weapon weapon;
    protected double direction;
    protected int health;
    protected Color orginalColor;
    protected int corpseTime;
    protected HealthBar healthBar;

    Entity(Room room, Point[] points) {
        super(points);
        this.room = room;
    }

    Entity(Room room) {
        this.room = room;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
        healthBar.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    protected Point[] getPoints() {
        return super.getPoints();
    }

    protected void attemptMove(int distance, double direction) {
        if (!getRoom()
                .intersects(directionTranslate(-distance, direction))) {
            directionMove(-distance, direction);
        } else {
            int l = 0;
            int r = distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                if (!getRoom()
                        .intersects(directionTranslate(-m, direction))) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            directionMove(-l, direction);
        }
    }

    void decreaseHealth(int damage) {
        this.health -= damage;
        new DamageNum(room, getCentroid(), damage);
    }

    protected Room getRoom() {
        return room;
    }

    protected Point getCentroid() {
        return super.getCentroid();
    }

    public abstract Entity clone();

    public abstract void process();

    protected abstract void hit();

    protected abstract void death();
}