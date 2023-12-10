import java.awt.Color;
import java.util.Timer;

abstract class Entity extends Polygon {
    Timer timer = new Timer();
    double direction;
    int health;
    int value;
    Color orginalColor;
    int corpseLength;

    Entity(Point[] points) {
        super(points);
    }

    void attemptMove(int distance, double direction) {
        if (!Game.getInstance().room
                .intersects(directionTranslate(-distance, direction))) {
                    directionMove(-distance, direction);
        } else {
            int l = 0;
            int r = distance;
            while (l < r) {
                int m = (l + r + 1) / 2;
                if (!Game.getInstance().room
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
