import java.awt.Graphics2D;
import java.util.Timer;

import Geo.Line;
import Geo.Point;

abstract class Projectile extends Line {
    private Room room;
    protected Timer timer = new Timer();
    protected boolean isPlayer;
    protected int damage;
    protected int knockback;

    protected double length;
    protected double direction;

    Projectile(Room room, Point p1, Point p2) {
        super(p1, p2);
        this.room = room;
    }

    Projectile(Room room) {
        this.room = room;
    }

    protected void hit(Entity entity, double direction) {
        entity.decreaseHealth(damage);
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
        bullet.direction = bullet.caculateRadian();
        getRoom().projectiles.add(bullet);
    }

    void process() {
        directionMove(speed, direction);
        if (getRoom().intersects(new Line(p2, p2
                .directionTranslate(-speed, direction)))) {
            getRoom().projectiles.remove(this);
        } else if (!getRoom().inside(getP2())) {
            getRoom().projectiles.remove(this);
        }
        if (!isPlayer) {
            if (getRoom().player.intersects(new Line(p2, p2.directionTranslate(-speed, direction)))) {
                hit(getRoom().player, direction);
                piercing--;
                if (piercing <= 0) {
                    getRoom().projectiles.remove(this);
                }
            }
            return;
        }
        for (Entity entity : getRoom().entities) {
            if (entity.getClass() == Player.class) {
                continue;
            }
            if (entity.intersects(new Line(p2, p2.directionTranslate(-speed, direction)))) {
                hit(entity, direction);
                piercing--;
                if (piercing <= 0) {
                    getRoom().projectiles.remove(this);
                }
            }
        }
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

    int frame = 0;

    void shoot(Point centroid, double direction) {
        LimitedBullet limitedBullet = clone();
        limitedBullet.setP1(centroid.directionTranslate(20, direction));
        limitedBullet.setP2(centroid.directionTranslate(20 + limitedBullet.length, direction));
        limitedBullet.direction = limitedBullet.caculateRadian();
        limitedBullet.duration = (int) ((0.8 + 0.4 * Math.random()) * duration);
        getRoom().projectiles.add(limitedBullet);
    }

    void process() {
        frame++;
        p1.directionMove(speed + (double) length / (duration * duration) * (2 * frame - 1), direction);
        p2.directionMove(speed, direction);
        if (getRoom().intersects(new Line(p2, p2
                .directionTranslate(-speed, direction)))) {
            getRoom().projectiles.remove(this);
        } else if (!getRoom().inside(getP2())) {
            getRoom().projectiles.remove(this);
        } else if (frame == duration) {
            getRoom().projectiles.remove(this);
        }
        if (!isPlayer) {
            if (getRoom().player.intersects(new Line(p2, p2.directionTranslate(-speed, direction)))) {
                hit(getRoom().player, direction);
                piercing--;
                if (piercing <= 0) {
                    getRoom().projectiles.remove(this);
                }
            }
            return;
        }
        for (Entity entity : getRoom().entities) {
            if (entity.getClass() == Player.class) {
                continue;
            }
            if (entity.intersects(new Line(p2, p2.directionTranslate(-speed, direction)))) {
                hit(entity, direction);
                piercing--;
                if (piercing <= 0) {
                    getRoom().projectiles.remove(this);
                }
            }
        }
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

    double distance;
    double randomDistance;
    int frame;

    void shoot(Point centroid, double direction) {
        Grenade grenade = clone();
        grenade.setP1(centroid.directionTranslate(20, direction));
        grenade.setP2(centroid.directionTranslate(20 + grenade.length, direction));
        grenade.direction = grenade.caculateRadian();
        grenade.distance = Math.min(maxDistance,
                new Line(grenade.p1, Game.getInstance().panel.mouseLocation()).getLength());
        grenade.randomDistance = (0.8 - 0.3 * Math.random()) * grenade.distance;
        getRoom().projectiles.add(grenade);
    }

    void process() {
        frame += speed;
        directionMove(speed * (2 * distance - 2 * frame - speed) / distance, direction);
        if (getRoom().intersects(new Line(p2, p2.directionTranslate(-speed, direction)))) {
            new Explosion(this);
            getRoom().projectiles.remove(this);
        } else if (!getRoom().inside(getP2())) {
            new Explosion(this);
            getRoom().projectiles.remove(this);
        } else if (frame >= randomDistance) {
            new Explosion(this);
            getRoom().projectiles.remove(this);
        }
    }
}