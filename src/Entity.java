import java.awt.Color;
import java.util.Timer;

class Entity extends Polygon {
    Timer timer = new Timer();
    double direction;
    int health;
    int value;
    Color orginalColor;
    int corpseLength;

    Entity(Point[] points) {
        super(points);
    }

    public Entity clone() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        Entity entity = new Entity(points);
        entity.color = color;
        entity.health = health;
        entity.corpseLength = corpseLength;
        return entity;
    }

    void attemptMovement(int distance, double direction) {
        if (!Game.getInstance().room
                .intersects(this.translate(distance * Math.cos(direction),
                        -distance * Math.sin(direction)))) {
            move(distance * Math.cos(direction),
                    -distance * Math.sin(direction));
        } else {
            int l = 0;
            int r = distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                if (!Game.getInstance().room
                        .intersects(this.translate(m * Math.cos(direction),
                                -m * Math.sin(direction)))) {
                    l = m;
                } else {
                    r = m - 1;
                }
            }
            move(l * Math.cos(direction),
                    -l * Math.sin(direction));
        }
    }

    // for inheritance
    void hit() {

    }

    void death() {

    }
}
