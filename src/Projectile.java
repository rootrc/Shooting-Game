import java.util.Timer;
import java.util.TimerTask;

class Projectile extends Line {
    boolean isPlayer;
    int damage;
    int piercing;
    double random;

    Projectile(Point p1, Point p2) {
        super(p1, p2);
    }
    //for inheritance
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
        bullet.random = random;
        return bullet;
    }

    void shoot(double direction) {
        Timer timer = new Timer();
        Bullet bullet = this.clone();
        TimerTask timertask = new TimerTask() {
            public void run() {
                bullet.p1.x -= speed * Math.cos(direction);
                bullet.p1.y += speed * Math.sin(direction);
                bullet.p2.x -= speed * Math.cos(direction);
                bullet.p2.y += speed * Math.sin(direction);
                if (Game.getInstance().room.intersects(bullet)) {
                    Game.getInstance().room.projectiles.remove(bullet);
                    timer.cancel();
                    timer.purge();
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
        limitedBullet.duration = duration;
        limitedBullet.random = random;
        return limitedBullet;
    }

    void shoot(double direction) {
        Timer timer = new Timer();
        LimitedBullet limitedBullet = this.clone();
        TimerTask timertask = new TimerTask() {
            int count = 0;
            public void run() {
                limitedBullet.p1.x -= speed * Math.cos(direction);
                limitedBullet.p1.y += speed * Math.sin(direction);
                limitedBullet.p2.x -= speed * Math.cos(direction);
                limitedBullet.p2.y += speed * Math.sin(direction);
                count++;
                if (Game.getInstance().room.intersects(limitedBullet) || count == limitedBullet.duration) {
                    Game.getInstance().room.projectiles.remove(limitedBullet);
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        Game.getInstance().room.projectiles.add(limitedBullet);
        timer.schedule(timertask, 0, Game.getInstance().delay);
    }
}
