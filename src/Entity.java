import java.awt.Color;
import java.util.Timer;

import Geo.Point;
import Geo.Polygon;

abstract class Entity extends Polygon {
    Room room;
    Timer timer = new Timer();
    double direction;
    int health;
    int value;
    Color orginalColor;
    int corpseLength;

    Entity(Room room, Point[] points) {
        super(points);
        this.room = room;
    }

    Entity(Room room) {
        this.room = room;
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

    abstract void hit();

    abstract void death();
}
