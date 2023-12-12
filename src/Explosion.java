import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Circle;
import Geo.Line;
import Geo.Point;

class Explosion extends Circle {
    private Room room;
    private Timer timer = new Timer();
    private double size;
    private int damage;
    private int knockback;

    Explosion(Grenade grenade) {
        super(grenade.getP2(), 0);
        room = grenade.getRoom();
        size = grenade.size;
        damage = grenade.damage;
        knockback = grenade.knockback;
        setColor(Color.red);
        setBorderColor(Color.red);
        process();
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void fill(Graphics2D g2d, int x, int y) {
        super.fill(g2d, x, y);
    }

    private void process() {
        room.explosions.add(this);
        TimerTask timertask = new TimerTask() {
            double count = 8;
            public void run() {
                r += count;
                count -= 0.1;
                for (Entity entity : room.entities) {
                    if (entity.isInside(Explosion.this)) {
                        entity.health -= damage;
                        double direction = new Line(Explosion.this.center, entity.getCentroid()).caculateRadian();
                        entity.attemptMove(-knockback, direction);
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                    }
                }
                if (r >= size) {
                    room.explosions.remove(Explosion.this);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timertask, 0, Game.delay);
    }
}
