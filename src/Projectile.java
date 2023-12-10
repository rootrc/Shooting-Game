import java.util.Timer;
import java.util.TimerTask;

class Projectile extends Line {
    boolean isPlayer;
    int damage;
    int piercing;
    int knockback;

    Projectile(Point p1, Point p2) {
        super(p1, p2);
    }

    // for inheritance
    void shoot(double direction) {
    }
}

class Bullet extends Projectile {
    int speed;

    Bullet(Point p1, Point p2) {
        super(p1, p2);
    }

    public Bullet clone() {
        Bullet bullet = new Bullet(p1, p2);
        bullet.isPlayer = isPlayer;
        bullet.damage = damage;
        bullet.piercing = piercing;
        bullet.speed = speed;
        bullet.knockback = knockback;
        return bullet;
    }

    void shoot(double direction) {
        Bullet bullet = this.clone();
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() {
            public void run() {
                bullet.p1.x -= speed * Math.cos(direction);
                bullet.p1.y += speed * Math.sin(direction);
                bullet.p2.x -= speed * Math.cos(direction);
                bullet.p2.y += speed * Math.sin(direction);
                if (Game.getInstance().room.intersects(new Line(bullet.p2, bullet.p2
                        .translate(-bullet.speed * Math.cos(direction),
                                bullet.speed * Math.sin(direction))))) {
                    Game.getInstance().room.projectiles.remove(bullet);
                    timer.cancel();
                    timer.purge();
                } else if (!(Game.getInstance().room.points[0].x < p2.x && p2.x < Game.getInstance().room.points[2].x
                        && Game.getInstance().room.points[0].y < p2.y && p2.y < Game.getInstance().room.points[2].y)) {
                    Game.getInstance().room.projectiles.remove(bullet);
                    timer.cancel();
                    timer.purge();
                }
                for (Entity entity : Game.getInstance().room.entities) {
                    if (entity.getClass() != Player.class && !bullet.isPlayer) {
                        continue;
                    }
                    if (entity.intersects(
                            new Line(bullet.p2, bullet.p2.translate(-bullet.speed * Math.cos(direction),
                                    bullet.speed * Math.sin(direction))))) {
                        entity.health -= bullet.damage;
                        entity.attemptMovement(-bullet.knockback, bullet.caculateRadian());
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
        LimitedBullet limitedBullet = new LimitedBullet(p1, p2);
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
        LimitedBullet limitedBullet = this.clone();
        TimerTask timertask = new TimerTask() {
            int count = 0;

            public void run() {
                limitedBullet.p1.x -= (speed + (double) limitedBullet.length
                        / (limitedBullet.duration * limitedBullet.duration) * (2 * count - 1)) * Math.cos(direction);
                limitedBullet.p1.y += (speed + (double) limitedBullet.length
                        / (limitedBullet.duration * limitedBullet.duration) * (2 * count - 1)) * Math.sin(direction);
                limitedBullet.p2.x -= speed * Math.cos(direction);
                limitedBullet.p2.y += speed * Math.sin(direction);
                count++;
                if (Game.getInstance().room.intersects(new Line(limitedBullet.p2, limitedBullet.p2
                        .translate(-limitedBullet.speed * Math.cos(direction),
                                limitedBullet.speed * Math.sin(direction))))) {
                    Game.getInstance().room.projectiles.remove(limitedBullet);
                    timer.cancel();
                    timer.purge();
                } else if (!(Game.getInstance().room.points[0].x < p2.x && p2.x < Game.getInstance().room.points[2].x
                        && Game.getInstance().room.points[0].y < p2.y && p2.y < Game.getInstance().room.points[2].y)) {
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
                            new Line(limitedBullet.p2, limitedBullet.p2.translate(-limitedBullet.speed * Math.cos(direction),
                                    limitedBullet.speed * Math.sin(direction))))) {
                        entity.health -= limitedBullet.damage;
                        entity.attemptMovement(-limitedBullet.knockback, limitedBullet.caculateRadian());
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
