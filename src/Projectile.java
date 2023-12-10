import java.util.Timer;
import java.util.TimerTask;

abstract class Projectile extends Line {
    boolean isPlayer;
    int damage;
    int piercing;
    int knockback;

    Projectile(Point p1, Point p2) {
        super(p1, p2);
    }

    abstract public Projectile clone();

    abstract void shoot(double direction);
}

class Bullet extends Projectile {
    int speed;

    Bullet(Point p1, Point p2) {
        super(p1, p2);
    }

    public Bullet clone() {
        Bullet bullet = new Bullet(getP1(), getP2());
        bullet.isPlayer = isPlayer;
        bullet.damage = damage;
        bullet.piercing = piercing;
        bullet.speed = speed;
        bullet.knockback = knockback;
        return bullet;
    }

    void shoot(double direction) {
        Bullet bullet = clone();
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                bullet.directionMove(speed, direction);
                if (Game.getInstance().room.intersects(new Line(bullet.getP2(), bullet.getP2()
                        .directionTranslate(-bullet.speed, direction)))) {
                    Game.getInstance().room.projectiles.remove(bullet);
                    timer.cancel();
                    timer.purge();
                } else if (!(Game.getInstance().room.points[0].getX() < getP2().getX() && getP2().getX() < Game.getInstance().room.points[2].getX()
                        && Game.getInstance().room.points[0].getY() < getP2().getY() && getP2().getY() < Game.getInstance().room.points[2].getY())) {
                    Game.getInstance().room.projectiles.remove(bullet);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : Game.getInstance().room.entities) {
                    if (entity.getClass() != Player.class && !bullet.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(bullet.getP2(), bullet.getP2().directionTranslate(-bullet.speed, direction)))) {
                        entity.health -= bullet.damage;
                        entity.attemptMove(-bullet.knockback, bullet.caculateRadian());
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                        bullet.piercing--;
                        if (bullet.piercing <= 0) {
                            Game.getInstance().room.projectiles.remove(bullet);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        Game.getInstance().room.projectiles.add(bullet);
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }
}

class LimitedBullet extends Projectile {
    int speed;
    int duration;

    LimitedBullet(Point p1, Point p2) {
        super(p1, p2);
    }

    public LimitedBullet clone() {
        LimitedBullet limitedBullet = new LimitedBullet(getP1(), getP2());
        limitedBullet.isPlayer = isPlayer;
        limitedBullet.damage = damage;
        limitedBullet.piercing = piercing;
        limitedBullet.speed = speed;
        limitedBullet.knockback = knockback;
        limitedBullet.duration = duration;
        return limitedBullet;
    }

    void shoot(double direction) {
        Timer timer = new Timer();
        LimitedBullet limitedBullet = clone();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                limitedBullet.getP1().directionMove(speed + (double) limitedBullet.length
                        / (limitedBullet.duration * limitedBullet.duration) * (2 * count - 1), direction);
                limitedBullet.getP2().directionMove(speed, direction);
                count++;
                if (Game.getInstance().room.intersects(new Line(limitedBullet.getP2(), limitedBullet.getP2()
                        .directionTranslate(-limitedBullet.speed, direction)))) {
                    Game.getInstance().room.projectiles.remove(limitedBullet);
                    timer.cancel();
                    timer.purge();
                } else if (!(Game.getInstance().room.points[0].getX() < getP2().getX() && getP2().getX() < Game.getInstance().room.points[2].getX()
                        && Game.getInstance().room.points[0].getY() < getP2().getY() && getP2().getY() < Game.getInstance().room.points[2].getY())) {
                    Game.getInstance().room.projectiles.remove(limitedBullet);
                    timer.cancel();
                    timer.purge();
                } else if (count == limitedBullet.duration) {
                    Game.getInstance().room.projectiles.remove(limitedBullet);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : Game.getInstance().room.entities) {
                    if (entity.getClass() != Player.class && !limitedBullet.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(limitedBullet.getP2(),
                                    limitedBullet.getP2().directionTranslate(-limitedBullet.speed, direction)))) {
                        entity.health -= limitedBullet.damage;
                        entity.attemptMove(-limitedBullet.knockback, limitedBullet.caculateRadian());
                        if (entity.health > 0) {
                            entity.hit();
                        } else {
                            entity.death();
                        }
                        limitedBullet.piercing--;
                        if (limitedBullet.piercing <= 0) {
                            Game.getInstance().room.projectiles.remove(limitedBullet);
                            timer.cancel();
                            timer.purge();
                        }
                    }
                }
            }
        };
        Game.getInstance().room.projectiles.add(limitedBullet);
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }
}
