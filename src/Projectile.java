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
    protected double length;
    protected int knockback;

    Projectile(Room room, Point p1, Point p2) {
        super(p1, p2);
        this.room = room;
    }

    Projectile(Room room) {
        this.room = room;
    }

    protected void remove() {
        getRoom().projectiles.remove(this);
        timer.cancel();
        timer.purge();
    }

    protected void hit(Entity entity, double direction) {
        entity.health -= damage;
        entity.attemptMove(-knockback, direction);
        if (entity.health > 0) {
            entity.hit();
        } else {
            entity.death();
        }
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
    int piercing;
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
        bullet.knockback = knockback;
        bullet.speed = speed;
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
                    remove();
                } else if (!getRoom().inside(getP2())) {
                    remove();
                }
                for (Entity entity : getRoom().entities) {
                    if (entity.getClass() != Player.class && !Bullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(Bullet.this.p2,
                                    Bullet.this.p2.directionTranslate(-Bullet.this.speed, direction)))) {
                        hit(entity, direction);
                        Bullet.this.piercing--;
                        if (Bullet.this.piercing <= 0) {
                            remove();
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
    int piercing;
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
        limitedBullet.knockback = knockback;
        limitedBullet.speed = speed;
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
        duration = (int) ((0.8 + 0.4 * Math.random()) * duration);
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                LimitedBullet.this.p1.directionMove(speed + (double) LimitedBullet.this.length
                        / (LimitedBullet.this.duration * LimitedBullet.this.duration) * (2 * count - 1), direction);
                LimitedBullet.this.p2.directionMove(speed, direction);
                count++;
                if (getRoom().intersects(new Line(LimitedBullet.this.p2, LimitedBullet.this.p2
                        .directionTranslate(-LimitedBullet.this.speed, direction)))) {
                    remove();
                } else if (!getRoom().inside(getP2())) {
                    remove();
                } else if (count == LimitedBullet.this.duration) {
                    remove();
                }
                for (Entity entity : getRoom().entities) {
                    if (entity.getClass() != Player.class && !LimitedBullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(LimitedBullet.this.p2,
                                    LimitedBullet.this.p2.directionTranslate(-LimitedBullet.this.speed,
                                            direction)))) {
                        hit(entity, direction);
                        LimitedBullet.this.piercing--;
                        if (LimitedBullet.this.piercing <= 0) {
                            remove();
                        }
                    }
                }
            }
        };
        getRoom().projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }
}

class Grenade extends Projectile {
    int speed;
    int size;
    int maxDistance;

    private Grenade(Room room, Point p1, Point p2) {
        super(room, p1, p2);
    }

    Grenade(Room room) {
        super(room);
    }

    public Grenade clone() {
        Grenade grenade = new Grenade(getRoom(), getP1(), getP2());
        grenade.setBorderColor(getBorderColor());
        grenade.setWidth(getWidth());
        grenade.length = length;
        grenade.isPlayer = isPlayer;
        grenade.damage = damage;
        grenade.knockback = knockback;
        grenade.speed = speed;
        grenade.size = size;
        grenade.maxDistance = maxDistance;
        return grenade;
    }

    void shoot(Point centroid, double direction) {
        Grenade grenade = clone();
        grenade.setP1(centroid.directionTranslate(20, direction));
        grenade.setP2(centroid.directionTranslate(20 + grenade.length, direction));
        grenade.process();
    }

    void process() {
        double direction = caculateRadian();
        double distance = Math.min(maxDistance, new Line(this.p1, Game.getInstance().panel.mouseLocation()).getLength());
        double randomDistance = (0.8 - 0.3 * Math.random()) * distance;
        TimerTask timertask = new TimerTask() {
            int count = 0;
            public void run() {
                count += speed;
                Grenade.this.directionMove(speed * (2 * distance - 2 * count - speed) / distance, direction);
                if (getRoom().intersects(new Line(Grenade.this.p2, Grenade.this.p2
                        .directionTranslate(-Grenade.this.speed, direction)))) {
                    new Explosion(Grenade.this);
                    remove();
                } else if (!getRoom().inside(getP2())) {
                    new Explosion(Grenade.this);
                    remove();
                } else if (count >= randomDistance) {
                    new Explosion(Grenade.this);
                    remove();
                }

            }
        };
        getRoom().projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }

}
