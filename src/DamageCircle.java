import java.awt.Color;
import java.awt.Graphics2D;

import Geo.Circle;
import Geo.Point;

class DamageCircle extends Circle {
    private Room room;

    DamageCircle(Room room, Point point, int r) {
        super(point, r);
        this.room = room;
        setColor(Color.red);
        room.damageCircles.add(this);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private int frame = 0;
    private int time = 4;
    void process() {
        frame++;
        if (frame == time) {
            room.damageCircles.remove(this);
        }
    }
}