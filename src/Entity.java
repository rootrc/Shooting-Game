import java.awt.Color;
import java.util.Timer;
import java.util.TimerTask;

class Entity extends Polygon {
    Timer timer = new Timer();
    double direction;
    int health;
    int corpseLength;

    Entity(Point[] points) {
        super(points);
    }

    public Entity clone() {
        Point[] points = new Point[length];
        for (int i = 0; i < length; i++) {
            points[i] = this.points[i].clone();
        }
        Entity entity = new Entity(points);
        entity.color = color;
        entity.health = health;
        entity.corpseLength = corpseLength;
        return entity;
    }

    void process() {
        for (Projectile projectile : Game.getInstance().room.projectiles) {
            if (this.intersects(projectile) && health != Integer.MAX_VALUE) {
                health -= projectile.damage;
                if (health <= 0) {
                    this.death();
                }
                projectile.piercing--;
                if (projectile.piercing <= 0) {
                    Game.getInstance().room.projectiles.remove(projectile);
                }
            }
        }
    }

    void death() {
        Game.getInstance().room.entities.remove(this);
        if (Enemy.class.isAssignableFrom(this.getClass()) || this.getClass() == Player.class) {
            timer.cancel();
            timer.purge();
            Timer timer = new Timer();
            Entity corpse = this.clone();
            corpse.color = Color.black;
            corpse.health = Integer.MAX_VALUE;
            TimerTask timertask = new TimerTask() {
                int frame = 0;
                public void run() {
                    frame++;
                    if (frame == corpse.corpseLength) {
                        Game.getInstance().room.entities.remove(corpse);
                        timer.cancel();
                        timer.purge();
                    }
                }
            };
            Game.getInstance().room.entities.add(corpse);
            timer.schedule(timertask, 0, Game.getInstance().delay);
        }
    }
}
