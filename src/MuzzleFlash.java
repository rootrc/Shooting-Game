import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

class MuzzleFlash extends Line {
    Room room;
    Point centroid;
    double direction;
    double length;

    Timer timer = new Timer();

    MuzzleFlash(Entity entity) {
        this.room = entity.room;
        this.centroid = entity.getCentroid().clone();
        this.direction = entity.direction;
        this.length = entity.weapon.projectile.length;
        setBorderColor(new Color(247, 241, 181));
        setWidth(8);
        process();
    }

    private void process() {
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                count++;
                setP1(centroid);
                setP2(centroid.directionTranslate(20 + length / 2, direction));
                if (count == 3) {
                    room.muzzleFlashes.remove(MuzzleFlash.this);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        room.muzzleFlashes.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }
}