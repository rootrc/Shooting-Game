import java.util.Timer;
import java.util.TimerTask;

class Projectile extends Line {
    boolean isPlayer;
    int damage;
    int piercing;
    int random;

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
        bullet.random = random;
        bullet.speed = speed;
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
