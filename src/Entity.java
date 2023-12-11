import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;

import Geo.Line;
import Geo.Point;
import Geo.Polygon;

abstract class Entity extends Polygon {
    Room room;
    Timer timer = new Timer();
    Weapon weapon;
    double direction;
    int health;
    int value;
    Color orginalColor;
    int corpseTime;

    Entity(Room room, Point[] points) {
        super(points);
        this.room = room;
    }

    Entity(Room room) {
        this.room = room;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    protected Point[] getPoints() {
        return super.getPoints();
    }

    void attemptMove(int distance, double direction) {
        if (!room
                .intersects(directionTranslate(-distance, direction))) {
            directionMove(-distance, direction);
        } else {
            int l = 0;
            int r = distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                if (!room
                        .intersects(directionTranslate(-m, direction))) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            directionMove(-l, direction);
        }
    }

    public abstract Entity clone();

    protected abstract void hit();

    protected abstract void death();
}
