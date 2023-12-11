import java.util.Timer;
import java.util.TimerTask;

import Geo.Line;
import Geo.Point;

abstract class Projectile extends Line {
    Room room;
    boolean isPlayer;
    double length;
    int damage;
    int piercing;
    int knockback;

    Projectile(Room room, Point p1, Point p2) {
        super(p1, p2);
        this.room = room;
    }

    Projectile(Room room) {
        this.room = room;
    }

    abstract public Projectile clone();

    abstract void shoot(Point centroid, double direction, double random);

    abstract void shoot(double direction);
}

class Bullet extends Projectile {
    int speed;

    Bullet(Room room, Point p1, Point p2) {
        super(room, p1, p2);
    }

    Bullet(Room room) {
        super(room);
    }

    public Bullet clone() {
        Bullet bullet = new Bullet(room, getP1(), getP2());
        bullet.setBorderColor(color);
        bullet.setWidth(width);
        bullet.length = length;
        bullet.isPlayer = isPlayer;
        bullet.damage = damage;
        bullet.piercing = piercing;
        bullet.speed = speed;
        bullet.knockback = knockback;
        return bullet;
    }

    void shoot(Point centroid, double direction, double random) {
        direction += random * Math.random() - random / 2;
        Bullet bullet = clone();
        bullet.set(centroid.directionTranslate(20, direction),
                centroid.directionTranslate(20 + bullet.length, direction));
        bullet.shoot(direction);
    }

    void shoot(double direction) {
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                Bullet.this.directionMove(speed, direction);
                if (room.intersects(new Line(Bullet.this.getP2(), Bullet.this.getP2()
                        .directionTranslate(-Bullet.this.speed, direction)))) {
                    room.projectiles.remove(Bullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (!(room.getPoints()[0].getX() < getP2().getX() && getP2().getX() < room.getPoints()[2].getX()
                        && room.getPoints()[0].getY() < getP2().getY()
                        && getP2().getY() < room.getPoints()[2].getY())) {
                    room.projectiles.remove(Bullet.this);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : room.entities) {
                    if (entity.getClass() != Player.class && !Bullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(Bullet.this.getP2(),
                                    Bullet.this.getP2().directionTranslate(-Bullet.this.speed, direction)))) {
                        entity.health -= Bullet.this.damage;
                        entity.attemptMove(-Bullet.this.knockback, Bullet.this.caculateRadian());
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                        Bullet.this.piercing--;
                        if (Bullet.this.piercing <= 0) {
                            room.projectiles.remove(Bullet.this);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        room.projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }
}

class LimitedBullet extends Projectile {
    int speed;
    int duration;

    LimitedBullet(Room room, Point p1, Point p2) {
        super(room, p1, p2);
    }

    LimitedBullet(Room room) {
        super(room);
    }

    public LimitedBullet clone() {
        LimitedBullet limitedBullet = new LimitedBullet(room, getP1(), getP2());
        limitedBullet.color = color;
        limitedBullet.width = width;
        limitedBullet.length = length;
        limitedBullet.isPlayer = isPlayer;
        limitedBullet.damage = damage;
        limitedBullet.piercing = piercing;
        limitedBullet.speed = speed;
        limitedBullet.knockback = knockback;
        limitedBullet.duration = duration;
        return limitedBullet;
    }

    void shoot(Point centroid, double direction, double random) {
        direction += random * Math.random() - random / 2;
        LimitedBullet limitedBullet = clone();
        limitedBullet.setP1(centroid.directionTranslate(20, direction));
        limitedBullet.setP2(centroid.directionTranslate(20 + limitedBullet.length, direction));
        limitedBullet.shoot(direction);
    }

    void shoot(double direction) {
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                LimitedBullet.this.getP1().directionMove(speed + (double) LimitedBullet.this.length
                        / (LimitedBullet.this.duration * LimitedBullet.this.duration) * (2 * count - 1), direction);
                LimitedBullet.this.getP2().directionMove(speed, direction);
                count++;
                if (room.intersects(new Line(LimitedBullet.this.getP2(), LimitedBullet.this.getP2()
                        .directionTranslate(-LimitedBullet.this.speed, direction)))) {
                    room.projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (!(room.getPoint(0).getX() < getP2().getX() && getP2().getX() < room.getPoint(2).getX()
                        && room.getPoint(0).getY() < getP2().getY() && getP2().getY() < room.getPoint(2).getY())) {
                    room.projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                } else if (count == LimitedBullet.this.duration) {
                    room.projectiles.remove(LimitedBullet.this);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : room.entities) {
                    if (entity.getClass() != Player.class && !LimitedBullet.this.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(LimitedBullet.this.getP2(),
                                    LimitedBullet.this.getP2().directionTranslate(-LimitedBullet.this.speed,
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
                            room.projectiles.remove(LimitedBullet.this);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        room.projectiles.add(this);
        timer.schedule(timertask, 0, Game.delay);
    }
}
