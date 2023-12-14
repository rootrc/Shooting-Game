import java.awt.Color;
import java.awt.Graphics2D;

import Geo.Line;
import Geo.Point;

class MuzzleFlash extends Line {
    private Room room;
    private Point centroid;
    private double direction;
    private double length;

    MuzzleFlash(Entity entity, double length) {
        room = entity.getRoom();
        centroid = entity.getCentroid().clone();
        direction = entity.direction;
        this.length = length;
        setBorderColor(new Color(247, 231, 141));
        setWidth(8);
        room.muzzleFlashes.add(this);
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    private int frame = 0;

    public void process() {
        frame++;
        setP1(centroid);
        setP2(centroid.directionTranslate(20 + length / 2, direction));
        if (frame == 3) {
            room.muzzleFlashes.remove(this);
        }
    }
}