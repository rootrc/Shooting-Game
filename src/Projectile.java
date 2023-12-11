import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

abstract class Projectile extends Line {
    private Room room;
    protected Timer timer = new Timer();
    protected boolean isPlayer;
    protected int damage;
    protected int piercing;
    protected double length;
    protected int knockback;

    Projectile(Room room, Point p1, Point p2) {
        super(p1, p2);
        this.room = room;
    }

    Projectile(Room room) {
        this.room = room;
    }

    protected void draw(Graphics2D g2d, int x, int y) {
        super.draw(g2d, x, y);
    }

    protected void setWidth(int width) {
        super.setWidth(width);
    }

    protected Room getRoom() {
        return room;
    }

    abstract public Projectile clone();

    abstract void shoot(Point centroid, double direction);

    abstract void process();

}

class Bullet extends Projectile {
    int speed;

    private Bullet(Room room, Point p1, Point p2) {
        super(room, p1, p2);
    }

    Bullet(Room room) {
        super(room);
    }

    public Bullet clone() {
        Bullet bullet = new Bullet(getRoom(), getP1(), getP2());
        bullet.setBorderColor(getBorderColor());
        bullet.setWidth(getWidth());
        bullet.length = length;
        bullet.isPlayer = isPlayer;
        bullet.damage = damage;
        bullet.piercing = piercing;
        bullet.speed = speed;
        bullet.knockback = knockback;
        return bullet;
    }

    void shoot(Point centroid, double direction) {
        Bullet bullet = clone();
        bullet.set(centroid.directionTranslate(20, direction),
                centroid.directionTranslate(20 + bullet.length, direction));
        bullet.process();
    }

    void process() {
        double direction = caculateRadian();
        TimerTask timertask = new TimerTask() {
            public void run() {
                Bullet.this.directionMove(speed, direction);
                if (getRoom().intersects(new Line(Bullet.this.p2, Bullet.this.p2
                        .directionTranslate(-Bullet.this.speed, direction)))) {
                    getRoom().projectiles.remove(Bullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (!getRoom().inside(getP2())) {
                    getRoom().projectiles.remove(Bullet.this);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : getRoom().entities) {
                    if (entity.getClass() != Player.class && !Bullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(Bullet.this.p2,
                                    Bullet.this.p2.directionTranslate(-Bullet.this.speed, direction)))) {
                        entity.health -= Bullet.this.damage;
                        entity.attemptMove(-Bullet.this.knockback, Bullet.this.caculateRadian());
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                        Bullet.this.piercing--;
                        if (Bullet.this.piercing <= 0) {
                            getRoom().projectiles.remove(Bullet.this);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        getRoom().projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }
}

class LimitedBullet extends Projectile {
    int speed;
    int duration;

    private LimitedBullet(Room room, Point p1, Point p2) {
        super(room, p1, p2);
    }

    LimitedBullet(Room room) {
        super(room);
    }

    public LimitedBullet clone() {
        LimitedBullet limitedBullet = new LimitedBullet(getRoom(), getP1(), getP2());
        limitedBullet.setBorderColor(getBorderColor());
        limitedBullet.setWidth(getWidth());
        limitedBullet.length = length;
        limitedBullet.isPlayer = isPlayer;
        limitedBullet.damage = damage;
        limitedBullet.piercing = piercing;
        limitedBullet.speed = speed;
        limitedBullet.knockback = knockback;
        limitedBullet.duration = duration;
        return limitedBullet;
    }

    void shoot(Point centroid, double direction) {
        LimitedBullet limitedBullet = clone();
        limitedBullet.setP1(centroid.directionTranslate(20, direction));
        limitedBullet.setP2(centroid.directionTranslate(20 + limitedBullet.length, direction));
        limitedBullet.process();
    }

    void process() {
        double direction = caculateRadian();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                LimitedBullet.this.p1.directionMove(speed + (double) LimitedBullet.this.length
                        / (LimitedBullet.this.duration * LimitedBullet.this.duration) * (2 * count - 1), direction);
                LimitedBullet.this.p2.directionMove(speed, direction);
                count++;
                if (getRoom().intersects(new Line(LimitedBullet.this.p2, LimitedBullet.this.p2
                        .directionTranslate(-LimitedBullet.this.speed, direction)))) {
                    getRoom().projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (!getRoom().inside(getP2())) {
                    getRoom().projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (count == LimitedBullet.this.duration) {
                    getRoom().projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : getRoom().entities) {
                    if (entity.getClass() != Player.class && !LimitedBullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(LimitedBullet.this.p2,
                                    LimitedBullet.this.p2.directionTranslate(-LimitedBullet.this.speed,
                                            direction)))) {
                        entity.health -= LimitedBullet.this.damage;
                        entity.attemptMove(-LimitedBullet.this.knockback, LimitedBullet.this.caculateRadian());
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                        LimitedBullet.this.piercing--;
                        if (LimitedBullet.this.piercing <= 0) {
                            getRoom().projectiles.remove(LimitedBullet.this);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        getRoom().projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }
}
